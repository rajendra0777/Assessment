package com.caprae.saasquatch.controller;

import com.caprae.saasquatch.dto.AiScoringResponse;
import com.caprae.saasquatch.dto.OutreachRequest;
import com.caprae.saasquatch.dto.OutreachResponse;
import com.caprae.saasquatch.entity.Lead;
import com.caprae.saasquatch.service.AiCopilotService;
import com.caprae.saasquatch.service.LeadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI Deal Copilot", description = "AI deal scoring, 7-year growth thesis generator, and personalized outreach sequence generator")
public class AiCopilotController {

    private final AiCopilotService aiCopilotService;
    private final LeadService leadService;

    public AiCopilotController(AiCopilotService aiCopilotService, LeadService leadService) {
        this.aiCopilotService = aiCopilotService;
        this.leadService = leadService;
    }

    @PostMapping("/evaluate/{leadId}")
    @Operation(summary = "Calculate AI Deal Readiness Score and generate Caprae 7-year ETA thesis")
    public ResponseEntity<AiScoringResponse> evaluateLead(@PathVariable Long leadId) {
        Lead lead = leadService.getLeadById(leadId);
        AiScoringResponse evaluation = aiCopilotService.evaluateLead(lead);

        // Update lead with latest AI findings
        lead.setDealScore(evaluation.getDealScore());
        lead.setAcquisitionFit(evaluation.getAcquisitionFit());
        lead.setAiGrowthThesis(evaluation.getGrowthThesis());
        leadService.updateStatus(lead.getId(), lead.getStatus()); // persists via repo

        return ResponseEntity.ok(evaluation);
    }

    @PostMapping("/outreach")
    @Operation(summary = "Generate high-converting personalized cold outreach sequence for searchers")
    public ResponseEntity<OutreachResponse> generateOutreach(@RequestBody OutreachRequest request) {
        Lead lead = leadService.getLeadById(request.getLeadId());
        OutreachResponse response = aiCopilotService.generateOutreach(lead, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/rag-search")
    @Operation(summary = "RAG real-time database retrieval and AI deal flow synthesis")
    public ResponseEntity<Map<String, Object>> ragSearch(@RequestBody Map<String, String> body) {
        String query = body.get("query");
        List<Lead> allLeads = leadService.getLeads(null, null, null, null);
        return ResponseEntity.ok(aiCopilotService.executeRagSearch(query, allLeads));
    }

    @PostMapping("/market-research")
    @Operation(summary = "AI Market Research, TAM sizing and EBITDA multiple benchmarks by industry")
    public ResponseEntity<Map<String, Object>> marketResearch(@RequestBody Map<String, String> body) {
        String industry = body.get("industry");
        return ResponseEntity.ok(aiCopilotService.generateMarketResearch(industry));
    }

    @GetMapping("/financial-report/{leadId}")
    @Operation(summary = "A-to-Z financial modeling, P&L breakdown, and 3 acquisition deal structures")
    public ResponseEntity<Map<String, Object>> getFinancialReport(@PathVariable Long leadId) {
        Lead lead = leadService.getLeadById(leadId);
        return ResponseEntity.ok(aiCopilotService.generateFinancialReport(lead));
    }

    @PostMapping("/summarize-conversation")
    @Operation(summary = "AI Conversation summarizer, sentiment analysis, priority classification & win probability estimation")
    public ResponseEntity<Map<String, Object>> summarizeConversation(@RequestBody Map<String, Object> body) {
        Long leadId = Long.valueOf(body.get("leadId").toString());
        String conversationText = (String) body.get("conversationText");
        Lead lead = leadService.getLeadById(leadId);

        Map<String, Object> summary = aiCopilotService.summarizeConversation(lead, conversationText);

        // Auto-update lead state in database
        String priority = (String) summary.get("detectedPriority");
        Integer winProb = (Integer) summary.get("winProbabilityPct");
        String sentiment = (String) summary.get("sentiment");
        String execSummary = (String) summary.get("executiveSummary");

        leadService.updatePriority(leadId, priority);
        leadService.updateWinProbability(leadId, winProb);
        leadService.updateNotes(leadId, execSummary, sentiment);

        return ResponseEntity.ok(summary);
    }

    @PostMapping("/generate-quote")
    @Operation(summary = "Generate formal M&A acquisition quote, term sheet terms and valuation multiple proposal")
    public ResponseEntity<Map<String, Object>> generateQuote(@RequestBody Map<String, Object> body) {
        Long leadId = Long.valueOf(body.get("leadId").toString());
        String structure = (String) body.get("structure");
        Double valuationOffer = body.get("valuationOffer") != null ? Double.valueOf(body.get("valuationOffer").toString()) : null;

        Lead lead = leadService.getLeadById(leadId);
        Map<String, Object> quote = aiCopilotService.generateSolutionQuote(lead, structure, valuationOffer);

        // Auto-attach quote to lead record
        String quoteSummary = "Proposed Valuation: " + quote.get("proposedEnterpriseValuation") +
                " (" + quote.get("impliedMultiple") + "). Cash at Close: " + quote.get("cashAtClose") +
                ". Seller Note: " + quote.get("sellerNote") + ". Rollover: " + quote.get("rolloverEquity");
        leadService.updateQuote(leadId, quoteSummary);

        return ResponseEntity.ok(quote);
    }

    @PostMapping("/parse-prompt")
    @Operation(summary = "Parse natural language scraping prompt into structured search & extraction parameters")
    public ResponseEntity<Map<String, Object>> parsePrompt(@RequestBody Map<String, String> body) {
        String prompt = body.get("prompt");
        return ResponseEntity.ok(aiCopilotService.parsePromptToScrapeCriteria(prompt));
    }
}
