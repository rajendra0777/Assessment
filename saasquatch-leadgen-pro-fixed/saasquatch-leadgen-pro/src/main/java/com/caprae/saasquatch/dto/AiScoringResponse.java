package com.caprae.saasquatch.dto;

import java.util.List;

public class AiScoringResponse {
    private Long leadId;
    private String companyName;
    private Integer dealScore; // 0-100
    private String acquisitionFit; // PRIME_TARGET, HIGH, MEDIUM, SPECULATIVE
    private String growthThesis;
    private String ebitdaMultipleRange; // e.g. "4.5x - 6.2x EBITDA"
    private String estimatedValuation; // e.g. "$12.5M - $16.0M"
    private List<String> keyStrengths;
    private List<String> operationalRisks;
    private String recommendedNextStep;

    public AiScoringResponse() {}

    public Long getLeadId() { return leadId; }
    public void setLeadId(Long leadId) { this.leadId = leadId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public Integer getDealScore() { return dealScore; }
    public void setDealScore(Integer dealScore) { this.dealScore = dealScore; }

    public String getAcquisitionFit() { return acquisitionFit; }
    public void setAcquisitionFit(String acquisitionFit) { this.acquisitionFit = acquisitionFit; }

    public String getGrowthThesis() { return growthThesis; }
    public void setGrowthThesis(String growthThesis) { this.growthThesis = growthThesis; }

    public String getEbitdaMultipleRange() { return ebitdaMultipleRange; }
    public void setEbitdaMultipleRange(String ebitdaMultipleRange) { this.ebitdaMultipleRange = ebitdaMultipleRange; }

    public String getEstimatedValuation() { return estimatedValuation; }
    public void setEstimatedValuation(String estimatedValuation) { this.estimatedValuation = estimatedValuation; }

    public List<String> getKeyStrengths() { return keyStrengths; }
    public void setKeyStrengths(List<String> keyStrengths) { this.keyStrengths = keyStrengths; }

    public List<String> getOperationalRisks() { return operationalRisks; }
    public void setOperationalRisks(List<String> operationalRisks) { this.operationalRisks = operationalRisks; }

    public String getRecommendedNextStep() { return recommendedNextStep; }
    public void setRecommendedNextStep(String recommendedNextStep) { this.recommendedNextStep = recommendedNextStep; }
}
