# SaaSquatch LeadGen Pro | Caprae Capital Partners
> **Enterprise Real-Time Deal Sourcing, AI Lead Generation & M&A Intelligence Platform**  
> *Engineered for Private Equity Deal Teams, Acquisition Entrepreneurs (ETA), and B2B Growth Operators.*

---

## 📌 Executive Overview & Business Background

**Caprae Capital Partners** (led by Managing Principal Kevin Hong) operates at the cutting edge of Entrepreneurship Through Acquisition (ETA) and Private Equity. Unlike legacy PE funds that lean heavily on short-term financial engineering, Caprae views M&A as a **seven-year operational transformation journey**, deploying proprietary technology, SaaS, and **MaaS (M&A as a Service)** models to accelerate businesses post-acquisition.

As demonstrated by Caprae's in-house platform **SaaSquatch Leads** (`saasquatchleads.com`), modern dealmakers need direct, proprietary off-market sourcing rather than renting overpriced, commoditized tools like ZoomInfo or PitchBook. 

**SaaSquatch LeadGen Pro** is a high-performance, enterprise-grade Java 17 / Spring Boot platform built to solve this exact challenge:
1. **Role-Based Access Control (RBAC) & Stateless JWT:** Multi-tenant access supporting `ROLE_ADMIN` (Managing Principal Kevin Hong) and `ROLE_OPERATOR` (Deal Sourcing Associates), complete with sign-out and instant account-switching workflows.
2. **Elevated Deal Pipeline (Kanban Board & Table View):** Visual Kanban board across 5 acquisition stages (`SOURCED`, `QUALIFIED`, `DILIGENCE`, `LOI_SUBMITTED`, `CLOSED`), win probability gauges (0–100%), deal priority tags (🔥 HOT, ⚡ WARM, ❄️ COLD), founder direct connection modal, AI conversation summarizer, and term sheet acquisition quote generator.
3. **AI-Assisted Natural Language Prompt Scraper & Real-Time Sourcing:** Enter plain English prompts (e.g., *"Find profitable B2B cybersecurity SaaS in Texas with $2M–$5M ARR using AWS and Stripe"*). The system parses taxonomy, sets constraints, scrapes live DOMs, identifies tech stack signatures, and commits enriched deals to the database. Only name, description, tech-stack hints and published emails come from the real site; revenue, EBITDA and headcount are model ESTIMATES (see Data Honesty).
4. **AI Deal Copilot Suite:**
   - **Deal retrieval and briefings:** keyword search across the deals in the database, producing a templated briefing that cites the matched records. This is retrieval without embeddings or an LLM.
   - **Market Research & Industry Benchmarks:** TAM/SAM analysis, EBITDA multiples (4.8x–7.2x), macro tailwinds, and Caprae roll-up playbooks.
   - **A-to-Z Financial Modeling & Valuations:** P&L breakdown, Seller Discretionary Earnings (SDE), Senior Bank Debt vs Founder Rollover deal structures, and 5-year exit ROI projections (MOIC).
   - **Searcher Cold Outreach Studio:** Founder-friendly email sequences with Day 3 and Day 7 follow-ups.
5. **Rich Visual Analytics & KPIs:** Interactive SVG Bar charts (Cumulative ARR by Industry), Donut charts (Deal Funnel Velocity), priority bars, vertical benchmarks, and multi-field sorting.
6. **Enterprise API Portal & Documentation:** Comprehensive 3rd-party integration guide for CRMs (HubSpot, Salesforce), Zapier webhooks, and Python scrapers, with interactive Swagger UI and `X-API-KEY` machine authentication.

---

## 🏗️ Technology Stack & Architecture

| Layer | Technologies Used |
|---|---|
| **Core Language** | **Java 17 LTS** (records, text blocks; the minimum for Spring Boot 3) |
| **Backend Framework** | **Spring Boot 3.3.4**, Spring MVC, Spring Security 6, Spring Data JPA |
| **Security & Auth** | **Stateless JWT (JJWT 0.12.6)** with HMAC-SHA256, BCrypt Password Hashing, `X-API-KEY` Machine Authentication |
| **Databases** | **H2 (Zero-dependency embedded)**, **PostgreSQL 16**, **MySQL 8** (Profiles: `default`, `postgres`, `mysql`) |
| **Scraping & HTML Engine** | **Jsoup 1.18.1** (High-speed DOM parser, tech stack fingerprint detector, contact extractor) |
| **AI Intelligence** | Rule-based scoring and templates, in-memory keyword retrieval over the deal table, prompt entity parser, conversation sentiment keywords. **No LLM or embeddings yet** (see Known Limitations) |
| **API Documentation** | **SpringDoc OpenAPI 2.6.0** / Swagger UI v3 (`/swagger-ui.html`) |
| **DevOps & Cloud** | **Docker** (Multi-stage build), **Docker Compose**, **Jenkinsfile** (CI/CD pipeline), **AWS Blueprint** (ECS Fargate, RDS Multi-AZ, S3) |
| **Testing** | **JUnit 5**, **Mockito**, Spring Boot Test / MockMvc. Run `mvn test`. Coverage is not measured |
| **Frontend UI** | **Vanilla HTML5/CSS3/JavaScript SPA** served directly from Spring Boot (`/static`) with custom Glassmorphism Dark Mode design system |

---

## 🚀 Quick Start

**Prerequisites:** JDK 17+ and Maven 3.9+ (or just Docker).

### Option 1: Run locally (in-memory H2 database)

```bash
# Optional locally. If SEED_PASSWORD is unset, a random password is generated and printed once in the startup log.
export SEED_PASSWORD='choose-a-strong-password'
export JWT_SECRET='at-least-32-random-characters'   # if unset, a random per-start key is used
mvn spring-boot:run
```

(PowerShell: `$env:SEED_PASSWORD='...'; mvn spring-boot:run`)

Then open:
- **Web app:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Health:** http://localhost:8080/actuator/health

### Option 2: Docker Compose with PostgreSQL

```bash
cp .env.example .env      # then edit the values; never commit .env
docker compose up --build
```

### Run the tests

```bash
mvn test
```

### Signing in

Two demo accounts are created on first start, both using the password you set in `SEED_PASSWORD`:

| Username | Role | Can do |
|---|---|---|
| `admin` | `ROLE_ADMIN` | Everything, including deleting deals and creating accounts |
| `searcher` | `ROLE_OPERATOR` | Sourcing, pipeline updates, AI tools, exports |

There are no default or shared passwords in this repository. Each user has a personal API key, returned by `POST /api/auth/login` and shown in the app's API portal. Send it as `X-API-KEY: <your key>` for machine access.

---

## 🔍 Data Honesty: what is real and what is estimated

| Field | Status |
|---|---|
| Company name, description, tech-stack hints, published emails and LinkedIn link | **LIVE_SCRAPED** from the company's public site (tech-stack detection is keyword matching and can be wrong) |
| Revenue, EBITDA, headcount, location, deal score, acquisition fit, valuations, MOIC | **ESTIMATES** from placeholder heuristics. They are not facts and not investment advice |
| Records created when a site cannot be fetched | **SIMULATED_FALLBACK**: placeholder profile, nothing verified |
| Records from the natural-language "discovery" flow | **SIMULATED_DISCOVERY**: generated candidates on reserved `example.com` domains, not real companies |
| The 16 bundled starter deals | **SEED_DEMO** sample data |

Every lead carries a `dataProvenance` field. The UI shows a badge for non-manual records and the CSV export includes a provenance column. Contact details are never guessed: if no email is published on the site, the field stays empty.

## 🔒 Security model

- JWT bearer tokens (1 hour by default) and per-user API keys; BCrypt password hashing; failed-login lockout (5 attempts, 15 minutes).
- All `/api/**` routes require authentication. Deleting deals and creating accounts require `ROLE_ADMIN`. New accounts are always `ROLE_OPERATOR`.
- No secrets in the repo: `JWT_SECRET`, `SEED_PASSWORD` and database credentials come from environment variables (`.env.example` lists them).
- CORS is limited to `CORS_ALLOWED_ORIGINS`. The H2 console is off unless `H2_CONSOLE_ENABLED=true`. Only `/actuator/health` and `/actuator/info` are exposed.
- Scraper safety: blocks private, loopback and cloud-metadata addresses (SSRF), re-checks every redirect, allows only ports 80/443, honors `robots.txt`, and identifies itself as `SaaSquatchLeadGenBot` with a contact URL (`SCRAPER_CONTACT_URL`).
- CSV export neutralizes spreadsheet formula injection.

## ⚠️ Known Limitations

- The "AI" features are rule-based and templated. There is no LLM or vector search yet. The planned design is a `LlmProvider` interface (OpenAI/Gemini) with a scripted fallback, and pgvector retrieval.
- Deduplication matches normalized domain and fuzzy company name (Jaro-Winkler). It does not yet match on phone or address, and there is no merge-review UI. Pass `confirmDistinct: true` to `POST /api/leads` to override a possible-duplicate warning.
- The scraper fetches one page per target with Jsoup. It does not run JavaScript, rotate proxies or solve CAPTCHAs. A DNS-rebinding window remains between the SSRF check and the fetch.
- The web UI keeps the JWT in `localStorage`, which is readable by any script on the page (XSS). All user and scraped text is HTML-escaped, but a production build should use HttpOnly cookies plus CSRF protection.
- Schema is managed by Hibernate `ddl-auto=update`; production should use Flyway migrations.
- `aws-architecture.md` and the `Jenkinsfile` deploy and SonarQube stages describe a target design and are not active. The Jenkins pipeline builds and tests only.
- Seed data lives in `LeadService`; moving it to a SQL file or dedicated seeder is a good next refactor.

---

## 📡 RESTful API Reference for 3rd Party Integrations

### 1. Authentication (`/api/auth`)
* `POST /api/auth/login`: Authenticate operator and issue JWT Bearer token.
* `POST /api/auth/register`: Register new searcher account.
* `GET /api/auth/me`: Verify session and retrieve active role permissions.

### 2. Deal Management & Pipeline Funnel (`/api/leads`)
* `GET /api/leads`: Search and filter deals (`query`, `industry`, `status`, `minScore`).
* `GET /api/leads/{id}`: Retrieve single deal with verified executive contacts.
* `POST /api/leads`: Create new deal with automatic domain deduplication.
* `PATCH /api/leads/{id}/status`: Advance stage (`SOURCED`, `QUALIFIED`, `DILIGENCE`, `LOI_SUBMITTED`, `CLOSED`).
* `PATCH /api/leads/{id}/priority`: Update priority (`HOT`, `WARM`, `COLD`).
* `PATCH /api/leads/{id}/notes`: Update call notes, sentiment, and win probability.
* `PATCH /api/leads/{id}/quote`: Attach formal acquisition terms.
* `GET /api/leads/export/csv`: Export pipeline to CSV spreadsheet.
* `GET /api/leads/export/json`: Export pipeline in JSON format.

### 3. Real-Time Scraper Engine (`/api/scraper`)
* `POST /api/scraper/extract`: Live DOM scraping, tech detection (AWS, Stripe, React, Shopify), email discovery, and automatic qualification.
* `GET /api/scraper/logs`: Telemetry logs of recent scraper executions.

### 4. AI Deal Copilot (`/api/ai`)
* `POST /api/ai/parse-prompt`: Parses natural language prompt into structured search parameters.
* `POST /api/ai/rag-search`: Semantic retrieval over active deals with executive briefing.
* `POST /api/ai/market-research`: Generates TAM/SAM analysis, EBITDA multiples, and roll-up playbooks.
* `GET /api/ai/financial-report/{leadId}`: A-to-Z financial modeling, P&L breakdown, and debt/equity deal structures.
* `POST /api/ai/summarize-conversation`: Ingests meeting notes, extracts sentiment, classifies priority, and sets win %.
* `POST /api/ai/generate-quote`: Generates formal LOI term sheet with cash at close and rollover equity.
* `POST /api/ai/outreach`: Generates personalized cold email sequences with Day 3/Day 7 follow-ups.

### 5. Telemetry & Analytics (`/api/metrics`)
* `GET /api/metrics/dashboard`: KPI metrics, revenue aggregations, and industry distribution.

---

## 📝 Section 4 & 7: Handbook Business Understanding Responses

### 1. What is Caprae’s Mission?
Caprae Capital’s mission is to fundamentally transform the Private Equity and ETA landscape by championing an **operator-first, value-creation-driven model** rather than relying on short-term financial engineering. Unlike traditional buyout firms that focus on extracting financial leverage over 3-year holding cycles and quickly flipping assets, Caprae views M&A as a **seven-year journey** where the true upside is unlocked *post-acquisition*. 

By combining proprietary technology (such as SaaSquatch Leads) with **MaaS (M&A as a Service)** and **SaaS models**, Caprae empowers acquisition entrepreneurs and portfolio companies to implement practical AI tooling, automate customer acquisition, streamline back-office operations, and institutionalize scalable go-to-market engines. Caprae’s mission is to identify world-class talent ("horsepower") before it is obvious on a resume and provide them with the capital, systems, and operational stamina to turn good businesses into exceptional, enduring enterprises.

### 2. Why do you want to work at Caprae Capital?
I want to work at Caprae Capital because of the firm’s uncompromising commitment to **building real technology rather than renting someone else's**, paired with the high-horsepower **#BleedandBuild** culture. Most finance firms view software engineering merely as an IT support function or purchase off-the-shelf subscriptions that constrain their competitive edge. In contrast, Caprae builds its own proprietary tools—like SaaSquatch Leads—giving its dealmakers an unmatched speed and proprietary data advantage in sourcing off-market transactions.

Furthermore, Kevin Hong’s philosophy of prioritizing **character, courage, creativity, and crazy** over conventional pedigree resonates deeply with me. I thrive in high-autonomy, high-velocity environments where results matter infinitely more than bureaucracy, and where engineers are expected to understand the commercial economics of the business they are building for. Building tools that directly generate real enterprise deal flow and power post-acquisition transformations at Caprae represents the ideal convergence of technical mastery and tangible business impact.

### 3. How is Caprae Changing the ETA Space and Broader PE?
Caprae is disrupting the Entrepreneurship Through Acquisition (ETA) and traditional PE ecosystem in three revolutionary ways:
1. **Shifting Value Creation from Financial Engineering to Operational AI:** Traditional PE relies on cheap debt and multiple expansion. Caprae recognizes that the next decade belongs to operational leverage. Through its MaaS model, Caprae injects automated lead generation, modern tech stacks, and AI workflows directly into acquired SMBs, doubling margins organically.
2. **Eliminating the Searcher Deal Sourcing Bottleneck:** Finding off-market, high-quality B2B targets has historically taken search fund entrepreneurs 12 to 24 months of grinding manual cold emails and broker negotiations. Caprae’s SaaSquatch technology industrializes proprietary deal discovery, delivering enriched target profiles and warm outreach directly to searchers, compressing the sourcing timeline by over 60%.
3. **The 7-Year Long-Term Partnership Horizon:** While conventional funds operate on 3-to-5-year quick flips that can destabilize small businesses, Caprae commits to a 7-year horizon. This long-term alignment gives searchers and founders the runway to rebuild legacy infrastructure, cultivate enduring company culture, and maximize terminal enterprise value.

---

### Candidate Background & Employment Expectations

- **What is your current working status in the US?** Authorized to work / Open to full-time remote engagement with US entities.
- **Are you willing and able to work a minimum of 40 hours/week?** **Yes, 100%.** Fully dedicated and capable of exceeding 40 hours/week.
- **Why Caprae Capital?** Because Caprae values raw horsepower, high-velocity builders, and proprietary software innovation over corporate red tape.
- **What is your expected salary?** Competitive market rate aligned with Full Stack Developer roles at Caprae Capital (open to discussing based on role structure and performance upside).
- **Confirmation of Basic Employment Expectations:**
  - 3-month probationary period: **Confirmed and accepted.**
  - Working 9 AM - 6 PM EST with a 1-hour lunch break during initial training: **Confirmed and accepted.**
  - Availability for off-hours customer service emergencies & time-sensitive projects (< 2 hrs/week): **Confirmed and accepted without hesitation.**

---

### Section 7: Reapplicant Questions

1. **What do you think is Caprae’s unfair advantage?**  
   Caprae’s unfair advantage is the symbiotic fusion of **proprietary internal technology** and an **operator-first, long-horizon culture**. While competitors rent the same public databases and compete for the exact same broker-listed auctions at inflated multiples, Caprae’s in-house software (SaaSquatch Leads) generates proprietary, off-market deal flow. Combined with the MaaS playbook, Caprae enters negotiations not as a predatory financial buyer, but as an operational growth engine that founders actually want to partner with.
2. **What does “To become a legend, you must take down legends” mean to you?**  
   It means that true greatness cannot be achieved by playing by the incumbents' rules or settling for incremental improvements. Established giants (traditional multi-billion-dollar PE firms, legacy sourcing monopolies) become complacent, bloated, and slow. Taking down legends requires having the audacity to challenge standard industry doctrine, out-working and out-innovating the competition with superior stamina, and building tools that render the old way obsolete.
3. **What do you think Caprae’s culture will be like?**  
   Caprae’s culture is defined by **relentless speed, high candor, extreme accountability, and intellectual stamina**. It is a builder-centric, no-excuses environment where output speaks louder than titles. Team members take deep pride in their craft, communicate with radical transparency, support one another through intense sprints (#BleedandBuild), and inject humor and grit into the journey of building an enduring institution.

---

## 🎬 2-Minute Video Walkthrough Script

- **[0:00 - 0:20] Hook & Business Context:**  
  *"Hello Kevin and the Caprae Capital team. Today, I’m excited to present **SaaSquatch LeadGen Pro**—an enterprise real-time deal sourcing and AI intelligence platform built specifically for Caprae's ETA and private equity workflows. Inspired by SaaSquatch Leads, our mission is to eliminate expensive data rentals and uncover proprietary, off-market B2B software acquisitions in the $1M to $8M ARR sweet spot."*

- **[0:20 - 0:45] Technical Architecture & Real-Time Scraper Demo:**  
  *"Under the hood, this application is powered by Java 17 LTS and Spring Boot 3.3.4, backed by Spring Data JPA and Spring Security with JWT and API Key authentication. Let’s look at the Real-Time Sourcing Engine: by entering a natural language prompt or domain, our Jsoup engine extracts live HTML, identifies core tech stack fingerprints—like AWS, Stripe, and React—locates executive contacts, and checks domain deduplication in under 800 milliseconds."*

- **[0:45 - 1:15] AI Deal Copilot & 7-Year Growth Thesis:**  
  *"Next is our core differentiator: the **AI Deal Sourcing Copilot**. Sourcing is only as good as qualification. The copilot features an in-memory keyword retrieval engine to search our deal database, calculates an algorithmic Deal Readiness Score from 0 to 100, estimates EBITDA multiples—typically 4.5x to 6.5x—and automatically crafts a custom **Caprae 7-Year Growth Thesis**. Searchers can summarize founder calls, attach formal LOI acquisition quotes, and generate tailored cold outreach sequences with Day 3 and Day 7 follow-ups."*

- **[1:15 - 1:40] Deal Pipeline, Analytics & Enterprise Cloud:**  
  *"The Deal Pipeline features an interactive Kanban board and Table view with win probability meters and priority badges. Our Analytics dashboard renders dynamic SVG bar charts and donut charts detailing pipeline value and stage conversion velocity. The architecture includes interactive Swagger UI docs, a multi-stage Docker container, a declarative Jenkinsfile CI/CD pipeline, and a production AWS blueprint spanning ECS Fargate, RDS PostgreSQL Multi-AZ, and S3."*

- **[1:40 - 2:00] Conclusion & Cultural Fit:**  
  *"This tool was engineered demonstrating technical rigor, speed, and deep alignment with Caprae’s mission to transform ETA through operational AI. I’m eager to bring this horsepower to the Caprae Capital team. Thank you!"*

---

## 📦 Submission Details

- **Role:** Full Stack Developer
- **Target Organization:** Caprae Capital Partners
- **Managing Principal:** Kevin Hong
- **Repository Location:** https://github.com/rajendra0777/Assessment/tree/main/saasquatch-leadgen-pro
- **Submission Email:** `recruiting@capraecapital.com`
- **Recommended Email Subject:** `Full Stack Developer - Handbook Submission - [Candidate Name]`
