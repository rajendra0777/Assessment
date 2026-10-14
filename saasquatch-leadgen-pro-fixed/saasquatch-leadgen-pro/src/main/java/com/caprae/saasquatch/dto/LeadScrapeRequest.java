package com.caprae.saasquatch.dto;

import jakarta.validation.constraints.NotBlank;

public class LeadScrapeRequest {

    @NotBlank(message = "Domain or target URL or search keyword is required")
    private String query; // e.g. "stripe.com" or "dental practice management saas" or "https://convertkit.com"

    private String industry; // Optional focus: B2B SaaS, E-Commerce, Agency, Healthcare IT
    private String region; // Optional: North America, Europe, West Coast, Texas, etc.
    private Boolean autoSave = true; // Auto-commit newly scraped company to database
    private Boolean runAiAnalysis = true; // Automatically run Deal-Score & Growth Thesis

    public LeadScrapeRequest() {}

    public LeadScrapeRequest(String query, String industry, String region, Boolean autoSave, Boolean runAiAnalysis) {
        this.query = query;
        this.industry = industry;
        this.region = region;
        this.autoSave = autoSave;
        this.runAiAnalysis = runAiAnalysis;
    }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public Boolean getAutoSave() { return autoSave; }
    public void setAutoSave(Boolean autoSave) { this.autoSave = autoSave; }

    public Boolean getRunAiAnalysis() { return runAiAnalysis; }
    public void setRunAiAnalysis(Boolean runAiAnalysis) { this.runAiAnalysis = runAiAnalysis; }
}
