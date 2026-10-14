package com.caprae.saasquatch.service;

import com.caprae.saasquatch.entity.Lead;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.List;

@Service
public class ExportService {

    private final ObjectMapper objectMapper;

    public ExportService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String exportToCsv(List<Lead> leads) {
        StringWriter writer = new StringWriter();
        // Header
        writer.append("ID,Company Name,Domain,Industry,Status,Deal Score,Acquisition Fit,ARR (ESTIMATE $),EBITDA (ESTIMATE $),Employees (ESTIMATE),Location,Primary Email,Tech Stack,AI Growth Thesis,Data Provenance\n");

        for (Lead lead : leads) {
            writer.append(escape(String.valueOf(lead.getId()))).append(",");
            writer.append(escape(lead.getCompanyName())).append(",");
            writer.append(escape(lead.getDomain())).append(",");
            writer.append(escape(lead.getIndustry())).append(",");
            writer.append(escape(lead.getStatus())).append(",");
            writer.append(escape(String.valueOf(lead.getDealScore()))).append(",");
            writer.append(escape(lead.getAcquisitionFit())).append(",");
            writer.append(escape(lead.getEstimatedRevenue() != null ? String.valueOf(lead.getEstimatedRevenue()) : "N/A")).append(",");
            writer.append(escape(lead.getEstimatedEbitda() != null ? String.valueOf(lead.getEstimatedEbitda()) : "N/A")).append(",");
            writer.append(escape(lead.getEmployeeCount() != null ? String.valueOf(lead.getEmployeeCount()) : "N/A")).append(",");
            writer.append(escape(lead.getLocation())).append(",");
            writer.append(escape(lead.getPrimaryEmail())).append(",");
            writer.append(escape(lead.getTechStack())).append(",");
            writer.append(escape(lead.getAiGrowthThesis())).append(",");
            writer.append(escape(lead.getDataProvenance())).append("\n");
        }

        return writer.toString();
    }

    public String exportToJson(List<Lead> leads) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(leads);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize leads to JSON", e);
        }
    }

    private String escape(String value) {
        if (value == null) return "\"\"";
        // Neutralize spreadsheet formula injection: scraped text must not execute as a formula in Excel/Sheets.
        if (!value.isEmpty() && "=+-@\t\r".indexOf(value.charAt(0)) >= 0) {
            value = "'" + value;
        }
        return "\"" + value.replace("\"", "\"\"").replace("\n", " ") + "\"";
    }
}
