document.addEventListener('DOMContentLoaded', () => {
    // ==========================================
    // APPLICATION STATE
    // ==========================================
    let allLeads = [];
    let filteredLeads = [];
    let currentViewMode = 'kanban'; // 'kanban' or 'table'
    let currentPage = 1;
    let pageSize = 10;
    const API_BASE = '/api';

    // Auth State (Default: Restricted until logged in)
    let jwtToken = localStorage.getItem('caprae_jwt_token');
    let authUser = null;
    try {
        authUser = JSON.parse(localStorage.getItem('caprae_user'));
    } catch (e) {
        authUser = null;
    }

    // ==========================================
    // DOM ELEMENTS - NAVIGATION & AUTH
    // ==========================================
    const brandHomeLink = document.getElementById('brand-home-link');
    const navTabs = document.querySelectorAll('.nav-tab');
    const tabContents = document.querySelectorAll('.tab-content');
    const navUserName = document.getElementById('nav-user-name');
    const navUserRole = document.getElementById('nav-user-role');
    const btnAuthAction = document.getElementById('btn-auth-action');
    const userPillContainer = document.getElementById('user-pill-container');

    // Login Gate Elements
    const loginGateModal = document.getElementById('login-gate-modal');
    const loginGateForm = document.getElementById('login-gate-form');
    const loginUsername = document.getElementById('login-username');
    const loginPassword = document.getElementById('login-password');
    const loginGateError = document.getElementById('login-gate-error');
        
    // DOM Elements - Pipeline View & Filters
    const kanbanView = document.getElementById('kanban-view');
    const tableView = document.getElementById('table-view');
    const btnViewKanban = document.getElementById('btn-view-kanban');
    const btnViewTable = document.getElementById('btn-view-table');
    const leadsTableBody = document.getElementById('leads-table-body');
    const filterSearch = document.getElementById('filter-search');
    const filterIndustry = document.getElementById('filter-industry');
    const filterStatus = document.getElementById('filter-status');
    const filterPriority = document.getElementById('filter-priority');
    const filterMinScore = document.getElementById('filter-min-score');
    const scoreValLabel = document.getElementById('score-val-label');
    const btnResetFilters = document.getElementById('btn-reset-filters');

    // Pagination Elements
    const paginationStart = document.getElementById('pagination-start');
    const paginationEnd = document.getElementById('pagination-end');
    const paginationTotal = document.getElementById('pagination-total');
    const paginationPageSize = document.getElementById('pagination-page-size');
    const btnPagePrev = document.getElementById('btn-page-prev');
    const btnPageNext = document.getElementById('btn-page-next');
    const paginationNumbers = document.getElementById('pagination-numbers');

    // KPI Elements
    const kpiTotalLeads = document.getElementById('kpi-total-leads');
    const kpiPipelineRev = document.getElementById('kpi-pipeline-rev');
    const kpiAvgScore = document.getElementById('kpi-avg-score');
    const kpiQualifiedLeads = document.getElementById('kpi-qualified-leads');

    // Scraper Elements
    const aiPromptInput = document.getElementById('ai-scraper-prompt-input');
    const btnRunPromptScrape = document.getElementById('btn-run-prompt-scrape');
    const promptChips = document.querySelectorAll('.prompt-chip');
    const scrapeTarget = document.getElementById('scrape-target');
    const scrapeIndustry = document.getElementById('scrape-industry');
    const scrapeRegion = document.getElementById('scrape-region');
    const scrapeAutosave = document.getElementById('scrape-autosave');
    const scrapeAiEval = document.getElementById('scrape-ai-eval');
    const btnRunScrape = document.getElementById('btn-run-scrape');
    const scrapeTerminal = document.getElementById('scrape-terminal');
    const terminalOutput = document.getElementById('terminal-output');
    const scrapeResultsContainer = document.getElementById('scrape-results-container');
    const upcomingDealsRadar = document.getElementById('upcoming-deals-radar');
    const sourcingStageDistribution = document.getElementById('sourcing-stage-distribution');

    // AI Copilot Subtabs
    const subTabs = document.querySelectorAll('.sub-tab');
    const subtabContents = document.querySelectorAll('.subtab-content');
    const ragQueryInput = document.getElementById('rag-query-input');
    const btnRunRag = document.getElementById('btn-run-rag');
    const ragResponseCard = document.getElementById('rag-response-card');
    const ragSynthesisBody = document.getElementById('rag-synthesis-body');
    const ragRetrievedDeals = document.getElementById('rag-retrieved-deals');

    const marketResearchIndustrySelect = document.getElementById('market-research-industry-select');
    const btnRunMarketResearch = document.getElementById('btn-run-market-research');
    const marketResearchResults = document.getElementById('market-research-results');
    const marketTam = document.getElementById('market-tam');
    const marketMultiple = document.getElementById('market-multiple');
    const marketMargin = document.getElementById('market-margin');
    const marketTailwindsList = document.getElementById('market-tailwinds-list');
    const marketRollupList = document.getElementById('market-rollup-list');

    const financeLeadSelect = document.getElementById('finance-lead-select');
    const btnLoadFinancialModel = document.getElementById('btn-load-financial-model');
    const financialModelResults = document.getElementById('financial-model-results');
    const finArr = document.getElementById('fin-arr');
    const finGrossMargin = document.getElementById('fin-gross-margin');
    const finEbitda = document.getElementById('fin-ebitda');
    const finSde = document.getElementById('fin-sde');
    const finDealStructuresContainer = document.getElementById('fin-deal-structures-container');
    const finProjArr = document.getElementById('fin-proj-arr');
    const finProjEbitda = document.getElementById('fin-proj-ebitda');
    const finProjExit = document.getElementById('fin-proj-exit');
    const finProjMoic = document.getElementById('fin-proj-moic');

    // Outreach Sequence Studio Elements
    const outreachLeadSelect = document.getElementById('outreach-lead-select');
    const outreachTone = document.getElementById('outreach-tone');
    const outreachSender = document.getElementById('outreach-sender');
    const btnGenerateOutreach = document.getElementById('btn-generate-outreach');
    const emailSubjectLine = document.getElementById('email-subject-line');
    const emailBodyText = document.getElementById('email-body-text');
    const emailSubjectLine2 = document.getElementById('email-subject-line-2');
    const emailDay3Body = document.getElementById('email-day3-body');
    const emailDay7Body = document.getElementById('email-day7-body');
    const btnCopyFullSequence = document.getElementById('btn-copy-full-sequence');
    const btnDownloadSequence = document.getElementById('btn-download-sequence');

    // Analytics Chart Elements
    const analyticsSortBy = document.getElementById('analytics-sort-by');
    const chartTooltip = document.getElementById('chart-tooltip');

    // Modals
    const dealDetailsModal = document.getElementById('deal-details-modal');
    const editDealModal = document.getElementById('edit-deal-modal');
    const editDealForm = document.getElementById('edit-deal-form');
    const connectModal = document.getElementById('connect-modal');
    const convoModal = document.getElementById('convo-modal');
    const quoteModal = document.getElementById('quote-modal');
    const createModal = document.getElementById('create-modal');
    const btnOpenCreateModal = document.getElementById('btn-open-create-modal');
    const toast = document.getElementById('toast');

    // Export & API
    const btnExportCsv = document.getElementById('btn-export-csv');
    const btnExportJson = document.getElementById('btn-export-json');
    const btnCopyKey = document.getElementById('btn-copy-key');

    // ==========================================
    // INITIALIZATION & AUTH GATE CHECK
    // ==========================================
    checkAuthSession();
    initNavigationTabs();
    initSubTabs();
    setupEventListeners();
    setupDragAndDrop();

    // ==========================================
    // AUTHENTICATION & RESTRICTION GATE
    // ==========================================
    function checkAuthSession() {
        const path = window.location.pathname.toLowerCase().replace(/\/+$/, '') || '/';
        if (!jwtToken || !authUser) {
            // First time visit or signed out: strictly restrict user with Login Gate
            loginGateModal.classList.remove('hidden');
            if (path === '/' || path === '' || path === '/login') {
                window.history.replaceState({ isLogin: true }, '', '/login');
            }
            navUserName.textContent = 'Restricted';
            navUserRole.textContent = 'LOCKED';
            btnAuthAction.textContent = 'Sign In';
            btnAuthAction.onclick = () => {
                loginGateModal.classList.remove('hidden');
                if (window.location.pathname !== '/login') {
                    window.history.pushState({ isLogin: true }, '', '/login');
                }
            };
        } else {
            // User session active
            loginGateModal.classList.add('hidden');
            navUserName.textContent = authUser.fullName || authUser.username;
            navUserRole.textContent = authUser.role === 'ROLE_ADMIN' ? 'ADMIN' : 'OPERATOR';
            btnAuthAction.textContent = 'Sign Out';
            btnAuthAction.onclick = signOut;
            if (authUser.apiKey) {
                const apiKeyElem = document.getElementById('api-key-display');
                if (apiKeyElem) apiKeyElem.textContent = authUser.apiKey;
            }
            loadDashboardMetrics();
            loadLeads();
        }
    }

    async function handleLogin(username, password) {
        loginGateError.classList.add('hidden');
        const submitBtn = document.getElementById('btn-submit-login');
        submitBtn.disabled = true;
        submitBtn.textContent = 'Authenticating JWT...';

        try {
            const res = await fetch(`${API_BASE}/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });

            if (!res.ok) {
                throw new Error('Invalid enterprise credentials. Please check your username and password.');
            }

            const data = await res.json();
            jwtToken = data.token;
            authUser = data;
            localStorage.setItem('caprae_jwt_token', jwtToken);
            localStorage.setItem('caprae_user', JSON.stringify(authUser));

            loginGateModal.classList.add('hidden');
            updateAuthUI();
            showToast(`Welcome, ${data.fullName || data.username}! Access granted.`);

            // Navigate from /login to /deal-pipeline on successful login
            const curPath = window.location.pathname.toLowerCase().replace(/\/+$/, '') || '/';
            if (curPath === '/login' || curPath === '/') {
                activateTab('pipeline-tab', true);
            }

            loadDashboardMetrics();
            loadLeads();
        } catch (err) {
            loginGateError.textContent = err.message;
            loginGateError.classList.remove('hidden');
        } finally {
            submitBtn.disabled = false;
            submitBtn.textContent = 'Authenticate & Unlock Portal';
        }
    }

    function signOut() {
        jwtToken = null;
        authUser = null;
        localStorage.removeItem('caprae_jwt_token');
        localStorage.removeItem('caprae_user');
        allLeads = [];
        filteredLeads = [];
        updateAuthUI();
        showToast('Signed out securely. Please log in to continue.');
        loginGateModal.classList.remove('hidden');
        if (window.location.pathname !== '/login') {
            window.history.pushState({ isLogin: true }, '', '/login');
        }
    }

    function updateAuthUI() {
        if (jwtToken && authUser) {
            navUserName.textContent = authUser.fullName || authUser.username;
            navUserRole.textContent = authUser.role === 'ROLE_ADMIN' ? 'ADMIN' : 'OPERATOR';
            btnAuthAction.textContent = 'Sign Out';
            btnAuthAction.onclick = signOut;
            if (authUser.apiKey) {
                const apiKeyElem = document.getElementById('api-key-display');
                if (apiKeyElem) apiKeyElem.textContent = authUser.apiKey;
            }
        } else {
            navUserName.textContent = 'Restricted';
            navUserRole.textContent = 'LOCKED';
            btnAuthAction.textContent = 'Sign In';
            btnAuthAction.onclick = () => {
                loginGateModal.classList.remove('hidden');
                if (window.location.pathname !== '/login') {
                    window.history.pushState({ isLogin: true }, '', '/login');
                }
            };
        }
    }

    if (loginGateForm) {
        loginGateForm.addEventListener('submit', (e) => {
            e.preventDefault();
            handleLogin(loginUsername.value.trim(), loginPassword.value);
        });
    }

    const btnFillAdmin = document.getElementById('btn-fill-admin');
    const btnFillSearcher = document.getElementById('btn-fill-searcher');
    if (btnFillAdmin) {
        btnFillAdmin.addEventListener('click', () => {
            loginUsername.value = 'admin';
            loginPassword.value = 'ADMIN';
        });
    }
    if (btnFillSearcher) {
        btnFillSearcher.addEventListener('click', () => {
            loginUsername.value = 'searcher';
            loginPassword.value = 'ADMIN';
        });
    }

    if (userPillContainer) {
        userPillContainer.addEventListener('click', () => {
            if (!jwtToken) {
                loginGateModal.classList.remove('hidden');
                if (window.location.pathname !== '/login') {
                    window.history.pushState({ isLogin: true }, '', '/login');
                }
            }
        });
    }

    // Provenance badge: makes simulated or demo records visible so estimates are never mistaken for verified facts.
    function provBadge(lead) {
        const map = {
            SEED_DEMO: ['DEMO DATA', 'Bundled demo record, not a real company'],
            SIMULATED_FALLBACK: ['SIMULATED', 'The site could not be fetched; this profile was generated and is unverified'],
            SIMULATED_DISCOVERY: ['SIMULATED', 'Generated candidate for demonstration, not a real company'],
            LIVE_SCRAPED: ['LIVE SCRAPE', 'Name, description, tech stack and emails come from the public site. Revenue, EBITDA, headcount and score are model ESTIMATES']
        };
        const m = map[lead.dataProvenance];
        if (!m) return '';
        return ` <span class="prov-badge prov-${escapeHtml(lead.dataProvenance)}" title="${escapeHtml(m[1])}">${m[0]}</span>`;
    }

    async function downloadExport(format) {
        try {
            const res = await apiFetch(`${API_BASE}/leads/export/${format}`);
            if (!res.ok) throw new Error('Export failed');
            const blob = await res.blob();
            const a = document.createElement('a');
            a.href = URL.createObjectURL(blob);
            a.download = `deal-export.${format}`;
            document.body.appendChild(a);
            a.click();
            a.remove();
            URL.revokeObjectURL(a.href);
        } catch (err) {
            showToast(err.message, true);
        }
    }

    // Authenticated API Helper
    async function apiFetch(endpoint, options = {}) {
        const headers = options.headers || {};
        headers['Content-Type'] = 'application/json';
        if (jwtToken) {
            headers['Authorization'] = `Bearer ${jwtToken}`;
        }
        if (authUser && authUser.apiKey) {
            headers['X-API-KEY'] = authUser.apiKey;
        }

        const res = await fetch(endpoint, { ...options, headers });
        if (res.status === 401) {
            signOut();
            throw new Error('Session expired or unauthorized. Please re-authenticate.');
        }
        if (res.status === 403) {
            throw new Error('You do not have permission to perform this action.');
        }
        return res;
    }

    // ==========================================
    // NAVIGATION & SINGLE PAGE RESPONSE ROUTING
    // ==========================================
    // ==========================================
    // DYNAMIC URL ROUTING & QUERY MANAGEMENT
    // ==========================================
    const tabRoutes = {
        'pipeline-tab': '/deal-pipeline',
        'scraper-tab': '/real-time-sourcing',
        'copilot-tab': '/ai-deal-copilot',
        'outreach-tab': '/outreach-studio',
        'analytics-tab': '/pipeline-analytics',
        'api-tab': '/api-docs'
    };

    const routeToTab = {
        '/deal-pipeline': 'pipeline-tab',
        '/real-time-sourcing': 'scraper-tab',
        '/ai-deal-copilot': 'copilot-tab',
        '/outreach-studio': 'outreach-tab',
        '/pipeline-analytics': 'analytics-tab',
        '/api-docs': 'api-tab',
        '/api-and-docs': 'api-tab',
        '/': 'pipeline-tab',
        '/index.html': 'pipeline-tab'
    };

    const statusSlugToVal = {
        'sourced': 'SOURCED',
        'qualified': 'QUALIFIED',
        'diligence': 'DILIGENCE',
        'in-diligence': 'DILIGENCE',
        'loi-submitted': 'LOI_SUBMITTED',
        'loi_submitted': 'LOI_SUBMITTED',
        'loi': 'LOI_SUBMITTED',
        'closed': 'CLOSED'
    };

    const statusValToSlug = {
        'SOURCED': 'sourced',
        'QUALIFIED': 'qualified',
        'DILIGENCE': 'diligence',
        'LOI_SUBMITTED': 'loi-submitted',
        'CLOSED': 'closed'
    };

    const subtabSlugMap = {
        'subtab-rag': 'rag',
        'subtab-market': 'market-research',
        'subtab-finance': 'financial-model'
    };

    const slugToSubtabMap = {
        'rag': 'subtab-rag',
        'market-research': 'subtab-market',
        'tam': 'subtab-market',
        'benchmarks': 'subtab-market',
        'financial-model': 'subtab-finance',
        'finance': 'subtab-finance',
        'lbo': 'subtab-finance'
    };

    let pendingModalDealId = null;
    let pendingOutreachLeadId = null;

    function parseCurrentRoute() {
        const fullPath = window.location.pathname.toLowerCase().replace(/\/+$/, '') || '/';
        const searchParams = new URLSearchParams(window.location.search);
        const parts = fullPath.split('/').filter(Boolean);

        const mainSegment = parts[0] || '';
        const subSegment = parts[1] || '';

        return { fullPath, mainSegment, subSegment, searchParams };
    }

    function updatePipelineUrl(push = true) {
        if (dealDetailsModal && !dealDetailsModal.classList.contains('hidden')) {
            return;
        }

        let path = '/deal-pipeline';
        const statusVal = filterStatus ? filterStatus.value : '';
        if (statusVal && statusValToSlug[statusVal]) {
            path += `/${statusValToSlug[statusVal]}`;
        }

        const params = new URLSearchParams();
        if (filterSearch && filterSearch.value.trim()) params.set('q', filterSearch.value.trim());
        if (filterIndustry && filterIndustry.value) params.set('industry', filterIndustry.value);
        if (filterPriority && filterPriority.value) params.set('priority', filterPriority.value);
        if (filterMinScore && parseInt(filterMinScore.value, 10) > 0) params.set('minScore', filterMinScore.value);

        const qs = params.toString() ? `?${params.toString()}` : '';
        const fullUrl = `${path}${qs}`;
        if (window.location.pathname + window.location.search !== fullUrl) {
            if (push) {
                window.history.pushState({ tabId: 'pipeline-tab', statusVal }, '', fullUrl);
            } else {
                window.history.replaceState({ tabId: 'pipeline-tab', statusVal }, '', fullUrl);
            }
        }
    }

    function initNavigationTabs() {
        // "CAPRAE CAPITAL" redirect to home page (Deal Pipeline)
        if (brandHomeLink) {
            brandHomeLink.addEventListener('click', () => {
                activateTab('pipeline-tab', true);
            });
        }

        // Header Navigation Tabs with dynamic routes
        navTabs.forEach(tab => {
            tab.addEventListener('click', () => {
                const targetTabId = tab.getAttribute('data-tab');
                activateTab(targetTabId, true);
            });
        });

        // Footer quick links
        document.querySelectorAll('.footer-nav-link').forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const tabId = link.getAttribute('data-tab') || 'pipeline-tab';
                activateTab(tabId, true);
            });
        });

        // Browser Back/Forward history support
        window.addEventListener('popstate', (e) => {
            const { fullPath, mainSegment, subSegment, searchParams } = parseCurrentRoute();
            if (fullPath === '/login') {
                loginGateModal.classList.remove('hidden');
                return;
            } else {
                loginGateModal.classList.add('hidden');
            }

            if (mainSegment === 'deal-pipeline') {
                activateTab('pipeline-tab', false);
                if (subSegment && (subSegment.toLowerCase().startsWith('cp-') || /^\d+$/.test(subSegment))) {
                    pendingModalDealId = subSegment;
                    checkPendingDealModal();
                } else {
                    if (dealDetailsModal) dealDetailsModal.classList.add('hidden');
                    if (subSegment && statusSlugToVal[subSegment.toLowerCase()]) {
                        filterStatus.value = statusSlugToVal[subSegment.toLowerCase()];
                    } else {
                        filterStatus.value = '';
                    }
                    if (searchParams.get('q')) filterSearch.value = searchParams.get('q');
                    else filterSearch.value = '';
                    applyFilters(false);
                }
                return;
            }

            if (dealDetailsModal) dealDetailsModal.classList.add('hidden');

            const targetTab = (e.state && e.state.tabId) || routeToTab[fullPath] || routeToTab['/' + mainSegment] || 'pipeline-tab';
            activateTab(targetTab, false);
        });

        // Resolve current URL route on initial page load
        handleInitialRoute();
    }

    function handleInitialRoute() {
        const { fullPath, mainSegment, subSegment, searchParams } = parseCurrentRoute();

        if (fullPath === '/login') {
            if (!jwtToken) {
                loginGateModal.classList.remove('hidden');
                window.history.replaceState({ isLogin: true }, '', '/login');
            } else {
                activateTab('pipeline-tab', false);
                window.history.replaceState({ tabId: 'pipeline-tab' }, '', '/deal-pipeline');
            }
            return;
        }

        // Deal Pipeline: /deal-pipeline or /deal-pipeline/<status> or /deal-pipeline/<deal-id>
        if (mainSegment === 'deal-pipeline') {
            activateTab('pipeline-tab', false);
            handlePipelineSubroute(subSegment, searchParams);
            return;
        }

        // Real-Time Sourcing: /real-time-sourcing
        if (mainSegment === 'real-time-sourcing') {
            activateTab('scraper-tab', false);
            if (searchParams.get('prompt') || searchParams.get('q')) {
                aiPromptInput.value = searchParams.get('prompt') || searchParams.get('q');
            }
            if (searchParams.get('industry') && scrapeIndustry) {
                scrapeIndustry.value = searchParams.get('industry');
            }
            if (searchParams.get('region') && scrapeRegion) {
                scrapeRegion.value = searchParams.get('region');
            }
            return;
        }

        // AI Deal Copilot: /ai-deal-copilot or /ai-deal-copilot/<subtab>
        if (mainSegment === 'ai-deal-copilot') {
            activateTab('copilot-tab', false);
            if (subSegment && slugToSubtabMap[subSegment]) {
                activateSubTab(slugToSubtabMap[subSegment], false);
            }
            return;
        }

        // Outreach Studio: /outreach-studio or /outreach-studio/<deal-id>
        if (mainSegment === 'outreach-studio') {
            activateTab('outreach-tab', false);
            if (subSegment || searchParams.get('leadId')) {
                pendingOutreachLeadId = subSegment || searchParams.get('leadId');
            }
            return;
        }

        // Pipeline Analytics: /pipeline-analytics
        if (mainSegment === 'pipeline-analytics') {
            activateTab('analytics-tab', false);
            if (searchParams.get('sortBy') && analyticsSortBy) {
                analyticsSortBy.value = searchParams.get('sortBy');
                renderAnalyticsVisuals();
            }
            return;
        }

        // API & Docs: /api-docs or /api-and-docs
        if (mainSegment === 'api-docs' || mainSegment === 'api-and-docs') {
            activateTab('api-tab', false);
            return;
        }

        const initialTab = routeToTab[fullPath] || 'pipeline-tab';
        activateTab(initialTab, false);
        const canonicalRoute = tabRoutes[initialTab] || '/deal-pipeline';
        window.history.replaceState({ tabId: initialTab }, '', canonicalRoute);
    }

    function handlePipelineSubroute(subSegment, searchParams) {
        if (!subSegment && !searchParams.toString()) {
            return;
        }

        // Case 1: Subsegment is a Deal ID (e.g. CP-0005, cp-1001, 5)
        if (subSegment && (subSegment.toLowerCase().startsWith('cp-') || /^\d+$/.test(subSegment))) {
            pendingModalDealId = subSegment;
            if (allLeads && allLeads.length > 0) {
                checkPendingDealModal();
            }
            return;
        }

        // Case 2: Subsegment is a status filter (e.g. sourced, qualified, diligence, loi-submitted, closed)
        if (subSegment && statusSlugToVal[subSegment.toLowerCase()]) {
            filterStatus.value = statusSlugToVal[subSegment.toLowerCase()];
        }

        if (searchParams.get('q')) filterSearch.value = searchParams.get('q');
        if (searchParams.get('industry')) filterIndustry.value = searchParams.get('industry');
        if (searchParams.get('priority')) filterPriority.value = searchParams.get('priority');
        if (searchParams.get('minScore')) {
            filterMinScore.value = searchParams.get('minScore');
            scoreValLabel.textContent = searchParams.get('minScore');
        }

        if (allLeads && allLeads.length > 0) {
            applyFilters(false);
        }
    }

    function checkPendingDealModal() {
        if (!pendingModalDealId || !allLeads || allLeads.length === 0) return;
        const normalized = pendingModalDealId.toLowerCase();
        const idNum = normalized.replace(/^cp-/i, '');
        const target = allLeads.find(l =>
            String(l.id) === idNum ||
            `cp-${String(l.id).padStart(4, '0')}`.toLowerCase() === normalized
        );
        if (target) {
            openDealDetailsModal(target, false);
            pendingModalDealId = null;
        }
    }

    function checkPendingOutreach() {
        if (!pendingOutreachLeadId || !allLeads || allLeads.length === 0) return;
        const normalized = pendingOutreachLeadId.toLowerCase();
        const idNum = normalized.replace(/^cp-/i, '');
        const target = allLeads.find(l =>
            String(l.id) === idNum ||
            `cp-${String(l.id).padStart(4, '0')}`.toLowerCase() === normalized
        );
        if (target && outreachLeadSelect) {
            outreachLeadSelect.value = target.id;
            const email = getLeadContactEmail(target);
            for (let i = 1; i <= 3; i++) {
                const recElem = document.getElementById(`email-recipient-${i}`);
                if (recElem) recElem.value = email;
            }
            updateMailtoLinks();
            btnGenerateOutreach.click();
            pendingOutreachLeadId = null;
        }
    }

    function activateTab(tabId, updateUrl = true) {
        navTabs.forEach(t => {
            if (t.getAttribute('data-tab') === tabId) t.classList.add('active');
            else t.classList.remove('active');
        });

        tabContents.forEach(c => {
            if (c.id === tabId) c.classList.add('active');
            else c.classList.remove('active');
        });

        if (tabId !== 'pipeline-tab' && dealDetailsModal) {
            dealDetailsModal.classList.add('hidden');
        }

        // Update URL dynamically in address bar
        if (updateUrl) {
            if (tabId === 'pipeline-tab') {
                updatePipelineUrl(true);
            } else if (tabRoutes[tabId]) {
                const newRoute = tabRoutes[tabId];
                if (window.location.pathname !== newRoute) {
                    window.history.pushState({ tabId }, '', newRoute);
                }
            }
        }

        // Initialize tab-specific data with 100% accuracy
        if (tabId === 'pipeline-tab') {
            renderPipeline();
        } else if (tabId === 'scraper-tab') {
            renderSourcingRadar();
        } else if (tabId === 'copilot-tab') {
            updateDropdowns(allLeads);
        } else if (tabId === 'outreach-tab') {
            populateOutreachDropdown(allLeads);
        } else if (tabId === 'analytics-tab') {
            renderAnalyticsVisuals();
        } else if (tabId === 'api-tab') {
            if (authUser && authUser.apiKey) {
                const apiKeyElem = document.getElementById('api-key-display');
                if (apiKeyElem) apiKeyElem.textContent = authUser.apiKey;
            }
        }

        window.scrollTo({ top: 0, behavior: 'smooth' });
    }

    function activateSubTab(subtabId, updateUrl = true) {
        subTabs.forEach(t => {
            if (t.getAttribute('data-subtab') === subtabId) t.classList.add('active');
            else t.classList.remove('active');
        });
        subtabContents.forEach(c => {
            if (c.id === subtabId) c.classList.add('active');
            else c.classList.remove('active');
        });

        if (updateUrl && subtabSlugMap[subtabId]) {
            const subSlug = subtabSlugMap[subtabId];
            const newUrl = `/ai-deal-copilot/${subSlug}`;
            if (window.location.pathname !== newUrl) {
                window.history.pushState({ tabId: 'copilot-tab', subtabId }, '', newUrl);
            }
        }
    }

    function initSubTabs() {
        subTabs.forEach(tab => {
            tab.addEventListener('click', () => {
                const targetSub = tab.getAttribute('data-subtab');
                activateSubTab(targetSub, true);
            });
        });
    }

    // View Toggle (Kanban vs Table)
    btnViewKanban.addEventListener('click', () => {
        currentViewMode = 'kanban';
        btnViewKanban.classList.add('active');
        btnViewTable.classList.remove('active');
        kanbanView.classList.remove('hidden');
        tableView.classList.add('hidden');
        renderPipeline();
    });

    btnViewTable.addEventListener('click', () => {
        currentViewMode = 'table';
        btnViewTable.classList.add('active');
        btnViewKanban.classList.remove('active');
        tableView.classList.remove('hidden');
        kanbanView.classList.add('hidden');
        renderPipeline();
    });

    // ==========================================
    // METRICS & KPIS LOADING
    // ==========================================
    async function loadDashboardMetrics() {
        try {
            const res = await apiFetch(`${API_BASE}/metrics/dashboard`);
            if (!res.ok) return;
            const data = await res.json();

            kpiTotalLeads.textContent = data.totalLeads;
            kpiPipelineRev.textContent = formatCurrency(data.totalPipelineRevenue);
            kpiAvgScore.textContent = (data.avgDealScore ? data.avgDealScore : 88) + ' / 100';
            kpiQualifiedLeads.textContent = (data.qualifiedLeadsCount + data.inDiligenceCount) + ' Deals';

            renderSourcingRadar();
            renderAnalyticsVisuals();
        } catch (err) {
            console.error('Failed to load metrics:', err);
        }
    }

    // ==========================================
    // DEAL PIPELINE RETRIEVAL, SEARCH & PAGINATION
    // ==========================================
    async function loadLeads() {
        try {
            const res = await apiFetch(`${API_BASE}/leads`);
            if (!res.ok) throw new Error('API error loading leads');
            allLeads = await res.json();
            applyFilters(false);
            updateDropdowns(allLeads);
            renderSourcingRadar();
            renderAnalyticsVisuals();

            // Open pending modal deal or outreach lead if routed directly by URL
            checkPendingDealModal();
            checkPendingOutreach();
        } catch (err) {
            console.error(err);
        }
    }

    function applyFilters(updateUrl = true) {
        const query = filterSearch.value.trim().toLowerCase();
        const industry = filterIndustry.value;
        const status = filterStatus.value;
        const priority = filterPriority.value;
        const minScore = parseInt(filterMinScore.value, 10) || 0;

        filteredLeads = allLeads.filter(lead => {
            const dealIdStr = `cp-${String(lead.id).padStart(4, '0')}`.toLowerCase();
            const idNum = String(lead.id);

            // Universal Search Match across ID, Name, Domain, Location, Tech, Description
            if (query) {
                const matchesId = dealIdStr.includes(query) || idNum === query;
                const matchesName = (lead.companyName || '').toLowerCase().includes(query);
                const matchesDomain = (lead.domain || '').toLowerCase().includes(query);
                const matchesLocation = (lead.location || '').toLowerCase().includes(query) || (lead.region || '').toLowerCase().includes(query);
                const matchesTech = (lead.techStack || '').toLowerCase().includes(query);
                const matchesDesc = (lead.description || '').toLowerCase().includes(query);
                const matchesIndustry = (lead.industry || '').toLowerCase().includes(query);

                if (!matchesId && !matchesName && !matchesDomain && !matchesLocation && !matchesTech && !matchesDesc && !matchesIndustry) {
                    return false;
                }
            }

            // Industry Filter
            if (industry && lead.industry !== industry) {
                return false;
            }

            // Status Filter
            if (status && (lead.status || 'SOURCED').toUpperCase() !== status) {
                return false;
            }

            // Priority Filter
            if (priority && (lead.priority || 'WARM') !== priority) {
                return false;
            }

            // Min Score Filter
            if (minScore > 0 && (lead.dealScore || 0) < minScore) {
                return false;
            }

            return true;
        });

        // Reset to page 1 on filter changes
        currentPage = 1;
        renderPipeline();

        if (updateUrl) {
            updatePipelineUrl(true);
        }
    }

    function renderPipeline() {
        const total = filteredLeads.length;
        const startIdx = total === 0 ? 0 : (currentPage - 1) * pageSize;
        const endIdx = Math.min(startIdx + pageSize, total);

        paginationStart.textContent = total === 0 ? 0 : startIdx + 1;
        paginationEnd.textContent = endIdx;
        paginationTotal.textContent = total;

        const pagedLeads = filteredLeads.slice(startIdx, endIdx);

        if (currentViewMode === 'kanban') {
            renderKanban(filteredLeads); // In Kanban board, shows filtered items across stages
        } else {
            renderTable(pagedLeads);
        }

        renderPaginationControls(total);
    }

    function renderPaginationControls(total) {
        const totalPages = Math.ceil(total / pageSize) || 1;
        btnPagePrev.disabled = currentPage <= 1;
        btnPageNext.disabled = currentPage >= totalPages;

        paginationNumbers.innerHTML = '';
        for (let i = 1; i <= totalPages; i++) {
            if (totalPages > 7 && Math.abs(currentPage - i) > 2 && i !== 1 && i !== totalPages) {
                if (i === 2 || i === totalPages - 1) {
                    const dots = document.createElement('span');
                    dots.className = 'page-dots';
                    dots.textContent = '...';
                    paginationNumbers.appendChild(dots);
                }
                continue;
            }
            const btn = document.createElement('button');
            btn.className = `page-btn ${i === currentPage ? 'active' : ''}`;
            btn.textContent = i;
            btn.addEventListener('click', () => {
                currentPage = i;
                renderPipeline();
            });
            paginationNumbers.appendChild(btn);
        }
    }

    // ==========================================
    // KANBAN BOARD VIEW (DRAG & DROP + 3-DOTS)
    // ==========================================
    function renderKanban(leads) {
        const stages = ['SOURCED', 'QUALIFIED', 'DILIGENCE', 'LOI_SUBMITTED', 'CLOSED'];
        const stageColMap = {
            'SOURCED': 'sourced',
            'QUALIFIED': 'qualified',
            'DILIGENCE': 'diligence',
            'LOI_SUBMITTED': 'loi',
            'CLOSED': 'closed'
        };

        stages.forEach(st => {
            const colKey = stageColMap[st] || st.toLowerCase().replace(/_/g, '');
            const colBody = document.getElementById(`col-${colKey}`) || document.getElementById(`col-${st.toLowerCase().replace(/_/g, '')}`);
            const colCount = document.getElementById(`count-${colKey}`) || document.getElementById(`count-${st.toLowerCase().replace(/_/g, '')}`);
            if (!colBody) return;

            const filtered = leads.filter(l => (l.status || 'SOURCED').toUpperCase() === st);
            if (colCount) colCount.textContent = filtered.length;
            colBody.innerHTML = '';

            filtered.forEach(lead => {
                const card = createDealCard(lead);
                colBody.appendChild(card);
            });
        });
    }

    function createDealCard(lead) {
        const card = document.createElement('div');
        card.className = 'deal-card';
        card.setAttribute('draggable', 'true');
        card.setAttribute('data-lead-id', lead.id);

        const dealIdStr = `CP-${String(lead.id).padStart(4, '0')}`;
        const priority = lead.priority || 'WARM';
        const priorityClass = priority === 'HOT' ? 'priority-hot' : (priority === 'COLD' ? 'priority-cold' : 'priority-warm');
        const priorityIcon = priority === 'HOT' ? '🔥' : (priority === 'COLD' ? '❄️' : '⚡');
        const winProb = lead.winProbability || 65;

        card.innerHTML = `
            <div class="deal-card-top-row">
                <span class="deal-id-tag">${dealIdStr}</span>
                <div class="flex-align-center gap-6">
                    <span class="priority-badge ${priorityClass}">${priorityIcon} ${priority}</span>
                    <button class="btn-card-more" title="Deal Actions (CRUD &amp; Stages)">⋮</button>
                </div>
            </div>

            <div class="card-company-title" style="cursor:pointer;" title="Click to view full company details">${escapeHtml(lead.companyName)}${provBadge(lead)}</div>
            <a href="${escapeHtml(lead.websiteUrl || 'https://' + lead.domain)}" target="_blank" class="card-domain">${escapeHtml(lead.domain)} ↗</a>

            <div class="card-metrics-grid" style="margin-top: 10px;">
                <div>ARR: <span class="card-metric-val">${lead.estimatedRevenue ? formatCurrency(lead.estimatedRevenue) : '$2.5M'}</span></div>
                <div>EBITDA: <span class="card-metric-val">${lead.estimatedEbitda ? formatCurrency(lead.estimatedEbitda) : '$700K'}</span></div>
            </div>

            <div class="card-win-prob">
                <div class="flex-between">
                    <span>Win Probability</span>
                    <strong>${winProb}%</strong>
                </div>
                <div class="win-prob-bar">
                    <div class="win-prob-fill" style="width: ${winProb}%"></div>
                </div>
            </div>

            <div style="margin-top: 8px;">
                <small class="text-muted">${escapeHtml(lead.region || 'North America')} • ${escapeHtml(lead.industry || 'B2B SaaS')}</small>
            </div>

            <div class="deal-card-actions">
                <button class="card-action-btn btn-connect" data-id="${lead.id}" title="Connect with Founder">📞 Contact</button>
                <button class="card-action-btn btn-summarize" data-id="${lead.id}" title="AI Meeting Notes &amp; Sentiment">📝 Notes</button>
                <button class="card-action-btn btn-quote" data-id="${lead.id}" title="Acquisition Quote">💼 Quote</button>
                <button class="card-action-btn btn-advance" data-id="${lead.id}" title="Advance to Next Stage">➔ Next</button>
            </div>
        `;

        // Card Click opens Full Details Modal
        card.querySelector('.card-company-title').addEventListener('click', (e) => {
            e.stopPropagation();
            openDealDetailsModal(lead);
        });

        // 3-Dot Menu Dropdown Toggle
        const btnMore = card.querySelector('.btn-card-more');
        btnMore.addEventListener('click', (e) => {
            e.stopPropagation();
            toggleActionDropdown(btnMore, lead);
        });

        // Quick buttons
        card.querySelector('.btn-connect').addEventListener('click', (e) => { e.stopPropagation(); openConnectModal(lead); });
        card.querySelector('.btn-summarize').addEventListener('click', (e) => { e.stopPropagation(); openConvoModal(lead); });
        card.querySelector('.btn-quote').addEventListener('click', (e) => { e.stopPropagation(); openQuoteModal(lead); });
        card.querySelector('.btn-advance').addEventListener('click', (e) => { e.stopPropagation(); advanceDealStage(lead); });

        // Drag Events
        card.addEventListener('dragstart', (e) => {
            card.classList.add('is-dragging');
            e.dataTransfer.setData('text/plain', String(lead.id));
            e.dataTransfer.effectAllowed = 'move';
        });

        card.addEventListener('dragend', () => {
            card.classList.remove('is-dragging');
        });

        return card;
    }

    // ==========================================
    // HTML5 DRAG & DROP FOR STAGES
    // ==========================================
    function setupDragAndDrop() {
        const dropZones = document.querySelectorAll('.kanban-col, .kanban-col-body');
        dropZones.forEach(zone => {
            zone.addEventListener('dragover', (e) => {
                e.preventDefault();
                e.dataTransfer.dropEffect = 'move';
                const colBody = zone.classList.contains('kanban-col-body') ? zone : zone.querySelector('.kanban-col-body');
                if (colBody) colBody.classList.add('drag-hover');
            });

            zone.addEventListener('dragleave', (e) => {
                if (!zone.contains(e.relatedTarget)) {
                    const colBody = zone.classList.contains('kanban-col-body') ? zone : zone.querySelector('.kanban-col-body');
                    if (colBody) colBody.classList.remove('drag-hover');
                }
            });

            zone.addEventListener('drop', async (e) => {
                e.preventDefault();
                e.stopPropagation();
                const colBody = zone.classList.contains('kanban-col-body') ? zone : zone.querySelector('.kanban-col-body');
                if (colBody) colBody.classList.remove('drag-hover');

                const leadId = e.dataTransfer.getData('text/plain');
                const targetStage = zone.getAttribute('data-stage') || zone.closest('[data-stage]')?.getAttribute('data-stage') || colBody?.getAttribute('data-stage');
                if (leadId && targetStage) {
                    await updateStatus(parseInt(leadId, 10), targetStage);
                }
            });
        });
    }

    // ==========================================
    // TABLE VIEW (3-DOT CRUD & DETAILS CLICK)
    // ==========================================
    function renderTable(leads) {
        leadsTableBody.innerHTML = '';
        if (!leads || leads.length === 0) {
            leadsTableBody.innerHTML = `<tr><td colspan="8" class="loading-cell">No matching deals found in pipeline.</td></tr>`;
            return;
        }

        leads.forEach(lead => {
            const tr = document.createElement('tr');
            const dealIdStr = `CP-${String(lead.id).padStart(4, '0')}`;
            const priority = lead.priority || 'WARM';
            const priorityClass = priority === 'HOT' ? 'priority-hot' : (priority === 'COLD' ? 'priority-cold' : 'priority-warm');

            tr.innerHTML = `
                <td>
                    <span class="deal-id-tag" style="cursor:pointer;" title="Click to view details">${dealIdStr}</span>
                </td>
                <td>
                    <div class="company-title" style="cursor:pointer;" title="Click to view details">${escapeHtml(lead.companyName)}${provBadge(lead)}</div>
                    <a href="${escapeHtml(lead.websiteUrl || 'https://' + lead.domain)}" target="_blank" class="company-domain">${escapeHtml(lead.domain)} ↗</a>
                    <div style="font-size:11px; color:var(--text-muted);">${escapeHtml(lead.region || 'North America')}</div>
                </td>
                <td>
                    <div>${escapeHtml(lead.industry || 'B2B SaaS')}</div>
                </td>
                <td>
                    <div class="revenue-text">${lead.estimatedRevenue ? formatCurrency(lead.estimatedRevenue) : '$2.5M'}</div>
                    <div class="ebitda-sub">${lead.estimatedEbitda ? 'EBITDA: ' + formatCurrency(lead.estimatedEbitda) : ''}</div>
                </td>
                <td>
                    <span class="priority-badge ${priorityClass}">${priority}</span>
                    <span style="font-size:12px; margin-left:6px;"><strong>${lead.winProbability || 65}%</strong> Win</span>
                </td>
                <td>
                    <select class="status-select" data-id="${lead.id}">
                        <option value="SOURCED" ${lead.status === 'SOURCED' ? 'selected' : ''}>Sourced</option>
                        <option value="QUALIFIED" ${lead.status === 'QUALIFIED' ? 'selected' : ''}>Qualified</option>
                        <option value="DILIGENCE" ${lead.status === 'DILIGENCE' ? 'selected' : ''}>In Diligence</option>
                        <option value="LOI_SUBMITTED" ${lead.status === 'LOI_SUBMITTED' ? 'selected' : ''}>LOI Submitted</option>
                        <option value="CLOSED" ${lead.status === 'CLOSED' ? 'selected' : ''}>Closed</option>
                    </select>
                </td>
                <td>
                    <div class="score-badge ${lead.dealScore >= 85 ? 'score-high' : 'score-mid'}">${lead.dealScore || 75}</div>
                </td>
                <td style="text-align: right; position: relative;">
                    <button class="btn-card-more btn-table-more" data-id="${lead.id}" title="Deal Actions (CRUD &amp; Stages)">⋮</button>
                </td>
            `;

            // Click on Company or Deal ID opens full details
            tr.querySelector('.company-title').addEventListener('click', () => openDealDetailsModal(lead));
            tr.querySelector('.deal-id-tag').addEventListener('click', () => openDealDetailsModal(lead));

            // Status select directly changes status
            tr.querySelector('.status-select').addEventListener('change', async (e) => {
                await updateStatus(lead.id, e.target.value);
            });

            // 3-Dot Action Menu in Table Row
            const btnMore = tr.querySelector('.btn-table-more');
            btnMore.addEventListener('click', (e) => {
                e.stopPropagation();
                toggleActionDropdown(btnMore, lead);
            });

            leadsTableBody.appendChild(tr);
        });
    }

    // ==========================================
    // 3-DOT ACTION MENU DROPDOWN
    // ==========================================
    function toggleActionDropdown(triggerBtn, lead) {
        // Remove existing dropdowns
        document.querySelectorAll('.action-dropdown').forEach(d => d.remove());

        const dropdown = document.createElement('div');
        dropdown.className = 'action-dropdown';

        dropdown.innerHTML = `
            <button class="action-dropdown-item btn-act-details">👁️ View Full Details</button>
            <button class="action-dropdown-item btn-act-edit">✏️ Edit Deal Target</button>
            <div class="action-dropdown-divider"></div>
            <div style="padding: 4px 14px; font-size: 10px; color: var(--text-muted); font-weight: 700; text-transform: uppercase;">Change Deal Stage:</div>
            <button class="action-dropdown-item btn-act-stage" data-stage="SOURCED">➔ Move to Sourced</button>
            <button class="action-dropdown-item btn-act-stage" data-stage="QUALIFIED">➔ Move to Qualified</button>
            <button class="action-dropdown-item btn-act-stage" data-stage="DILIGENCE">➔ Move to In Diligence</button>
            <button class="action-dropdown-item btn-act-stage" data-stage="LOI_SUBMITTED">➔ Move to LOI Submitted</button>
            <button class="action-dropdown-item btn-act-stage" data-stage="CLOSED">➔ Move to Closed</button>
            <div class="action-dropdown-divider"></div>
            <button class="action-dropdown-item btn-act-contact">📞 Connect with Founder</button>
            <button class="action-dropdown-item btn-act-notes">📝 AI Meeting Notes</button>
            <button class="action-dropdown-item btn-act-quote">💼 Generate LOI Quote</button>
            <button class="action-dropdown-item btn-act-outreach">✉️ Draft AI Outreach</button>
            <div class="action-dropdown-divider"></div>
            <button class="action-dropdown-item danger btn-act-delete">🗑️ Delete Deal</button>
        `;

        dropdown.querySelector('.btn-act-details').addEventListener('click', () => { dropdown.remove(); openDealDetailsModal(lead); });
        dropdown.querySelector('.btn-act-edit').addEventListener('click', () => { dropdown.remove(); openEditDealModal(lead); });
        dropdown.querySelector('.btn-act-contact').addEventListener('click', () => { dropdown.remove(); openConnectModal(lead); });
        dropdown.querySelector('.btn-act-notes').addEventListener('click', () => { dropdown.remove(); openConvoModal(lead); });
        dropdown.querySelector('.btn-act-quote').addEventListener('click', () => { dropdown.remove(); openQuoteModal(lead); });
        dropdown.querySelector('.btn-act-outreach').addEventListener('click', () => {
            dropdown.remove();
            activateTab('outreach-tab');
            outreachLeadSelect.value = lead.id;
            btnGenerateOutreach.click();
        });
        dropdown.querySelector('.btn-act-delete').addEventListener('click', () => { dropdown.remove(); deleteDeal(lead); });

        dropdown.querySelectorAll('.btn-act-stage').forEach(btn => {
            btn.addEventListener('click', async () => {
                const stage = btn.getAttribute('data-stage');
                dropdown.remove();
                await updateStatus(lead.id, stage);
            });
        });

        triggerBtn.parentElement.appendChild(dropdown);

        // Click outside closes dropdown
        const closeDropdown = (e) => {
            if (!dropdown.contains(e.target) && e.target !== triggerBtn) {
                dropdown.remove();
                document.removeEventListener('click', closeDropdown);
            }
        };
        setTimeout(() => document.addEventListener('click', closeDropdown), 50);
    }

    // ==========================================
    // FULL COMPANY DEAL DETAILS MODAL
    // ==========================================
    function openDealDetailsModal(lead, push = true) {
        const dealIdStr = `CP-${String(lead.id).padStart(4, '0')}`;
        document.getElementById('detail-deal-id').textContent = dealIdStr;
        document.getElementById('detail-company-title').innerHTML = escapeHtml(lead.companyName) + provBadge(lead);
        document.getElementById('detail-status-badge').textContent = lead.status || 'SOURCED';
        document.getElementById('detail-priority-badge').textContent = (lead.priority || 'WARM') + ' PRIORITY';
        document.getElementById('detail-priority-badge').className = `priority-badge ${lead.priority === 'HOT' ? 'priority-hot' : (lead.priority === 'COLD' ? 'priority-cold' : 'priority-warm')}`;

        document.getElementById('detail-arr').textContent = lead.estimatedRevenue ? formatCurrency(lead.estimatedRevenue) : '$2,500,000';
        document.getElementById('detail-ebitda').textContent = lead.estimatedEbitda ? formatCurrency(lead.estimatedEbitda) : '$750,000';
        document.getElementById('detail-win-prob').textContent = (lead.winProbability || 75) + '%';
        document.getElementById('detail-ai-score').textContent = (lead.dealScore || 85) + ' / 100';
        document.getElementById('detail-fit-badge').textContent = lead.acquisitionFit || 'PRIME TARGET';

        const domainLink = document.getElementById('detail-domain-link');
        domainLink.textContent = lead.domain;
        domainLink.href = lead.websiteUrl || `https://${lead.domain}`;

        document.getElementById('detail-location-text').textContent = (lead.location || 'Remote, North America');
        document.getElementById('detail-employees-text').textContent = (lead.employeeCount ? `${lead.employeeCount} FTEs` : '20-50 FTEs');
        document.getElementById('detail-industry-text').textContent = lead.industry || 'B2B SaaS';
        document.getElementById('detail-description-text').textContent = lead.description || 'Enterprise recurring B2B software platform with robust retention and low churn metrics.';

        // Tech chips
        const chipsContainer = document.getElementById('detail-tech-chips');
        chipsContainer.innerHTML = '';
        if (lead.techStack) {
            lead.techStack.split(',').forEach(t => {
                const chip = document.createElement('span');
                chip.className = 'tech-chip';
                chip.textContent = t.trim();
                chipsContainer.appendChild(chip);
            });
        }

        // Contact details
        const contact = (lead.contacts && lead.contacts.length > 0) ? lead.contacts[0] : null;
        document.getElementById('detail-contact-name').textContent = contact ? contact.fullName : 'Executive Managing Principal';
        document.getElementById('detail-contact-title').textContent = contact ? contact.title : 'Founder & CEO';

        const emailElem = document.getElementById('detail-contact-email');
        const primaryEmail = lead.primaryEmail || (contact ? contact.email : '');
        emailElem.textContent = primaryEmail || 'Not found';
        emailElem.href = primaryEmail ? `mailto:${primaryEmail}` : '#';

        document.getElementById('detail-contact-phone').textContent = lead.primaryPhone || (contact ? contact.phone : '+1 (555) 392-1082');
        document.getElementById('detail-contact-linkedin').href = lead.linkedinCompanyUrl || (contact ? contact.linkedinUrl : '#');

        document.getElementById('detail-growth-thesis').textContent = lead.aiGrowthThesis || 'Prime ETA Target: Strong SaaS recurring gross margin profile, low customer churn, and sticky operational moat.';
        document.getElementById('detail-notes-text').textContent = lead.notes || 'No recent call notes or executive conversation recorded. Click "Notes" to analyze transcription.';
        document.getElementById('detail-quote-text').textContent = lead.quoteDetails || 'No active term sheet or acquisition offer generated. Click "Quote" to attach valuation terms.';

        document.getElementById('btn-detail-edit').onclick = () => {
            closeDealDetailsModal();
            openEditDealModal(lead);
        };

        document.getElementById('btn-detail-outreach').onclick = () => {
            closeDealDetailsModal();
            activateTab('outreach-tab');
            outreachLeadSelect.value = lead.id;
            btnGenerateOutreach.click();
        };

        dealDetailsModal.classList.remove('hidden');

        if (push) {
            const dealIdStr = `CP-${String(lead.id).padStart(4, '0')}`;
            const targetUrl = `/deal-pipeline/${dealIdStr}`;
            if (window.location.pathname !== targetUrl) {
                window.history.pushState({ tabId: 'pipeline-tab', dealId: lead.id, isModal: true }, '', targetUrl);
            }
        }
    }

    function closeDealDetailsModal() {
        if (dealDetailsModal) {
            dealDetailsModal.classList.add('hidden');
        }
        updatePipelineUrl(true);
    }

    // ==========================================
    // EDIT DEAL TARGET MODAL
    // ==========================================
    function openEditDealModal(lead) {
        document.getElementById('edit-deal-id-display').textContent = `CP-${String(lead.id).padStart(4, '0')}`;
        document.getElementById('edit-lead-id').value = lead.id;
        document.getElementById('edit-company').value = lead.companyName || '';
        document.getElementById('edit-domain').value = lead.domain || '';
        document.getElementById('edit-industry').value = lead.industry || 'B2B SaaS';
        document.getElementById('edit-location').value = lead.location || '';
        document.getElementById('edit-region').value = lead.region || 'North America';
        document.getElementById('edit-arr').value = lead.estimatedRevenue || 2500000;
        document.getElementById('edit-ebitda').value = lead.estimatedEbitda || 750000;
        document.getElementById('edit-priority').value = lead.priority || 'WARM';
        document.getElementById('edit-status').value = lead.status || 'SOURCED';
        document.getElementById('edit-email').value = lead.primaryEmail || '';
        document.getElementById('edit-tech').value = lead.techStack || '';
        document.getElementById('edit-desc').value = lead.description || '';

        editDealModal.classList.remove('hidden');
    }

    if (editDealForm) {
        editDealForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const id = document.getElementById('edit-lead-id').value;
            const payload = {
                companyName: document.getElementById('edit-company').value.trim(),
                domain: document.getElementById('edit-domain').value.trim(),
                industry: document.getElementById('edit-industry').value.trim(),
                location: document.getElementById('edit-location').value.trim(),
                region: document.getElementById('edit-region').value.trim(),
                estimatedRevenue: parseFloat(document.getElementById('edit-arr').value) || 2500000,
                estimatedEbitda: parseFloat(document.getElementById('edit-ebitda').value) || 750000,
                priority: document.getElementById('edit-priority').value,
                status: document.getElementById('edit-status').value,
                primaryEmail: document.getElementById('edit-email').value.trim(),
                techStack: document.getElementById('edit-tech').value.trim(),
                description: document.getElementById('edit-desc').value.trim()
            };

            try {
                const res = await apiFetch(`${API_BASE}/leads/${id}`, {
                    method: 'PUT',
                    body: JSON.stringify(payload)
                });

                if (!res.ok) throw new Error('Failed to update deal');
                showToast(`Deal target updated successfully!`);
                editDealModal.classList.add('hidden');
                loadLeads();
                loadDashboardMetrics();
            } catch (err) {
                showToast('Update error: ' + err.message, true);
            }
        });
    }

    // ==========================================
    // DELETE DEAL TARGET
    // ==========================================
    async function deleteDeal(lead) {
        const confirmDelete = confirm(`Are you sure you want to delete "${lead.companyName}" (CP-${String(lead.id).padStart(4, '0')}) from the pipeline?`);
        if (!confirmDelete) return;

        try {
            const res = await apiFetch(`${API_BASE}/leads/${lead.id}`, {
                method: 'DELETE'
            });
            if (res.ok || res.status === 204) {
                showToast(`Deal "${lead.companyName}" removed from pipeline`);
                loadLeads();
                loadDashboardMetrics();
            } else {
                throw new Error('Deletion failed');
            }
        } catch (e) {
            showToast('Delete error: ' + e.message, true);
        }
    }

    // ==========================================
    // STAGE UPDATES
    // ==========================================
    async function advanceDealStage(lead) {
        const flow = ['SOURCED', 'QUALIFIED', 'DILIGENCE', 'LOI_SUBMITTED', 'CLOSED'];
        const currentIdx = flow.indexOf((lead.status || 'SOURCED').toUpperCase());
        if (currentIdx < flow.length - 1) {
            const nextStage = flow[currentIdx + 1];
            await updateStatus(lead.id, nextStage);
        } else {
            showToast('Deal is already in the final Closed stage!');
        }
    }

    async function updateStatus(id, newStatus) {
        try {
            const res = await apiFetch(`${API_BASE}/leads/${id}/status`, {
                method: 'PATCH',
                body: JSON.stringify({ status: newStatus })
            });
            if (res.ok) {
                showToast(`Deal moved to ${newStatus}`);
                loadLeads();
                loadDashboardMetrics();
            }
        } catch (e) {
            showToast('Failed to update stage', true);
        }
    }

    // ==========================================
    // MODAL DIALOGS (CONNECT, CONVO, QUOTE)
    // ==========================================
    function openConnectModal(lead) {
        document.getElementById('connect-company-name').textContent = lead.companyName;
        document.getElementById('connect-location').textContent = (lead.region || 'North America') + ' • ' + (lead.location || 'Remote');

        const primaryEmail = lead.primaryEmail || (lead.contacts && lead.contacts.length > 0 ? lead.contacts[0].email : '') || '';
        const phone = lead.primaryPhone || (lead.contacts && lead.contacts.length > 0 ? lead.contacts[0].phone : '') || '';
        const linkedin = lead.linkedinCompanyUrl || '#';

        document.getElementById('connect-owner-name').textContent = lead.contacts && lead.contacts.length > 0 ? lead.contacts[0].fullName + ' (' + lead.contacts[0].title + ')' : 'Founder & Principal Executive';
        document.getElementById('connect-email-text').textContent = primaryEmail || 'Not found';
        document.getElementById('connect-email-link').href = !primaryEmail ? '#' : `mailto:${primaryEmail}?subject=Confidential Inquiry: ${lead.companyName} & Caprae Capital`;
        document.getElementById('connect-phone-text').textContent = phone || 'Not found';
        document.getElementById('connect-phone-link').href = phone ? `tel:${phone}` : '#';
        document.getElementById('connect-linkedin-link').href = linkedin;

        document.getElementById('btn-quick-outreach-jump').onclick = () => {
            connectModal.classList.add('hidden');
            activateTab('outreach-tab');
            outreachLeadSelect.value = lead.id;
            btnGenerateOutreach.click();
        };

        connectModal.classList.remove('hidden');
    }

    function openConvoModal(lead) {
        document.getElementById('convo-lead-id').value = lead.id;
        document.getElementById('convo-company-display').value = `${lead.companyName} (${lead.domain})`;
        document.getElementById('convo-text-input').value = lead.notes || '';
        document.getElementById('convo-analysis-result').classList.add('hidden');
        convoModal.classList.remove('hidden');
    }

    document.getElementById('convo-summary-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const leadId = document.getElementById('convo-lead-id').value;
        const text = document.getElementById('convo-text-input').value;

        showToast('AI analyzing conversation notes...');
        try {
            const res = await apiFetch(`${API_BASE}/ai/summarize-conversation`, {
                method: 'POST',
                body: JSON.stringify({ leadId, conversationText: text })
            });
            if (!res.ok) throw new Error('Failed to analyze');
            const data = await res.json();

            const resultCard = document.getElementById('convo-analysis-result');
            resultCard.classList.remove('hidden');
            document.getElementById('convo-detected-priority').textContent = data.detectedPriority + ' DEAL';
            document.getElementById('convo-detected-sentiment').textContent = data.sentiment;
            document.getElementById('convo-detected-prob').textContent = data.winProbabilityPct + '%';
            document.getElementById('convo-summary-output').textContent = data.executiveSummary;

            showToast('Deal updated with AI conversation analysis!');
            loadLeads();
            loadDashboardMetrics();
        } catch (err) {
            showToast('Analysis error: ' + err.message, true);
        }
    });

    function openQuoteModal(lead) {
        document.getElementById('quote-lead-id').value = lead.id;
        document.getElementById('quote-company-display').value = `${lead.companyName} (${lead.domain})`;
        const defaultVal = lead.estimatedEbitda ? Math.round(lead.estimatedEbitda * 5.8) : 6500000;
        document.getElementById('quote-val-input').value = defaultVal;
        document.getElementById('quote-result-card').classList.add('hidden');
        quoteModal.classList.remove('hidden');
    }

    document.getElementById('quote-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const leadId = document.getElementById('quote-lead-id').value;
        const val = parseFloat(document.getElementById('quote-val-input').value) || 6000000;
        const struct = document.getElementById('quote-structure-select').value;

        showToast('Generating LOI term sheet quote...');
        try {
            const res = await apiFetch(`${API_BASE}/ai/generate-quote`, {
                method: 'POST',
                body: JSON.stringify({ leadId, structure: struct, valuationOffer: val })
            });
            if (!res.ok) throw new Error('Quote failed');
            const data = await res.json();

            const resultCard = document.getElementById('quote-result-card');
            resultCard.classList.remove('hidden');
            document.getElementById('quote-details-output').innerHTML = `
                <div style="background: rgba(0,0,0,0.3); padding: 12px; border-radius: 6px;">
                    <div><strong>Proposed Valuation:</strong> <span style="color:var(--accent-gold);">${data.proposedEnterpriseValuation} (${data.impliedMultiple})</span></div>
                    <div><strong>Cash at Close:</strong> <span style="color:var(--accent-green);">${data.cashAtClose}</span></div>
                    <div><strong>Seller Note:</strong> ${data.sellerNote}</div>
                    <div><strong>Rollover Equity:</strong> ${data.rolloverEquity}</div>
                    <div style="margin-top:6px; font-size:11px; color:var(--text-muted);">${data.governanceStructure} • Exclusivity: ${data.exclusivityPeriod}</div>
                </div>
            `;
            showToast('Acquisition quote attached to deal!');
            loadLeads();
        } catch (err) {
            showToast('Quote error: ' + err.message, true);
        }
    });

    // Close Modals
    document.querySelectorAll('.modal-close-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const wasDetailOpen = dealDetailsModal && !dealDetailsModal.classList.contains('hidden');
            document.querySelectorAll('.modal-overlay').forEach(m => m.classList.add('hidden'));
            if (wasDetailOpen) {
                closeDealDetailsModal();
            }
        });
    });

    if (dealDetailsModal) {
        dealDetailsModal.addEventListener('click', (e) => {
            if (e.target === dealDetailsModal) {
                closeDealDetailsModal();
            }
        });
    }

    // ==========================================
    // REAL-TIME SOURCING & PROMPT SCRAPING
    // ==========================================
    function renderSourcingRadar() {
        if (!upcomingDealsRadar || !allLeads) return;

        const sorted = [...allLeads].sort((a, b) => (b.winProbability || 0) - (a.winProbability || 0)).slice(0, 4);
        upcomingDealsRadar.innerHTML = '';

        sorted.forEach(lead => {
            const item = document.createElement('div');
            item.className = 'radar-item';
            item.style.cursor = 'pointer';
            item.innerHTML = `
                <div>
                    <strong>${escapeHtml(lead.companyName)}</strong>
                    <div style="font-size:11px; color:var(--text-muted);">${escapeHtml(lead.industry || 'B2B SaaS')} • ${escapeHtml(lead.region || 'North America')}</div>
                </div>
                <div style="text-align:right;">
                    <span class="priority-badge ${lead.priority === 'HOT' ? 'priority-hot' : 'priority-warm'}">${lead.priority || 'WARM'}</span>
                    <div style="font-size:11px; color:var(--accent-green); font-weight:700;">${lead.winProbability || 80}% Win Prob</div>
                </div>
            `;
            item.addEventListener('click', () => openDealDetailsModal(lead));
            upcomingDealsRadar.appendChild(item);
        });

        // Pipeline stage distribution bars
        if (sourcingStageDistribution) {
            const counts = {};
            allLeads.forEach(l => {
                const st = l.status || 'SOURCED';
                counts[st] = (counts[st] || 0) + 1;
            });
            sourcingStageDistribution.innerHTML = '';
            Object.entries(counts).forEach(([st, cnt]) => {
                const pct = allLeads.length > 0 ? Math.round((cnt / allLeads.length) * 100) : 0;
                const d = document.createElement('div');
                d.innerHTML = `
                    <div class="progress-item-header">
                        <span>${st}</span>
                        <span><strong>${cnt}</strong> (${pct}%)</span>
                    </div>
                    <div class="progress-bar-bg">
                        <div class="progress-bar-fill" style="width: ${pct}%"></div>
                    </div>
                `;
                sourcingStageDistribution.appendChild(d);
            });
        }
    }

    // AI Prompt Scraper
    btnRunPromptScrape.addEventListener('click', executePromptScrape);

    promptChips.forEach(chip => {
        chip.addEventListener('click', () => {
            aiPromptInput.value = chip.getAttribute('data-prompt');
            executePromptScrape();
        });
    });

    async function executePromptScrape() {
        const prompt = aiPromptInput.value.trim();
        if (!prompt) {
            showToast('Please type a prompt to search & scrape', true);
            return;
        }

        scrapeTerminal.classList.remove('hidden');
        terminalOutput.textContent = `[SaaSquatch Real-Time Investment Search Engine v2.0]\n> Parsing acquisition query: "${prompt}"\n> Invoking AI Entity Extraction for industry taxonomy, revenue constraints & geographic regions...`;

        btnRunPromptScrape.disabled = true;
        btnRunPromptScrape.innerHTML = '<span>⏳</span> Searching &amp; Extracting Deals...';

        try {
            // Step 1: Parse prompt
            const parseRes = await apiFetch(`${API_BASE}/ai/parse-prompt`, {
                method: 'POST',
                body: JSON.stringify({ prompt })
            });
            const parsed = await parseRes.json();

            terminalOutput.textContent += `\n> Criteria Detected: Industry=${parsed.targetIndustry}, Region=${parsed.targetRegion}, Keyword=${parsed.parsedQuery}\n> Initializing live web socket and crawling B2B directories...`;

            // Step 2: Scrape with parsed criteria
            const scrapeRes = await apiFetch(`${API_BASE}/scraper/extract`, {
                method: 'POST',
                body: JSON.stringify({
                    query: parsed.parsedQuery,
                    industry: parsed.targetIndustry,
                    region: parsed.targetRegion,
                    autoSave: true,
                    runAiAnalysis: true
                })
            });
            const results = await scrapeRes.json();

            terminalOutput.textContent += `\n> Extracted ${results.length} refined candidate deals matching PE investment criteria!\n> Synthesized ARR/EBITDA models and Caprae 7-year growth theses.\n> Successfully saved to deal pipeline.`;

            showToast(`Extracted ${results.length} investment deals via live search!`);
            loadLeads();
            loadDashboardMetrics();
            renderScrapeResults(results);

        } catch (err) {
            terminalOutput.textContent += `\n[ERROR]: ${err.message}`;
            showToast('Prompt scraper error: ' + err.message, true);
        } finally {
            btnRunPromptScrape.disabled = false;
            btnRunPromptScrape.innerHTML = '<span>⚡</span> Run Real-Time Search &amp; Scrape';
        }
    }

    // Direct Domain Scrape
    btnRunScrape.addEventListener('click', async () => {
        const query = scrapeTarget.value.trim();
        const industry = scrapeIndustry.value.trim();
        const region = scrapeRegion.value;
        const autoSave = scrapeAutosave.checked;
        const runAiAnalysis = scrapeAiEval.checked;

        if (!query) {
            showToast('Please enter a target domain or keyword', true);
            return;
        }

        scrapeTerminal.classList.remove('hidden');
        terminalOutput.textContent = `[SaaSquatch Real-Time Scraper Engine]\n> Target domain: ${query}\n> Connecting to target domain and parsing HTML headers...`;

        btnRunScrape.disabled = true;
        btnRunScrape.innerHTML = '<span>⏳</span> Scraping Domain...';

        try {
            const res = await apiFetch(`${API_BASE}/scraper/extract`, {
                method: 'POST',
                body: JSON.stringify({ query, industry, region, autoSave, runAiAnalysis })
            });
            if (!res.ok) throw new Error('Scraping failed');
            const data = await res.json();

            terminalOutput.textContent += `\n> Extraction completed! Found ${data.length} candidate record(s).\n> Tech stack detected and revenue synthesized.`;
            showToast(`Extracted ${data.length} deal target(s)!`);
            loadLeads();
            loadDashboardMetrics();
            renderScrapeResults(data);
        } catch (e) {
            terminalOutput.textContent += `\n[ERROR]: ${e.message}`;
            showToast('Scrape error: ' + e.message, true);
        } finally {
            btnRunScrape.disabled = false;
            btnRunScrape.innerHTML = '<span>🌐</span> Execute Live Domain Scrape';
        }
    });

    function renderScrapeResults(results) {
        scrapeResultsContainer.innerHTML = '';
        if (!results || results.length === 0) return;

        const heading = document.createElement('h3');
        heading.style.margin = '20px 0 12px 0';
        heading.textContent = `Extracted Investment Targets (${results.length})`;
        scrapeResultsContainer.appendChild(heading);

        const grid = document.createElement('div');
        grid.className = 'grid-2-col';

        results.forEach(lead => {
            const card = document.createElement('div');
            card.className = 'deal-card';
            const dealIdStr = `CP-${String(lead.id || 'NEW').padStart(4, '0')}`;
            card.innerHTML = `
                <div class="deal-card-top-row">
                    <span class="deal-id-tag">${dealIdStr}</span>
                    <span class="score-badge score-high">${lead.dealScore || 85} Score</span>
                </div>
                <div class="card-company-title" style="cursor:pointer;">${escapeHtml(lead.companyName)}</div>
                <a href="${escapeHtml(lead.websiteUrl || 'https://' + lead.domain)}" target="_blank" class="card-domain">${escapeHtml(lead.domain)} ↗</a>
                <div class="card-metrics-grid" style="margin-top:8px;">
                    <div>ARR: <span class="card-metric-val">${lead.estimatedRevenue ? formatCurrency(lead.estimatedRevenue) : '$2.8M'}</span></div>
                    <div>EBITDA: <span class="card-metric-val">${lead.estimatedEbitda ? formatCurrency(lead.estimatedEbitda) : '$850K'}</span></div>
                </div>
                <div style="font-size:12px; margin-top:8px; color:var(--text-secondary);">${escapeHtml(lead.description || '')}</div>
                <div style="margin-top:10px;">
                    <button class="btn btn-secondary btn-sm btn-view-extracted" style="width:100%;">👁️ Inspect Deal Details</button>
                </div>
            `;
            card.querySelector('.btn-view-extracted').addEventListener('click', () => openDealDetailsModal(lead));
            card.querySelector('.card-company-title').addEventListener('click', () => openDealDetailsModal(lead));
            grid.appendChild(card);
        });

        scrapeResultsContainer.appendChild(grid);
    }

    // ==========================================
    // AI COPILOT: RAG, MARKET, FINANCE
    // ==========================================
    btnRunRag.addEventListener('click', async () => {
        const query = ragQueryInput.value.trim();
        if (!query) return;

        btnRunRag.disabled = true;
        btnRunRag.innerHTML = '<span>⏳</span> Searching Dealflow...';

        try {
            const res = await apiFetch(`${API_BASE}/ai/rag-search`, {
                method: 'POST',
                body: JSON.stringify({ query })
            });
            const data = await res.json();

            ragResponseCard.classList.remove('hidden');
            ragSynthesisBody.innerHTML = formatMarkdown(data.ragSynthesisMarkdown);

            ragRetrievedDeals.innerHTML = '';
            if (data.retrievedLeads) {
                data.retrievedLeads.forEach(lead => {
                    const d = document.createElement('div');
                    d.className = 'card';
                    d.style.cssText = 'background: rgba(0,0,0,0.3); padding: 12px; border-radius: 8px; cursor: pointer;';
                    d.innerHTML = `
                        <div class="flex-between">
                            <strong>${escapeHtml(lead.companyName)}</strong>
                            <span class="badge" style="background:#10b981; color:#000;">${lead.dealScore || 80} Score</span>
                        </div>
                        <div style="font-size:11px; color:var(--text-muted); margin-top:2px;">${escapeHtml(lead.industry || '')} • ARR: ${formatCurrency(lead.estimatedRevenue)}</div>
                    `;
                    d.addEventListener('click', () => openDealDetailsModal(lead));
                    ragRetrievedDeals.appendChild(d);
                });
            }
            showToast('RAG search completed!');
        } catch (e) {
            showToast('RAG query error', true);
        } finally {
            btnRunRag.disabled = false;
            btnRunRag.innerHTML = '<span>⚡</span> Execute RAG Query';
        }
    });

    btnRunMarketResearch.addEventListener('click', async () => {
        const ind = marketResearchIndustrySelect.value;
        btnRunMarketResearch.disabled = true;
        btnRunMarketResearch.textContent = 'Generating Research...';

        try {
            const res = await apiFetch(`${API_BASE}/ai/market-research`, {
                method: 'POST',
                body: JSON.stringify({ industry: ind })
            });
            const data = await res.json();

            marketResearchResults.classList.remove('hidden');
            marketTam.textContent = data.estimatedTam;
            marketMultiple.textContent = data.medianMultiple;
            marketMargin.textContent = data.medianMargin;

            marketTailwindsList.innerHTML = '';
            (data.macroTailwinds || []).forEach(t => {
                const li = document.createElement('li');
                li.textContent = t;
                marketTailwindsList.appendChild(li);
            });

            marketRollupList.innerHTML = '';
            (data.capraeRollUpStrategy || []).forEach(r => {
                const li = document.createElement('li');
                li.textContent = r;
                marketRollupList.appendChild(li);
            });

            showToast('Market research generated!');
        } catch (e) {
            showToast('Market research error', true);
        } finally {
            btnRunMarketResearch.disabled = false;
            btnRunMarketResearch.textContent = 'Generate Research';
        }
    });

    btnLoadFinancialModel.addEventListener('click', async () => {
        const leadId = financeLeadSelect.value;
        if (!leadId) return;

        btnLoadFinancialModel.disabled = true;
        btnLoadFinancialModel.textContent = 'Modeling...';

        try {
            const res = await apiFetch(`${API_BASE}/ai/financial-report/${leadId}`);
            const data = await res.json();

            financialModelResults.classList.remove('hidden');
            finArr.textContent = formatCurrency(data.currentArr);
            finGrossMargin.textContent = data.grossMarginPct + '%';
            finEbitda.textContent = formatCurrency(data.adjustedEbitda);
            finSde.textContent = formatCurrency(data.sellerDiscretionaryEarnings);

            finDealStructuresContainer.innerHTML = '';
            (data.dealFinancingOptions || []).forEach(opt => {
                const box = document.createElement('div');
                box.className = 'deal-struct-box';
                box.innerHTML = `
                    <div class="flex-between">
                        <strong>${opt.structureName}</strong>
                        <span style="color:var(--accent-gold); font-weight:700;">${opt.valuationMultiple}</span>
                    </div>
                    <div style="font-size:12px; margin-top:4px; color:var(--text-secondary);">${opt.breakdown}</div>
                    <div style="font-size:11px; color:var(--accent-green); margin-top:4px;">${opt.pros}</div>
                `;
                finDealStructuresContainer.appendChild(box);
            });

            finProjArr.textContent = formatCurrency(data.projectedYear5Arr);
            finProjEbitda.textContent = formatCurrency(data.projectedYear5Ebitda);
            finProjExit.textContent = formatCurrency(data.projectedEnterpriseValueAtExit);
            finProjMoic.textContent = data.targetMoic;

            showToast('Financial model calculated!');
        } catch (e) {
            showToast('Financial model error', true);
        } finally {
            btnLoadFinancialModel.disabled = false;
            btnLoadFinancialModel.textContent = 'Generate Financial Model';
        }
    });

    // ==========================================
    // AI OUTREACH SEQUENCE STUDIO (ENHANCED)
    // ==========================================
    function populateOutreachDropdown(leads) {
        if (!outreachLeadSelect) return;
        const currentVal = outreachLeadSelect.value;
        outreachLeadSelect.innerHTML = '';
        leads.forEach(lead => {
            const opt = document.createElement('option');
            opt.value = lead.id;
            opt.textContent = `CP-${String(lead.id).padStart(4, '0')} • ${lead.companyName} (${lead.domain}) - ARR: ${formatCurrency(lead.estimatedRevenue)}`;
            outreachLeadSelect.appendChild(opt);
        });
        if (currentVal) outreachLeadSelect.value = currentVal;
    }

    // Helper to get selected lead email
    function getLeadContactEmail(lead) {
        if (!lead) return 'founder@domain.com';
        if (lead.primaryEmail) return lead.primaryEmail;
        if (lead.contacts && lead.contacts.length > 0 && lead.contacts[0].email) return lead.contacts[0].email;
        if (lead.domain) return `founder@${lead.domain}`;
        return 'founder@domain.com';
    }

    function updateMailtoLinks() {
        for (let i = 1; i <= 3; i++) {
            const rec = document.getElementById(`email-recipient-${i}`)?.value.trim() || '';
            const sub = document.getElementById(`email-subject-input-${i}`)?.value.trim() || '';
            let body = '';
            if (i === 1) body = document.getElementById('email-body-text')?.value || '';
            if (i === 2) body = document.getElementById('email-day3-body')?.value || '';
            if (i === 3) body = document.getElementById('email-day7-body')?.value || '';

            const mailto = document.getElementById(`mailto-touch-${i}`);
            if (mailto) {
                mailto.href = `mailto:${encodeURIComponent(rec)}?subject=${encodeURIComponent(sub)}&body=${encodeURIComponent(body)}`;
            }
        }
    }

    // Lead select change updates recipient fields
    if (outreachLeadSelect) {
        outreachLeadSelect.addEventListener('change', () => {
            const leadId = outreachLeadSelect.value;
            const lead = allLeads.find(l => String(l.id) === String(leadId));
            if (lead) {
                const email = getLeadContactEmail(lead);
                for (let i = 1; i <= 3; i++) {
                    const recElem = document.getElementById(`email-recipient-${i}`);
                    if (recElem) recElem.value = email;
                }
                updateMailtoLinks();
                const dealIdStr = `CP-${String(lead.id).padStart(4, '0')}`;
                window.history.pushState({ tabId: 'outreach-tab', leadId: lead.id }, '', `/outreach-studio/${dealIdStr}`);
            } else {
                window.history.pushState({ tabId: 'outreach-tab' }, '', '/outreach-studio');
            }
        });
    }

    // Real-time typing updates mailto links
    ['email-recipient-1', 'email-recipient-2', 'email-recipient-3',
     'email-subject-input-1', 'email-subject-input-2', 'email-subject-input-3',
     'email-body-text', 'email-day3-body', 'email-day7-body'].forEach(id => {
        const el = document.getElementById(id);
        if (el) {
            el.addEventListener('input', updateMailtoLinks);
        }
    });

    btnGenerateOutreach.addEventListener('click', async () => {
        const leadId = outreachLeadSelect.value;
        if (!leadId) {
            showToast('Please select a target company', true);
            return;
        }

        const lead = allLeads.find(l => String(l.id) === String(leadId)) || {};
        const tone = outreachTone.value;
        const sender = outreachSender.value.trim();
        const recipientEmail = getLeadContactEmail(lead);

        btnGenerateOutreach.disabled = true;
        btnGenerateOutreach.innerHTML = '<span>⏳</span> Drafting 3-Touch Sequence...';

        try {
            const res = await apiFetch(`${API_BASE}/ai/outreach`, {
                method: 'POST',
                body: JSON.stringify({ leadId, tone, senderName: sender })
            });
            const data = await res.json();

            // Set recipients
            for (let i = 1; i <= 3; i++) {
                const recElem = document.getElementById(`email-recipient-${i}`);
                if (recElem) recElem.value = recipientEmail;
            }

            // Touch 1
            const sub1 = document.getElementById('email-subject-input-1');
            if (sub1) sub1.value = data.subjectLine || `Confidential Acquisition Inquiry // ${lead.companyName}`;
            emailBodyText.value = data.emailBody || '';

            // Touch 2 (Day 3)
            const founderName = (lead.contacts && lead.contacts.length > 0) ? lead.contacts[0].fullName.split(' ')[0] : 'there';
            const sub2 = document.getElementById('email-subject-input-2');
            if (sub2) sub2.value = `Re: ${sub1 ? sub1.value : 'Confidential Acquisition Inquiry'}`;
            emailDay3Body.value = `Hi ${founderName},\n\nFollowing up on my note from Tuesday regarding ${lead.companyName}.\n\nAt Caprae Capital, we specialize in 7-year patient capital rather than rapid flips. Given your ${lead.industry || 'B2B software'} architecture, we see strong alignment with our roll-up thesis, offering valuations structured around ${lead.estimatedEbitda ? formatCurrency(lead.estimatedEbitda * 5.8) : 'attractive EBITDA multiples'} with flexible rollover equity if desired.\n\nAre you open to a brief 10-minute founder-to-operator introduction this Thursday or Friday afternoon?\n\nBest,\n${sender}`;

            // Touch 3 (Day 7)
            const sub3 = document.getElementById('email-subject-input-3');
            if (sub3) sub3.value = `Re: Permission to close file // ${lead.companyName}`;
            emailDay7Body.value = `Hi ${founderName},\n\nI know you're heavily focused on scaling ${lead.companyName}, so I will keep this brief.\n\nIf acquisition or succession isn't a top priority this quarter, no problem at all. We will keep your metrics on our confidential watch list. Should timing ever align, feel free to reach out directly at this address.\n\nWishing you continued momentum.\n\nWarm regards,\n${sender}`;

            // Reset status badges
            for (let i = 1; i <= 3; i++) {
                const badge = document.getElementById(`status-touch-${i}`);
                if (badge) {
                    badge.className = 'email-status-badge status-ready';
                    badge.textContent = 'Ready';
                }
                const audit = document.getElementById(`audit-touch-${i}`);
                if (audit) {
                    audit.textContent = 'Status: Ready to send or save as draft';
                }
            }

            updateMailtoLinks();
            showToast('3-Touch Outreach Sequence Generated!');
        } catch (e) {
            showToast('Outreach error: ' + e.message, true);
        } finally {
            btnGenerateOutreach.disabled = false;
            btnGenerateOutreach.innerHTML = '<span>✉️</span> Generate 3-Touch Sequence';
        }
    });

    // Send single touch email
    async function sendTouchEmail(touchNum) {
        const leadId = outreachLeadSelect.value;
        const recipient = document.getElementById(`email-recipient-${touchNum}`)?.value.trim() || '';
        const subject = document.getElementById(`email-subject-input-${touchNum}`)?.value.trim() || '';
        let body = '';
        if (touchNum === 1) body = document.getElementById('email-body-text')?.value.trim() || '';
        if (touchNum === 2) body = document.getElementById('email-day3-body')?.value.trim() || '';
        if (touchNum === 3) body = document.getElementById('email-day7-body')?.value.trim() || '';

        if (!body) {
            showToast(`Please generate or write an email body for Touch ${touchNum} first!`, true);
            return;
        }
        if (!recipient) {
            showToast(`Please specify recipient email for Touch ${touchNum}!`, true);
            return;
        }

        const sendBtn = document.querySelector(`.btn-send-step[data-touch="${touchNum}"]`);
        if (sendBtn) {
            sendBtn.disabled = true;
            sendBtn.textContent = '✈️ Sending...';
        }

        try {
            const res = await apiFetch(`${API_BASE}/ai/outreach/send`, {
                method: 'POST',
                body: JSON.stringify({
                    leadId: leadId ? parseInt(leadId, 10) : null,
                    touchNumber: touchNum,
                    recipientEmail: recipient,
                    subject: subject,
                    body: body
                })
            });
            const data = await res.json();

            const badge = document.getElementById(`status-touch-${touchNum}`);
            if (badge) {
                badge.className = 'email-status-badge status-sent';
                badge.textContent = 'Sent';
            }
            const audit = document.getElementById(`audit-touch-${touchNum}`);
            if (audit) {
                audit.innerHTML = `<span style="color:var(--accent-green);">✓ Dispatched to <strong>${escapeHtml(recipient)}</strong> [Tracking: ${data.trackingId}]</span>`;
            }

            updateMailtoLinks();
            showToast(`Touch ${touchNum} email sent to ${recipient}!`);
        } catch (err) {
            showToast(`Failed to send Touch ${touchNum}: ${err.message}`, true);
        } finally {
            if (sendBtn) {
                sendBtn.disabled = false;
                sendBtn.textContent = '✈️ Send Email';
            }
        }
    }

    // Save single touch draft
    async function saveTouchDraft(touchNum, silent = false) {
        const leadId = outreachLeadSelect.value;
        const recipient = document.getElementById(`email-recipient-${touchNum}`)?.value.trim() || '';
        const subject = document.getElementById(`email-subject-input-${touchNum}`)?.value.trim() || '';
        let body = '';
        if (touchNum === 1) body = document.getElementById('email-body-text')?.value.trim() || '';
        if (touchNum === 2) body = document.getElementById('email-day3-body')?.value.trim() || '';
        if (touchNum === 3) body = document.getElementById('email-day7-body')?.value.trim() || '';

        if (!body && !subject) {
            if (!silent) showToast(`No email content to save for Touch ${touchNum}!`, true);
            return;
        }

        try {
            await apiFetch(`${API_BASE}/ai/outreach/draft`, {
                method: 'POST',
                body: JSON.stringify({
                    leadId: leadId ? parseInt(leadId, 10) : null,
                    touchNumber: touchNum,
                    recipientEmail: recipient,
                    subject: subject,
                    body: body
                })
            });

            // LocalStorage backup
            localStorage.setItem(`caprae_draft_${leadId || 'def'}_touch_${touchNum}`, JSON.stringify({ recipient, subject, body, time: Date.now() }));

            const badge = document.getElementById(`status-touch-${touchNum}`);
            if (badge) {
                badge.className = 'email-status-badge status-draft';
                badge.textContent = 'Draft Saved';
            }
            const audit = document.getElementById(`audit-touch-${touchNum}`);
            if (audit) {
                audit.innerHTML = `<span style="color:var(--accent-gold);">💾 Saved as draft at ${new Date().toLocaleTimeString()}</span>`;
            }

            updateMailtoLinks();
            if (!silent) showToast(`Touch ${touchNum} draft saved successfully!`);
        } catch (err) {
            if (!silent) showToast(`Draft error: ${err.message}`, true);
        }
    }

    // Wire up step Send and Draft buttons
    document.querySelectorAll('.btn-send-step').forEach(btn => {
        btn.addEventListener('click', () => {
            const touch = parseInt(btn.getAttribute('data-touch'), 10);
            sendTouchEmail(touch);
        });
    });

    document.querySelectorAll('.btn-save-draft-step').forEach(btn => {
        btn.addEventListener('click', () => {
            const touch = parseInt(btn.getAttribute('data-touch'), 10);
            saveTouchDraft(touch);
        });
    });

    // Save All Drafts
    const btnSaveAllDrafts = document.getElementById('btn-save-all-drafts');
    if (btnSaveAllDrafts) {
        btnSaveAllDrafts.addEventListener('click', async () => {
            for (let i = 1; i <= 3; i++) {
                await saveTouchDraft(i, true);
            }
            showToast('All 3 touches saved as drafts!');
        });
    }

    // Send Full Drip Sequence
    const btnSendFullSequence = document.getElementById('btn-send-full-sequence');
    if (btnSendFullSequence) {
        btnSendFullSequence.addEventListener('click', async () => {
            btnSendFullSequence.disabled = true;
            btnSendFullSequence.textContent = '🚀 Queuing 3-Touch Drip...';

            try {
                for (let i = 1; i <= 3; i++) {
                    await sendTouchEmail(i);
                }
                showToast('Full 3-Touch automated acquisition sequence dispatched!');
            } catch (e) {
                showToast('Drip sequence error: ' + e.message, true);
            } finally {
                btnSendFullSequence.disabled = false;
                btnSendFullSequence.textContent = '🚀 Send Full Drip Sequence';
            }
        });
    }

    // Copy Step buttons
    document.querySelectorAll('.btn-copy-step').forEach(btn => {
        btn.addEventListener('click', () => {
            const targetId = btn.getAttribute('data-target');
            const targetElem = document.getElementById(targetId);
            if (targetElem) {
                targetElem.select();
                navigator.clipboard.writeText(targetElem.value);
                showToast('Email body copied to clipboard!');
            }
        });
    });

    if (btnCopyFullSequence) {
        btnCopyFullSequence.addEventListener('click', () => {
            const sub1 = document.getElementById('email-subject-input-1')?.value || 'Confidential Inquiry';
            const sub2 = document.getElementById('email-subject-input-2')?.value || 'Re: Valuation';
            const sub3 = document.getElementById('email-subject-input-3')?.value || 'Re: Permission to close';
            const fullText = `=== TOUCH 1 (DAY 1) ===\nTO: ${document.getElementById('email-recipient-1')?.value || ''}\nSUBJECT: ${sub1}\n\n${emailBodyText.value}\n\n` +
                             `=== TOUCH 2 (DAY 3) ===\nTO: ${document.getElementById('email-recipient-2')?.value || ''}\nSUBJECT: ${sub2}\n\n${emailDay3Body.value}\n\n` +
                             `=== TOUCH 3 (DAY 7) ===\nTO: ${document.getElementById('email-recipient-3')?.value || ''}\nSUBJECT: ${sub3}\n\n${emailDay7Body.value}`;
            navigator.clipboard.writeText(fullText);
            showToast('Full 3-touch sequence copied!');
        });
    }

    if (btnDownloadSequence) {
        btnDownloadSequence.addEventListener('click', () => {
            const sub1 = document.getElementById('email-subject-input-1')?.value || 'Confidential Inquiry';
            const sub2 = document.getElementById('email-subject-input-2')?.value || 'Re: Valuation';
            const sub3 = document.getElementById('email-subject-input-3')?.value || 'Re: Permission to close';
            const fullText = `CAPRAE CAPITAL PARTNERS - OUTREACH SEQUENCE\nTarget: ${outreachLeadSelect.options[outreachLeadSelect.selectedIndex]?.text || ''}\nDate: ${new Date().toLocaleDateString()}\n\n` +
                             `========================================\nTOUCH 1 (DAY 1 - INITIAL INQUIRY)\nTO: ${document.getElementById('email-recipient-1')?.value || ''}\nSUBJECT: ${sub1}\n========================================\n\n${emailBodyText.value}\n\n` +
                             `========================================\nTOUCH 2 (DAY 3 - VALUE MULTIPLE)\nTO: ${document.getElementById('email-recipient-2')?.value || ''}\nSUBJECT: ${sub2}\n========================================\n\n${emailDay3Body.value}\n\n` +
                             `========================================\nTOUCH 3 (DAY 7 - BREAK-UP / PERMISSION)\nTO: ${document.getElementById('email-recipient-3')?.value || ''}\nSUBJECT: ${sub3}\n========================================\n\n${emailDay7Body.value}`;
            const blob = new Blob([fullText], { type: 'text/plain' });
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `caprae-outreach-sequence-${Date.now()}.txt`;
            a.click();
            URL.revokeObjectURL(url);
            showToast('Sequence exported to .txt!');
        });
    }

    // ==========================================
    // PIPELINE ANALYTICS WITH AXES & TOOLTIPS
    // ==========================================
    function renderAnalyticsVisuals() {
        if (!allLeads || allLeads.length === 0) return;

        // Group ARR by Industry
        const indArr = {};
        const indCounts = {};
        const indScores = {};
        const priorityCounts = { HOT: 0, WARM: 0, COLD: 0 };
        const statusCounts = {};

        allLeads.forEach(l => {
            const ind = l.industry || 'B2B SaaS';
            indArr[ind] = (indArr[ind] || 0) + (l.estimatedRevenue || 2500000);
            indCounts[ind] = (indCounts[ind] || 0) + 1;
            if (!indScores[ind]) indScores[ind] = [];
            indScores[ind].push(l.dealScore || 75);

            const pr = l.priority || 'WARM';
            priorityCounts[pr] = (priorityCounts[pr] || 0) + 1;

            const st = l.status || 'SOURCED';
            statusCounts[st] = (statusCounts[st] || 0) + 1;
        });

        // 1. SVG Bar Chart: ARR by Industry with Real Axes & Gridlines
        const barContainer = document.getElementById('bar-chart-container');
        if (barContainer) {
            const entries = Object.entries(indArr).slice(0, 5);
            const maxVal = Math.max(...entries.map(e => e[1]), 12000000);
            const maxMillions = Math.ceil(maxVal / 1000000);

            let svgHtml = `
                <svg width="100%" height="220" viewBox="0 0 540 220" style="overflow: visible;">
                    <defs>
                        <linearGradient id="barGrad" x1="0" y1="0" x2="0" y2="1">
                            <stop offset="0%" stop-color="#f59e0b" />
                            <stop offset="100%" stop-color="#3b82f6" />
                        </linearGradient>
                    </defs>

                    <!-- Y-Axis Grid Lines & Tick Labels -->
                    <line x1="50" y1="20" x2="520" y2="20" stroke="rgba(255,255,255,0.06)" stroke-dasharray="3 3" />
                    <text x="42" y="24" font-size="9" fill="#64748b" text-anchor="end">$${maxMillions}M</text>

                    <line x1="50" y1="65" x2="520" y2="65" stroke="rgba(255,255,255,0.06)" stroke-dasharray="3 3" />
                    <text x="42" y="69" font-size="9" fill="#64748b" text-anchor="end">$${Math.round(maxMillions * 0.75)}M</text>

                    <line x1="50" y1="110" x2="520" y2="110" stroke="rgba(255,255,255,0.06)" stroke-dasharray="3 3" />
                    <text x="42" y="114" font-size="9" fill="#64748b" text-anchor="end">$${Math.round(maxMillions * 0.5)}M</text>

                    <line x1="50" y1="155" x2="520" y2="155" stroke="rgba(255,255,255,0.06)" stroke-dasharray="3 3" />
                    <text x="42" y="159" font-size="9" fill="#64748b" text-anchor="end">$${Math.round(maxMillions * 0.25)}M</text>

                    <!-- X & Y Axes Axes Lines -->
                    <line x1="50" y1="20" x2="50" y2="175" stroke="rgba(255,255,255,0.2)" stroke-width="1.5" />
                    <line x1="50" y1="175" x2="520" y2="175" stroke="rgba(255,255,255,0.2)" stroke-width="1.5" />
            `;

            const barWidth = 48;
            const gap = 45;

            entries.forEach(([ind, total], idx) => {
                const x = 65 + (idx * (barWidth + gap));
                const barHeight = Math.max(10, Math.round((total / (maxMillions * 1000000)) * 150));
                const y = 175 - barHeight;
                const millions = (total / 1000000).toFixed(1) + 'M';
                const count = indCounts[ind] || 1;
                const shortLabel = ind.length > 12 ? ind.substring(0, 10) + '..' : ind;

                svgHtml += `
                    <rect class="chart-bar-hoverable" x="${x}" y="${y}" width="${barWidth}" height="${barHeight}" rx="4"
                          fill="url(#barGrad)"
                          data-tooltip="<strong>${escapeHtml(ind)}</strong><br>Pipeline ARR: $${millions}<br>Deals Count: ${count}<br>Avg EBITDA: $${formatCurrency(total * 0.28 / count)}" />
                    <text x="${x + (barWidth / 2)}" y="${y - 6}" font-size="10" font-weight="700" fill="#f59e0b" text-anchor="middle">$${millions}</text>
                    <text x="${x + (barWidth / 2)}" y="195" font-size="10" fill="#94a3b8" text-anchor="middle">${shortLabel}</text>
                `;
            });

            svgHtml += `</svg>`;
            barContainer.innerHTML = svgHtml;
            attachTooltipListeners(barContainer);
        }

        // 2. SVG Donut & Conversion Pipeline with Hover Tooltip
        const donutContainer = document.getElementById('donut-chart-container');
        if (donutContainer) {
            const total = allLeads.length;
            const colors = ['#f59e0b', '#3b82f6', '#10b981', '#06b6d4', '#8b5cf6'];
            let currentAngle = 0;
            let donutSvg = `
                <div style="display:flex; align-items:center; gap:28px;">
                    <svg width="160" height="160" viewBox="0 0 100 100">
                        <circle cx="50" cy="50" r="35" fill="none" stroke="rgba(255,255,255,0.05)" stroke-width="16" />
            `;

            let legendHtml = '<div style="font-size:12px; display:flex; flex-direction:column; gap:6px;">';
            Object.entries(statusCounts).forEach(([st, cnt], i) => {
                const pct = Math.round((cnt / total) * 100);
                const strokeDash = `${pct * 2.2} 220`;
                const color = colors[i % colors.length];

                donutSvg += `
                    <circle class="chart-bar-hoverable" cx="50" cy="50" r="35" fill="none" stroke="${color}" stroke-width="16"
                        stroke-dasharray="${strokeDash}" stroke-dashoffset="-${currentAngle}" stroke-linecap="round"
                        data-tooltip="<strong>Stage: ${st}</strong><br>Active Deals: ${cnt}<br>Pipeline Share: ${pct}%" />
                `;
                currentAngle += (pct * 2.2);

                legendHtml += `<div><span style="color:${color}">■</span> ${st}: <strong>${cnt}</strong> (${pct}%)</div>`;
            });

            donutSvg += `
                        <text x="50" y="52" font-size="13" font-weight="800" fill="#fff" text-anchor="middle">${total}</text>
                        <text x="50" y="64" font-size="8" fill="#94a3b8" text-anchor="middle">TOTAL DEALS</text>
                    </svg>
                    ${legendHtml}</div>
                </div>
            `;
            donutContainer.innerHTML = donutSvg;
            attachTooltipListeners(donutContainer);
        }

        // 3. Scatter Plot: EBITDA ($M) vs AI Deal Score (0-100) with Axes & Quadrants
        const scatterContainer = document.getElementById('scatter-chart-container');
        if (scatterContainer) {
            let scatterSvg = `
                <svg width="100%" height="220" viewBox="0 0 540 220" style="overflow: visible;">
                    <!-- Y-Axis Gridlines & Labels (0 to 100 Score) -->
                    <line x1="50" y1="20" x2="520" y2="20" stroke="rgba(255,255,255,0.06)" stroke-dasharray="3 3" />
                    <text x="42" y="24" font-size="9" fill="#64748b" text-anchor="end">100</text>

                    <line x1="50" y1="65" x2="520" y2="65" stroke="rgba(255,255,255,0.06)" stroke-dasharray="3 3" />
                    <text x="42" y="69" font-size="9" fill="#64748b" text-anchor="end">85</text>

                    <line x1="50" y1="110" x2="520" y2="110" stroke="rgba(255,255,255,0.06)" stroke-dasharray="3 3" />
                    <text x="42" y="114" font-size="9" fill="#64748b" text-anchor="end">70</text>

                    <line x1="50" y1="155" x2="520" y2="155" stroke="rgba(255,255,255,0.06)" stroke-dasharray="3 3" />
                    <text x="42" y="159" font-size="9" fill="#64748b" text-anchor="end">50</text>

                    <!-- Axes Lines -->
                    <line x1="50" y1="20" x2="50" y2="175" stroke="rgba(255,255,255,0.2)" stroke-width="1.5" />
                    <line x1="50" y1="175" x2="520" y2="175" stroke="rgba(255,255,255,0.2)" stroke-width="1.5" />

                    <!-- X-Axis Labels (EBITDA $0 to $3M+) -->
                    <text x="50" y="195" font-size="9" fill="#64748b" text-anchor="middle">$0M</text>
                    <text x="165" y="195" font-size="9" fill="#64748b" text-anchor="middle">$1.0M</text>
                    <text x="280" y="195" font-size="9" fill="#64748b" text-anchor="middle">$2.0M</text>
                    <text x="395" y="195" font-size="9" fill="#64748b" text-anchor="middle">$3.0M</text>
                    <text x="500" y="195" font-size="9" fill="#64748b" text-anchor="middle">$4.0M+</text>
            `;

            // Plot deals as interactive dots
            allLeads.forEach(lead => {
                const ebitda = lead.estimatedEbitda || 500000;
                const score = lead.dealScore || 75;
                const cx = Math.min(505, Math.max(55, 50 + (ebitda / 4000000) * 450));
                const cy = Math.max(25, Math.min(170, 175 - ((score / 100) * 150)));

                const color = lead.priority === 'HOT' ? '#ef4444' : (lead.priority === 'COLD' ? '#3b82f6' : '#f59e0b');
                const dealIdStr = `CP-${String(lead.id).padStart(4, '0')}`;

                scatterSvg += `
                    <circle class="chart-scatter-point" cx="${cx}" cy="${cy}" r="6" fill="${color}" stroke="#fff" stroke-width="1.5"
                        data-tooltip="<strong>${dealIdStr}: ${escapeHtml(lead.companyName)}</strong><br>EBITDA: ${formatCurrency(ebitda)}<br>AI Score: ${score}/100<br>Win Prob: ${lead.winProbability || 65}%<br>Stage: ${lead.status || 'SOURCED'}" />
                `;
            });

            scatterSvg += `</svg>`;
            scatterContainer.innerHTML = scatterSvg;
            attachTooltipListeners(scatterContainer);
        }

        // Priority Breakdown Bars
        const prioContainer = document.getElementById('priority-chart-container');
        if (prioContainer) {
            const tot = allLeads.length;
            prioContainer.innerHTML = `
                <div class="progress-item-header"><span>🔥 HOT Priority Deals</span><span><strong>${priorityCounts.HOT}</strong> (${Math.round(priorityCounts.HOT/tot*100)}%)</span></div>
                <div class="progress-bar-bg"><div class="progress-bar-fill" style="width:${priorityCounts.HOT/tot*100}%; background:#ef4444;"></div></div>

                <div class="progress-item-header" style="margin-top:10px;"><span>⚡ WARM Pipeline Deals</span><span><strong>${priorityCounts.WARM}</strong> (${Math.round(priorityCounts.WARM/tot*100)}%)</span></div>
                <div class="progress-bar-bg"><div class="progress-bar-fill" style="width:${priorityCounts.WARM/tot*100}%; background:#f59e0b;"></div></div>

                <div class="progress-item-header" style="margin-top:10px;"><span>❄️ COLD Nurture Deals</span><span><strong>${priorityCounts.COLD}</strong> (${Math.round(priorityCounts.COLD/tot*100)}%)</span></div>
                <div class="progress-bar-bg"><div class="progress-bar-fill" style="width:${priorityCounts.COLD/tot*100}%; background:#3b82f6;"></div></div>
            `;
        }

        // Score Chart
        const scoreContainer = document.getElementById('score-chart-container');
        if (scoreContainer) {
            scoreContainer.innerHTML = '';
            Object.entries(indScores).slice(0, 4).forEach(([ind, scores]) => {
                const avg = Math.round(scores.reduce((a, b) => a + b, 0) / scores.length);
                const item = document.createElement('div');
                item.innerHTML = `
                    <div class="progress-item-header"><span>${ind}</span><span><strong>${avg} / 100</strong> Score</span></div>
                    <div class="progress-bar-bg"><div class="progress-bar-fill" style="width:${avg}%; background:linear-gradient(90deg, #10b981, #06b6d4);"></div></div>
                `;
                scoreContainer.appendChild(item);
            });
        }
    }

    // Interactive Tooltip Hover Engine
    function attachTooltipListeners(container) {
        container.querySelectorAll('[data-tooltip]').forEach(elem => {
            elem.addEventListener('mouseenter', (e) => {
                const text = elem.getAttribute('data-tooltip');
                chartTooltip.innerHTML = text;
                chartTooltip.classList.remove('hidden');
                positionTooltip(e);
            });

            elem.addEventListener('mousemove', (e) => {
                positionTooltip(e);
            });

            elem.addEventListener('mouseleave', () => {
                chartTooltip.classList.add('hidden');
            });
        });
    }

    function positionTooltip(e) {
        const x = e.clientX + 14;
        const y = e.clientY - 12;
        chartTooltip.style.left = `${x}px`;
        chartTooltip.style.top = `${y}px`;
    }

    // ==========================================
    // HELPERS & GENERAL EVENT LISTENERS
    // ==========================================
    function updateDropdowns(leads) {
        populateOutreachDropdown(leads);
        [financeLeadSelect].forEach(sel => {
            if (!sel) return;
            const currentVal = sel.value;
            sel.innerHTML = '';
            leads.forEach(lead => {
                const opt = document.createElement('option');
                opt.value = lead.id;
                opt.textContent = `CP-${String(lead.id).padStart(4, '0')} • ${lead.companyName} (${lead.domain}) - ARR: ${formatCurrency(lead.estimatedRevenue)}`;
                sel.appendChild(opt);
            });
            if (currentVal) sel.value = currentVal;
        });
    }

    function setupEventListeners() {
        let debounceTimer;
        filterSearch.addEventListener('input', () => {
            clearTimeout(debounceTimer);
            debounceTimer = setTimeout(() => applyFilters(true), 250);
        });

        filterIndustry.addEventListener('change', () => applyFilters(true));
        filterStatus.addEventListener('change', () => applyFilters(true));
        filterPriority.addEventListener('change', () => applyFilters(true));
        filterMinScore.addEventListener('input', () => {
            scoreValLabel.textContent = filterMinScore.value;
            clearTimeout(debounceTimer);
            debounceTimer = setTimeout(() => applyFilters(true), 250);
        });

        btnResetFilters.addEventListener('click', () => {
            filterSearch.value = '';
            filterIndustry.value = '';
            filterStatus.value = '';
            filterPriority.value = '';
            filterMinScore.value = 0;
            scoreValLabel.textContent = '0';
            applyFilters(true);
        });

        if (analyticsSortBy) {
            analyticsSortBy.addEventListener('change', () => {
                const sortVal = analyticsSortBy.value;
                window.history.pushState({ tabId: 'analytics-tab', sortBy: sortVal }, '', `/pipeline-analytics?sortBy=${sortVal}`);
                renderAnalyticsVisuals();
            });
        }

        paginationPageSize.addEventListener('change', () => {
            pageSize = parseInt(paginationPageSize.value, 10) || 10;
            currentPage = 1;
            renderPipeline();
        });

        btnPagePrev.addEventListener('click', () => {
            if (currentPage > 1) {
                currentPage--;
                renderPipeline();
            }
        });

        btnPageNext.addEventListener('click', () => {
            const totalPages = Math.ceil(filteredLeads.length / pageSize) || 1;
            if (currentPage < totalPages) {
                currentPage++;
                renderPipeline();
            }
        });

        analyticsSortBy.addEventListener('change', () => {
            const by = analyticsSortBy.value;
            if (by === 'arr') {
                allLeads.sort((a, b) => (b.estimatedRevenue || 0) - (a.estimatedRevenue || 0));
            } else if (by === 'score') {
                allLeads.sort((a, b) => (b.dealScore || 0) - (a.dealScore || 0));
            } else if (by === 'probability') {
                allLeads.sort((a, b) => (b.winProbability || 0) - (a.winProbability || 0));
            } else if (by === 'name') {
                allLeads.sort((a, b) => a.companyName.localeCompare(b.companyName));
            }
            applyFilters();
            renderAnalyticsVisuals();
        });

        btnOpenCreateModal.addEventListener('click', () => createModal.classList.remove('hidden'));

        document.getElementById('create-lead-form').addEventListener('submit', async (e) => {
            e.preventDefault();
            const payload = {
                companyName: document.getElementById('modal-company').value.trim(),
                domain: document.getElementById('modal-domain').value.trim(),
                industry: document.getElementById('modal-industry').value.trim(),
                region: document.getElementById('modal-region').value.trim(),
                estimatedRevenue: parseFloat(document.getElementById('modal-arr').value) || 2500000,
                techStack: document.getElementById('modal-tech').value.trim(),
                priority: document.getElementById('modal-priority').value,
                primaryEmail: document.getElementById('modal-email').value.trim(),
                description: document.getElementById('modal-desc').value.trim(),
                status: 'SOURCED'
            };

            try {
                const res = await apiFetch(`${API_BASE}/leads`, {
                    method: 'POST',
                    body: JSON.stringify(payload)
                });
                if (!res.ok) {
                    const err = await res.json();
                    throw new Error(err.message || 'Creation failed');
                }
                showToast('New deal target saved to pipeline!');
                createModal.classList.add('hidden');
                document.getElementById('create-lead-form').reset();
                loadLeads();
                loadDashboardMetrics();
            } catch (err) {
                showToast(err.message, true);
            }
        });

        btnExportCsv.addEventListener('click', () => {
            downloadExport('csv');
            showToast('Downloading CSV deal export...');
        });

        btnExportJson.addEventListener('click', () => {
            downloadExport('json');
            showToast('Downloading JSON deal export...');
        });

        btnCopyKey.addEventListener('click', () => {
            const keyText = document.getElementById('api-key-display').textContent;
            navigator.clipboard.writeText(keyText);
            showToast('API Key copied to clipboard!');
        });
    }

    function formatCurrency(amount) {
        if (!amount && amount !== 0) return '$0';
        return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD', maximumFractionDigits: 0 }).format(amount);
    }

    function formatMarkdown(text) {
        if (!text) return '';
        return text
            .replace(/### (.*?)\n/g, '<h4 style="color:var(--accent-green); margin:8px 0;">$1</h4>')
            .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
            .replace(/`(.*?)`/g, '<code style="background:rgba(255,255,255,0.06); padding:2px 4px; border-radius:4px;">$1</code>')
            .replace(/- (.*?)\n/g, '<div style="margin:4px 0 4px 12px;">• $1</div>')
            .replace(/\n\n/g, '<br>');
    }

    function showToast(message, isError = false) {
        toast.textContent = message;
        toast.style.borderColor = isError ? 'var(--accent-red)' : 'var(--accent-gold)';
        toast.classList.remove('hidden');
        setTimeout(() => toast.classList.add('hidden'), 3500);
    }

    function escapeHtml(text) {
        if (!text) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
});
