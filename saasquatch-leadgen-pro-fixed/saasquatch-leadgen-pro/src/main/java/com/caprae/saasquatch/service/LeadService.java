package com.caprae.saasquatch.service;

import com.caprae.saasquatch.dto.DashboardMetricsDto;
import com.caprae.saasquatch.dto.LeadCreateDto;
import com.caprae.saasquatch.entity.Lead;
import com.caprae.saasquatch.entity.LeadContact;
import com.caprae.saasquatch.exception.DuplicateResourceException;
import com.caprae.saasquatch.exception.ResourceNotFoundException;
import com.caprae.saasquatch.repository.LeadRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class LeadService {

    private final LeadRepository leadRepository;
    private final AiCopilotService aiCopilotService;

    public LeadService(LeadRepository leadRepository, AiCopilotService aiCopilotService) {
        this.leadRepository = leadRepository;
        this.aiCopilotService = aiCopilotService;
    }

    @PostConstruct
    public void seedInitialDeals() {
        if (leadRepository.count() == 0) {
            List<Lead> seed = new ArrayList<>();

            // Deal 1: High-Growth Micro-SaaS
            Lead l1 = new Lead();
            l1.setCompanyName("CloudPulse Observability");
            l1.setDomain("cloudpulse.io");
            l1.setWebsiteUrl("https://cloudpulse.io");
            l1.setIndustry("DevOps & Cloud");
            l1.setDescription("Lightweight real-time Kubernetes APM and telemetry platform for microservices.");
            l1.setLocation("Austin, TX, USA");
            l1.setRegion("North America - South");
            l1.setEmployeeCount(32);
            l1.setEstimatedRevenue(3_800_000.0);
            l1.setEstimatedEbitda(1_050_000.0);
            l1.setBusinessModel("B2B Recurring SaaS");
            l1.setTechStack("Go, React, AWS, ClickHouse, Docker, Stripe");
            l1.setSource("SaaSquatch Deal Engine");
            l1.setStatus("QUALIFIED");
            l1.setPriority("HOT");
            l1.setWinProbability(82);
            l1.setSentiment("POSITIVE");
            l1.setDealScore(92);
            l1.setAcquisitionFit("PRIME_TARGET");
            l1.setNotes("Spoke with Marcus (CEO). Founder willing to transition out within 12 months. Clean cap table, zero external VC debt.");
            l1.setQuoteDetails("Initial Caprae Acquisition Offer: $6.2M Total Enterprise Value (5.9x EBITDA). Structure: $4.8M Cash at close, $800k Seller Note at 6.5%, $600k Rollover equity.");
            l1.setAiGrowthThesis("Prime ETA candidate: Low customer churn (<0.8% net monthly), sticky enterprise integration, and $1.05M EBITDA. Ideal for Caprae 7-year growth injection via enterprise sales expansion.");
            l1.setPrimaryEmail("contact@cloudpulse.io");
            l1.setLinkedinCompanyUrl("https://linkedin.com/company/cloudpulse-io");
            l1.addContact(new LeadContact("Marcus Vance", "Founder & CEO", "marcus@cloudpulse.io", "https://linkedin.com/in/marcus-vance", "+1 (512) 890-4421", true));
            l1.addContact(new LeadContact("Elena Rostova", "Head of Product", "elena@cloudpulse.io", "https://linkedin.com/in/elena-rostova", "+1 (512) 890-4422", true));
            seed.add(l1);

            // Deal 2: B2B FinTech
            Lead l2 = new Lead();
            l2.setCompanyName("FinEdge Treasury");
            l2.setDomain("finedge.co");
            l2.setWebsiteUrl("https://finedge.co");
            l2.setIndustry("FinTech Infrastructure");
            l2.setDescription("Automated multi-bank cash balance reconciliation and automated FX hedging for mid-market CFOs.");
            l2.setLocation("New York, NY, USA");
            l2.setRegion("North America - East Coast");
            l2.setEmployeeCount(48);
            l2.setEstimatedRevenue(6_200_000.0);
            l2.setEstimatedEbitda(1_800_000.0);
            l2.setBusinessModel("B2B Recurring SaaS");
            l2.setTechStack("Java, Spring Boot, PostgreSQL, AWS, Plaid, Redis");
            l2.setSource("Proprietary ETA Network");
            l2.setStatus("DILIGENCE");
            l2.setPriority("HOT");
            l2.setWinProbability(90);
            l2.setSentiment("POSITIVE");
            l2.setDealScore(95);
            l2.setAcquisitionFit("PRIME_TARGET");
            l2.setNotes("Diligence Phase: Quality of Earnings (QofE) underway. SaaS gross margin verified at 84%. Excellent cohort retention.");
            l2.setQuoteDetails("Proposed LOI: $11.5M Valuation (6.4x EBITDA). $8.5M Senior Bank Financing, $1.5M Seller Note, $1.5M 10% Rollover.");
            l2.setAiGrowthThesis("Exceptional cash flow profile ($1.8M EBITDA) with strong pricing power. High switching barrier due to core banking integrations.");
            l2.setPrimaryEmail("partners@finedge.co");
            l2.setLinkedinCompanyUrl("https://linkedin.com/company/finedge-treasury");
            l2.addContact(new LeadContact("Sarah Sterling", "Co-Founder & COO", "sarah@finedge.co", "https://linkedin.com/in/sarah-sterling", "+1 (212) 555-0199", true));
            seed.add(l2);

            // Deal 3: HealthTech EHR
            Lead l3 = new Lead();
            l3.setCompanyName("MedAlign Health");
            l3.setDomain("medalignhealth.com");
            l3.setWebsiteUrl("https://medalignhealth.com");
            l3.setIndustry("Healthcare IT");
            l3.setDescription("HIPAA-compliant patient intake and automated medical billing workflow platform.");
            l3.setLocation("Denver, CO, USA");
            l3.setRegion("North America - West");
            l3.setEmployeeCount(25);
            l3.setEstimatedRevenue(2_400_000.0);
            l3.setEstimatedEbitda(620_000.0);
            l3.setBusinessModel("B2B Recurring SaaS");
            l3.setTechStack("Python, React, AWS, Stripe, Twilio");
            l3.setSource("SaaSquatch Discovery AI");
            l3.setStatus("SOURCED");
            l3.setPriority("WARM");
            l3.setWinProbability(60);
            l3.setSentiment("NEUTRAL");
            l3.setDealScore(84);
            l3.setAcquisitionFit("HIGH");
            l3.setNotes("Sourced via SaaSquatch medical billing filter. Sent introductory founder inquiry; awaiting calendly booking.");
            l3.setAiGrowthThesis("Resilient healthcare niche with high recurring LTV. Ripe for AI billing automation upgrade.");
            l3.setPrimaryEmail("hello@medalignhealth.com");
            l3.setLinkedinCompanyUrl("https://linkedin.com/company/medalignhealth");
            l3.addContact(new LeadContact("Dr. James Thorne", "Founder", "jthorne@medalignhealth.com", "https://linkedin.com/in/jthorne-md", "+1 (303) 844-9021", true));
            seed.add(l3);

            // Deal 4: E-Commerce Operations
            Lead l4 = new Lead();
            l4.setCompanyName("OmniPack Logistics");
            l4.setDomain("omnipacklogistics.com");
            l4.setWebsiteUrl("https://omnipacklogistics.com");
            l4.setIndustry("Supply Chain & Logistics");
            l4.setDescription("Warehouse inventory tracking and 3PL routing intelligence for direct-to-consumer merchants.");
            l4.setLocation("Chicago, IL, USA");
            l4.setRegion("North America - Midwest");
            l4.setEmployeeCount(60);
            l4.setEstimatedRevenue(7_500_000.0);
            l4.setEstimatedEbitda(1_450_000.0);
            l4.setBusinessModel("Tech-Enabled Services");
            l4.setTechStack("React, Node.js, Shopify Plus, MySQL, GCP");
            l4.setSource("SaaSquatch Lead Scraper");
            l4.setStatus("LOI_SUBMITTED");
            l4.setPriority("HOT");
            l4.setWinProbability(95);
            l4.setSentiment("POSITIVE");
            l4.setDealScore(89);
            l4.setAcquisitionFit("HIGH");
            l4.setNotes("LOI executed on Sept 28. 60-day exclusivity granted. Founder retiring; second-in-command COO eager to stay on with Caprae.");
            l4.setQuoteDetails("Executed LOI Term Sheet: $7.8M Purchase Price (5.4x EBITDA). Cash: $5.8M, Seller Financing: $1.2M (5 yr amort @ 7%), Equity: $800k.");
            l4.setAiGrowthThesis("Strong regional moat with over 450 active warehouses. Synergistic for software margin expansion.");
            l4.setPrimaryEmail("info@omnipacklogistics.com");
            l4.setLinkedinCompanyUrl("https://linkedin.com/company/omnipack-logistics");
            l4.addContact(new LeadContact("David Miller", "CEO", "dmiller@omnipacklogistics.com", "https://linkedin.com/in/david-miller-logistics", "+1 (312) 441-8930", true));
            seed.add(l4);

            // Deal 5: CyberShield SecOps
            Lead l5 = new Lead();
            l5.setCompanyName("CyberShield SecOps");
            l5.setDomain("cybershieldsec.io");
            l5.setWebsiteUrl("https://cybershieldsec.io");
            l5.setIndustry("Cybersecurity");
            l5.setDescription("Continuous compliance posture automation (SOC2, ISO27001, HIPAA) for mid-market engineering teams.");
            l5.setLocation("Austin, TX, USA");
            l5.setRegion("North America - Texas");
            l5.setEmployeeCount(32);
            l5.setEstimatedRevenue(3_900_000.0);
            l5.setEstimatedEbitda(1_100_000.0);
            l5.setBusinessModel("B2B Recurring SaaS");
            l5.setTechStack("Go, React, AWS, Stripe, Vault");
            l5.setSource("Proprietary ETA Network");
            l5.setStatus("SOURCED");
            l5.setPriority("HOT");
            l5.setWinProbability(82);
            l5.setSentiment("POSITIVE");
            l5.setDealScore(91);
            l5.setAcquisitionFit("PRIME_TARGET");
            l5.setNotes("Founder wants succession partner to scale federal sales desk. High gross retention (98%).");
            l5.setAiGrowthThesis("High-growth compliance tailwinds with low churn and 28% EBITDA margin.");
            l5.setPrimaryEmail("contact@cybershieldsec.io");
            l5.setLinkedinCompanyUrl("https://linkedin.com/company/cybershieldsec");
            l5.addContact(new LeadContact("Ryan Vance", "Founder & CTO", "ryan@cybershieldsec.io", "https://linkedin.com/in/ryan-vance", "+1 (512) 330-9988", true));
            seed.add(l5);

            // Deal 6: DataWeave ETL
            Lead l6 = new Lead();
            l6.setCompanyName("DataWeave ETL");
            l6.setDomain("dataweave.ai");
            l6.setWebsiteUrl("https://dataweave.ai");
            l6.setIndustry("DevOps & Cloud");
            l6.setDescription("Zero-maintenance data pipeline sync between PostgreSQL, Snowflake, and BigQuery.");
            l6.setLocation("San Francisco, CA, USA");
            l6.setRegion("North America - West Coast");
            l6.setEmployeeCount(40);
            l6.setEstimatedRevenue(5_100_000.0);
            l6.setEstimatedEbitda(1_400_000.0);
            l6.setBusinessModel("B2B Recurring SaaS");
            l6.setTechStack("Rust, Python, AWS, Snowflake, Kubernetes");
            l6.setSource("SaaSquatch Discovery AI");
            l6.setStatus("QUALIFIED");
            l6.setPriority("HOT");
            l6.setWinProbability(88);
            l6.setSentiment("POSITIVE");
            l6.setDealScore(94);
            l6.setAcquisitionFit("PRIME_TARGET");
            l6.setNotes("Met founder at SaaStr. Open to 100% full buyout with 6-month consulting handover.");
            l6.setAiGrowthThesis("Sticky technical data moat with expansion potential into enterprise data catalogs.");
            l6.setPrimaryEmail("founders@dataweave.ai");
            l6.setLinkedinCompanyUrl("https://linkedin.com/company/dataweave-ai");
            l6.addContact(new LeadContact("Chloe Zheng", "CEO & Co-Founder", "chloe@dataweave.ai", "https://linkedin.com/in/chloe-zheng", "+1 (415) 880-1200", true));
            seed.add(l6);

            // Deal 7: FleetFlow Logistics
            Lead l7 = new Lead();
            l7.setCompanyName("FleetFlow Logistics");
            l7.setDomain("fleetflow.net");
            l7.setWebsiteUrl("https://fleetflow.net");
            l7.setIndustry("Supply Chain & Logistics");
            l7.setDescription("IoT telemetry tracking and automated route fuel optimization for commercial trucking fleets.");
            l7.setLocation("Dallas, TX, USA");
            l7.setRegion("North America - Texas");
            l7.setEmployeeCount(55);
            l7.setEstimatedRevenue(4_200_000.0);
            l7.setEstimatedEbitda(950_000.0);
            l7.setBusinessModel("Tech-Enabled SaaS");
            l7.setTechStack("React, Node.js, AWS IoT, PostgreSQL");
            l7.setSource("Proprietary Broker Referral");
            l7.setStatus("CLOSED");
            l7.setPriority("WARM");
            l7.setWinProbability(100);
            l7.setSentiment("POSITIVE");
            l7.setDealScore(91);
            l7.setAcquisitionFit("PRIME_TARGET");
            l7.setNotes("Acquired by Caprae Fund I. Search CEO installed as Managing Director.");
            l7.setAiGrowthThesis("Successful acquisition blueprint: Expanded cross-selling into mid-west logistics hubs.");
            l7.setPrimaryEmail("executive@fleetflow.net");
            l7.addContact(new LeadContact("Tom Hastings", "Former Founder / Board Member", "tom@fleetflow.net", "https://linkedin.com/in/tom-hastings", "+1 (214) 770-4321", true));
            seed.add(l7);

            // Deal 8: CareSync Health
            Lead l8 = new Lead();
            l8.setCompanyName("CareSync Health");
            l8.setDomain("caresyncsoftware.com");
            l8.setWebsiteUrl("https://caresyncsoftware.com");
            l8.setIndustry("Healthcare IT");
            l8.setDescription("Integrated outpatient appointment booking, digital check-in, and insurance pre-authorization.");
            l8.setLocation("Boston, MA, USA");
            l8.setRegion("North America - East Coast");
            l8.setEmployeeCount(28);
            l8.setEstimatedRevenue(2_900_000.0);
            l8.setEstimatedEbitda(720_000.0);
            l8.setBusinessModel("B2B Recurring SaaS");
            l8.setTechStack("React, Java, Spring Boot, AWS, Stripe");
            l8.setSource("SaaSquatch Web Scraper");
            l8.setStatus("DILIGENCE");
            l8.setPriority("HOT");
            l8.setWinProbability(85);
            l8.setSentiment("POSITIVE");
            l8.setDealScore(89);
            l8.setAcquisitionFit("HIGH");
            l8.setNotes("Third-party QofE audit initiated. Net revenue retention is 112%.");
            l8.setAiGrowthThesis("Expansion into dental and behavioral health clinics will triple addressable market.");
            l8.setPrimaryEmail("partners@caresyncsoftware.com");
            l8.addContact(new LeadContact("Rachel Adams", "Founder & CEO", "rachel@caresyncsoftware.com", "https://linkedin.com/in/rachel-adams-health", "+1 (617) 540-9921", true));
            seed.add(l8);

            // Deal 9: RevenueEngine CRM
            Lead l9 = new Lead();
            l9.setCompanyName("RevenueEngine CRM");
            l9.setDomain("revenueengine.io");
            l9.setWebsiteUrl("https://revenueengine.io");
            l9.setIndustry("B2B SaaS");
            l9.setDescription("AI-guided CPQ (Configure, Price, Quote) and recurring billing orchestrator for B2B distributors.");
            l9.setLocation("New York, NY, USA");
            l9.setRegion("North America - East Coast");
            l9.setEmployeeCount(65);
            l9.setEstimatedRevenue(6_800_000.0);
            l9.setEstimatedEbitda(1_900_000.0);
            l9.setBusinessModel("B2B Recurring SaaS");
            l9.setTechStack("React, TypeScript, Node.js, Stripe Billing, AWS");
            l9.setSource("Proprietary ETA Network");
            l9.setStatus("QUALIFIED");
            l9.setPriority("HOT");
            l9.setWinProbability(92);
            l9.setSentiment("POSITIVE");
            l9.setDealScore(96);
            l9.setAcquisitionFit("PRIME_TARGET");
            l9.setNotes("Founder wants liquidity to pursue philanthropy. Unanimous board approval for sale.");
            l9.setAiGrowthThesis("Premier asset: $1.9M EBITDA with 85% gross margins. Ideal core anchor for 7-year Caprae roll-up.");
            l9.setPrimaryEmail("founder@revenueengine.io");
            l9.addContact(new LeadContact("Jordan Taylor", "Founder & CEO", "jordan@revenueengine.io", "https://linkedin.com/in/jordan-taylor-crm", "+1 (212) 890-7711", true));
            seed.add(l9);

            // Deal 10: BuildMetric Cloud
            Lead l10 = new Lead();
            l10.setCompanyName("BuildMetric Cloud");
            l10.setDomain("buildmetric.dev");
            l10.setWebsiteUrl("https://buildmetric.dev");
            l10.setIndustry("DevOps & Cloud");
            l10.setDescription("Cloud infrastructure cost governance and automated AWS/Azure unused resource terminator.");
            l10.setLocation("Seattle, WA, USA");
            l10.setRegion("North America - West Coast");
            l10.setEmployeeCount(30);
            l10.setEstimatedRevenue(3_200_000.0);
            l10.setEstimatedEbitda(820_000.0);
            l10.setBusinessModel("B2B Recurring SaaS");
            l10.setTechStack("Python, Go, AWS, Azure, React, Redis");
            l10.setSource("SaaSquatch Discovery AI");
            l10.setStatus("SOURCED");
            l10.setPriority("WARM");
            l10.setWinProbability(70);
            l10.setSentiment("NEUTRAL");
            l10.setDealScore(83);
            l10.setAcquisitionFit("HIGH");
            l10.setNotes("First outreach sent. Strong organic GitHub adoption with over 150 enterprise customer logos.");
            l10.setAiGrowthThesis("FinOps tooling has top-of-funnel tailwinds as enterprise CFOs mandate cloud spend reduction.");
            l10.setPrimaryEmail("team@buildmetric.dev");
            l10.addContact(new LeadContact("Eric Larson", "Co-Founder", "eric@buildmetric.dev", "https://linkedin.com/in/eric-larson-devops", "+1 (206) 430-8812", true));
            seed.add(l10);

            // Deal 11: ApexLedger Tax
            Lead l11 = new Lead();
            l11.setCompanyName("ApexLedger Tax");
            l11.setDomain("apexledger.co");
            l11.setWebsiteUrl("https://apexledger.co");
            l11.setIndustry("FinTech Infrastructure");
            l11.setDescription("Automated multi-jurisdiction sales tax calculation and automated state nexus filings.");
            l11.setLocation("Atlanta, GA, USA");
            l11.setRegion("North America - South");
            l11.setEmployeeCount(42);
            l11.setEstimatedRevenue(4_500_000.0);
            l11.setEstimatedEbitda(1_300_000.0);
            l11.setBusinessModel("B2B Recurring SaaS");
            l11.setTechStack("Java, Spring Boot, React, AWS, Stripe");
            l11.setSource("SaaSquatch Web Scraper");
            l11.setStatus("LOI_SUBMITTED");
            l11.setPriority("HOT");
            l11.setWinProbability(90);
            l11.setSentiment("POSITIVE");
            l11.setDealScore(92);
            l11.setAcquisitionFit("PRIME_TARGET");
            l11.setNotes("LOI terms agreed: $8.2M valuation (6.3x EBITDA). Financing structure under review.");
            l11.setAiGrowthThesis("Mission-critical compliance software with near-zero voluntary churn.");
            l11.setPrimaryEmail("hello@apexledger.co");
            l11.addContact(new LeadContact("Victor Ward", "Managing Partner", "victor@apexledger.co", "https://linkedin.com/in/victor-ward", "+1 (404) 910-3344", true));
            seed.add(l11);

            // Deal 12: ClearClaim Medical
            Lead l12 = new Lead();
            l12.setCompanyName("ClearClaim Medical");
            l12.setDomain("clearclaim.io");
            l12.setWebsiteUrl("https://clearclaim.io");
            l12.setIndustry("Healthcare IT");
            l12.setDescription("AI-powered insurance denial resolution and automated revenue cycle management for surgery centers.");
            l12.setLocation("San Diego, CA, USA");
            l12.setRegion("North America - West Coast");
            l12.setEmployeeCount(34);
            l12.setEstimatedRevenue(3_100_000.0);
            l12.setEstimatedEbitda(780_000.0);
            l12.setBusinessModel("Tech-Enabled SaaS");
            l12.setTechStack("Python, React, AWS, Snowflake, OCR");
            l12.setSource("Proprietary Sourcing");
            l12.setStatus("QUALIFIED");
            l12.setPriority("WARM");
            l12.setWinProbability(78);
            l12.setSentiment("POSITIVE");
            l12.setDealScore(86);
            l12.setAcquisitionFit("HIGH");
            l12.setNotes("Completed preliminary founder call. Founder receptive to 20% equity rollover in Caprae holding company.");
            l12.setAiGrowthThesis("High customer ROI: recovers on average $340k in denied claims annually per surgery clinic.");
            l12.setPrimaryEmail("ops@clearclaim.io");
            l12.addContact(new LeadContact("Dr. Nathan Hall", "Founder", "nathan@clearclaim.io", "https://linkedin.com/in/nathan-hall-md", "+1 (619) 302-8819", true));
            seed.add(l12);

            // Deal 13: SecureRoute ZeroTrust
            Lead l13 = new Lead();
            l13.setCompanyName("SecureRoute ZeroTrust");
            l13.setDomain("secureroute.net");
            l13.setWebsiteUrl("https://secureroute.net");
            l13.setIndustry("Cybersecurity");
            l13.setDescription("Micro-segmentation and software-defined perimeter security for distributed remote engineering organizations.");
            l13.setLocation("Denver, CO, USA");
            l13.setRegion("North America - West");
            l13.setEmployeeCount(46);
            l13.setEstimatedRevenue(4_800_000.0);
            l13.setEstimatedEbitda(1_200_000.0);
            l13.setBusinessModel("B2B Recurring SaaS");
            l13.setTechStack("Rust, Go, React, AWS, Cloudflare, WireGuard");
            l13.setSource("Proprietary Broker Referral");
            l13.setStatus("DILIGENCE");
            l13.setPriority("HOT");
            l13.setWinProbability(86);
            l13.setSentiment("POSITIVE");
            l13.setDealScore(93);
            l13.setAcquisitionFit("PRIME_TARGET");
            l13.setNotes("Diligence deep-dive: Code architecture audit confirmed zero technical debt. Outstanding unit economics.");
            l13.setAiGrowthThesis("Direct replacement for outdated enterprise VPNs; high land-and-expand revenue velocity.");
            l13.setPrimaryEmail("contact@secureroute.net");
            l13.addContact(new LeadContact("Samantha Cruz", "CEO & Co-Founder", "samantha@secureroute.net", "https://linkedin.com/in/samantha-cruz-sec", "+1 (303) 710-4499", true));
            seed.add(l13);

            // Deal 14: QuickFreight 3PL
            Lead l14 = new Lead();
            l14.setCompanyName("QuickFreight 3PL");
            l14.setDomain("quickfreight.io");
            l14.setWebsiteUrl("https://quickfreight.io");
            l14.setIndustry("Supply Chain & Logistics");
            l14.setDescription("Carrier matching algorithm and automated bill of lading (BOL) processing for freight brokers.");
            l14.setLocation("Miami, FL, USA");
            l14.setRegion("North America - East Coast");
            l14.setEmployeeCount(38);
            l14.setEstimatedRevenue(3_600_000.0);
            l14.setEstimatedEbitda(840_000.0);
            l14.setBusinessModel("B2B Recurring SaaS");
            l14.setTechStack("React, Python, Django, PostgreSQL, AWS");
            l14.setSource("SaaSquatch Discovery AI");
            l14.setStatus("SOURCED");
            l14.setPriority("COLD");
            l14.setWinProbability(55);
            l14.setSentiment("NEUTRAL");
            l14.setDealScore(78);
            l14.setAcquisitionFit("MEDIUM");
            l14.setNotes("Initial outreach launched. Nurturing founder relationship for future acquisition window.");
            l14.setAiGrowthThesis("Moderate competitive pressure in freight brokerage; focus on high-margin spot quote automation.");
            l14.setPrimaryEmail("hello@quickfreight.io");
            l14.addContact(new LeadContact("Carlos Mendez", "Founder", "carlos@quickfreight.io", "https://linkedin.com/in/carlos-mendez-freight", "+1 (305) 991-4402", true));
            seed.add(l14);

            // Deal 15: PulseMetrics API
            Lead l15 = new Lead();
            l15.setCompanyName("PulseMetrics API");
            l15.setDomain("pulsemetrics.co");
            l15.setWebsiteUrl("https://pulsemetrics.co");
            l15.setIndustry("B2B SaaS");
            l15.setDescription("Real-time application performance monitoring and distributed tracing for serverless microservices.");
            l15.setLocation("Toronto, Canada");
            l15.setRegion("North America");
            l15.setEmployeeCount(26);
            l15.setEstimatedRevenue(2_700_000.0);
            l15.setEstimatedEbitda(680_000.0);
            l15.setBusinessModel("B2B Recurring SaaS");
            l15.setTechStack("Node.js, React, ClickHouse, AWS, Stripe");
            l15.setSource("SaaSquatch Lead Scraper");
            l15.setStatus("SOURCED");
            l15.setPriority("COLD");
            l15.setWinProbability(60);
            l15.setSentiment("NEUTRAL");
            l15.setDealScore(80);
            l15.setAcquisitionFit("MEDIUM");
            l15.setNotes("High product NPS score. Bootstrap founder open to discussing growth recapitalization.");
            l15.setAiGrowthThesis("Low-cost alternative to Datadog/NewRelic with high developer affinity.");
            l15.setPrimaryEmail("support@pulsemetrics.co");
            l15.addContact(new LeadContact("Liam Tremblay", "Co-Founder", "liam@pulsemetrics.co", "https://linkedin.com/in/liam-tremblay", "+1 (416) 780-9931", true));
            seed.add(l15);

            // Deal 16: CloudVault Storage
            Lead l16 = new Lead();
            l16.setCompanyName("CloudVault Storage");
            l16.setDomain("cloudvault.io");
            l16.setWebsiteUrl("https://cloudvault.io");
            l16.setIndustry("DevOps & Cloud");
            l16.setDescription("Encrypted immutable backup storage and disaster recovery for Kubernetes workloads.");
            l16.setLocation("Salt Lake City, UT, USA");
            l16.setRegion("North America - West");
            l16.setEmployeeCount(52);
            l16.setEstimatedRevenue(5_400_000.0);
            l16.setEstimatedEbitda(1_600_000.0);
            l16.setBusinessModel("B2B Recurring SaaS");
            l16.setTechStack("Go, React, AWS, MinIO, Kubernetes, Stripe");
            l16.setSource("Proprietary ETA Network");
            l16.setStatus("CLOSED");
            l16.setPriority("HOT");
            l16.setWinProbability(100);
            l16.setSentiment("POSITIVE");
            l16.setDealScore(95);
            l16.setAcquisitionFit("PRIME_TARGET");
            l16.setNotes("Acquisition closed in Q2. Integration complete with 32% year-over-year ARR growth.");
            l16.setAiGrowthThesis("Core exemplar of Caprae 7-year patient capital thesis.");
            l16.setPrimaryEmail("investor-relations@cloudvault.io");
            l16.addContact(new LeadContact("Brandon Wright", "Operating CEO (Caprae Partner)", "bwright@cloudvault.io", "https://linkedin.com/in/brandon-wright-ceo", "+1 (801) 640-1199", true));
            seed.add(l16);

            seed.forEach(l -> l.setDataProvenance("SEED_DEMO"));
            leadRepository.saveAll(seed);
        }
    }

    public List<Lead> getLeads(String query, String industry, String status, Integer minScore) {
        return leadRepository.searchLeads(
                (query != null && !query.isBlank()) ? query.trim() : null,
                (industry != null && !industry.isBlank()) ? industry.trim() : null,
                (status != null && !status.isBlank()) ? status.trim() : null,
                minScore
        );
    }

    public Lead getLeadById(Long id) {
        return leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));
    }

    @Transactional
    public Lead createLead(LeadCreateDto dto) {
        String normalizedDomain = DeduplicationService.normalizeDomain(dto.getDomain());
        if (leadRepository.existsByDomain(normalizedDomain)) {
            throw new DuplicateResourceException("Lead already exists for domain: " + dto.getDomain());
        }
        dto.setDomain(normalizedDomain);

        if (!Boolean.TRUE.equals(dto.getConfirmDistinct())) {
            DeduplicationService.findDuplicate(dto.getCompanyName(), normalizedDomain, leadRepository.findAll())
                    .ifPresent(m -> {
                        throw new DuplicateResourceException(String.format(
                                "Possible duplicate of '%s' (%s, similarity %.2f). Resend with confirmDistinct=true to create anyway.",
                                m.existing().getCompanyName(), m.reason(), m.similarity()));
                    });
        }

        Lead lead = new Lead();
        mapDtoToEntity(dto, lead);

        if (lead.getDealScore() == null) {
            var evaluation = aiCopilotService.evaluateLead(lead);
            if (evaluation != null) {
                lead.setDealScore(evaluation.getDealScore());
                lead.setAcquisitionFit(evaluation.getAcquisitionFit());
                lead.setAiGrowthThesis(evaluation.getGrowthThesis());
            } else {
                lead.setDealScore(75);
                lead.setAcquisitionFit("HIGH");
                lead.setAiGrowthThesis("Standard high-potential candidate under ETA review.");
            }
        }

        if (dto.getContacts() != null) {
            for (var cDto : dto.getContacts()) {
                lead.addContact(new LeadContact(
                        cDto.getFullName(),
                        cDto.getTitle(),
                        cDto.getEmail(),
                        cDto.getLinkedinUrl(),
                        cDto.getPhone(),
                        cDto.getIsVerified() != null ? cDto.getIsVerified() : false
                ));
            }
        }

        return leadRepository.save(lead);
    }

    @Transactional
    public Lead updateLead(Long id, LeadCreateDto dto) {
        Lead lead = getLeadById(id);
        mapDtoToEntity(dto, lead);
        return leadRepository.save(lead);
    }

    @Transactional
    public Lead updateStatus(Long id, String status) {
        Lead lead = getLeadById(id);
        lead.setStatus(status.toUpperCase());
        return leadRepository.save(lead);
    }

    @Transactional
    public Lead updatePriority(Long id, String priority) {
        Lead lead = getLeadById(id);
        lead.setPriority(priority.toUpperCase());
        return leadRepository.save(lead);
    }

    @Transactional
    public Lead updateWinProbability(Long id, Integer winProbability) {
        Lead lead = getLeadById(id);
        lead.setWinProbability(winProbability);
        return leadRepository.save(lead);
    }

    @Transactional
    public Lead updateNotes(Long id, String notes, String sentiment) {
        Lead lead = getLeadById(id);
        lead.setNotes(notes);
        if (sentiment != null) lead.setSentiment(sentiment);
        return leadRepository.save(lead);
    }

    @Transactional
    public Lead updateQuote(Long id, String quoteDetails) {
        Lead lead = getLeadById(id);
        lead.setQuoteDetails(quoteDetails);
        return leadRepository.save(lead);
    }

    @Transactional
    public void deleteLead(Long id) {
        if (!leadRepository.existsById(id)) {
            throw new ResourceNotFoundException("Lead not found with id: " + id);
        }
        leadRepository.deleteById(id);
    }

    public DashboardMetricsDto getDashboardMetrics() {
        DashboardMetricsDto metrics = new DashboardMetricsDto();
        metrics.setTotalLeads(leadRepository.countTotalLeads());
        Double avg = leadRepository.getAverageDealScore();
        metrics.setAvgDealScore(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
        Double totalRev = leadRepository.getTotalPipelineRevenue();
        metrics.setTotalPipelineRevenue(totalRev != null ? totalRev : 0.0);

        List<Lead> all = leadRepository.findAll();
        long qual = all.stream().filter(l -> "QUALIFIED".equalsIgnoreCase(l.getStatus())).count();
        long dilig = all.stream().filter(l -> "DILIGENCE".equalsIgnoreCase(l.getStatus())).count();
        long loi = all.stream().filter(l -> "LOI_SUBMITTED".equalsIgnoreCase(l.getStatus())).count();

        metrics.setQualifiedLeadsCount(qual);
        metrics.setInDiligenceCount(dilig);
        metrics.setLoiSubmittedCount(loi);

        Map<String, Long> indMap = new LinkedHashMap<>();
        for (Object[] row : leadRepository.countLeadsByIndustry()) {
            if (row[0] != null) indMap.put((String) row[0], (Long) row[1]);
        }
        metrics.setLeadsByIndustry(indMap);

        Map<String, Long> statusMap = new LinkedHashMap<>();
        for (Object[] row : leadRepository.countLeadsByStatus()) {
            if (row[0] != null) statusMap.put((String) row[0], (Long) row[1]);
        }
        metrics.setLeadsByStatus(statusMap);

        return metrics;
    }

    private void mapDtoToEntity(LeadCreateDto dto, Lead lead) {
        lead.setCompanyName(dto.getCompanyName());
        lead.setDomain(dto.getDomain().trim());
        lead.setWebsiteUrl(dto.getWebsiteUrl());
        lead.setIndustry(dto.getIndustry());
        lead.setDescription(dto.getDescription());
        lead.setLocation(dto.getLocation());
        lead.setEmployeeCount(dto.getEmployeeCount());
        lead.setEstimatedRevenue(dto.getEstimatedRevenue());
        lead.setEstimatedEbitda(dto.getEstimatedEbitda());
        lead.setBusinessModel(dto.getBusinessModel());
        lead.setTechStack(dto.getTechStack());
        lead.setPrimaryEmail(dto.getPrimaryEmail());
        lead.setPrimaryPhone(dto.getPrimaryPhone());
        lead.setLinkedinCompanyUrl(dto.getLinkedinCompanyUrl());
        if (dto.getPriority() != null) lead.setPriority(dto.getPriority());
        if (dto.getWinProbability() != null) lead.setWinProbability(dto.getWinProbability());
        if (dto.getRegion() != null) lead.setRegion(dto.getRegion());
        if (dto.getNotes() != null) lead.setNotes(dto.getNotes());
        if (dto.getQuoteDetails() != null) lead.setQuoteDetails(dto.getQuoteDetails());
        if (dto.getSentiment() != null) lead.setSentiment(dto.getSentiment());
        if (dto.getStatus() != null) lead.setStatus(dto.getStatus());
        if (dto.getDealScore() != null) lead.setDealScore(dto.getDealScore());
        if (dto.getAcquisitionFit() != null) lead.setAcquisitionFit(dto.getAcquisitionFit());
        if (dto.getAiGrowthThesis() != null) lead.setAiGrowthThesis(dto.getAiGrowthThesis());
    }
}
