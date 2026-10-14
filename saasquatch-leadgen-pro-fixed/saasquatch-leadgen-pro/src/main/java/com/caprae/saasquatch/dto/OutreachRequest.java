package com.caprae.saasquatch.dto;

public class OutreachRequest {
    private Long leadId;
    private String tone; // e.g. "CONFIDENTIAL_ACQUISITION", "PE_GROWTH_PARTNER", "STRATEGIC_VALUE_ADD", "CASUAL_FOUNDER_TO_FOUNDER"
    private String recipientTitle; // e.g. "CEO / Founder", "Head of Growth"
    private String senderName; // e.g. "Caprae Investment Team"
    private String specialAngle; // Optional notes or strategic intent

    public OutreachRequest() {}

    public Long getLeadId() { return leadId; }
    public void setLeadId(Long leadId) { this.leadId = leadId; }

    public String getTone() { return tone; }
    public void setTone(String tone) { this.tone = tone; }

    public String getRecipientTitle() { return recipientTitle; }
    public void setRecipientTitle(String recipientTitle) { this.recipientTitle = recipientTitle; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSpecialAngle() { return specialAngle; }
    public void setSpecialAngle(String specialAngle) { this.specialAngle = specialAngle; }
}
