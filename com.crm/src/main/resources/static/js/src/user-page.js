import {html, css, LitElement} from 'lit';

export class UsersPage extends LitElement {
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

    /* Modal styles */
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

    .form-group input {
      width: 100%;
      padding: 8px;
      background-color: #0f172a;
      border: 1px solid #475569;
      color: #f8fafc;
      border-radius: 4px;
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
  `;

  static properties = {
    name: {type: String},
    page: {type: Number},
    totalPages: {type: Number},
    searchQuery: {state: true},
    users: {state: true},
    isModalOpen: {state: true},
    formId: {state: true},
    formName: {state: true},
    formPrefName: {state: true},
    formPhone: {state: true},
    formError: {state: true}
  };

  constructor() {
    super();
    this.page = 0;
    this.totalPages = 1;
    this.searchQuery = '';
    this.users = [];
    this.isModalOpen = false;
    this.formId = '';
    this.formName = '';
    this.formPrefName = '';
    this.formPhone = '';
    this.formError = '';
  }

  async connectedCallback() {
    super.connectedCallback();
    await this.loadUsers();
  }

  async loadUsers() {
    let url = '';
    
    if (this.searchQuery.trim() === '') {
      url = `/api/users/get_all_users?page=${this.page}`;
    } else {
      url = `/api/users/search?partName=${encodeURIComponent(this.searchQuery)}&page=${this.page}`;
    }

    try {
      let response = await fetch(url);
      let json = await response.json();

      this.users = json.content || [];
      this.totalPages = json.totalPages || 1;
    } catch (error) {
      console.error("Failed to load users:", error);
    }
  }

  handleSearchInput(e) {
    this.searchQuery = e.target.value;
    this.page = 0; 
    this.loadUsers();
  }

  prevPage() {
    if (this.page > 0) {
      this.page--;
      this.loadUsers();
    }
  }

  nextPage() {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.loadUsers();
    }
  }

  handlePageInputChange(e) {
    let newPage = parseInt(e.target.value, 10);
    if (!isNaN(newPage)) {
      let targetPage = newPage - 1;
      if (targetPage >= 0 && targetPage < this.totalPages) {
        this.page = targetPage;
        this.loadUsers();
      }
    }
  }

  openModal() {
    this.formId = '';
    this.formName = '';
    this.formPrefName = '';
    this.formPhone = '';
    this.formError = '';
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
  }

  handleFormInput(e, field) {
    this[field] = e.target.value;
  }

  async submitNewUser(e) {
    e.preventDefault();
    this.formError = '';

    if (!this.formName.includes(',')) {
      this.formError = 'Name must be in "Last, First" format (including the comma).';
      return;
    }

    const phoneRegex = /^\+1\d{10}$/;
    if (this.formPhone && !phoneRegex.test(this.formPhone)) {
      this.formError = 'Phone number must be in +1XXXXXXXXXX format.';
      return;
    }

    let payload = {
      name: this.formName.trim(),
      prefName: this.formPrefName.trim(),
      phone: this.formPhone.trim() ? this.formPhone.trim() : null,
      varList: []
    };

    if (this.formId.trim() !== '') {
      payload.id = parseInt(this.formId, 10);
    }

    try {
      let response = await fetch('/api/users/create_user', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
      });

      if (response.ok) {
        this.closeModal();
        this.loadUsers();
      } else {
        this.formError = 'Failed to create user on the server.';
      }
    } catch (error) {
      console.error('Error creating user:', error);
      this.formError = 'Network error while creating user.';
    }
  }

  renderUsers() {
    if (this.users.length === 0) {
      return html`<p>No users found.</p>`;
    }
    return this.users.map(user => html`
      <user-card userId="${user.id}" name="${user.name || ''}" phone="${user.phone || ''}"></user-card>
    `);
  }

  renderModal() {
    if (!this.isModalOpen) return html``;

    return html`
      <div class="modal-overlay">
        <div class="modal">
          <h3>Create New User</h3>
          <form @submit="${this.submitNewUser}">
            <div class="form-group">
              <label>Chart # (optional):</label>
              <input type="number" .value="${this.formId}" @input="${e => this.handleFormInput(e, 'formId')}" />
            </div>

            <div class="form-group">
              <label>Name (Last, First):</label>
              <input type="text" .value="${this.formName}" @input="${e => this.handleFormInput(e, 'formName')}" required />
            </div>

            <div class="form-group">
              <label>Nickname:</label>
              <input type="text" .value="${this.formPrefName}" @input="${e => this.handleFormInput(e, 'formPrefName')}" />
            </div>

            <div class="form-group">
              <label>Phone (+1XXXXXXXXXX):</label>
              <input type="text" .value="${this.formPhone}" @input="${e => this.handleFormInput(e, 'formPhone')}" placeholder="+15551234567" />
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
            placeholder="Search users by name..." 
            .value="${this.searchQuery}"
            @input="${this.handleSearchInput}"
          />
        </div>
        <button class="create-btn" @click="${this.openModal}">Create New User</button>
      </div>

      <div class="content-wrapper">
        <div class="cardContainer">
          ${this.renderUsers()}
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

customElements.define('users-page', UsersPage);
