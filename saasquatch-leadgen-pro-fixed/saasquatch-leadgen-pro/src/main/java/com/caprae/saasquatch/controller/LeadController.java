package com.caprae.saasquatch.controller;

import com.caprae.saasquatch.dto.DashboardMetricsDto;
import com.caprae.saasquatch.dto.LeadCreateDto;
import com.caprae.saasquatch.entity.Lead;
import com.caprae.saasquatch.service.ExportService;
import com.caprae.saasquatch.service.LeadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leads")
@Tag(name = "Lead Management", description = "Real-time B2B Lead and M&A Deal pipeline management")
public class LeadController {

    private final LeadService leadService;
    private final ExportService exportService;

    public LeadController(LeadService leadService, ExportService exportService) {
        this.leadService = leadService;
        this.exportService = exportService;
    }

    @GetMapping
    @Operation(summary = "Search and filter leads with multi-criteria parameters")
    public ResponseEntity<List<Lead>> getLeads(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer minScore) {
        return ResponseEntity.ok(leadService.getLeads(query, industry, status, minScore));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve a single lead by its unique database ID")
    public ResponseEntity<Lead> getLeadById(@PathVariable Long id) {
        return ResponseEntity.ok(leadService.getLeadById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new lead with deduplication enforcement")
    public ResponseEntity<Lead> createLead(@Valid @RequestBody LeadCreateDto dto) {
        return ResponseEntity.ok(leadService.createLead(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing lead's details")
    public ResponseEntity<Lead> updateLead(@PathVariable Long id, @Valid @RequestBody LeadCreateDto dto) {
        return ResponseEntity.ok(leadService.updateLead(id, dto));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update a lead's stage in the ETA/deal funnel")
    public ResponseEntity<Lead> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> statusBody) {
        String newStatus = statusBody.get("status");
        return ResponseEntity.ok(leadService.updateStatus(id, newStatus));
    }

    @PatchMapping("/{id}/priority")
    @Operation(summary = "Update deal priority (HOT, WARM, COLD)")
    public ResponseEntity<Lead> updatePriority(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(leadService.updatePriority(id, body.get("priority")));
    }

    @PatchMapping("/{id}/notes")
    @Operation(summary = "Update deal notes and sentiment")
    public ResponseEntity<Lead> updateNotes(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(leadService.updateNotes(id, body.get("notes"), body.get("sentiment")));
    }

    @PatchMapping("/{id}/quote")
    @Operation(summary = "Attach or update M&A offer quote / terms")
    public ResponseEntity<Lead> updateQuote(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(leadService.updateQuote(id, body.get("quoteDetails")));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete or archive a lead")
    public ResponseEntity<Void> deleteLead(@PathVariable Long id) {
        leadService.deleteLead(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export/csv")
    @Operation(summary = "Export filtered leads as a clean CSV spreadsheet")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer minScore) {

        List<Lead> leads = leadService.getLeads(query, industry, status, minScore);
        String csvContent = exportService.exportToCsv(leads);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=saasquatch_leads_" + System.currentTimeMillis() + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvContent.getBytes());
    }

    @GetMapping("/export/json")
    @Operation(summary = "Export leads in JSON format for CRM or webhook integration")
    public ResponseEntity<String> exportJson(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer minScore) {

        List<Lead> leads = leadService.getLeads(query, industry, status, minScore);
        String jsonContent = exportService.exportToJson(leads);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=saasquatch_leads_" + System.currentTimeMillis() + ".json")
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonContent);
    }
}
