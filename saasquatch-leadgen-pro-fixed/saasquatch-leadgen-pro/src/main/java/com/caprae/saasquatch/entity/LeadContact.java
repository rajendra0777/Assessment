package com.caprae.saasquatch.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "lead_contacts")
public class LeadContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    private String title; // e.g. "Founder & CEO", "VP of Operations", "Co-Founder"

    private String email;

    private String linkedinUrl;

    private String phone;

    private Boolean isVerified = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id")
    @JsonBackReference
    private Lead lead;

    public LeadContact() {}

    public LeadContact(String fullName, String title, String email, String linkedinUrl, String phone, Boolean isVerified) {
        this.fullName = fullName;
        this.title = title;
        this.email = email;
        this.linkedinUrl = linkedinUrl;
        this.phone = phone;
        this.isVerified = isVerified;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Lead getLead() { return lead; }
    public void setLead(Lead lead) { this.lead = lead; }
}
