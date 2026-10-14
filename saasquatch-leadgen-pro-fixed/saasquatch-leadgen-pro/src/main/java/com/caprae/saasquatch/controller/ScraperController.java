package com.caprae.saasquatch.controller;

import com.caprae.saasquatch.dto.LeadScrapeRequest;
import com.caprae.saasquatch.entity.Lead;
import com.caprae.saasquatch.entity.ScrapingLog;
import com.caprae.saasquatch.repository.ScrapingLogRepository;
import com.caprae.saasquatch.service.WebScraperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scraper")
@Tag(name = "Real-Time Scraper Engine", description = "High-performance web scraping and data enrichment pipelines")
public class ScraperController {

    private final WebScraperService webScraperService;
    private final ScrapingLogRepository scrapingLogRepository;

    public ScraperController(WebScraperService webScraperService,
                             ScrapingLogRepository scrapingLogRepository) {
        this.webScraperService = webScraperService;
        this.scrapingLogRepository = scrapingLogRepository;
    }

    @PostMapping("/extract")
    @Operation(summary = "Execute real-time scraping and enrichment on a domain or discover niche candidates")
    public ResponseEntity<List<Lead>> extractLeads(@Valid @RequestBody LeadScrapeRequest request) {
        List<Lead> leads = webScraperService.scrapeAndEnrich(request);
        return ResponseEntity.ok(leads);
    }

    @GetMapping("/logs")
    @Operation(summary = "View telemetry logs of recent scraping executions")
    public ResponseEntity<List<ScrapingLog>> getScrapingLogs() {
        return ResponseEntity.ok(scrapingLogRepository.findTop20ByOrderByCreatedAtDesc());
    }
}
