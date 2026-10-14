package com.caprae.saasquatch.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scraping_logs")
public class ScrapingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String targetUrlOrQuery;

    private Integer resultsFound;

    private String status; // SUCCESS, FAILED, PARTIAL

    private Long executionTimeMs;

    @Column(length = 2000)
    private String message;

    private LocalDateTime createdAt;

    public ScrapingLog() {}

    public ScrapingLog(String targetUrlOrQuery, Integer resultsFound, String status, Long executionTimeMs, String message) {
        this.targetUrlOrQuery = targetUrlOrQuery;
        this.resultsFound = resultsFound;
        this.status = status;
        this.executionTimeMs = executionTimeMs;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTargetUrlOrQuery() { return targetUrlOrQuery; }
    public void setTargetUrlOrQuery(String targetUrlOrQuery) { this.targetUrlOrQuery = targetUrlOrQuery; }

    public Integer getResultsFound() { return resultsFound; }
    public void setResultsFound(Integer resultsFound) { this.resultsFound = resultsFound; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(Long executionTimeMs) { this.executionTimeMs = executionTimeMs; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
