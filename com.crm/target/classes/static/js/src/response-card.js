import {html, css, LitElement} from 'lit';

export class ResponseCard extends LitElement {
  static styles = css`
    :host {
      --font-family: system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
      --primary-color: #3b82f6;
      --primary-hover: #2563eb;
      --danger-color: #ef4444;
      --danger-hover: #dc2626;
      --secondary-color: #334155;
      --secondary-hover: #1e293b;
      
      --bg-surface: #1e293b;
      --bg-main: #0f172a;
      --text-main: #f8fafc;
      --text-muted: #94a3b8;
      --text-light: #ffffff;
      --border-color: #334155;
      
      --radius-sm: 6px;
      --radius-md: 12px;
      --shadow-sm: 0 4px 6px -1px rgba(0, 0, 0, 0.3), 0 2px 4px -1px rgba(0, 0, 0, 0.18);
      --shadow-lg: 0 20px 25px -5px rgba(0, 0, 0, 0.5), 0 10px 10px -5px rgba(0, 0, 0, 0.3);
    }

    .responseContainer p {
      margin: 0 0 8px 0;
      color: var(--text-main);
      font-size: 0.9rem;
      line-height: 1.4;
    }   
    
    .responseContainer {
      background-color: var(--bg-surface);
      border: 1px solid var(--border-color);
      border-radius: var(--radius-md);
      padding: 16px;
      display: flex;
      flex-direction: column;
      align-items: stretch;
      box-shadow: var(--shadow-sm);
      transition: box-shadow 0.2s ease, transform 0.2s ease;
    }

    .responseContainer:hover {
      box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.4), 0 4px 6px -2px rgba(0, 0, 0, 0.2);
    }

    .buttonContainer {
      display: flex;
      flex-direction: row; 
      justify-content: flex-end;
      gap: 8px;
      margin-top: 12px;
      padding-top: 10px;
      border-top: 1px solid var(--border-color);
    }

    .buttonContainer button {
      font-family: inherit;
      font-size: 0.8rem;
      font-weight: 500;
      padding: 6px 12px;
      border-radius: var(--radius-sm);
      border: none;
      cursor: pointer;
      transition: background-color 0.2s ease, transform 0.1s ease;
    }

    .buttonContainer button:active {
      transform: scale(0.98);
    }

    .editResponse {
      background-color: var(--primary-color);
      color: var(--text-light);
    }

    .editResponse:hover {
      background-color: var(--primary-hover);
    }

    .deleteResponse {
      background-color: var(--danger-color);
      color: var(--text-light);
    }

    .deleteResponse:hover {
      background-color: var(--danger-hover);
    }

    .cancelButton {
      background-color: var(--secondary-color);
      color: var(--text-main);
      border: 1px solid var(--border-color);
    }

    .cancelButton:hover {
      background-color: var(--secondary-hover);
      color: var(--text-light);
    }

    .saveButton {
      background-color: #22c55e;
      color: var(--text-light);
    }

    .saveButton:hover {
      background-color: #16a34a;
    }

    .editResponsePopup input,
    .editResponsePopup select,
    .editResponsePopup textarea {
      width: 100%;
      padding: 8px 10px;
      background-color: #0f172a;
      border: 1px solid var(--border-color);
      border-radius: var(--radius-sm);
      color: var(--text-main);
      font-size: 0.85rem;
      box-sizing: border-box;
      margin-top: 4px;
      margin-bottom: 8px;
    }

    .editResponsePopup input:focus,
    .editResponsePopup select:focus,
    .editResponsePopup textarea:focus {
      outline: none;
      border-color: var(--primary-color);
      box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
    }

    .editResponsePopup label {
      font-size: 0.8rem;
      color: var(--text-muted);
      font-weight: 500;
    }

    .editResponsePopup {
      display: flex;
      flex-direction: column;
      align-items: stretch;
      padding: 24px;
      position: fixed;
      background-color: var(--bg-surface);
      border-radius: var(--radius-md);
      box-shadow: var(--shadow-lg);
      width: 100%;
      max-width: 450px;
      max-height: 85vh;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      box-sizing: border-box;
      border: 1px solid var(--border-color);
      z-index: 1000;
      color: var(--text-main);
      overflow-y: auto;
    }
    
    .deleteResponsePopup {
      display: flex;
      flex-direction: column;
      align-items: stretch;
      padding: 24px;
      position: fixed;
      background-color: var(--bg-surface);
      border-radius: var(--radius-md);
      box-shadow: var(--shadow-lg);
      width: 100%;
      max-width: 350px;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      box-sizing: border-box;
      border: 1px solid var(--border-color);
      z-index: 1000;
      color: var(--text-main);
    }
    
    .deleteResponsePopup .deleteButton {
      background-color: var(--danger-color);
      color: var(--text-light);
    }
  `;

  static properties = {
    responseId: {type: Number},
    mode: {type: String},
    text: {type: String},
    responseText: {type: String},
    action: {type: String},
    matchModes: {type: Array},
    actionsList: {type: Array},
    showEditPopup: {state: true},
    showDeletePopup: {state: true},
    formMode: {state: true},
    formText: {state: true},
    formResponseText: {state: true},
    formAction: {state: true}
  };

  constructor() {
    super();
    this.responseId = null;
    this.mode = 'N/A';
    this.text = 'N/A';
    this.responseText = 'N/A';
    this.action = 'N/A';
    this.matchModes = [];
    this.actionsList = [];
    this.showEditPopup = false;
    this.showDeletePopup = false;
    this.formMode = '';
    this.formText = '';
    this.formResponseText = '';
    this.formAction = '';
    this.updateInterval = null;
  }

  connectedCallback() {
    super.connectedCallback();
    this.updateResponseData();
    this.fetchUtils();

    this.updateInterval = setInterval(() => {
      this.updateResponseData();
    }, 1000);
  }

  disconnectedCallback() {
    super.disconnectedCallback();
    clearInterval(this.updateInterval);
  }

  async fetchUtils() {
    try {
      let [modesRes, actionsRes] = await Promise.all([
        fetch('/api/messages/responses/match_modes'),
        fetch('/api/messages/responses/get_actions')
      ]);
      if (modesRes.ok) this.matchModes = await modesRes.json();
      if (actionsRes.ok) this.actionsList = await actionsRes.json();
    } catch (e) {
      console.error("Failed to load utility enums", e);
    }
  }

  async updateResponseData() {
    if (this.responseId == null) return;

    try {
      let res = await fetch(`/api/messages/responses/get_response_by_id?id=${this.responseId}`);
      if (res.status === 404) {
        this.remove();
        return;
      }
      if (!res.ok) throw new Error("Server error");

      let r = await res.json();
      this.mode = r.mode ?? 'N/A';
      this.text = r.text ?? 'N/A';
      this.responseText = r.response ?? 'N/A';
      this.action = r.action ?? 'N/A';
    } catch (e) {
      console.log(e);
    }
  }

  openEditPopup() {
    this.formMode = this.mode;
    this.formText = this.text;
    this.formResponseText = this.responseText;
    this.formAction = this.action;
    this.fetchUtils();
    this.showEditPopup = true;
  }

  renderEditPopup() {
    if (!this.showEditPopup) return html``;

    return html`
      <div class="editResponsePopup">
        <p style="font-size: 1.05rem; font-weight: 600; border-bottom: 1px solid var(--border-color); padding-bottom: 8px;">Edit Response</p>
        
        <label>Match Mode:</label>
        <select .value="${this.formMode}" @change="${e => this.formMode = e.target.value}">
          ${this.matchModes.map(m => html`<option value="${m}">${m}</option>`)}
        </select>

        <label>Trigger Text:</label>
        <input type="text" .value="${this.formText}" @input="${e => this.formText = e.target.value}">

        <label>Response Content:</label>
        <textarea rows="3" .value="${this.formResponseText}" @input="${e => this.formResponseText = e.target.value}"></textarea>

        <label>Action:</label>
        <select .value="${this.formAction}" @change="${e => this.formAction = e.target.value}">
          ${this.actionsList.map(a => html`<option value="${a}">${a}</option>`)}
        </select>

        <div class="buttonContainer">
          <button @click="${() => this.showEditPopup = false}" class="cancelButton">Cancel</button>
          <button @click="${this.saveResponse}" class="saveButton">Save</button>
        </div>
      </div>
    `;
  }

  renderDeletePopup() {
    if (!this.showDeletePopup) return html``;

    return html`
      <div class="deleteResponsePopup">
        <p style="font-size: 1.05rem; font-weight: 600; border-bottom: 1px solid var(--border-color); padding-bottom: 8px;">Delete Response?</p>
        <div class="buttonContainer">
          <button @click="${() => this.showDeletePopup = false}" class="cancelButton">Cancel</button>
          <button @click="${this.deleteResponse}" class="deleteButton">Delete</button>
        </div>
      </div>
    `;
  }

  async saveResponse() {
    let updated = {
      id: this.responseId,
      mode: this.formMode,
      text: this.formText,
      response: this.formResponseText,
      action: this.formAction
    };

    try {
      let res = await fetch("/api/messages/responses/update_response", {
        method: "PUT",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(updated)
      });

      if (res.ok) {
        this.showEditPopup = false;
        this.updateResponseData();
      }
    } catch (e) {
      console.error(e);
    }
  }

  async deleteResponse() {
    if (this.responseId == null) return;
    try {
      let res = await fetch(`/api/messages/responses/delete_response?id=${this.responseId}`, { method: "DELETE" });
      if (res.ok) {
        this.remove();
      }
    } catch (e) {
      console.error(e);
    }
  }

  render() {
    return html`
      <div class="responseContainer">
        ${this.renderDeletePopup()}
        ${this.renderEditPopup()}
        <p><strong>Mode:</strong> ${this.mode}</p>
        <p><strong>Trigger:</strong> ${this.text}</p>
        <p><strong>Action:</strong> ${this.action}</p>
        <p><strong>Reply:</strong> ${this.responseText}</p>
        <div class="buttonContainer">
          <button @click="${this.openEditPopup}" class="editResponse">Edit</button>
          <button @click="${() => this.showDeletePopup = true}" class="deleteResponse">Delete</button>
        </div>
      </div>
    `;
  }
}

if (!customElements.get('response-card')) {
  customElements.define('response-card', ResponseCard);
}
