> **Status: target design only.** This blueprint describes the intended AWS deployment. It has not been provisioned or deployed.

# Caprae Capital - Enterprise Cloud Architecture Blueprint

This document details the production-ready AWS cloud infrastructure designed for **SaaSquatch LeadGen Pro**, supporting real-time web scraping, AI deal evaluation, and high-volume ETA deal flow sourcing.

---

## 1. Architectural Diagram (AWS Topology)

```
                       [ Internet Users / Searchers / Webhooks ]
                                          |
                                    (HTTPS: 443)
                                          v
                              [ Amazon Route 53 (DNS) ]
                                          |
                                          v
                              [ AWS CloudFront (CDN) ]
                                          |
                                 [ AWS WAF (Security) ]
                                          |
                     +--------------------+--------------------+
                     |                                         |
            [ Static UI Assets ]                         [ REST APIs ]
            (S3 Bucket + OAC)                                  |
                                                               v
                                             [ Application Load Balancer (ALB) ]
                                             (Public Subnet 1A & 1B)
                                                               |
                                 +-----------------------------+-----------------------------+
                                 |                                                           |
                                 v                                                           v
                     [ ECS Fargate Task (Container) ]            [ ECS Fargate Task (Container) ]
                     (Private App Subnet 1A)                     (Private App Subnet 1B)
                     - Spring Boot 3.3.4 (Java 17)               - Spring Boot 3.3.4 (Java 17)
                     - Scraper & Enrichment Engine               - Scraper & Enrichment Engine
                     - AI Copilot Scoring Service                - AI Copilot Scoring Service
                                 |                                                           |
                                 +-----------------------------+-----------------------------+
                                                               |
                     +-----------------------------------------+-----------------------------------------+
                     |                                         |                                         |
                     v                                         v                                         v
         [ Amazon RDS PostgreSQL ]                  [ Amazon S3 Bucket ]                     [ AWS Secrets Manager ]
         (Isolated Data Subnet)                     - Deal CSV/JSON Exports                  - JWT Secret Key
         - Multi-AZ High Availability               - Scraped DOM Snapshots                  - DB Passwords & API Keys
         - Automated Daily Backups                  - SSE-KMS Encrypted                      - Rotation enabled
```

---

## 2. Infrastructure Components Breakdown

### 2.1 Networking & Security (VPC & Subnets)
- **VPC CIDR:** `10.0.0.0/16` across two Availability Zones (`us-east-1a`, `us-east-1b`).
- **Public Subnets:** `10.0.1.0/24`, `10.0.2.0/24` (Hosts ALB and NAT Gateways).
- **Private Application Subnets:** `10.0.10.0/24`, `10.0.20.0/24` (Hosts ECS Fargate container tasks).
- **Isolated Database Subnets:** `10.0.30.0/24`, `10.0.40.0/24` (No outbound internet; strictly RDS PostgreSQL).
- **Security Groups:**
  - `ALB-SG`: Ingress `443` from `0.0.0.0/0`. Egress to `ECS-SG` on port `8080`.
  - `ECS-SG`: Ingress `8080` only from `ALB-SG`. Egress to RDS on `5432` and NAT Gateway for outbound web scraping.
  - `RDS-SG`: Ingress `5432` strictly from `ECS-SG`.

### 2.2 Compute & Container Orchestration (Amazon ECS Fargate)
- **Engine:** AWS ECS Fargate (Serverless container runtime).
- **Task Definition:**
  - 1 vCPU, 2 GB RAM per task.
  - Base Image: `eclipse-temurin:17-jre-alpine` (multi-stage build from ECR).
  - Health check path: `/actuator/health`.
- **Auto-Scaling Policy:**
  - Min tasks: 2, Max tasks: 10.
  - Target tracking: 65% average CPU utilization or 500 requests/minute per task.

### 2.3 Database Tier (Amazon RDS PostgreSQL Multi-AZ)
- **Engine:** PostgreSQL 16.2.
- **Instance Class:** `db.m6g.large` (AWS Graviton3).
- **Storage:** 100 GB gp3 SSD with autoscaling up to 1 TB.
- **High Availability:** Multi-AZ synchronous replication with automated failover in under 60 seconds.
- **Maintenance & Backups:** 30-day automated snapshot retention with Point-in-Time Recovery (PITR).

### 2.4 Object Storage (Amazon S3)
- **Bucket:** `caprae-saasquatch-leads-exports`
- **Use Cases:**
  - Staging generated CSV / JSON deal files for searcher downloads.
  - Archiving raw web scraper DOM telemetry for audit and retraining.
- **Security:**
  - SSE-KMS encryption with customer-managed keys (CMK).
  - Block Public Access enabled; signed pre-signed URLs with 15-minute TTL for downloads.

### 2.5 Secrets & Configuration Management
- **AWS Secrets Manager:**
  - Stores `SPRING_DATASOURCE_PASSWORD`, `APP_JWT_SECRET`, and external enrichment API tokens.
  - Auto-injected into ECS task environment variables at launch.

### 2.6 Monitoring, Telemetry & Logging
- **Amazon CloudWatch:**
  - Logs aggregated via AWS FireLens / FluentBit from all container tasks.
  - Alarms configured for 5xx error spikes (>1% error rate) and high P95 latency (>1200ms).
- **Spring Boot Actuator:**
  - Real-time Prometheus metrics exposed at `/actuator/prometheus` for Grafana dashboards.

---

## 3. High-Performance Caching Strategy
- **Layer 1 (L1 In-Memory):** Spring Cache (`Caffeine`) for frequently accessed search queries and static industry taxonomy.
- **Layer 2 (Distributed Cache):** Amazon ElastiCache for Redis (optional scale-out) for shared session rate limiting, web scraper deduplication hashes, and active JWT token blocklists.
