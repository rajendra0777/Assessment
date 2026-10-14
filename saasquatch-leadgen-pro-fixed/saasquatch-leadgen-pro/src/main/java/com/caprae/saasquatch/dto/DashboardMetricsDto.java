package com.caprae.saasquatch.dto;

import java.util.Map;

public class DashboardMetricsDto {
    private long totalLeads;
    private double avgDealScore;
    private double totalPipelineRevenue;
    private long qualifiedLeadsCount;
    private long inDiligenceCount;
    private long loiSubmittedCount;
    private Map<String, Long> leadsByIndustry;
    private Map<String, Long> leadsByStatus;

    public DashboardMetricsDto() {}

    public long getTotalLeads() { return totalLeads; }
    public void setTotalLeads(long totalLeads) { this.totalLeads = totalLeads; }

    public double getAvgDealScore() { return avgDealScore; }
    public void setAvgDealScore(double avgDealScore) { this.avgDealScore = avgDealScore; }

    public double getTotalPipelineRevenue() { return totalPipelineRevenue; }
    public void setTotalPipelineRevenue(double totalPipelineRevenue) { this.totalPipelineRevenue = totalPipelineRevenue; }

    public long getQualifiedLeadsCount() { return qualifiedLeadsCount; }
    public void setQualifiedLeadsCount(long qualifiedLeadsCount) { this.qualifiedLeadsCount = qualifiedLeadsCount; }

    public long getInDiligenceCount() { return inDiligenceCount; }
    public void setInDiligenceCount(long inDiligenceCount) { this.inDiligenceCount = inDiligenceCount; }

    public long getLoiSubmittedCount() { return loiSubmittedCount; }
    public void setLoiSubmittedCount(long loiSubmittedCount) { this.loiSubmittedCount = loiSubmittedCount; }

    public Map<String, Long> getLeadsByIndustry() { return leadsByIndustry; }
    public void setLeadsByIndustry(Map<String, Long> leadsByIndustry) { this.leadsByIndustry = leadsByIndustry; }

    public Map<String, Long> getLeadsByStatus() { return leadsByStatus; }
    public void setLeadsByStatus(Map<String, Long> leadsByStatus) { this.leadsByStatus = leadsByStatus; }
}
