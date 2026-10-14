package com.caprae.saasquatch.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "leads")
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false, unique = true)
    private String domain;

    private String websiteUrl;

    private String industry;

    @Column(length = 4000)
    private String description;

    private String location;

    private Integer employeeCount;

    private Double estimatedRevenue; // ARR in USD

    private Double estimatedEbitda; // EBITDA in USD

    private String businessModel; // SaaS, Agency, Tech-Enabled B2B

    private String techStack; // Detected technologies (React, Stripe, AWS, etc.)

    private String source; // Live Scraper, SaaSquatch Engine, Manual

    @Column(nullable = false)
    private String status; // SOURCED, QUALIFIED, DILIGENCE, LOI_SUBMITTED, CLOSED, ARCHIVED

    private Integer dealScore; // 0-100 AI Score

    private String acquisitionFit; // PRIME_TARGET, HIGH, MEDIUM, SPECULATIVE

    @Column(length = 4000)
    private String aiGrowthThesis;

    private String primaryEmail;

    private String primaryPhone;

    private String linkedinCompanyUrl;

    private String priority = "WARM"; // HOT, WARM, COLD

    private Integer winProbability = 65; // 0-100%

    private String region = "North America";

    @Column(length = 4000)
    private String notes;

    @Column(length = 3000)
    private String quoteDetails;

    private String sentiment = "POSITIVE"; // POSITIVE, NEUTRAL, CAUTIOUS

    /**
     * Where this record's data came from, so estimates are never mistaken for verified facts:
     * LIVE_SCRAPED (fetched from the company's site; financials are still model ESTIMATES),
     * SIMULATED_FALLBACK (site not fetchable; profile generated), SIMULATED_DISCOVERY (generated candidate),
     * SEED_DEMO (bundled demo data), MANUAL (entered by a user).
     */
    private String dataProvenance = "MANUAL";

    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<LeadContact> contacts = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Lead() {}

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "SOURCED";
        }
        if (this.dealScore == null) {
            this.dealScore = 70;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addContact(LeadContact contact) {
        contacts.add(contact);
        contact.setLead(this);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

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

    public String getDataProvenance() { return dataProvenance; }
    public void setDataProvenance(String dataProvenance) { this.dataProvenance = dataProvenance; }

    public String getSentiment() { return sentiment; }
    public void setSentiment(String sentiment) { this.sentiment = sentiment; }

    public List<LeadContact> getContacts() { return contacts; }
    public void setContacts(List<LeadContact> contacts) { this.contacts = contacts; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
