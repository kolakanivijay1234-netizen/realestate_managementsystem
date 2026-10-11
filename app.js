/**
 * Real Estate Agent Mandate Management System
 * Front-end OOP and Database Management Architecture
 *
 * Implements:
 * 1. Object-Oriented Modeling (Agent, GeographicalArea, Agency, IndependentAgency, FranchiseAgency, Mandate)
 * 2. Inheritance and Polymorphism (Agency hierarchy)
 * 3. Encapsulation (methods, validation)
 * 4. Exception Handling
 * 5. Full CRUD & Search Operations across all modules
 * 6. Date validation: End Date > Start Date
 * 7. 15 Analytical Reports
 * 8. Automatic Storage Layer: Hybrid Client-side LocalStorage (with MySQL schema parity) + optional Backend API
 */

// ============================================================================
// OOP DOMAIN MODELS
// ============================================================================

/**
 * Superclass Agency (demonstrating Inheritance & Polymorphism)
 */
class Agency {
  constructor(agencyId, agencyName, agencyType, address, phone) {
    this._agencyId = parseInt(agencyId, 10);
    this._agencyName = agencyName;
    this._agencyType = agencyType;
    this._address = address || '';
    this._phone = phone || '';
  }

  get agencyId() { return this._agencyId; }
  set agencyId(val) { this._agencyId = parseInt(val, 10); }

  get agencyName() { return this._agencyName; }
  set agencyName(val) { this._agencyName = val; }

  get agencyType() { return this._agencyType; }
  set agencyType(val) { this._agencyType = val; }

  get address() { return this._address; }
  set address(val) { this._address = val; }

  get phone() { return this._phone; }
  set phone(val) { this._phone = val; }

  /**
   * Polymorphic method overridden by subclasses
   */
  getAgencyDescription() {
    return `Agency: ${this._agencyName} (${this._agencyType})`;
  }

  toJSON() {
    return {
      agencyId: this._agencyId,
      agencyName: this._agencyName,
      agencyType: this._agencyType,
      address: this._address,
      phone: this._phone
    };
  }
}

/**
 * Subclass IndependentAgency
 */
class IndependentAgency extends Agency {
  constructor(agencyId, agencyName, agencyType, address, phone, ownerName) {
    super(agencyId, agencyName, 'Independent', address, phone);
    this._ownerName = ownerName || 'Proprietor';
  }

  get ownerName() { return this._ownerName; }
  set ownerName(val) { this._ownerName = val; }

  /**
   * Polymorphic override
   */
  getAgencyDescription() {
    return `Independent Agency: ${this.agencyName} owned by ${this._ownerName}`;
  }

  toJSON() {
    return {
      ...super.toJSON(),
      ownerName: this._ownerName
    };
  }
}

/**
 * Subclass FranchiseAgency
 */
class FranchiseAgency extends Agency {
  constructor(agencyId, agencyName, agencyType, address, phone, franchiseBrand) {
    super(agencyId, agencyName, 'Franchise', address, phone);
    this._franchiseBrand = franchiseBrand || agencyName;
  }

  get franchiseBrand() { return this._franchiseBrand; }
  set franchiseBrand(val) { this._franchiseBrand = val; }

  /**
   * Polymorphic override
   */
  getAgencyDescription() {
    return `Franchise Agency: ${this.agencyName} under brand ${this._franchiseBrand}`;
  }

  toJSON() {
    return {
      ...super.toJSON(),
      franchiseBrand: this._franchiseBrand
    };
  }
}

/**
 * Factory for polymorphic agency creation
 */
function createAgencyInstance(raw) {
  if (raw.agencyType === 'Independent') {
    return new IndependentAgency(
      raw.agencyId,
      raw.agencyName,
      raw.agencyType,
      raw.address,
      raw.phone,
      raw.ownerName || raw.subParam || 'Independent Owner'
    );
  } else {
    return new FranchiseAgency(
      raw.agencyId,
      raw.agencyName,
      raw.agencyType,
      raw.address,
      raw.phone,
      raw.franchiseBrand || raw.subParam || 'Global Realty Network'
    );
  }
}

/**
 * Agent Model
 */
class Agent {
  constructor(agentId, agentName, phone, email, agencyId) {
    this._agentId = parseInt(agentId, 10);
    this._agentName = agentName;
    this._phone = phone;
    this._email = email;
    this._agencyId = parseInt(agencyId, 10);
  }

  get agentId() { return this._agentId; }
  set agentId(val) { this._agentId = parseInt(val, 10); }

  get agentName() { return this._agentName; }
  set agentName(val) { this._agentName = val; }

  get phone() { return this._phone; }
  set phone(val) { this._phone = val; }

  get email() { return this._email; }
  set email(val) { this._email = val; }

  get agencyId() { return this._agencyId; }
  set agencyId(val) { this._agencyId = parseInt(val, 10); }

  toJSON() {
    return {
      agentId: this._agentId,
      agentName: this._agentName,
      phone: this._phone,
      email: this._email,
      agencyId: this._agencyId
    };
  }
}

/**
 * GeographicalArea Model
 */
class GeographicalArea {
  constructor(areaId, areaName, population) {
    this._areaId = parseInt(areaId, 10);
    this._areaName = areaName;
    this._population = parseInt(population, 10);
  }

  get areaId() { return this._areaId; }
  set areaId(val) { this._areaId = parseInt(val, 10); }

  get areaName() { return this._areaName; }
  set areaName(val) { this._areaName = val; }

  get population() { return this._population; }
  set population(val) { this._population = parseInt(val, 10); }

  toJSON() {
    return {
      areaId: this._areaId,
      areaName: this._areaName,
      population: this._population
    };
  }
}

/**
 * Mandate Model
 */
class Mandate {
  constructor(mandateId, agentId, agencyId, areaId, startDate, endDate, mandateType) {
    this._mandateId = parseInt(mandateId, 10);
    this._agentId = parseInt(agentId, 10);
    this._agencyId = parseInt(agencyId, 10);
    this._areaId = parseInt(areaId, 10);
    this._startDate = startDate; // 'YYYY-MM-DD'
    this._endDate = endDate;     // 'YYYY-MM-DD'
    this._mandateType = mandateType; // 'SALE', 'RENT', 'PURCHASE', 'LEASE'
  }

  get mandateId() { return this._mandateId; }
  set mandateId(val) { this._mandateId = parseInt(val, 10); }

  get agentId() { return this._agentId; }
  set agentId(val) { this._agentId = parseInt(val, 10); }

  get agencyId() { return this._agencyId; }
  set agencyId(val) { this._agencyId = parseInt(val, 10); }

  get areaId() { return this._areaId; }
  set areaId(val) { this._areaId = parseInt(val, 10); }

  get startDate() { return this._startDate; }
  set startDate(val) { this._startDate = val; }

  get endDate() { return this._endDate; }
  set endDate(val) { this._endDate = val; }

  get mandateType() { return this._mandateType; }
  set mandateType(val) { this._mandateType = val; }

  /**
   * Validates date sequence: End Date MUST be > Start Date
   */
  validateDates() {
    if (!this._startDate || !this._endDate) {
      throw new Error("Both Start Date and End Date are required.");
    }
    const start = new Date(this._startDate);
    const end = new Date(this._endDate);
    if (isNaN(start.getTime()) || isNaN(end.getTime())) {
      throw new Error("Invalid date format. Use YYYY-MM-DD.");
    }
    if (end <= start) {
      throw new Error("Invalid Mandate: End date must be greater than start date.");
    }
  }

  /**
   * Check if active on a specific date (defaults to current date)
   */
  isActive(dateStr) {
    const target = dateStr ? new Date(dateStr) : new Date();
    target.setHours(0, 0, 0, 0);

    const start = new Date(this._startDate);
    start.setHours(0, 0, 0, 0);

    const end = new Date(this._endDate);
    end.setHours(23, 59, 59, 999);

    return target >= start && target <= end;
  }

  toJSON() {
    return {
      mandateId: this._mandateId,
      agentId: this._agentId,
      agencyId: this._agencyId,
      areaId: this._areaId,
      startDate: this._startDate,
      endDate: this._endDate,
      mandateType: this._mandateType
    };
  }
}

// ============================================================================
// DATA STORAGE & PERSISTENCE LAYER (MySQL Parity + LocalStorage fallback)
// ============================================================================

const STORAGE_KEYS = {
  AGENTS: 'rem_agents',
  AGENCIES: 'rem_agencies',
  AREAS: 'rem_areas',
  MANDATES: 'rem_mandates'
};

const DEFAULT_DATA = {
  agencies: [
    { agencyId: 1, agencyName: 'Prime Realty Network', agencyType: 'Franchise', franchiseBrand: 'Prime Global', address: '101 Corporate Blvd, Metro City', phone: '9876543210' },
    { agencyId: 2, agencyName: 'Heritage Real Estate', agencyType: 'Independent', ownerName: 'A. R. Heritage', address: '45 Market Street, Old Town', phone: '9876543211' },
    { agencyId: 3, agencyName: 'Global Apex Estates', agencyType: 'Franchise', franchiseBrand: 'Apex International', address: '500 Skyline Tower, Downtown', phone: '9876543212' }
  ],
  areas: [
    { areaId: 1, areaName: 'Downtown Metro', population: 250000 },
    { areaId: 2, areaName: 'North Hills Suburb', population: 85000 },
    { areaId: 3, areaName: 'West Coast Bay', population: 140000 }
  ],
  agents: [
    { agentId: 1, agentName: 'Vijay Kumar', phone: '9876543220', email: 'vijay.k@realty.com', agencyId: 1 },
    { agentId: 2, agentName: 'Ananya Sharma', phone: '9876543221', email: 'ananya.s@heritage.com', agencyId: 2 },
    { agentId: 3, agentName: 'Rahul Verma', phone: '9876543222', email: 'rahul.v@apex.com', agencyId: 3 }
  ],
  mandates: [
    { mandateId: 1, agentId: 1, agencyId: 1, areaId: 1, startDate: '2026-01-01', endDate: '2026-12-31', mandateType: 'SALE' },
    { mandateId: 2, agentId: 1, agencyId: 1, areaId: 2, startDate: '2026-02-15', endDate: '2026-11-30', mandateType: 'RENT' },
    { mandateId: 3, agentId: 2, agencyId: 2, areaId: 2, startDate: '2026-03-01', endDate: '2026-09-30', mandateType: 'PURCHASE' },
    { mandateId: 4, agentId: 3, agencyId: 3, areaId: 3, startDate: '2025-01-01', endDate: '2025-12-31', mandateType: 'LEASE' }
  ]
};

class DatabaseManager {
  static init() {
    if (!localStorage.getItem(STORAGE_KEYS.AGENCIES)) {
      DatabaseManager.resetDemoData();
    }
  }

  static resetDemoData() {
    localStorage.setItem(STORAGE_KEYS.AGENCIES, JSON.stringify(DEFAULT_DATA.agencies));
    localStorage.setItem(STORAGE_KEYS.AREAS, JSON.stringify(DEFAULT_DATA.areas));
    localStorage.setItem(STORAGE_KEYS.AGENTS, JSON.stringify(DEFAULT_DATA.agents));
    localStorage.setItem(STORAGE_KEYS.MANDATES, JSON.stringify(DEFAULT_DATA.mandates));
  }

  // --- AGENCIES DAO ---
  static getAgencies() {
    const raw = JSON.parse(localStorage.getItem(STORAGE_KEYS.AGENCIES) || '[]');
    return raw.map(createAgencyInstance);
  }

  static saveAgencies(list) {
    localStorage.setItem(STORAGE_KEYS.AGENCIES, JSON.stringify(list.map(a => a.toJSON())));
  }

  static getAgencyById(id) {
    return this.getAgencies().find(a => a.agencyId === parseInt(id, 10)) || null;
  }

  static addAgency(agency) {
    const list = this.getAgencies();
    if (list.some(a => a.agencyId === agency.agencyId)) {
      throw new Error(`Duplicate Record: Agency with ID ${agency.agencyId} already exists.`);
    }
    list.push(agency);
    this.saveAgencies(list);
  }

  static updateAgency(agency) {
    const list = this.getAgencies();
    const idx = list.findIndex(a => a.agencyId === agency.agencyId);
    if (idx === -1) {
      throw new Error(`Agency ID ${agency.agencyId} not found.`);
    }
    list[idx] = agency;
    this.saveAgencies(list);
  }

  static deleteAgency(id) {
    const numericId = parseInt(id, 10);
    // Check foreign keys in agents & mandates
    const agents = this.getAgents();
    if (agents.some(a => a.agencyId === numericId)) {
      throw new Error(`Cannot delete Agency ${numericId}: Agents are currently assigned to it.`);
    }
    const mandates = this.getMandates();
    if (mandates.some(m => m.agencyId === numericId)) {
      throw new Error(`Cannot delete Agency ${numericId}: Mandates are linked to it.`);
    }

    const list = this.getAgencies().filter(a => a.agencyId !== numericId);
    this.saveAgencies(list);
  }

  // --- AGENTS DAO ---
  static getAgents() {
    const raw = JSON.parse(localStorage.getItem(STORAGE_KEYS.AGENTS) || '[]');
    return raw.map(r => new Agent(r.agentId, r.agentName, r.phone, r.email, r.agencyId));
  }

  static saveAgents(list) {
    localStorage.setItem(STORAGE_KEYS.AGENTS, JSON.stringify(list.map(a => a.toJSON())));
  }

  static getAgentById(id) {
    return this.getAgents().find(a => a.agentId === parseInt(id, 10)) || null;
  }

  static addAgent(agent) {
    const list = this.getAgents();
    if (list.some(a => a.agentId === agent.agentId)) {
      throw new Error(`Duplicate Record: Agent ID ${agent.agentId} already exists.`);
    }
    if (!this.getAgencyById(agent.agencyId)) {
      throw new Error(`Agency ID ${agent.agencyId} does not exist.`);
    }
    list.push(agent);
    this.saveAgents(list);
  }

  static updateAgent(agent) {
    const list = this.getAgents();
    const idx = list.findIndex(a => a.agentId === agent.agentId);
    if (idx === -1) throw new Error(`Agent ID ${agent.agentId} not found.`);
    if (!this.getAgencyById(agent.agencyId)) throw new Error(`Agency ID ${agent.agencyId} does not exist.`);
    list[idx] = agent;
    this.saveAgents(list);
  }

  static deleteAgent(id) {
    const numericId = parseInt(id, 10);
    const mandates = this.getMandates();
    if (mandates.some(m => m.agentId === numericId)) {
      throw new Error(`Cannot delete Agent ${numericId}: Mandates are assigned to this agent.`);
    }
    const list = this.getAgents().filter(a => a.agentId !== numericId);
    this.saveAgents(list);
  }

  // --- AREAS DAO ---
  static getAreas() {
    const raw = JSON.parse(localStorage.getItem(STORAGE_KEYS.AREAS) || '[]');
    return raw.map(r => new GeographicalArea(r.areaId, r.areaName, r.population));
  }

  static saveAreas(list) {
    localStorage.setItem(STORAGE_KEYS.AREAS, JSON.stringify(list.map(a => a.toJSON())));
  }

  static getAreaById(id) {
    return this.getAreas().find(a => a.areaId === parseInt(id, 10)) || null;
  }

  static addArea(area) {
    const list = this.getAreas();
    if (list.some(a => a.areaId === area.areaId)) {
      throw new Error(`Duplicate Record: Area ID ${area.areaId} already exists.`);
    }
    list.push(area);
    this.saveAreas(list);
  }

  static updateArea(area) {
    const list = this.getAreas();
    const idx = list.findIndex(a => a.areaId === area.areaId);
    if (idx === -1) throw new Error(`Area ID ${area.areaId} not found.`);
    list[idx] = area;
    this.saveAreas(list);
  }

  static deleteArea(id) {
    const numericId = parseInt(id, 10);
    const mandates = this.getMandates();
    if (mandates.some(m => m.areaId === numericId)) {
      throw new Error(`Cannot delete Area ${numericId}: Mandates are active in this geographical area.`);
    }
    const list = this.getAreas().filter(a => a.areaId !== numericId);
    this.saveAreas(list);
  }

  // --- MANDATES DAO ---
  static getMandates() {
    const raw = JSON.parse(localStorage.getItem(STORAGE_KEYS.MANDATES) || '[]');
    return raw.map(r => new Mandate(r.mandateId, r.agentId, r.agencyId, r.areaId, r.startDate, r.endDate, r.mandateType));
  }

  static saveMandates(list) {
    localStorage.setItem(STORAGE_KEYS.MANDATES, JSON.stringify(list.map(m => m.toJSON())));
  }

  static getMandateById(id) {
    return this.getMandates().find(m => m.mandateId === parseInt(id, 10)) || null;
  }

  static addMandate(mandate) {
    // Validations
    mandate.validateDates();

    if (!this.getAgentById(mandate.agentId)) throw new Error(`Agent ID ${mandate.agentId} does not exist.`);
    if (!this.getAgencyById(mandate.agencyId)) throw new Error(`Agency ID ${mandate.agencyId} does not exist.`);
    if (!this.getAreaById(mandate.areaId)) throw new Error(`Area ID ${mandate.areaId} does not exist.`);

    const list = this.getMandates();
    if (list.some(m => m.mandateId === mandate.mandateId)) {
      throw new Error(`Duplicate Record: Mandate ID ${mandate.mandateId} already exists.`);
    }
    list.push(mandate);
    this.saveMandates(list);
  }

  static updateMandate(mandate) {
    mandate.validateDates();
    if (!this.getAgentById(mandate.agentId)) throw new Error(`Agent ID ${mandate.agentId} does not exist.`);
    if (!this.getAgencyById(mandate.agencyId)) throw new Error(`Agency ID ${mandate.agencyId} does not exist.`);
    if (!this.getAreaById(mandate.areaId)) throw new Error(`Area ID ${mandate.areaId} does not exist.`);

    const list = this.getMandates();
    const idx = list.findIndex(m => m.mandateId === mandate.mandateId);
    if (idx === -1) throw new Error(`Mandate ID ${mandate.mandateId} not found.`);
    list[idx] = mandate;
    this.saveMandates(list);
  }

  static deleteMandate(id) {
    const numericId = parseInt(id, 10);
    const list = this.getMandates().filter(m => m.mandateId !== numericId);
    this.saveMandates(list);
  }

  // Helper next ID generators
  static nextAgentId() {
    const list = this.getAgents();
    return list.length ? Math.max(...list.map(a => a.agentId)) + 1 : 1;
  }
  static nextAgencyId() {
    const list = this.getAgencies();
    return list.length ? Math.max(...list.map(a => a.agencyId)) + 1 : 1;
  }
  static nextAreaId() {
    const list = this.getAreas();
    return list.length ? Math.max(...list.map(a => a.areaId)) + 1 : 1;
  }
  static nextMandateId() {
    const list = this.getMandates();
    return list.length ? Math.max(...list.map(m => m.mandateId)) + 1 : 1;
  }
}

// ============================================================================
// UI CONTROLLER & VIEW LOGIC
// ============================================================================

class AppController {
  constructor() {
    DatabaseManager.init();
    this.initElements();
    this.bindEvents();
    this.refreshAll();
  }

  initElements() {
    // Navigation
    this.tabButtons = document.querySelectorAll('.tab-btn');
    this.tabPanes = document.querySelectorAll('.tab-pane');

    // Dashboard
    this.dashTotalAgents = document.getElementById('dashTotalAgents');
    this.dashTotalAgencies = document.getElementById('dashTotalAgencies');
    this.dashTotalAreas = document.getElementById('dashTotalAreas');
    this.dashTotalMandates = document.getElementById('dashTotalMandates');
    this.dashActiveMandates = document.getElementById('dashActiveMandates');
    this.dashAgentsWithMandates = document.getElementById('dashAgentsWithMandates');
    this.dashMaxAgentName = document.getElementById('dashMaxAgentName');
    this.dashMaxAgentCount = document.getElementById('dashMaxAgentCount');
    this.dashAgencyBreakdown = document.getElementById('dashAgencyBreakdown');
    this.btnRefreshDashboard = document.getElementById('btnRefreshDashboard');

    // Agents
    this.agentForm = document.getElementById('agentForm');
    this.agentIdInput = document.getElementById('agentId');
    this.agentNameInput = document.getElementById('agentName');
    this.agentPhoneInput = document.getElementById('agentPhone');
    this.agentEmailInput = document.getElementById('agentEmail');
    this.agentAgencySelect = document.getElementById('agentAgencyId');
    this.agentTableBody = document.getElementById('agentTableBody');
    this.agentCountBadge = document.getElementById('agentCountBadge');

    // Agencies
    this.agencyForm = document.getElementById('agencyForm');
    this.agencyIdInput = document.getElementById('agencyId');
    this.agencyNameInput = document.getElementById('agencyName');
    this.agencyTypeSelect = document.getElementById('agencyType');
    this.agencySubParamInput = document.getElementById('agencySubParam');
    this.lblSubParam = document.getElementById('lblSubParam');
    this.agencyAddressInput = document.getElementById('agencyAddress');
    this.agencyPhoneInput = document.getElementById('agencyPhone');
    this.agencyTableBody = document.getElementById('agencyTableBody');
    this.agencyCountBadge = document.getElementById('agencyCountBadge');

    // Areas
    this.areaForm = document.getElementById('areaForm');
    this.areaIdInput = document.getElementById('areaId');
    this.areaNameInput = document.getElementById('areaName');
    this.areaPopulationInput = document.getElementById('areaPopulation');
    this.areaTableBody = document.getElementById('areaTableBody');
    this.areaCountBadge = document.getElementById('areaCountBadge');

    // Mandates
    this.mandateForm = document.getElementById('mandateForm');
    this.mandateIdInput = document.getElementById('mandateId');
    this.mandateAgentSelect = document.getElementById('mandateAgentId');
    this.mandateAgencySelect = document.getElementById('mandateAgencyId');
    this.mandateAreaSelect = document.getElementById('mandateAreaId');
    this.mandateStartDateInput = document.getElementById('mandateStartDate');
    this.mandateEndDateInput = document.getElementById('mandateEndDate');
    this.mandateTypeSelect = document.getElementById('mandateType');
    this.mandateTableBody = document.getElementById('mandateTableBody');
    this.mandateCountBadge = document.getElementById('mandateCountBadge');

    // Reports
    this.reportSelector = document.getElementById('reportSelector');
    this.reportDateFilter = document.getElementById('reportDateFilter');
    this.reportDateInput = document.getElementById('reportDateInput');
    this.btnRunReport = document.getElementById('btnRunReport');
    this.btnExportReport = document.getElementById('btnExportReport');
    this.reportTitle = document.getElementById('reportTitle');
    this.reportCountBadge = document.getElementById('reportCountBadge');
    this.reportTableHead = document.getElementById('reportTableHead');
    this.reportTableBody = document.getElementById('reportTableBody');

    // Modal
    this.modalBackdrop = document.getElementById('modalBackdrop');
    this.modalTitle = document.getElementById('modalTitle');
    this.modalMessage = document.getElementById('modalMessage');
    this.modalBtnConfirm = document.getElementById('modalBtnConfirm');
    this.modalBtnCancel = document.getElementById('modalBtnCancel');

    // Top Header Buttons
    this.btnExportAll = document.getElementById('btnExportAll');
    this.btnResetData = document.getElementById('btnResetData');

    // Set today's date for report date picker default
    this.reportDateInput.value = new Date().toISOString().split('T')[0];
  }

  bindEvents() {
    // Navigation tabs
    this.tabButtons.forEach(btn => {
      btn.addEventListener('click', () => {
        const tabId = btn.getAttribute('data-tab');
        this.switchTab(tabId);
      });
    });

    // Dashboard refresh
    this.btnRefreshDashboard.addEventListener('click', () => {
      this.renderDashboard();
      this.toast('Statistics refreshed.', 'info');
    });

    // Agency type change (toggles label for owner / franchise brand)
    this.agencyTypeSelect.addEventListener('change', () => {
      if (this.agencyTypeSelect.value === 'Independent') {
        this.lblSubParam.textContent = 'Owner Name (for Independent):';
        this.agencySubParamInput.placeholder = 'e.g. John Doe';
      } else {
        this.lblSubParam.textContent = 'Franchise Brand (for Franchise):';
        this.agencySubParamInput.placeholder = 'e.g. Apex Global Network';
      }
    });

    // AGENT BUTTONS
    document.getElementById('btnAddAgent').addEventListener('click', () => this.handleAddAgent());
    document.getElementById('btnUpdateAgent').addEventListener('click', () => this.handleUpdateAgent());
    document.getElementById('btnDeleteAgent').addEventListener('click', () => this.handleDeleteAgent());
    document.getElementById('btnSearchAgent').addEventListener('click', () => this.handleSearchAgent());
    document.getElementById('btnClearAgent').addEventListener('click', () => this.clearAgentForm());
    document.getElementById('btnRefreshAgents').addEventListener('click', () => this.renderAgents());

    // AGENCY BUTTONS
    document.getElementById('btnAddAgency').addEventListener('click', () => this.handleAddAgency());
    document.getElementById('btnUpdateAgency').addEventListener('click', () => this.handleUpdateAgency());
    document.getElementById('btnDeleteAgency').addEventListener('click', () => this.handleDeleteAgency());
    document.getElementById('btnSearchAgency').addEventListener('click', () => this.handleSearchAgency());
    document.getElementById('btnClearAgency').addEventListener('click', () => this.clearAgencyForm());
    document.getElementById('btnRefreshAgencies').addEventListener('click', () => this.renderAgencies());

    // AREA BUTTONS
    document.getElementById('btnAddArea').addEventListener('click', () => this.handleAddArea());
    document.getElementById('btnUpdateArea').addEventListener('click', () => this.handleUpdateArea());
    document.getElementById('btnDeleteArea').addEventListener('click', () => this.handleDeleteArea());
    document.getElementById('btnSearchArea').addEventListener('click', () => this.handleSearchArea());
    document.getElementById('btnClearArea').addEventListener('click', () => this.clearAreaForm());
    document.getElementById('btnRefreshAreas').addEventListener('click', () => this.renderAreas());

    // MANDATE BUTTONS
    document.getElementById('btnAddMandate').addEventListener('click', () => this.handleAddMandate());
    document.getElementById('btnUpdateMandate').addEventListener('click', () => this.handleUpdateMandate());
    document.getElementById('btnDeleteMandate').addEventListener('click', () => this.handleDeleteMandate());
    document.getElementById('btnSearchMandate').addEventListener('click', () => this.handleSearchMandate());
    document.getElementById('btnClearMandate').addEventListener('click', () => this.clearMandateForm());
    document.getElementById('btnRefreshMandates').addEventListener('click', () => this.renderMandates());

    // REPORT EVENTS
    this.reportSelector.addEventListener('change', () => {
      const val = this.reportSelector.value;
      this.reportDateFilter.style.display = (val === '4') ? 'block' : 'none';
      this.runReport();
    });
    this.btnRunReport.addEventListener('click', () => this.runReport());
    this.btnExportReport.addEventListener('click', () => this.exportReportToCSV());

    // TOP ACTIONS
    this.btnExportAll.addEventListener('click', () => this.exportAllDataJSON());
    this.btnResetData.addEventListener('click', () => {
      this.showConfirmDialog("Are you sure you want to reset all data back to the default demo state?", () => {
        DatabaseManager.resetDemoData();
        this.refreshAll();
        this.toast("Demo data reloaded.", "info");
      });
    });

    // MODAL CANCEL
    this.modalBtnCancel.addEventListener('click', () => {
      this.modalBackdrop.style.display = 'none';
    });
  }

  switchTab(tabId) {
    this.tabButtons.forEach(btn => {
      btn.classList.toggle('active', btn.getAttribute('data-tab') === tabId);
    });
    this.tabPanes.forEach(pane => {
      pane.classList.toggle('active', pane.id === `tab-${tabId}`);
    });

    // Auto refresh active pane
    if (tabId === 'dashboard') this.renderDashboard();
    if (tabId === 'agents') this.renderAgents();
    if (tabId === 'agencies') this.renderAgencies();
    if (tabId === 'areas') this.renderAreas();
    if (tabId === 'mandates') this.renderMandates();
    if (tabId === 'reports') this.runReport();
  }

  refreshAll() {
    this.populateDropdowns();
    this.renderDashboard();
    this.renderAgents();
    this.renderAgencies();
    this.renderAreas();
    this.renderMandates();
    this.runReport();
  }

  populateDropdowns() {
    const agencies = DatabaseManager.getAgencies();
    const agents = DatabaseManager.getAgents();
    const areas = DatabaseManager.getAreas();

    // Agent Form: Agency dropdown
    this.agentAgencySelect.innerHTML = agencies.map(a =>
      `<option value="${a.agencyId}">${a.agencyId} - ${a.agencyName}</option>`
    ).join('');

    // Mandate Form: Dropdowns
    this.mandateAgencySelect.innerHTML = agencies.map(a =>
      `<option value="${a.agencyId}">${a.agencyId} - ${a.agencyName}</option>`
    ).join('');

    this.mandateAgentSelect.innerHTML = agents.map(a =>
      `<option value="${a.agentId}">${a.agentId} - ${a.agentName}</option>`
    ).join('');

    this.mandateAreaSelect.innerHTML = areas.map(a =>
      `<option value="${a.areaId}">${a.areaId} - ${a.areaName}</option>`
    ).join('');
  }

  // ==========================================================================
  // DASHBOARD RENDER
  // ==========================================================================
  renderDashboard() {
    const agents = DatabaseManager.getAgents();
    const agencies = DatabaseManager.getAgencies();
    const areas = DatabaseManager.getAreas();
    const mandates = DatabaseManager.getMandates();

    this.dashTotalAgents.textContent = agents.length;
    this.dashTotalAgencies.textContent = agencies.length;
    this.dashTotalAreas.textContent = areas.length;
    this.dashTotalMandates.textContent = mandates.length;

    // Active Mandates count
    const activeMandates = mandates.filter(m => m.isActive()).length;
    this.dashActiveMandates.textContent = activeMandates;

    // Agents with at least 1 mandate
    const agentsWithMandatesSet = new Set(mandates.map(m => m.agentId));
    this.dashAgentsWithMandates.textContent = agentsWithMandatesSet.size;

    // Top Performing Agent
    const agentCountMap = {};
    mandates.forEach(m => {
      agentCountMap[m.agentId] = (agentCountMap[m.agentId] || 0) + 1;
    });

    let maxAgentId = null;
    let maxCount = 0;
    for (const [aId, count] of Object.entries(agentCountMap)) {
      if (count > maxCount) {
        maxCount = count;
        maxAgentId = parseInt(aId, 10);
      }
    }

    if (maxAgentId) {
      const topAgent = agents.find(a => a.agentId === maxAgentId);
      this.dashMaxAgentName.textContent = topAgent ? topAgent.agentName : `Agent #${maxAgentId}`;
      this.dashMaxAgentCount.textContent = `${maxCount} Mandate(s) assigned`;
    } else {
      this.dashMaxAgentName.textContent = 'None';
      this.dashMaxAgentCount.textContent = '0 Mandates assigned';
    }

    // Agency breakdown
    const independentCount = agencies.filter(a => a.agencyType === 'Independent').length;
    const franchiseCount = agencies.filter(a => a.agencyType === 'Franchise').length;
    this.dashAgencyBreakdown.textContent = `${independentCount} Ind / ${franchiseCount} Fran`;
  }

  // ==========================================================================
  // AGENTS MODULE
  // ==========================================================================
  renderAgents(list = null) {
    const agents = list || DatabaseManager.getAgents();
    const agencies = DatabaseManager.getAgencies();
    const agencyMap = Object.fromEntries(agencies.map(a => [a.agencyId, a.agencyName]));

    this.agentCountBadge.textContent = `${agents.length} agent(s)`;
    this.agentTableBody.innerHTML = '';

    agents.forEach(agent => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td><strong>#${agent.agentId}</strong></td>
        <td>${escapeHtml(agent.agentName)}</td>
        <td>${escapeHtml(agent.phone)}</td>
        <td>${escapeHtml(agent.email)}</td>
        <td>${agent.agencyId}</td>
        <td>${escapeHtml(agencyMap[agent.agencyId] || 'N/A')}</td>
      `;
      tr.addEventListener('click', () => {
        document.querySelectorAll('#agentTable tbody tr').forEach(r => r.classList.remove('selected-row'));
        tr.classList.add('selected-row');
        this.populateAgentForm(agent);
      });
      this.agentTableBody.appendChild(tr);
    });
  }

  populateAgentForm(agent) {
    this.agentIdInput.value = agent.agentId;
    this.agentNameInput.value = agent.agentName;
    this.agentPhoneInput.value = agent.phone;
    this.agentEmailInput.value = agent.email;
    this.agentAgencySelect.value = agent.agencyId;
  }

  clearAgentForm() {
    this.agentIdInput.value = '';
    this.agentNameInput.value = '';
    this.agentPhoneInput.value = '';
    this.agentEmailInput.value = '';
    document.querySelectorAll('#agentTable tbody tr').forEach(r => r.classList.remove('selected-row'));
  }

  handleAddAgent() {
    try {
      const name = this.agentNameInput.value.trim();
      const phone = this.agentPhoneInput.value.trim();
      const email = this.agentEmailInput.value.trim();
      const agencyId = parseInt(this.agentAgencySelect.value, 10);
      const agentId = this.agentIdInput.value ? parseInt(this.agentIdInput.value, 10) : DatabaseManager.nextAgentId();

      if (!name) throw new Error("Agent Name cannot be empty.");
      if (!/^\d{10,}$/.test(phone)) throw new Error("Phone must contain at least 10 digits.");
      if (!email.includes('@') || !email.includes('.')) throw new Error("Please enter a valid email address.");

      const agent = new Agent(agentId, name, phone, email, agencyId);
      DatabaseManager.addAgent(agent);

      this.toast(`Agent added successfully (ID #${agentId})`, 'success');
      this.clearAgentForm();
      this.refreshAll();
    } catch (err) {
      this.toast(err.message, 'error');
    }
  }

  handleUpdateAgent() {
    try {
      const idStr = this.agentIdInput.value.trim();
      if (!idStr) throw new Error("Please select an agent to update or enter Agent ID.");

      const agentId = parseInt(idStr, 10);
      const name = this.agentNameInput.value.trim();
      const phone = this.agentPhoneInput.value.trim();
      const email = this.agentEmailInput.value.trim();
      const agencyId = parseInt(this.agentAgencySelect.value, 10);

      if (!name) throw new Error("Agent Name cannot be empty.");
      if (!/^\d{10,}$/.test(phone)) throw new Error("Phone must contain at least 10 digits.");
      if (!email.includes('@') || !email.includes('.')) throw new Error("Please enter a valid email address.");

      const agent = new Agent(agentId, name, phone, email, agencyId);
      DatabaseManager.updateAgent(agent);

      this.toast(`Agent #${agentId} updated successfully.`, 'success');
      this.clearAgentForm();
      this.refreshAll();
    } catch (err) {
      this.toast(err.message, 'error');
    }
  }

  handleDeleteAgent() {
    const idStr = this.agentIdInput.value.trim();
    if (!idStr) {
      this.toast("Please select an Agent to delete.", 'error');
      return;
    }
    const agentId = parseInt(idStr, 10);
    this.showConfirmDialog(`Are you sure you want to delete Agent ID ${agentId}?`, () => {
      try {
        DatabaseManager.deleteAgent(agentId);
        this.toast(`Agent #${agentId} deleted.`, 'success');
        this.clearAgentForm();
        this.refreshAll();
      } catch (err) {
        this.toast(err.message, 'error');
      }
    });
  }

  handleSearchAgent() {
    const idQuery = this.agentIdInput.value.trim();
    const nameQuery = this.agentNameInput.value.trim().toLowerCase();

    let results = DatabaseManager.getAgents();
    if (idQuery) {
      results = results.filter(a => a.agentId === parseInt(idQuery, 10));
    } else if (nameQuery) {
      results = results.filter(a => a.agentName.toLowerCase().includes(nameQuery));
    } else {
      this.toast("Enter Agent ID or Name to search.", 'info');
      this.renderAgents();
      return;
    }

    this.renderAgents(results);
    this.toast(`Found ${results.length} matching agent(s).`, 'info');
  }

  // ==========================================================================
  // AGENCIES MODULE (Polymorphic)
  // ==========================================================================
  renderAgencies(list = null) {
    const agencies = list || DatabaseManager.getAgencies();
    this.agencyCountBadge.textContent = `${agencies.length} agency(ies)`;
    this.agencyTableBody.innerHTML = '';

    agencies.forEach(agency => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td><strong>#${agency.agencyId}</strong></td>
        <td>${escapeHtml(agency.agencyName)}</td>
        <td><span class="status-pill ${agency.agencyType === 'Independent' ? 'status-active' : 'status-expired'}">${escapeHtml(agency.agencyType)}</span></td>
        <td><em>${escapeHtml(agency.getAgencyDescription())}</em></td>
        <td>${escapeHtml(agency.address || '-')}</td>
        <td>${escapeHtml(agency.phone || '-')}</td>
      `;
      tr.addEventListener('click', () => {
        document.querySelectorAll('#agencyTable tbody tr').forEach(r => r.classList.remove('selected-row'));
        tr.classList.add('selected-row');
        this.populateAgencyForm(agency);
      });
      this.agencyTableBody.appendChild(tr);
    });
  }

  populateAgencyForm(agency) {
    this.agencyIdInput.value = agency.agencyId;
    this.agencyNameInput.value = agency.agencyName;
    this.agencyTypeSelect.value = agency.agencyType;
    this.agencyAddressInput.value = agency.address;
    this.agencyPhoneInput.value = agency.phone;

    if (agency instanceof IndependentAgency) {
      this.agencySubParamInput.value = agency.ownerName;
      this.lblSubParam.textContent = 'Owner Name (for Independent):';
    } else if (agency instanceof FranchiseAgency) {
      this.agencySubParamInput.value = agency.franchiseBrand;
      this.lblSubParam.textContent = 'Franchise Brand (for Franchise):';
    }
  }

  clearAgencyForm() {
    this.agencyIdInput.value = '';
    this.agencyNameInput.value = '';
    this.agencySubParamInput.value = '';
    this.agencyAddressInput.value = '';
    this.agencyPhoneInput.value = '';
    document.querySelectorAll('#agencyTable tbody tr').forEach(r => r.classList.remove('selected-row'));
  }

  handleAddAgency() {
    try {
      const name = this.agencyNameInput.value.trim();
      const type = this.agencyTypeSelect.value;
      const subParam = this.agencySubParamInput.value.trim();
      const address = this.agencyAddressInput.value.trim();
      const phone = this.agencyPhoneInput.value.trim();
      const agencyId = this.agencyIdInput.value ? parseInt(this.agencyIdInput.value, 10) : DatabaseManager.nextAgencyId();

      if (!name) throw new Error("Agency Name cannot be empty.");
      if (!type) throw new Error("Agency Type must be selected.");

      const agency = createAgencyInstance({
        agencyId,
        agencyName: name,
        agencyType: type,
        subParam,
        address,
        phone
      });

      DatabaseManager.addAgency(agency);
      this.toast(`Agency added successfully (${agency.getAgencyDescription()})`, 'success');
      this.clearAgencyForm();
      this.refreshAll();
    } catch (err) {
      this.toast(err.message, 'error');
    }
  }

  handleUpdateAgency() {
    try {
      const idStr = this.agencyIdInput.value.trim();
      if (!idStr) throw new Error("Please select an agency or enter Agency ID to update.");
      const agencyId = parseInt(idStr, 10);
      const name = this.agencyNameInput.value.trim();
      const type = this.agencyTypeSelect.value;
      const subParam = this.agencySubParamInput.value.trim();
      const address = this.agencyAddressInput.value.trim();
      const phone = this.agencyPhoneInput.value.trim();

      if (!name) throw new Error("Agency Name cannot be empty.");

      const agency = createAgencyInstance({
        agencyId,
        agencyName: name,
        agencyType: type,
        subParam,
        address,
        phone
      });

      DatabaseManager.updateAgency(agency);
      this.toast(`Agency #${agencyId} updated.`, 'success');
      this.clearAgencyForm();
      this.refreshAll();
    } catch (err) {
      this.toast(err.message, 'error');
    }
  }

  handleDeleteAgency() {
    const idStr = this.agencyIdInput.value.trim();
    if (!idStr) {
      this.toast("Please select an Agency to delete.", 'error');
      return;
    }
    const agencyId = parseInt(idStr, 10);
    this.showConfirmDialog(`Are you sure you want to delete Agency ID ${agencyId}?`, () => {
      try {
        DatabaseManager.deleteAgency(agencyId);
        this.toast(`Agency #${agencyId} deleted.`, 'success');
        this.clearAgencyForm();
        this.refreshAll();
      } catch (err) {
        this.toast(err.message, 'error');
      }
    });
  }

  handleSearchAgency() {
    const idQuery = this.agencyIdInput.value.trim();
    const nameQuery = this.agencyNameInput.value.trim().toLowerCase();

    let results = DatabaseManager.getAgencies();
    if (idQuery) {
      results = results.filter(a => a.agencyId === parseInt(idQuery, 10));
    } else if (nameQuery) {
      results = results.filter(a => a.agencyName.toLowerCase().includes(nameQuery));
    } else {
      this.toast("Enter Agency ID or Name to search.", 'info');
      this.renderAgencies();
      return;
    }

    this.renderAgencies(results);
    this.toast(`Found ${results.length} matching agency(ies).`, 'info');
  }

  // ==========================================================================
  // GEOGRAPHICAL AREAS MODULE
  // ==========================================================================
  renderAreas(list = null) {
    const areas = list || DatabaseManager.getAreas();
    this.areaCountBadge.textContent = `${areas.length} area(s)`;
    this.areaTableBody.innerHTML = '';

    areas.forEach(area => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td><strong>#${area.areaId}</strong></td>
        <td>${escapeHtml(area.areaName)}</td>
        <td>${area.population.toLocaleString()} citizens</td>
      `;
      tr.addEventListener('click', () => {
        document.querySelectorAll('#areaTable tbody tr').forEach(r => r.classList.remove('selected-row'));
        tr.classList.add('selected-row');
        this.populateAreaForm(area);
      });
      this.areaTableBody.appendChild(tr);
    });
  }

  populateAreaForm(area) {
    this.areaIdInput.value = area.areaId;
    this.areaNameInput.value = area.areaName;
    this.areaPopulationInput.value = area.population;
  }

  clearAreaForm() {
    this.areaIdInput.value = '';
    this.areaNameInput.value = '';
    this.areaPopulationInput.value = '';
    document.querySelectorAll('#areaTable tbody tr').forEach(r => r.classList.remove('selected-row'));
  }

  handleAddArea() {
    try {
      const name = this.areaNameInput.value.trim();
      const popStr = this.areaPopulationInput.value.trim();
      const areaId = this.areaIdInput.value ? parseInt(this.areaIdInput.value, 10) : DatabaseManager.nextAreaId();

      if (!name) throw new Error("Area Name cannot be empty.");
      const population = parseInt(popStr, 10);
      if (isNaN(population) || population <= 0) {
        throw new Error("Population must be a positive integer number.");
      }

      const area = new GeographicalArea(areaId, name, population);
      DatabaseManager.addArea(area);

      this.toast(`Geographical Area added (ID #${areaId})`, 'success');
      this.clearAreaForm();
      this.refreshAll();
    } catch (err) {
      this.toast(err.message, 'error');
    }
  }

  handleUpdateArea() {
    try {
      const idStr = this.areaIdInput.value.trim();
      if (!idStr) throw new Error("Please select an area or enter Area ID to update.");
      const areaId = parseInt(idStr, 10);
      const name = this.areaNameInput.value.trim();
      const popStr = this.areaPopulationInput.value.trim();

      const population = parseInt(popStr, 10);
      if (isNaN(population) || population <= 0) {
        throw new Error("Population must be a positive integer number.");
      }

      const existing = DatabaseManager.getAreaById(areaId);
      if (!existing) throw new Error(`Area ID ${areaId} not found.`);

      // Requirements: allow updating population
      existing.population = population;
      if (name) existing.areaName = name;

      DatabaseManager.updateArea(existing);
      this.toast(`Population updated for Area #${areaId}.`, 'success');
      this.clearAreaForm();
      this.refreshAll();
    } catch (err) {
      this.toast(err.message, 'error');
    }
  }

  handleDeleteArea() {
    const idStr = this.areaIdInput.value.trim();
    if (!idStr) {
      this.toast("Please select an Area to delete.", 'error');
      return;
    }
    const areaId = parseInt(idStr, 10);
    this.showConfirmDialog(`Are you sure you want to delete Geographical Area #${areaId}?`, () => {
      try {
        DatabaseManager.deleteArea(areaId);
        this.toast(`Area #${areaId} deleted.`, 'success');
        this.clearAreaForm();
        this.refreshAll();
      } catch (err) {
        this.toast(err.message, 'error');
      }
    });
  }

  handleSearchArea() {
    const idQuery = this.areaIdInput.value.trim();
    const nameQuery = this.areaNameInput.value.trim().toLowerCase();

    let results = DatabaseManager.getAreas();
    if (idQuery) {
      results = results.filter(a => a.areaId === parseInt(idQuery, 10));
    } else if (nameQuery) {
      results = results.filter(a => a.areaName.toLowerCase().includes(nameQuery));
    } else {
      this.toast("Enter Area ID or Name to search.", 'info');
      this.renderAreas();
      return;
    }

    this.renderAreas(results);
    this.toast(`Found ${results.length} matching area(s).`, 'info');
  }

  // ==========================================================================
  // MANDATES MODULE (Date Validations: End Date > Start Date)
  // ==========================================================================
  renderMandates(list = null) {
    const mandates = list || DatabaseManager.getMandates();
    const agents = DatabaseManager.getAgents();
    const agencies = DatabaseManager.getAgencies();
    const areas = DatabaseManager.getAreas();

    const agentMap = Object.fromEntries(agents.map(a => [a.agentId, a.agentName]));
    const agencyMap = Object.fromEntries(agencies.map(a => [a.agencyId, a.agencyName]));
    const areaMap = Object.fromEntries(areas.map(a => [a.areaId, a.areaName]));

    this.mandateCountBadge.textContent = `${mandates.length} mandate(s)`;
    this.mandateTableBody.innerHTML = '';

    mandates.forEach(m => {
      const isActive = m.isActive();
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td><strong>#${m.mandateId}</strong></td>
        <td>#${m.agentId} ${escapeHtml(agentMap[m.agentId] || '')}</td>
        <td>#${m.agencyId} ${escapeHtml(agencyMap[m.agencyId] || '')}</td>
        <td>#${m.areaId} ${escapeHtml(areaMap[m.areaId] || '')}</td>
        <td>${m.startDate}</td>
        <td>${m.endDate}</td>
        <td><strong>${escapeHtml(m.mandateType)}</strong></td>
        <td><span class="status-pill ${isActive ? 'status-active' : 'status-expired'}">${isActive ? 'ACTIVE' : 'EXPIRED'}</span></td>
      `;
      tr.addEventListener('click', () => {
        document.querySelectorAll('#mandateTable tbody tr').forEach(r => r.classList.remove('selected-row'));
        tr.classList.add('selected-row');
        this.populateMandateForm(m);
      });
      this.mandateTableBody.appendChild(tr);
    });
  }

  populateMandateForm(mandate) {
    this.mandateIdInput.value = mandate.mandateId;
    this.mandateAgentSelect.value = mandate.agentId;
    this.mandateAgencySelect.value = mandate.agencyId;
    this.mandateAreaSelect.value = mandate.areaId;
    this.mandateStartDateInput.value = mandate.startDate;
    this.mandateEndDateInput.value = mandate.endDate;
    this.mandateTypeSelect.value = mandate.mandateType;
  }

  clearMandateForm() {
    this.mandateIdInput.value = '';
    this.mandateStartDateInput.value = '';
    this.mandateEndDateInput.value = '';
    document.querySelectorAll('#mandateTable tbody tr').forEach(r => r.classList.remove('selected-row'));
  }

  handleAddMandate() {
    try {
      const agentId = parseInt(this.mandateAgentSelect.value, 10);
      const agencyId = parseInt(this.mandateAgencySelect.value, 10);
      const areaId = parseInt(this.mandateAreaSelect.value, 10);
      const startDate = this.mandateStartDateInput.value;
      const endDate = this.mandateEndDateInput.value;
      const mandateType = this.mandateTypeSelect.value;
      const mandateId = this.mandateIdInput.value ? parseInt(this.mandateIdInput.value, 10) : DatabaseManager.nextMandateId();

      const mandate = new Mandate(mandateId, agentId, agencyId, areaId, startDate, endDate, mandateType);

      // DATE VALIDATION (End Date > Start Date)
      mandate.validateDates();

      DatabaseManager.addMandate(mandate);
      this.toast(`Mandate #${mandateId} registered successfully.`, 'success');
      this.clearMandateForm();
      this.refreshAll();
    } catch (err) {
      this.toast(err.message, 'error');
    }
  }

  handleUpdateMandate() {
    try {
      const idStr = this.mandateIdInput.value.trim();
      if (!idStr) throw new Error("Please select a mandate or enter Mandate ID to update.");
      const mandateId = parseInt(idStr, 10);
      const agentId = parseInt(this.mandateAgentSelect.value, 10);
      const agencyId = parseInt(this.mandateAgencySelect.value, 10);
      const areaId = parseInt(this.mandateAreaSelect.value, 10);
      const startDate = this.mandateStartDateInput.value;
      const endDate = this.mandateEndDateInput.value;
      const mandateType = this.mandateTypeSelect.value;

      const mandate = new Mandate(mandateId, agentId, agencyId, areaId, startDate, endDate, mandateType);
      mandate.validateDates();

      DatabaseManager.updateMandate(mandate);
      this.toast(`Mandate #${mandateId} updated successfully.`, 'success');
      this.clearMandateForm();
      this.refreshAll();
    } catch (err) {
      this.toast(err.message, 'error');
    }
  }

  handleDeleteMandate() {
    const idStr = this.mandateIdInput.value.trim();
    if (!idStr) {
      this.toast("Please select a Mandate to delete.", 'error');
      return;
    }
    const mandateId = parseInt(idStr, 10);
    this.showConfirmDialog(`Are you sure you want to delete Mandate #${mandateId}?`, () => {
      try {
        DatabaseManager.deleteMandate(mandateId);
        this.toast(`Mandate #${mandateId} deleted.`, 'success');
        this.clearMandateForm();
        this.refreshAll();
      } catch (err) {
        this.toast(err.message, 'error');
      }
    });
  }

  handleSearchMandate() {
    const idQuery = this.mandateIdInput.value.trim();
    const typeQuery = this.mandateTypeSelect.value;

    let results = DatabaseManager.getMandates();
    if (idQuery) {
      results = results.filter(m => m.mandateId === parseInt(idQuery, 10));
    } else if (typeQuery) {
      results = results.filter(m => m.mandateType === typeQuery);
    }

    this.renderMandates(results);
    this.toast(`Found ${results.length} matching mandate(s).`, 'info');
  }

  // ==========================================================================
  // 15 ANALYTICAL REPORTS
  // ==========================================================================
  runReport() {
    const reportNum = parseInt(this.reportSelector.value, 10);
    const agents = DatabaseManager.getAgents();
    const agencies = DatabaseManager.getAgencies();
    const areas = DatabaseManager.getAreas();
    const mandates = DatabaseManager.getMandates();

    const agentMap = Object.fromEntries(agents.map(a => [a.agentId, a.agentName]));
    const agencyMap = Object.fromEntries(agencies.map(a => [a.agencyId, a.agencyName]));
    const areaMap = Object.fromEntries(areas.map(a => [a.areaId, a.areaName]));

    let columns = [];
    let rows = [];
    let title = '';

    switch (reportNum) {
      case 1: // 1. Display all agents
        title = "Report 1: All Registered Agents";
        columns = ["Agent ID", "Agent Name", "Phone", "Email", "Agency ID", "Agency Name"];
        rows = agents.map(a => [
          `#${a.agentId}`,
          a.agentName,
          a.phone,
          a.email,
          a.agencyId,
          agencyMap[a.agencyId] || '-'
        ]);
        break;

      case 2: // 2. Display all agencies with agency type
        title = "Report 2: All Agencies with Type & Description";
        columns = ["Agency ID", "Agency Name", "Type", "Polymorphic Description", "Address", "Phone"];
        rows = agencies.map(a => [
          `#${a.agencyId}`,
          a.agencyName,
          a.agencyType,
          a.getAgencyDescription(),
          a.address || '-',
          a.phone || '-'
        ]);
        break;

      case 3: // 3. Display all geographical areas
        title = "Report 3: All Geographical Coverage Areas";
        columns = ["Area ID", "Area Name", "Population"];
        rows = areas.map(ar => [
          `#${ar.areaId}`,
          ar.areaName,
          ar.population.toLocaleString()
        ]);
        break;

      case 4: // 4. Display active mandates on a given date
        const targetDate = this.reportDateInput.value || new Date().toISOString().split('T')[0];
        title = `Report 4: Active Mandates on Date (${targetDate})`;
        columns = ["Mandate ID", "Agent", "Agency", "Area", "Start Date", "End Date", "Type", "Status"];
        const activeOnDate = mandates.filter(m => m.isActive(targetDate));
        rows = activeOnDate.map(m => [
          `#${m.mandateId}`,
          `#${m.agentId} ${agentMap[m.agentId] || ''}`,
          `#${m.agencyId} ${agencyMap[m.agencyId] || ''}`,
          `#${m.areaId} ${areaMap[m.areaId] || ''}`,
          m.startDate,
          m.endDate,
          m.mandateType,
          `<span class="status-pill status-active">ACTIVE on ${targetDate}</span>`
        ]);
        break;

      case 5: // 5. Calculate total number of agents
        title = "Report 5: Total Number of Agents";
        columns = ["Metric", "Total Count", "Status"];
        rows = [["Total Registered Agents", agents.length, "Active in System"]];
        break;

      case 6: // 6. Calculate total number of agencies
        title = "Report 6: Total Number of Agencies";
        columns = ["Metric", "Independent", "Franchise", "Grand Total"];
        const indCount = agencies.filter(a => a.agencyType === 'Independent').length;
        const franCount = agencies.filter(a => a.agencyType === 'Franchise').length;
        rows = [["Registered Agencies", indCount, franCount, agencies.length]];
        break;

      case 7: // 7. Calculate total number of mandates
        title = "Report 7: Total Number of Mandates";
        columns = ["Metric", "Active", "Expired", "Grand Total"];
        const actCount = mandates.filter(m => m.isActive()).length;
        rows = [["Mandates Volume", actCount, mandates.length - actCount, mandates.length]];
        break;

      case 8: // 8. Calculate number of mandates handled by each agent
        title = "Report 8: Number of Mandates Handled by Each Agent";
        columns = ["Agent ID", "Agent Name", "Agency", "Mandate Count"];
        rows = agents.map(a => {
          const count = mandates.filter(m => m.agentId === a.agentId).length;
          return [`#${a.agentId}`, a.agentName, agencyMap[a.agencyId] || '-', count];
        }).sort((a, b) => b[3] - a[3]);
        break;

      case 9: // 9. Display agents with at least one mandate
        title = "Report 9: Agents with at Least One Mandate";
        columns = ["Agent ID", "Agent Name", "Phone", "Email", "Mandate Count"];
        rows = agents
          .map(a => {
            const count = mandates.filter(m => m.agentId === a.agentId).length;
            return { agent: a, count };
          })
          .filter(item => item.count >= 1)
          .map(item => [
            `#${item.agent.agentId}`,
            item.agent.agentName,
            item.agent.phone,
            item.agent.email,
            item.count
          ]);
        break;

      case 10: // 10. Display agencies that have issued mandates
        title = "Report 10: Agencies That Have Issued Mandates";
        columns = ["Agency ID", "Agency Name", "Type", "Mandates Issued"];
        rows = agencies
          .map(ag => {
            const count = mandates.filter(m => m.agencyId === ag.agencyId).length;
            return { agency: ag, count };
          })
          .filter(item => item.count >= 1)
          .map(item => [
            `#${item.agency.agencyId}`,
            item.agency.agencyName,
            item.agency.agencyType,
            item.count
          ]);
        break;

      case 11: // 11. Find the agent handling the maximum number of mandates
        title = "Report 11: Agent Handling Maximum Mandates";
        columns = ["Agent ID", "Agent Name", "Phone", "Agency", "Mandates Handled"];
        const agentStats = agents.map(a => ({
          agent: a,
          count: mandates.filter(m => m.agentId === a.agentId).length
        })).sort((a, b) => b.count - a.count);

        if (agentStats.length && agentStats[0].count > 0) {
          const top = agentStats[0];
          rows = [[
            `#${top.agent.agentId}`,
            top.agent.agentName,
            top.agent.phone,
            agencyMap[top.agent.agencyId] || '-',
            `<strong>${top.count} (MAXIMUM)</strong>`
          ]];
        } else {
          rows = [["-", "No mandates assigned yet", "-", "-", "0"]];
        }
        break;

      case 12: // 12. Display active and expired mandates
        title = "Report 12: Active and Expired Mandates Breakdown";
        columns = ["Mandate ID", "Agent", "Agency", "Start Date", "End Date", "Type", "Status"];
        rows = mandates.map(m => {
          const act = m.isActive();
          return [
            `#${m.mandateId}`,
            agentMap[m.agentId] || `#${m.agentId}`,
            agencyMap[m.agencyId] || `#${m.agencyId}`,
            m.startDate,
            m.endDate,
            m.mandateType,
            `<span class="status-pill ${act ? 'status-active' : 'status-expired'}">${act ? 'ACTIVE' : 'EXPIRED'}</span>`
          ];
        });
        break;

      case 13: // 13. Display mandates grouped by mandate type
        title = "Report 13: Mandates Grouped by Mandate Type";
        columns = ["Mandate Type", "Total Count", "Active Count", "Expired Count"];
        const types = ['SALE', 'RENT', 'PURCHASE', 'LEASE'];
        rows = types.map(t => {
          const matching = mandates.filter(m => m.mandateType === t);
          const active = matching.filter(m => m.isActive()).length;
          return [t, matching.length, active, matching.length - active];
        });
        break;

      case 14: // 14. Display mandates grouped by agency
        title = "Report 14: Mandates Grouped by Agency";
        columns = ["Agency ID", "Agency Name", "Type", "Total Mandates", "Active Mandates"];
        rows = agencies.map(ag => {
          const matching = mandates.filter(m => m.agencyId === ag.agencyId);
          const active = matching.filter(m => m.isActive()).length;
          return [`#${ag.agencyId}`, ag.agencyName, ag.agencyType, matching.length, active];
        });
        break;

      case 15: // 15. Display mandates grouped by geographical area
        title = "Report 15: Mandates Grouped by Geographical Area";
        columns = ["Area ID", "Area Name", "Population", "Total Mandates", "Active Mandates"];
        rows = areas.map(ar => {
          const matching = mandates.filter(m => m.areaId === ar.areaId);
          const active = matching.filter(m => m.isActive()).length;
          return [`#${ar.areaId}`, ar.areaName, ar.population.toLocaleString(), matching.length, active];
        });
        break;

      default:
        break;
    }

    // Render report UI
    this.reportTitle.textContent = title;
    this.reportCountBadge.textContent = `${rows.length} row(s)`;

    this.reportTableHead.innerHTML = `<tr>${columns.map(c => `<th>${c}</th>`).join('')}</tr>`;
    this.reportTableBody.innerHTML = rows.map(r =>
      `<tr>${r.map(cell => `<td>${cell}</td>`).join('')}</tr>`
    ).join('');
  }

  exportReportToCSV() {
    const table = document.getElementById('reportTable');
    let csv = [];
    for (let row of table.rows) {
      let cols = [];
      for (let cell of row.cells) {
        // Strip html tags
        let txt = cell.innerText.replace(/"/g, '""');
        cols.push(`"${txt}"`);
      }
      csv.push(cols.join(','));
    }
    const blob = new Blob([csv.join('\n')], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = `report_${Date.now()}.csv`;
    link.click();
    this.toast("Report exported to CSV.", "success");
  }

  exportAllDataJSON() {
    const data = {
      agencies: DatabaseManager.getAgencies().map(a => a.toJSON()),
      areas: DatabaseManager.getAreas().map(a => a.toJSON()),
      agents: DatabaseManager.getAgents().map(a => a.toJSON()),
      mandates: DatabaseManager.getMandates().map(m => m.toJSON()),
      exportedAt: new Date().toISOString()
    };
    const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = `real_estate_mandate_backup_${Date.now()}.json`;
    link.click();
    this.toast("Full database exported to JSON.", "success");
  }

  // ==========================================================================
  // MODAL CONFIRMATION DIALOG
  // ==========================================================================
  showConfirmDialog(message, onConfirm) {
    this.modalMessage.textContent = message;
    this.modalBackdrop.style.display = 'flex';

    const handleConfirm = () => {
      this.modalBackdrop.style.display = 'none';
      this.modalBtnConfirm.removeEventListener('click', handleConfirm);
      onConfirm();
    };

    this.modalBtnConfirm.addEventListener('click', handleConfirm);
  }

  // ==========================================================================
  // TOAST NOTIFICATIONS
  // ==========================================================================
  toast(message, type = 'info') {
    const container = document.getElementById('toastContainer');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.textContent = message;
    container.appendChild(toast);
    setTimeout(() => {
      toast.style.opacity = '0';
      setTimeout(() => toast.remove(), 300);
    }, 3500);
  }
}

// Helper: Escape HTML
function escapeHtml(str) {
  if (typeof str !== 'string') return str;
  return str.replace(/[&<>"']/g, match => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;'
  }[match]));
}

// Initialize Application when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
  window.app = new AppController();
});
