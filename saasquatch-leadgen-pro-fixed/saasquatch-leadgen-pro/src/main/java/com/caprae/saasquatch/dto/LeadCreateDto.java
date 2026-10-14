package com.caprae.saasquatch.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class LeadCreateDto {

    @NotBlank(message = "Company name is required")
    private String companyName;

    @NotBlank(message = "Domain is required")
    private String domain;
    private Boolean confirmDistinct; // set true to create even if a similar company name exists

    private String websiteUrl;
    private String industry;
    private String description;
    private String location;
    private Integer employeeCount;
    private Double estimatedRevenue;
    private Double estimatedEbitda;
    private String businessModel;
    private String techStack;
    private String status;
    private Integer dealScore;
    private String acquisitionFit;
    private String aiGrowthThesis;
    private String primaryEmail;
    private String primaryPhone;
    private String linkedinCompanyUrl;
    private String priority;
    private Integer winProbability;
    private String region;
    private String notes;
    private String quoteDetails;
    private String sentiment;
    private List<ContactDto> contacts;

    public static class ContactDto {
        private String fullName;
        private String title;
        private String email;
        private String linkedinUrl;
        private String phone;
        private Boolean isVerified;

        public ContactDto() {}
        public ContactDto(String fullName, String title, String email, String linkedinUrl, String phone, Boolean isVerified) {
            this.fullName = fullName;
            this.title = title;
            this.email = email;
            this.linkedinUrl = linkedinUrl;
            this.phone = phone;
            this.isVerified = isVerified;
        }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getLinkedinUrl() { return linkedinUrl; }
        public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public Boolean getIsVerified() { return isVerified; }
        public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
    }

    public LeadCreateDto() {}

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public String getWebsiteUrl() { return websiteUrl; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getEmployeeCount() { return employeeCount; }
    public void setEmployeeCount(Integer employeeCount) { this.employeeCount = employeeCount; }

    public Double getEstimatedRevenue() { return estimatedRevenue; }
    public void setEstimatedRevenue(Double estimatedRevenue) { this.estimatedRevenue = estimatedRevenue; }

    public Double getEstimatedEbitda() { return estimatedEbitda; }
    public void setEstimatedEbitda(Double estimatedEbitda) { this.estimatedEbitda = estimatedEbitda; }

    public String getBusinessModel() { return businessModel; }
    public void setBusinessModel(String businessModel) { this.businessModel = businessModel; }

    public String getTechStack() { return techStack; }
    public void setTechStack(String techStack) { this.techStack = techStack; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getDealScore() { return dealScore; }
    public void setDealScore(Integer dealScore) { this.dealScore = dealScore; }

    public String getAcquisitionFit() { return acquisitionFit; }
    public void setAcquisitionFit(String acquisitionFit) { this.acquisitionFit = acquisitionFit; }

    public String getAiGrowthThesis() { return aiGrowthThesis; }
    public void setAiGrowthThesis(String aiGrowthThesis) { this.aiGrowthThesis = aiGrowthThesis; }

    public String getPrimaryEmail() { return primaryEmail; }
    public void setPrimaryEmail(String primaryEmail) { this.primaryEmail = primaryEmail; }

    public String getPrimaryPhone() { return primaryPhone; }
    public void setPrimaryPhone(String primaryPhone) { this.primaryPhone = primaryPhone; }

    public String getLinkedinCompanyUrl() { return linkedinCompanyUrl; }
    public void setLinkedinCompanyUrl(String linkedinCompanyUrl) { this.linkedinCompanyUrl = linkedinCompanyUrl; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public Integer getWinProbability() { return winProbability; }
    public void setWinProbability(Integer winProbability) { this.winProbability = winProbability; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getQuoteDetails() { return quoteDetails; }
    public void setQuoteDetails(String quoteDetails) { this.quoteDetails = quoteDetails; }

    public String getSentiment() { return sentiment; }
    public void setSentiment(String sentiment) { this.sentiment = sentiment; }

    public List<ContactDto> getContacts() { return contacts; }
    public void setContacts(List<ContactDto> contacts) { this.contacts = contacts; }

    public Boolean getConfirmDistinct() { return confirmDistinct; }
    public void setConfirmDistinct(Boolean confirmDistinct) { this.confirmDistinct = confirmDistinct; }
}
