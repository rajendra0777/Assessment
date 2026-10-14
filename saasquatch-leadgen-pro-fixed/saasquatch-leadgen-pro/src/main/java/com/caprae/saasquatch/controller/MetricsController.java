package com.caprae.saasquatch.controller;

import com.caprae.saasquatch.dto.DashboardMetricsDto;
import com.caprae.saasquatch.service.LeadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
@Tag(name = "Telemetry & Metrics", description = "Real-time pipeline analytics, deal score averages, and industry distributions")
public class MetricsController {

    private final LeadService leadService;

    public MetricsController(LeadService leadService) {
        this.leadService = leadService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get high-level deal pipeline KPI metrics and distribution summaries")
    public ResponseEntity<DashboardMetricsDto> getDashboardMetrics() {
        return ResponseEntity.ok(leadService.getDashboardMetrics());
    }
}
