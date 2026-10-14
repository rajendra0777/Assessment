package com.caprae.saasquatch;

import com.caprae.saasquatch.dto.AiScoringResponse;
import com.caprae.saasquatch.dto.OutreachRequest;
import com.caprae.saasquatch.dto.OutreachResponse;
import com.caprae.saasquatch.entity.Lead;
import com.caprae.saasquatch.service.AiCopilotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AiCopilotServiceTest {

    private AiCopilotService aiCopilotService;
    private Lead lead;

    @BeforeEach
    void setUp() {
        aiCopilotService = new AiCopilotService();
        lead = new Lead();
        lead.setId(10L);
        lead.setCompanyName("Nexus Analytics");
        lead.setDomain("nexusanalytics.io");
        lead.setIndustry("B2B SaaS");
        lead.setEstimatedRevenue(3_000_000.0);
        lead.setEstimatedEbitda(850_000.0);
        lead.setBusinessModel("B2B Recurring SaaS");
        lead.setTechStack("React, AWS, Stripe, PostgreSQL");
    }

    @Test
    @DisplayName("Should evaluate lead and compute Caprae 7-year growth thesis and valuation multiples")
    void testEvaluateLead() {
        AiScoringResponse res = aiCopilotService.evaluateLead(lead);

        assertNotNull(res);
        assertEquals("Nexus Analytics", res.getCompanyName());
        assertTrue(res.getDealScore() >= 80, "Deal score should be high for profitable SaaS");
        assertEquals("PRIME_TARGET", res.getAcquisitionFit());
        assertNotNull(res.getEbitdaMultipleRange());
        assertNotNull(res.getEstimatedValuation());
        assertTrue(res.getGrowthThesis().contains("Caprae Capital"));
        assertFalse(res.getKeyStrengths().isEmpty());
    }

    @Test
    @DisplayName("Should generate high-converting cold email outreach for searchers")
    void testGenerateOutreach() {
        OutreachRequest req = new OutreachRequest();
        req.setLeadId(10L);
        req.setTone("CONFIDENTIAL_ACQUISITION");
        req.setSenderName("Kevin Hong");

        OutreachResponse out = aiCopilotService.generateOutreach(lead, req);

        assertNotNull(out);
        assertNotNull(out.getSubjectLine());
        assertTrue(out.getSubjectLine().contains("Nexus Analytics"));
        assertTrue(out.getEmailBody().contains("Caprae Capital"));
        assertNotNull(out.getFollowUpDay3Snippet());
    }
}
