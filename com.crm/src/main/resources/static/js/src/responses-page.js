import {html, css, LitElement} from 'lit';
import './response-card.js';

export class ResponsesPage extends LitElement {
  static styles = css`
    :host {
      display: block;
      color: #f8fafc;
      padding: 16px;
    }

    .top-bar {
      display: flex;
      justify-content: flex-start;
      align-items: center;
      margin-bottom: 20px;
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

    .cardContainer {
      display: grid;
      grid-template-columns: repeat(5, 1fr);
      gap: 16px;
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
      max-width: 450px;
      border: 1px solid #334155;
      color: #f8fafc;
      max-height: 85vh;
      overflow-y: auto;
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
      font-size: 0.85rem;
      color: #94a3b8;
    }

    .form-group input,
    .form-group select,
    .form-group textarea {
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
  `;

  static properties = {
    responses: {state: true},
    matchModes: {state: true},
    actionsList: {state: true},
    isModalOpen: {state: true},
    formMode: {state: true},
    formText: {state: true},
    formResponseText: {state: true},
    formAction: {state: true},
    formError: {state: true}
  };

  constructor() {
    super();
    this.responses = [];
    this.matchModes = [];
    this.actionsList = [];
    this.isModalOpen = false;
    this.formMode = '';
    this.formText = '';
    this.formResponseText = '';
    this.formAction = '';
    this.formError = '';
  }

  async connectedCallback() {
    super.connectedCallback();
    await this.loadData();
  }

  async loadData() {
    try {
      let [resList, modesRes, actionsRes] = await Promise.all([
        fetch('/api/messages/responses/get_all_responses'),
        fetch('/api/messages/responses/match_modes'),
        fetch('/api/messages/responses/get_actions')
      ]);
      if (resList.ok) this.responses = await resList.json();
      if (modesRes.ok) this.matchModes = await modesRes.json();
      if (actionsRes.ok) this.actionsList = await actionsRes.json();

      if (this.matchModes.length > 0 && !this.formMode) this.formMode = this.matchModes[0];
      if (this.actionsList.length > 0 && !this.formAction) this.formAction = this.actionsList[0];
    } catch (e) {
      console.error(e);
    }
  }

  openModal() {
    this.formMode = this.matchModes[0] || '';
    this.formText = '';
    this.formResponseText = '';
    this.formAction = this.actionsList[0] || '';
    this.formError = '';
    this.isModalOpen = true;
  }

  async submitNewResponse(e) {
    e.preventDefault();
    this.formError = '';

    let payload = {
      mode: this.formMode,
      text: this.formText,
      response: this.formResponseText,
      action: this.formAction
    };

    try {
      let res = await fetch('/api/messages/responses/create_response', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(payload)
      });

      if (res.ok) {
        this.isModalOpen = false;
        this.loadData();
      } else {
        this.formError = 'Failed to create response.';
      }
    } catch (e) {
      console.error(e);
      this.formError = 'Network error.';
    }
  }

  render() {
    return html`
      <div class="top-bar">
        <button class="create-btn" @click="${this.openModal}">Create New Response</button>
      </div>

      <div class="cardContainer">
        ${this.responses.length === 0 ? html`<p>No responses found.</p>` : this.responses.map(r => html`
          <response-card responseId="${r.id}"></response-card>
        `)}
      </div>

      ${this.isModalOpen ? html`
        <div class="modal-overlay">
          <div class="modal">
            <h3>Create New Response</h3>
            <form @submit="${this.submitNewResponse}">
              <div class="form-group">
                <label>Match Mode:</label>
                <select .value="${this.formMode}" @change="${e => this.formMode = e.target.value}">
                  ${this.matchModes.map(m => html`<option value="${m}">${m}</option>`)}
                </select>
              </div>

              <div class="form-group">
                <label>Trigger Text:</label>
                <input type="text" .value="${this.formText}" @input="${e => this.formText = e.target.value}" required />
              </div>

              <div class="form-group">
                <label>Response Content:</label>
                <textarea rows="3" .value="${this.formResponseText}" @input="${e => this.formResponseText = e.target.value}" required></textarea>
              </div>

              <div class="form-group">
                <label>Action:</label>
                <select .value="${this.formAction}" @change="${e => this.formAction = e.target.value}">
                  ${this.actionsList.map(a => html`<option value="${a}">${a}</option>`)}
                </select>
              </div>

              ${this.formError ? html`<div class="error-text">${this.formError}</div>` : ''}

              <div class="modal-actions">
                <button type="button" class="cancel-btn" @click="${() => this.isModalOpen = false}">Cancel</button>
                <button type="submit" class="submit-btn">Save</button>
              </div>
            </form>
          </div>
        </div>
      ` : ''}
    `;
  }
}

customElements.define('responses-page', ResponsesPage);
