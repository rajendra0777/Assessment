package com.caprae.saasquatch.service;

import com.caprae.saasquatch.dto.LeadScrapeRequest;
import com.caprae.saasquatch.entity.Lead;
import com.caprae.saasquatch.entity.LeadContact;
import com.caprae.saasquatch.entity.ScrapingLog;
import com.caprae.saasquatch.exception.ScrapeBlockedException;
import com.caprae.saasquatch.exception.UnsafeUrlException;
import com.caprae.saasquatch.repository.LeadRepository;
import com.caprae.saasquatch.repository.ScrapingLogRepository;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WebScraperService {

    private static final Logger log = LoggerFactory.getLogger(WebScraperService.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");

    /** Token used both in the User-Agent header and when matching robots.txt groups. */
    private static final String BOT_TOKEN = "SaaSquatchLeadGenBot";
    private static final int MAX_REDIRECTS = 3;

    /** Page where site owners can learn about the bot and ask to be excluded. Set APP_SCRAPER_CONTACT_URL. */
    @Value("${app.scraper.contact-url:https://example.com/bot-info}")
    private String contactUrl;

    private final LeadRepository leadRepository;
    private final ScrapingLogRepository scrapingLogRepository;
    private final AiCopilotService aiCopilotService;

    public WebScraperService(LeadRepository leadRepository,
                             ScrapingLogRepository scrapingLogRepository,
                             AiCopilotService aiCopilotService) {
        this.leadRepository = leadRepository;
        this.scrapingLogRepository = scrapingLogRepository;
        this.aiCopilotService = aiCopilotService;
    }

    public List<Lead> scrapeAndEnrich(LeadScrapeRequest request) {
        long startTime = System.currentTimeMillis();
        String query = request.getQuery().trim();
        List<Lead> leadsFound = new ArrayList<>();

        try {
            if (isUrlOrDomain(query)) {
                // Direct Domain Scrape
                Lead singleLead = scrapeSingleTarget(query, request.getIndustry());
                if (singleLead != null) {
                    if (request.getRegion() != null && !request.getRegion().isBlank()) {
                        singleLead.setRegion(request.getRegion());
                    }
                    if (request.getRunAiAnalysis()) {
                        var evaluation = aiCopilotService.evaluateLead(singleLead);
                        singleLead.setDealScore(evaluation.getDealScore());
                        singleLead.setAcquisitionFit(evaluation.getAcquisitionFit());
                        singleLead.setAiGrowthThesis(evaluation.getGrowthThesis());
                    }
                    if (request.getAutoSave()) {
                        singleLead = persistOrUpdate(singleLead);
                    }
                    leadsFound.add(singleLead);
                }
            } else {
                // Industry / Niche Discovery Engine
                leadsFound = discoverB2bNicheLeads(query, request.getIndustry(), request.getRegion(), request.getAutoSave(), request.getRunAiAnalysis());
            }

            long elapsed = System.currentTimeMillis() - startTime;
            scrapingLogRepository.save(new ScrapingLog(
                    query,
                    leadsFound.size(),
                    "SUCCESS",
                    elapsed,
                    "Successfully extracted and enriched " + leadsFound.size() + " leads for query: " + query
            ));

        } catch (UnsafeUrlException | ScrapeBlockedException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            log.warn("Scrape rejected for query {}: {}", query, e.getMessage());
            scrapingLogRepository.save(new ScrapingLog(query, 0, "BLOCKED", elapsed, e.getMessage()));
            throw e;
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            log.error("Scraping execution error for query: {}", query, e);
            scrapingLogRepository.save(new ScrapingLog(
                    query,
                    0,
                    "FAILED",
                    elapsed,
                    "Error during scraping: " + e.getMessage()
            ));
        }

        return leadsFound;
    }

    public Lead scrapeSingleTarget(String urlOrDomain, String fallbackIndustry) {
        String cleanDomain = normalizeDomain(urlOrDomain);
        String targetUrl = urlOrDomain.startsWith("http://") || urlOrDomain.startsWith("https://")
                ? urlOrDomain
                : "https://" + cleanDomain;

        Lead lead = new Lead();
        lead.setDomain(cleanDomain);
        lead.setWebsiteUrl(targetUrl);
        lead.setSource("SaaSquatch Real-Time Scraper");
        lead.setStatus("SOURCED");

        // Guards run first and are never swallowed: SSRF check, then robots.txt.
        URI safeUri = UrlSafetyValidator.validate(targetUrl);
        assertRobotsAllow(safeUri);

        try {
            Document doc = fetchHtml(safeUri);
            lead.setDataProvenance("LIVE_SCRAPED");

            // Extract Company Name & Title
            String pageTitle = doc.title();
            String companyName = extractCompanyName(pageTitle, cleanDomain);
            lead.setCompanyName(companyName);

            // Extract Meta Description
            Element metaDesc = doc.selectFirst("meta[name=description], meta[property=og:description]");
            String description = metaDesc != null ? metaDesc.attr("content") : "High-growth B2B technology platform operating at " + cleanDomain;
            lead.setDescription(truncate(description, 3500));

            // Detect Tech Stack
            String detectedTech = detectTechStack(doc);
            lead.setTechStack(detectedTech);

            // Extract Contacts / Emails / LinkedIn
            extractContactDetails(doc, cleanDomain, lead);

            // Synthesize financial estimates based on domain metrics
            synthesizeEstimates(lead, fallbackIndustry);

            return lead;
        } catch (UnsafeUrlException | ScrapeBlockedException e) {
            throw e; // a redirect hop was rejected by the guards
        } catch (Exception e) {
            log.warn("Direct HTTP fetch failed for {}: {}. Returning a clearly labelled SIMULATED profile.", targetUrl, e.getMessage());
            // Fallback: nothing here was observed on the real site, so it is labelled SIMULATED_FALLBACK.
            lead.setDataProvenance("SIMULATED_FALLBACK");
            lead.setCompanyName(capitalizeDomain(cleanDomain));
            lead.setDescription("Unverified placeholder profile. The site " + cleanDomain + " could not be fetched, so no details were collected.");
            lead.setTechStack("");
            synthesizeEstimates(lead, fallbackIndustry);
            return lead;
        }
    }

    private void extractContactDetails(Document doc, String domain, Lead lead) {
        Set<String> emails = new HashSet<>();
        // Look in mailto: links
        Elements mailtoLinks = doc.select("a[href^=mailto:]");
        for (Element link : mailtoLinks) {
            String href = link.attr("href").replace("mailto:", "").trim();
            if (EMAIL_PATTERN.matcher(href).matches()) {
                emails.add(href);
            }
        }

        // Look for a LinkedIn company link on the page (never guess one)
        Element linkedinLink = doc.selectFirst("a[href*='linkedin.com/company/']");
        if (linkedinLink != null) {
            lead.setLinkedinCompanyUrl(linkedinLink.attr("href"));
        }

        // Search body text for emails
        Matcher matcher = EMAIL_PATTERN.matcher(doc.text());
        while (matcher.find() && emails.size() < 3) {
            String found = matcher.group();
            String lower = found.toLowerCase();
            if (!lower.endsWith(".png") && !lower.endsWith(".jpg") && !lower.endsWith(".gif") && !lower.endsWith(".svg")) {
                emails.add(found);
            }
        }

        // Only record contact details that were actually published on the page. Nothing is guessed or invented.
        if (!emails.isEmpty()) {
            String email = emails.iterator().next();
            lead.setPrimaryEmail(email);
            lead.addContact(new LeadContact("Unnamed public contact", "Published on company website", email,
                    lead.getLinkedinCompanyUrl(), null, false));
        }
    }

    private String detectTechStack(Document doc) {
        Set<String> stack = new LinkedHashSet<>();
        String html = doc.html().toLowerCase();

        if (html.contains("stripe.com") || html.contains("stripe")) stack.add("Stripe Payments");
        if (html.contains("aws") || html.contains("amazonaws")) stack.add("Amazon Web Services (AWS)");
        if (html.contains("cloudflare")) stack.add("Cloudflare CDN");
        if (html.contains("react") || html.contains("_next")) stack.add("React / Next.js");
        if (html.contains("vue") || html.contains("nuxt")) stack.add("Vue.js");
        if (html.contains("hubspot")) stack.add("HubSpot CRM");
        if (html.contains("intercom")) stack.add("Intercom Live Chat");
        if (html.contains("google-analytics") || html.contains("gtag")) stack.add("Google Analytics 4");
        if (html.contains("shopify")) stack.add("Shopify Plus");
        if (html.contains("wp-content") || html.contains("wordpress")) stack.add("WordPress Headless");

        // Keyword matching on page HTML is a heuristic and can produce false positives.
        return stack.isEmpty() ? "" : String.join(", ", stack);
    }

    /**
     * Placeholder ESTIMATES only. Employee count, revenue, EBITDA and location are generated deterministically
     * from the domain name. They are not collected from any source and must never be presented as facts.
     */
    private void synthesizeEstimates(Lead lead, String fallbackIndustry) {
        Random rand = new Random(lead.getDomain().hashCode());
        int employees = 15 + Math.abs(rand.nextInt(85));
        lead.setEmployeeCount(employees);

        double rev = 1_200_000.0 + (employees * (80_000.0 + Math.abs(rand.nextInt(40_000))));
        lead.setEstimatedRevenue(Math.round(rev / 10_000.0) * 10_000.0);
        lead.setEstimatedEbitda(Math.round((rev * (0.20 + (rand.nextDouble() * 0.15))) / 10_000.0) * 10_000.0);

        String[] locations = {"Austin, TX, USA", "New York, NY, USA", "San Francisco, CA, USA", "Chicago, IL, USA", "Denver, CO, USA", "Boston, MA, USA", "Toronto, Canada"};
        lead.setLocation(locations[Math.abs(rand.nextInt(locations.length))]);

        if (fallbackIndustry != null && !fallbackIndustry.isBlank()) {
            lead.setIndustry(fallbackIndustry);
        } else {
            String[] industries = {"B2B SaaS", "FinTech Infrastructure", "Healthcare IT", "Cybersecurity", "DevOps & Cloud", "HR Tech", "Supply Chain SaaS"};
            lead.setIndustry(industries[Math.abs(rand.nextInt(industries.length))]);
        }
        lead.setBusinessModel("B2B Recurring SaaS");
    }

    private List<Lead> discoverB2bNicheLeads(String query, String industry, String region, boolean autoSave, boolean runAi) {
        List<Lead> leads = new ArrayList<>();
        String[] prefixes = {"nexus", "pulse", "cloud", "apex", "strata", "omni", "vanguard", "catalyst"};
        String cleanQuery = query.toLowerCase().replaceAll("[^a-z0-9]", "");

        for (int i = 0; i < 4; i++) {
            String brand = prefixes[i] + cleanQuery;
            String domain = brand + ".example.com"; // reserved example domain: these candidates are not real companies

            Lead lead = new Lead();
            lead.setDomain(domain);
            lead.setCompanyName(capitalize(prefixes[i]) + " " + capitalize(cleanQuery));
            lead.setWebsiteUrl("https://" + domain);
            lead.setIndustry(industry != null && !industry.isBlank() ? industry : "B2B SaaS - " + capitalize(query));
            lead.setRegion(region != null && !region.isBlank() ? region : "North America");
            lead.setDescription("SIMULATED candidate generated for demonstration. Not a real company. Query: " + query);
            lead.setTechStack("React, TypeScript, AWS, PostgreSQL, Stripe, Redis");
            lead.setBusinessModel("B2B Recurring SaaS");
            lead.setSource("Simulated discovery (demo)");
            lead.setDataProvenance("SIMULATED_DISCOVERY");
            lead.setStatus("SOURCED");
            lead.setPriority("WARM");
            lead.setWinProbability(50);
            synthesizeEstimates(lead, industry);

            lead.setPrimaryEmail("contact@" + domain);
            
            LeadContact c = new LeadContact("Simulated Contact", "Placeholder", "contact@" + domain, null, "+1 (555) 555-010" + i, false);
            lead.addContact(c);

            if (runAi) {
                var evaluation = aiCopilotService.evaluateLead(lead);
                lead.setDealScore(evaluation.getDealScore());
                lead.setAcquisitionFit(evaluation.getAcquisitionFit());
                lead.setAiGrowthThesis(evaluation.getGrowthThesis());
            }

            if (autoSave) {
                lead = persistOrUpdate(lead);
            }
            leads.add(lead);
        }
        return leads;
    }

    private Lead persistOrUpdate(Lead lead) {
        return leadRepository.findByDomain(lead.getDomain())
                .map(existing -> {
                    existing.setDescription(lead.getDescription());
                    existing.setDataProvenance(lead.getDataProvenance());
                    existing.setTechStack(lead.getTechStack());
                    existing.setDealScore(lead.getDealScore());
                    existing.setAcquisitionFit(lead.getAcquisitionFit());
                    existing.setAiGrowthThesis(lead.getAiGrowthThesis());
                    return leadRepository.save(existing);
                })
                .orElseGet(() -> leadRepository.save(lead));
    }

    private boolean isUrlOrDomain(String query) {
        return query.contains(".") && !query.contains(" ");
    }

    private String normalizeDomain(String input) {
        try {
            String url = input.trim();
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
            URI uri = URI.create(url);
            String host = uri.getHost();
            if (host != null) {
                return host.startsWith("www.") ? host.substring(4) : host;
            }
        } catch (Exception ignored) {}
        return input.replace("http://", "").replace("https://", "").replace("www.", "").split("/")[0].trim();
    }

    private String extractCompanyName(String pageTitle, String domain) {
        if (pageTitle == null || pageTitle.isBlank()) {
            return capitalizeDomain(domain);
        }
        String[] parts = pageTitle.split("[-|–—:]");
        String first = parts[0].trim();
        return first.length() > 30 ? capitalizeDomain(domain) : first;
    }

    private String capitalizeDomain(String domain) {
        String base = domain.split("\\.")[0];
        return capitalize(base);
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return "";
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    private String truncate(String text, int length) {
        if (text == null) return "";
        return text.length() > length ? text.substring(0, length) : text;
    }

    private String userAgent() {
        return BOT_TOKEN + "/1.0 (+" + contactUrl + ")";
    }

    /** Fetches a page, following at most MAX_REDIRECTS redirects and re-validating every hop against the SSRF guard. */
    private Connection.Response fetchWithSafeRedirects(URI start, int timeoutMs) throws IOException {
        URI current = start;
        for (int hop = 0; hop <= MAX_REDIRECTS; hop++) {
            UrlSafetyValidator.validate(current.toString());
            Connection.Response res = Jsoup.connect(current.toString())
                    .userAgent(userAgent())
                    .timeout(timeoutMs)
                    .followRedirects(false)
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .maxBodySize(2 * 1024 * 1024)
                    .execute();
            int code = res.statusCode();
            if (code >= 300 && code < 400) {
                String location = res.header("Location");
                if (location == null || location.isBlank()) {
                    throw new IOException("Redirect without Location header");
                }
                current = current.resolve(location);
                continue;
            }
            return res;
        }
        throw new IOException("Too many redirects");
    }

    private Document fetchHtml(URI uri) throws IOException {
        Connection.Response res = fetchWithSafeRedirects(uri, 8000);
        if (res.statusCode() >= 400) {
            throw new IOException("HTTP " + res.statusCode());
        }
        return res.parse();
    }

    /** Honors robots.txt. A missing file (4xx) allows access; a server error or unreachable file is treated as disallow. */
    private void assertRobotsAllow(URI uri) {
        String path = (uri.getRawPath() == null || uri.getRawPath().isEmpty()) ? "/" : uri.getRawPath();
        try {
            Connection.Response res = fetchWithSafeRedirects(uri.resolve("/robots.txt"), 5000);
            int code = res.statusCode();
            if (code >= 500) {
                throw new ScrapeBlockedException("robots.txt could not be read (server error), so " + uri.getHost() + " was skipped");
            }
            if (code >= 400) {
                return; // no robots.txt published
            }
            if (!RobotsTxtPolicy.isAllowed(res.body(), BOT_TOKEN, path)) {
                throw new ScrapeBlockedException(uri.getHost() + " disallows automated access in robots.txt, so it was not scraped");
            }
        } catch (IOException e) {
            throw new ScrapeBlockedException("robots.txt for " + uri.getHost() + " could not be retrieved, so the site was skipped");
        }
    }
}
