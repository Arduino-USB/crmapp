import {html, css, LitElement} from 'lit';
import './appointment-card.js';

export class AppointmentsPage extends LitElement {
  static styles = css`
    :host {
      display: block;
      color: #f8fafc;
      padding: 16px;
    }

    .top-bar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      gap: 16px;
    }

    .search-container {
      flex: 1;
      max-width: 400px;
    }

    .search-container input {
      width: 100%;
      padding: 8px 12px;
      font-size: 1rem;
      border-radius: 4px;
      border: 1px solid #cbd5e1;
      background-color: #1e293b;
      color: #f8fafc;
    }

    .create-btn {
      padding: 8px 16px;
      background-color: #38bdf8;
      color: #0f172a;
      font-weight: 600;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    }

    .create-btn:hover {
      background-color: #0ea5e9;
    }

    .content-wrapper {
      display: flex;
      flex-direction: column;
      gap: 24px;
    }

    .cardContainer {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 5%;
    }

    .pagination-container {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 12px;
    }

    .pagination-container button {
      padding: 6px 12px;
      background-color: #334155;
      color: #f8fafc;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    }

    .pagination-container button:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }

    .pagination-container input {
      width: 50px;
      text-align: center;
      padding: 6px;
      background-color: #1e293b;
      color: #f8fafc;
      border: 1px solid #cbd5e1;
      border-radius: 4px;
    }

    .modal-overlay {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-color: rgba(0, 0, 0, 0.6);
      display: flex;
      justify-content: center;
      align-items: center;
      z-index: 1000;
    }

    .modal {
      background-color: #1e293b;
      padding: 24px;
      border-radius: 8px;
      width: 100%;
      max-width: 400px;
      border: 1px solid #334155;
    }

    .modal h3 {
      margin-top: 0;
      margin-bottom: 16px;
    }

    .form-group {
      margin-bottom: 14px;
    }

    .form-group label {
      display: block;
      margin-bottom: 4px;
      font-size: 0.875rem;
      color: #94a3b8;
    }

    .form-group input,
    .form-group select {
      width: 100%;
      padding: 8px;
      background-color: #0f172a;
      border: 1px solid #475569;
      color: #f8fafc;
      border-radius: 4px;
      box-sizing: border-box;
    }

    .error-text {
      color: #f87171;
      font-size: 0.8rem;
      margin-top: 4px;
    }

    .modal-actions {
      display: flex;
      justify-content: flex-end;
      gap: 8px;
      margin-top: 20px;
    }

    .modal-actions button {
      padding: 6px 12px;
      border-radius: 4px;
      cursor: pointer;
      border: none;
    }

    .cancel-btn {
      background-color: #475569;
      color: #f8fafc;
    }

    .submit-btn {
      background-color: #38bdf8;
      color: #0f172a;
      font-weight: 600;
    }

    .user-search-results {
      max-height: 120px;
      overflow-y: auto;
      background: #0f172a;
      border: 1px solid #475569;
      border-radius: 4px;
      margin-top: 4px;
    }

    .user-search-item {
      padding: 6px 10px;
      cursor: pointer;
      font-size: 0.85rem;
      border-bottom: 1px solid #334155;
    }

    .user-search-item:hover {
      background-color: #334155;
    }
  `;

  static properties = {
    page: {type: Number},
    totalPages: {type: Number},
    searchQuery: {state: true},
    appointments: {state: true},
    isModalOpen: {state: true},
    formId: {state: true},
    formUserId: {state: true},
    formUserName: {state: true},
    formTime: {state: true},
    formError: {state: true},
    userSearchResults: {state: true}
  };

  constructor() {
    super();
    this.page = 0;
    this.totalPages = 1;
    this.searchQuery = '';
    this.appointments = [];
    this.isModalOpen = false;
    this.formId = '';
    this.formUserId = null;
    this.formUserName = '';
    this.formTime = '';
    this.formError = '';
    this.userSearchResults = [];
  }

  async connectedCallback() {
    super.connectedCallback();
    await this.loadAppointments();
  }

  async loadAppointments() {
    let url = '';
    if (this.searchQuery.trim() === '') {
      url = `/api/appointments/get_all_appointments?page=${this.page}`;
    } else {
      url = `/api/appointments/search?partName=${encodeURIComponent(this.searchQuery)}&page=${this.page}`;
    }

    try {
      let response = await fetch(url);
      let json = await response.json();
      this.appointments = json.content || [];
      this.totalPages = json.totalPages || 1;
    } catch (error) {
      console.error("Failed to load appointments:", error);
    }
  }

  handleSearchInput(e) {
    this.searchQuery = e.target.value;
    this.page = 0;
    this.loadAppointments();
  }

  prevPage() {
    if (this.page > 0) {
      this.page--;
      this.loadAppointments();
    }
  }

  nextPage() {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.loadAppointments();
    }
  }

  handlePageInputChange(e) {
    let newPage = parseInt(e.target.value, 10);
    if (!isNaN(newPage)) {
      let targetPage = newPage - 1;
      if (targetPage >= 0 && targetPage < this.totalPages) {
        this.page = targetPage;
        this.loadAppointments();
      }
    }
  }

  openModal() {
    this.formId = '';
    this.formUserId = null;
    this.formUserName = '';
    this.formTime = '';
    this.formError = '';
    this.userSearchResults = [];
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
  }

  async handleModalUserSearch(e) {
    const query = e.target.value;
    this.formUserName = query;
    if (query.trim() === '') {
      this.userSearchResults = [];
      return;
    }
    try {
      let res = await fetch(`/api/users/search?partName=${encodeURIComponent(query)}`);
      let data = await res.json();
      this.userSearchResults = data.content || [];
    } catch (err) {
      console.error("Search error:", err);
    }
  }

  selectModalUser(user) {
    this.formUserId = user.id;
    this.formUserName = user.name;
    this.userSearchResults = [];
  }

  async submitNewAppointment(e) {
    e.preventDefault();
    this.formError = '';

    if (!this.formUserId) {
      this.formError = 'Please select a valid user from the search results.';
      return;
    }

    if (!this.formTime) {
      this.formError = 'Please select an appointment time.';
      return;
    }

    const instantTime = new Date(this.formTime).toISOString();

    let payload = {
      userId: parseInt(this.formUserId, 10),
      time: instantTime
    };

    if (this.formId.trim() !== '') {
      payload.id = parseInt(this.formId, 10);
    }

    try {
      let response = await fetch('/api/appointments/create_appointment', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
      });

      if (response.ok) {
        this.closeModal();
        this.loadAppointments();
      } else {
        this.formError = 'Failed to create appointment on the server.';
      }
    } catch (error) {
      console.error('Error creating appointment:', error);
      this.formError = 'Network error while creating appointment.';
    }
  }

  renderAppointments() {
    if (this.appointments.length === 0) {
      return html`<p>No appointments found.</p>`;
    }
    return this.appointments.map(appt => html`
      <appointment-card 
        appointmentId="${appt.id}" 
        userId="${appt.userId || ''}" 
        name="${appt.name || ''}" 
        time="${appt.time || ''}"
      ></appointment-card>
    `);
  }

  renderModal() {
    if (!this.isModalOpen) return html``;

    return html`
      <div class="modal-overlay">
        <div class="modal">
          <h3>Create New Appointment</h3>
          <form @submit="${this.submitNewAppointment}">

            <div class="form-group">
              <label>Search User (Last, First):</label>
              <input 
                type="text" 
                .value="${this.formUserName}" 
                @input="${this.handleModalUserSearch}" 
                placeholder="Type name to search..."
                required 
              />
              ${this.userSearchResults.length > 0 ? html`
                <div class="user-search-results">
                  ${this.userSearchResults.map(user => html`
                    <div class="user-search-item" @click="${() => this.selectModalUser(user)}">
                      ${user.name} (ID: ${user.id})
                    </div>
                  `)}
                </div>
              ` : ''}
            </div>

            <div class="form-group">
              <label>Selected User ID: ${this.formUserId ? this.formUserId : 'None selected'}</label>
            </div>

            <div class="form-group">
              <label>Time:</label>
              <input 
                type="datetime-local" 
                .value="${this.formTime}" 
                @input="${e => this.formTime = e.target.value}" 
                required 
              />
            </div>

            ${this.formError ? html`<div class="error-text">${this.formError}</div>` : ''}

            <div class="modal-actions">
              <button type="button" class="cancel-btn" @click="${this.closeModal}">Cancel</button>
              <button type="submit" class="submit-btn">Save</button>
            </div>
          </form>
        </div>
      </div>
    `;
  }

  render() {
    return html`
      <div class="top-bar">
        <div class="search-container">
          <input 
            type="text" 
            placeholder="Search appointments by user name..." 
            .value="${this.searchQuery}"
            @input="${this.handleSearchInput}"
          />
        </div>
        <button class="create-btn" @click="${this.openModal}">Create New Appointment</button>
      </div>

      <div class="content-wrapper">
        <div class="cardContainer">
          ${this.renderAppointments()}
        </div>

        <div class="pagination-container">
          <button @click="${this.prevPage}" ?disabled="${this.page === 0}">&lt;</button>
          <span>[ <input 
            type="number" 
            .value="${this.page + 1}" 
            @change="${this.handlePageInputChange}"
            min="1"
            max="${this.totalPages}"
          /> ]</span>
          <button @click="${this.nextPage}" ?disabled="${this.page >= this.totalPages - 1}">&gt;</button>
        </div>
      </div>

      ${this.renderModal()}
    `;
  }
}

customElements.define('appointments-page', AppointmentsPage);
