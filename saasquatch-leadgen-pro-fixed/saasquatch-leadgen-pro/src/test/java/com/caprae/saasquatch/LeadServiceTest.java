package com.caprae.saasquatch;

import com.caprae.saasquatch.dto.AiScoringResponse;
import com.caprae.saasquatch.dto.DashboardMetricsDto;
import com.caprae.saasquatch.dto.LeadCreateDto;
import com.caprae.saasquatch.entity.Lead;
import com.caprae.saasquatch.exception.DuplicateResourceException;
import com.caprae.saasquatch.exception.ResourceNotFoundException;
import com.caprae.saasquatch.repository.LeadRepository;
import com.caprae.saasquatch.service.AiCopilotService;
import com.caprae.saasquatch.service.LeadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LeadServiceTest {

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private AiCopilotService aiCopilotService;

    @InjectMocks
    private LeadService leadService;

    private Lead sampleLead;

    @BeforeEach
    void setUp() {
        sampleLead = new Lead();
        sampleLead.setId(1L);
        sampleLead.setCompanyName("Alpha Tech");
        sampleLead.setDomain("alphatech.io");
        sampleLead.setIndustry("B2B SaaS");
        sampleLead.setStatus("SOURCED");
        sampleLead.setDealScore(88);
        sampleLead.setEstimatedRevenue(2_500_000.0);
    }

    @Test
    @DisplayName("Should create lead successfully and auto-evaluate score")
    void testCreateLeadSuccess() {
        LeadCreateDto dto = new LeadCreateDto();
        dto.setCompanyName("Alpha Tech");
        dto.setDomain("alphatech.io");
        dto.setIndustry("B2B SaaS");

        AiScoringResponse evalResponse = new AiScoringResponse();
        evalResponse.setDealScore(88);
        evalResponse.setAcquisitionFit("PRIME_TARGET");
        evalResponse.setGrowthThesis("Strong candidate");

        when(leadRepository.existsByDomain("alphatech.io")).thenReturn(false);
        when(aiCopilotService.evaluateLead(any(Lead.class))).thenReturn(evalResponse);
        when(leadRepository.save(any(Lead.class))).thenReturn(sampleLead);

        Lead created = leadService.createLead(dto);

        assertNotNull(created);
        assertEquals("Alpha Tech", created.getCompanyName());
        verify(leadRepository, times(1)).save(any(Lead.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when domain already exists")
    void testCreateLeadDuplicateDomain() {
        LeadCreateDto dto = new LeadCreateDto();
        dto.setCompanyName("Alpha Tech");
        dto.setDomain("alphatech.io");

        when(leadRepository.existsByDomain("alphatech.io")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> leadService.createLead(dto));
        verify(leadRepository, never()).save(any(Lead.class));
    }

    @Test
    @DisplayName("Should update lead status in deal funnel")
    void testUpdateStatusSuccess() {
        when(leadRepository.findById(1L)).thenReturn(Optional.of(sampleLead));
        when(leadRepository.save(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Lead updated = leadService.updateStatus(1L, "DILIGENCE");

        assertNotNull(updated);
        assertEquals("DILIGENCE", updated.getStatus());
        verify(leadRepository, times(1)).save(sampleLead);
    }

    @Test
    @DisplayName("Should retrieve dashboard KPI metrics correctly")
    void testGetDashboardMetrics() {
        when(leadRepository.countTotalLeads()).thenReturn(10L);
        when(leadRepository.getAverageDealScore()).thenReturn(82.5);
        when(leadRepository.getTotalPipelineRevenue()).thenReturn(25_000_000.0);
        when(leadRepository.findAll()).thenReturn(List.of(sampleLead));

        DashboardMetricsDto metrics = leadService.getDashboardMetrics();

        assertNotNull(metrics);
        assertEquals(10L, metrics.getTotalLeads());
        assertEquals(82.5, metrics.getAvgDealScore());
        assertEquals(25_000_000.0, metrics.getTotalPipelineRevenue());
    }

    @Test
    @DisplayName("Should reject a lead whose name closely matches an existing company")
    void testCreateLeadFuzzyDuplicate() {
        Lead existing = new Lead();
        existing.setCompanyName("Alpha Tech");
        existing.setDomain("alphatech.io");
        when(leadRepository.existsByDomain("alphatech.co")).thenReturn(false);
        when(leadRepository.findAll()).thenReturn(List.of(existing));

        LeadCreateDto dto = new LeadCreateDto();
        dto.setCompanyName("Alpha Tech, Inc.");
        dto.setDomain("alphatech.co");

        assertThrows(DuplicateResourceException.class, () -> leadService.createLead(dto));
        verify(leadRepository, never()).save(any(Lead.class));
    }
}
