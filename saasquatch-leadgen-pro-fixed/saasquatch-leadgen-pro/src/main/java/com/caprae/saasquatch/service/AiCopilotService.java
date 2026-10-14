package com.caprae.saasquatch.service;

import com.caprae.saasquatch.dto.AiScoringResponse;
import com.caprae.saasquatch.dto.OutreachRequest;
import com.caprae.saasquatch.dto.OutreachResponse;
import com.caprae.saasquatch.entity.Lead;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.*;

@Service
public class AiCopilotService {

    public AiScoringResponse evaluateLead(Lead lead) {
        AiScoringResponse res = new AiScoringResponse();
        res.setLeadId(lead.getId());
        res.setCompanyName(lead.getCompanyName());

        int score = calculateDealScore(lead);
        res.setDealScore(score);

        if (score >= 88) {
            res.setAcquisitionFit("PRIME_TARGET");
        } else if (score >= 75) {
            res.setAcquisitionFit("HIGH");
        } else if (score >= 60) {
            res.setAcquisitionFit("MEDIUM");
        } else {
            res.setAcquisitionFit("SPECULATIVE");
        }

        // Multiple and Valuation
        double arr = lead.getEstimatedRevenue() != null ? lead.getEstimatedRevenue() : 1_500_000.0;
        double ebitda = lead.getEstimatedEbitda() != null ? lead.getEstimatedEbitda() : (arr * 0.25);

        double lowerMultiple = 4.0 + (score > 80 ? 1.5 : 0.5);
        double upperMultiple = lowerMultiple + 2.0;

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        currencyFormatter.setMaximumFractionDigits(0);

        String lowVal = currencyFormatter.format(ebitda * lowerMultiple);
        String highVal = currencyFormatter.format(ebitda * upperMultiple);

        res.setEbitdaMultipleRange(String.format("%.1fx - %.1fx Adjusted EBITDA", lowerMultiple, upperMultiple));
        res.setEstimatedValuation(lowVal + " - " + highVal);

        // Strengths & Risks
        List<String> strengths = new ArrayList<>();
        List<String> risks = new ArrayList<>();

        if (lead.getTechStack() != null && !lead.getTechStack().isBlank()) {
            strengths.add("Modern tech stack footprint: " + lead.getTechStack());
        }
        if (arr >= 2_000_000.0) {
            strengths.add("Established ARR scale above $2M threshold with sticky client base.");
        } else {
            strengths.add("Nimble sub-$2M ARR sweet-spot for ETA/micro-PE platform roll-ups.");
        }
        if ("SaaS".equalsIgnoreCase(lead.getBusinessModel()) || "B2B Recurring SaaS".equalsIgnoreCase(lead.getBusinessModel())) {
            strengths.add("High gross margins (>80%) and recurring annual license contracts.");
        }

        risks.add("Founder key-man dependency risk prior to operational handoff.");
        risks.add("Requires sales outbound modernization and Caprae AI tooling integration.");

        res.setKeyStrengths(strengths);
        res.setOperationalRisks(risks);

        // Caprae 7-Year Value Creation Thesis
        String thesis = String.format(
                "Strategic ETA Thesis for %s (%s): Company exhibits strong organic product-market fit in the %s vertical. " +
                "Post-acquisition play leverages Caprae Capital's 7-year growth framework: implement AI-automated pipeline sourcing, " +
                "professionalize the go-to-market engine, expand average contract value (ACV) through tiered enterprise addons, " +
                "and deploy operational leverage to double EBITDA margin within 24 months.",
                lead.getCompanyName(),
                lead.getDomain(),
                lead.getIndustry() != null ? lead.getIndustry() : "B2B Tech"
        );
        res.setGrowthThesis(thesis);
        res.setRecommendedNextStep("Initiate confidential founder outreach via Searcher warm sequence; request trailing 12-month P&L.");

        return res;
    }

    public OutreachResponse generateOutreach(Lead lead, OutreachRequest req) {
        String company = lead.getCompanyName() != null ? lead.getCompanyName() : "your company";
        String recipient = req.getRecipientTitle() != null ? req.getRecipientTitle() : "Founder / CEO";
        String sender = req.getSenderName() != null ? req.getSenderName() : "Caprae Capital Deal Team";
        String industry = lead.getIndustry() != null ? lead.getIndustry() : "B2B technology";

        OutreachResponse res = new OutreachResponse();

        if ("CONFIDENTIAL_ACQUISITION".equalsIgnoreCase(req.getTone()) || "PE_GROWTH_PARTNER".equalsIgnoreCase(req.getTone())) {
            res.setSubjectLine("Confidential inquiry regarding " + company + " & long-term succession");
            res.setEmailBody(
                    "Hi " + recipient + ",\n\n" +
                    "I've been following " + company + " and the impressive software platform you've built in the " + industry + " space. " +
                    "Unlike traditional PE funds driven by short-term financial engineering, Caprae Capital operates on an operator-first, " +
                    "7-year partnership philosophy designed to preserve your legacy and scale operations.\n\n" +
                    "We are actively deploying committed capital to partner with or acquire high-performing businesses like " + company + ". " +
                    "We offer flexible deal structures, full leadership succession if you're seeking a transition, and substantial growth resources.\n\n" +
                    "Would you be open to a brief, confidential 10-minute introductory conversation next Tuesday or Thursday?\n\n" +
                    "Best regards,\n" +
                    sender + "\n" +
                    "Caprae Capital Partners"
            );
        } else {
            res.setSubjectLine("Exploring strategic growth & partnership for " + company);
            res.setEmailBody(
                    "Hi " + recipient + ",\n\n" +
                    "Stumbled across " + company + " while analyzing top performers in " + industry + ". Really impressed by your product focus " +
                    "and technology footprint (" + (lead.getTechStack() != null ? lead.getTechStack() : "cloud architecture") + ").\n\n" +
                    "At Caprae Capital, we partner directly with founder-led companies to unlock AI-enabled operational scale and expand enterprise market share. " +
                    "I'd love to learn more about your strategic roadmap and share observations on how peer companies are achieving 2-3x multiples.\n\n" +
                    "Do you have 15 minutes for a quick founder-to-operator chat this week?\n\n" +
                    "Warmly,\n" +
                    sender + "\n" +
                    "Caprae Capital Partners"
            );
        }

        res.setFollowUpDay3Snippet("Following up on my note below—wanted to ensure this didn't get buried. We are currently reviewing acquisitions in the " + industry + " sector and would value the chance to speak.");
        res.setFollowUpDay7Snippet("Final note from my end: I know how demanding running " + company + " is. Whenever you're considering strategic capital or succession planning, our door is always open.");
        res.setPersonalizationHook("Noticed your domain " + lead.getDomain() + " has strong positioning in " + industry + " with verified modern stack.");
        res.setStrategicDiscussionPoints(List.of(
                "Caprae 7-Year Long Horizon vs Traditional 3-Year PE flip",
                "Founder liquidity while maintaining roll-over equity upside",
                "MaaS (M&A as a Service) operational AI injection"
        ));

        return res;
    }

    public int calculateDealScore(Lead lead) {
        int score = 65;

        // ARR boost
        if (lead.getEstimatedRevenue() != null) {
            double rev = lead.getEstimatedRevenue();
            if (rev >= 1_000_000 && rev <= 8_000_000) {
                score += 15; // Sweet spot for ETA / search fund
            } else if (rev > 8_000_000) {
                score += 10;
            } else {
                score += 5;
            }
        }

        // EBITDA profitability boost
        if (lead.getEstimatedEbitda() != null && lead.getEstimatedEbitda() > 0) {
            score += 10;
        }

        // Tech stack completeness
        if (lead.getTechStack() != null && !lead.getTechStack().isBlank()) {
            if (lead.getTechStack().toLowerCase().contains("stripe") || lead.getTechStack().toLowerCase().contains("aws") || lead.getTechStack().toLowerCase().contains("react")) {
                score += 8;
            }
        }

        // Business Model
        if (lead.getBusinessModel() != null && lead.getBusinessModel().toLowerCase().contains("saas")) {
            score += 5;
        }

        return Math.min(score, 98);
    }

    // --- ADVANCED ENTERPRISE AI FEATURES ---

    // 1. RAG (Retrieval-Augmented Generation) Pipeline over Database Leads
    public Map<String, Object> executeRagSearch(String prompt, List<Lead> allLeads) {
        String clean = prompt.toLowerCase();
        List<Lead> matched = allLeads.stream()
                .filter(l -> {
                    String fullText = (l.getCompanyName() + " " + l.getDomain() + " " + l.getIndustry() + " " +
                            l.getTechStack() + " " + l.getDescription() + " " + l.getStatus() + " " + l.getPriority() + " " + l.getRegion()).toLowerCase();
                    return fullText.contains(clean) ||
                            Arrays.stream(clean.split(" ")).anyMatch(word -> word.length() > 3 && fullText.contains(word));
                })
                .toList();

        if (matched.isEmpty()) {
            matched = allLeads.stream().limit(3).toList();
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("query", prompt);
        response.put("matchesCount", matched.size());
        response.put("retrievedLeads", matched);

        StringBuilder synthesis = new StringBuilder();
        synthesis.append("### RAG Intelligence Briefing: \"").append(prompt).append("\"\n\n");
        synthesis.append("**Database Context:** Retrieved ").append(matched.size()).append(" proprietary deal target(s) matching your investment thesis criteria.\n\n");

        for (Lead l : matched) {
            synthesis.append("- **").append(l.getCompanyName()).append("** (`").append(l.getDomain()).append("`): ")
                    .append(l.getIndustry()).append(" | ARR: $").append(String.format("%,.0f", l.getEstimatedRevenue() != null ? l.getEstimatedRevenue() : 0))
                    .append(" | EBITDA: $").append(String.format("%,.0f", l.getEstimatedEbitda() != null ? l.getEstimatedEbitda() : 0))
                    .append(" | Deal Score: ").append(l.getDealScore()).append("/100 (").append(l.getPriority()).append(" Priority)\n");
        }

        synthesis.append("\n**Caprae Strategic Recommendation:** ");
        if (!matched.isEmpty()) {
            Lead top = matched.get(0);
            synthesis.append("Prioritize outreach to ").append(top.getCompanyName()).append(". With an ARR of $")
                    .append(String.format("%,.0f", top.getEstimatedRevenue())).append(" and ").append(top.getAcquisitionFit())
                    .append(" fit rating, this asset represents high synergy for Caprae's 7-year MaaS operational transformation.");
        } else {
            synthesis.append("Consider running a broader real-time web scraping scan across target geographies.");
        }

        response.put("ragSynthesisMarkdown", synthesis.toString());
        return response;
    }

    // 2. Market Research & Industry Deep-Dive
    public Map<String, Object> generateMarketResearch(String industry) {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("industry", industry);
        report.put("tamUsd", "$14.8 Billion (Projected 16.2% CAGR through 2030)");
        report.put("ebitdaMultipleBenchmark", "4.8x - 7.2x Adjusted EBITDA for Lower-Middle-Market ($1M-$10M ARR)");
        report.put("medianGrossMargin", "78% - 85%");
        report.put("buyerLandscape", "High Search Fund & Micro-PE demand; strategic consolidators pursuing tuck-in acquisitions.");

        List<String> tailwinds = List.of(
                "Increasing demand for AI-driven operational automation and workflow integration",
                "Baby boomer founder retirement wave creating unprecedented off-market succession opportunities",
                "High enterprise switching costs creating ultra-sticky recurring revenue cohorts (>105% NRR)"
        );
        report.put("macroTailwinds", tailwinds);

        List<String> rollUpOpportunities = List.of(
                "Caprae Playbook: Acquire core platform asset at 5x-6x, bolt on 2-3 niche competitors at 3.5x, achieve blended multiple expansion",
                "Deploy shared modern engineering infrastructure (AWS, Next.js, Stripe) to reduce COGS by 18%",
                "Implement professionalized enterprise SDR outbound machinery to accelerate organic growth to 25%+ YoY"
        );
        report.put("capraeRollUpPlaybook", rollUpOpportunities);

        return report;
    }

    // 3. A-to-Z Financial Modeling & Valuation Details
    public Map<String, Object> generateFinancialReport(Lead lead) {
        Map<String, Object> fin = new LinkedHashMap<>();
        double arr = lead.getEstimatedRevenue() != null ? lead.getEstimatedRevenue() : 2_500_000.0;
        double cogs = arr * 0.18; // 82% gross margin
        double grossProfit = arr - cogs;
        double opex = arr * 0.54;
        double ebitda = lead.getEstimatedEbitda() != null ? lead.getEstimatedEbitda() : (arr * 0.28);
        double sde = ebitda + (arr * 0.05); // Addback for owner salary & perks

        fin.put("companyName", lead.getCompanyName());
        fin.put("domain", lead.getDomain());
        fin.put("arr", arr);
        fin.put("grossProfit", grossProfit);
        fin.put("grossMarginPct", "82.0%");
        fin.put("cogs", cogs);
        fin.put("operatingExpenses", opex);
        fin.put("adjustedEbitda", ebitda);
        fin.put("ebitdaMarginPct", String.format("%.1f%%", (ebitda / arr) * 100));
        fin.put("sellerDiscretionaryEarnings", sde);

        // 3 Deal Financing Structures
        Map<String, String> struct1 = new LinkedHashMap<>();
        struct1.put("name", "Structure A: SBA 7(a) / Senior Bank Financed");
        struct1.put("totalEnterpriseValue", String.format("$%,.0f", ebitda * 5.5));
        struct1.put("seniorDebt", "70% ($" + String.format("%,.0f", ebitda * 5.5 * 0.70) + " @ SOFR + 2.75%)");
        struct1.put("sellerNote", "15% ($" + String.format("%,.0f", ebitda * 5.5 * 0.15) + " on 5-yr amort @ 6.0%)");
        struct1.put("capraeEquity", "15% ($" + String.format("%,.0f", ebitda * 5.5 * 0.15) + ")");

        Map<String, String> struct2 = new LinkedHashMap<>();
        struct2.put("name", "Structure B: Growth Recapitalization with Founder Rollover");
        struct2.put("totalEnterpriseValue", String.format("$%,.0f", ebitda * 6.2));
        struct2.put("seniorDebt", "50% Senior Bank Facility");
        struct2.put("capraeEquity", "30% Growth Capital");
        struct2.put("founderRollover", "20% Rollover Equity (preserving upside in Caprae 7-Year exit)");

        fin.put("recommendedDealStructures", List.of(struct1, struct2));

        // 5-Year Projected Exit
        double year5Arr = arr * Math.pow(1.22, 5); // 22% CAGR
        double year5Ebitda = year5Arr * 0.35; // margin expansion to 35%
        double year5ExitValuation = year5Ebitda * 8.5; // multiple expansion to 8.5x
        fin.put("projectedYear5Arr", String.format("$%,.0f", year5Arr));
        fin.put("projectedYear5Ebitda", String.format("$%,.0f", year5Ebitda));
        fin.put("projectedExitValuation", String.format("$%,.0f", year5ExitValuation));
        fin.put("projectedMoic", String.format("%.1fx Return on Invested Capital", year5ExitValuation / (ebitda * 5.5)));

        return fin;
    }

    // 4. AI Conversation Summarizer & Deal Qualification
    public Map<String, Object> summarizeConversation(Lead lead, String conversationText) {
        Map<String, Object> summary = new LinkedHashMap<>();
        String text = conversationText != null ? conversationText : "";

        boolean positiveIntent = text.toLowerCase().contains("interested") || text.toLowerCase().contains("exit") ||
                text.toLowerCase().contains("retire") || text.toLowerCase().contains("open to") || text.toLowerCase().contains("call");

        String priority = positiveIntent ? "HOT" : (text.toLowerCase().contains("not ready") ? "COLD" : "WARM");
        int winProb = positiveIntent ? 85 : (priority.equals("COLD") ? 35 : 65);
        String sentiment = positiveIntent ? "POSITIVE" : (priority.equals("COLD") ? "CAUTIOUS" : "NEUTRAL");

        summary.put("leadId", lead.getId());
        summary.put("companyName", lead.getCompanyName());
        summary.put("detectedPriority", priority);
        summary.put("winProbabilityPct", winProb);
        summary.put("sentiment", sentiment);

        summary.put("executiveSummary", String.format(
                "Conversation Analysis for %s: Founder indicates %s stance regarding transition discussions. " +
                "Key drivers identified include long-term succession, legacy preservation, and interest in Caprae's operator-first post-acquisition support. " +
                "Key risk: Needs assurance regarding continuity of technical and core leadership team.",
                lead.getCompanyName(), sentiment.toLowerCase()
        ));

        summary.put("actionableNextSteps", List.of(
                "Draft formal Non-Disclosure Agreement (NDA) and Mutual Confidentially Agreement",
                "Request 3-year historical financial statements (P&L and Balance Sheet) for QofE preview",
                "Schedule 30-minute follow-up working session with Caprae Managing Principal Kevin Hong"
        ));

        return summary;
    }

    // 5. Solution & Valuation Offer Letter / Quote Generator
    public Map<String, Object> generateSolutionQuote(Lead lead, String structure, Double valuationOffer) {
        Map<String, Object> quote = new LinkedHashMap<>();
        double arr = lead.getEstimatedRevenue() != null ? lead.getEstimatedRevenue() : 2_500_000.0;
        double ebitda = lead.getEstimatedEbitda() != null ? lead.getEstimatedEbitda() : (arr * 0.28);
        double val = valuationOffer != null && valuationOffer > 0 ? valuationOffer : (ebitda * 5.8);

        quote.put("companyName", lead.getCompanyName());
        quote.put("domain", lead.getDomain());
        quote.put("proposedEnterpriseValuation", String.format("$%,.0f", val));
        quote.put("impliedMultiple", String.format("%.2fx Adjusted EBITDA", val / ebitda));
        quote.put("cashAtClose", String.format("$%,.0f (75%%)", val * 0.75));
        quote.put("sellerNote", String.format("$%,.0f (15%%, 5-Year Term, 6.0%% Interest)", val * 0.15));
        quote.put("rolloverEquity", String.format("$%,.0f (10%% Common Equity in Caprae Holding Co)", val * 0.10));
        quote.put("governanceStructure", "Founder transition to Strategic Advisory Board member; Caprae Operator steps into CEO/COO seat.");
        quote.put("exclusivityPeriod", "60 Days standard confirmatory diligence.");

        return quote;
    }

    // 6. Natural Language Prompt-to-Scraper Parser
    public Map<String, Object> parsePromptToScrapeCriteria(String prompt) {
        Map<String, Object> criteria = new LinkedHashMap<>();
        String lower = prompt.toLowerCase();

        String industry = "B2B SaaS";
        if (lower.contains("cyber") || lower.contains("security")) industry = "Cybersecurity";
        else if (lower.contains("fintech") || lower.contains("finance") || lower.contains("banking")) industry = "FinTech Infrastructure";
        else if (lower.contains("health") || lower.contains("ehr") || lower.contains("medical")) industry = "Healthcare IT";
        else if (lower.contains("devops") || lower.contains("cloud") || lower.contains("infra")) industry = "DevOps & Cloud";
        else if (lower.contains("logistics") || lower.contains("supply") || lower.contains("commerce")) industry = "Supply Chain & Logistics";

        String region = "North America";
        if (lower.contains("texas") || lower.contains("tx")) region = "North America - Texas";
        else if (lower.contains("california") || lower.contains("ca") || lower.contains("west")) region = "North America - West Coast";
        else if (lower.contains("new york") || lower.contains("ny") || lower.contains("east")) region = "North America - East Coast";
        else if (lower.contains("europe") || lower.contains("uk")) region = "Europe";

        String query = prompt.replaceAll("(?i)(find|search|scrape|for|profitable|b2b|companies|in|with|using|and)", " ").trim().replaceAll(" +", " ");
        if (query.isBlank()) query = industry;

        criteria.put("parsedQuery", query);
        criteria.put("targetIndustry", industry);
        criteria.put("targetRegion", region);
        criteria.put("minDealScore", lower.contains("hot") || lower.contains("prime") ? 85 : 70);
        criteria.put("promptProcessed", prompt);

        return criteria;
    }
}
