import {html, css, LitElement} from 'lit';

export class AppointmentCard extends LitElement {
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

    .appointmentContainer p {
      margin: 0 0 12px 0;
      color: var(--text-main);
      font-size: 0.95rem;
      line-height: 1.5;
    }   
    
    .appointmentContainer {
      background-color: var(--bg-surface);
      border: 1px solid var(--border-color);
      border-radius: var(--radius-md);
      padding: 20px;
      display: flex;
      flex-direction: column;
      align-items: stretch;
      box-shadow: var(--shadow-sm);
      transition: box-shadow 0.2s ease, transform 0.2s ease;
    }

    .appointmentContainer:hover {
      box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.4), 0 4px 6px -2px rgba(0, 0, 0, 0.2);
    }

    .status-badge {
      display: inline-block;
      padding: 4px 10px;
      font-size: 0.75rem;
      font-weight: 600;
      border-radius: var(--radius-sm);
      text-transform: uppercase;
      letter-spacing: 0.05em;
      margin-bottom: 12px;
      width: fit-content;
    }

    .status-badge.confirmed {
      background-color: rgba(34, 197, 94, 0.2);
      color: #4ade80;
      border: 1px solid rgba(34, 197, 94, 0.3);
    }

    .status-badge.cancelled {
      background-color: rgba(239, 68, 68, 0.2);
      color: #f87171;
      border: 1px solid rgba(239, 68, 68, 0.3);
    }

    .status-badge.pending {
      background-color: rgba(234, 179, 8, 0.2);
      color: #facc15;
      border: 1px solid rgba(234, 179, 8, 0.3);
    }

    .buttonContainer {
      display: flex;
      flex-direction: row; 
      justify-content: flex-end;
      gap: 12px;
      margin-top: 16px;
      padding-top: 12px;
      border-top: 1px solid var(--border-color);
    }

    .buttonContainer button,
    .buttonContainer .btn {
      font-family: inherit;
      font-size: 0.875rem;
      font-weight: 500;
      padding: 8px 16px;
      border-radius: var(--radius-sm);
      border: none;
      cursor: pointer;
      transition: background-color 0.2s ease, transform 0.1s ease;
    }

    .buttonContainer button:active,
    .buttonContainer .btn:active {
      transform: scale(0.98);
    }

    .buttonContainer .editAppointment,
    .buttonContainer .btn-primary {
      background-color: var(--primary-color);
      color: var(--text-light);
    }

    .buttonContainer .editAppointment:hover,
    .buttonContainer .btn-primary:hover {
      background-color: var(--primary-hover);
    }

    .buttonContainer .deleteAppointment,
    .buttonContainer .btn-danger {
      background-color: var(--danger-color);
      color: var(--text-light);
    }

    .buttonContainer .deleteAppointment:hover,
    .buttonContainer .btn-danger:hover {
      background-color: var(--danger-hover);
    }

    .buttonContainer .cancelButton,
    .buttonContainer .btn-secondary {
      background-color: var(--secondary-color);
      color: var(--text-main);
      border: 1px solid var(--border-color);
    }

    .buttonContainer .cancelButton:hover,
    .buttonContainer .btn-secondary:hover {
      background-color: var(--secondary-hover);
      color: var(--text-light);
    }

    .buttonContainer .saveButton {
      background-color: #22c55e;
      color: var(--text-light);
    }

    .buttonContainer .saveButton:hover {
      background-color: #16a34a;
    }

    .buttonContainer .showTextLogs {
      background-color: #22c55e;
      color: var(--text-light);
    }

    .buttonContainer .showTextLogs:hover {
      background-color: #16a34a;
    }

    .editAppointmentPopup input,
    .editAppointmentPopup select,
    .createAppointmentPopup input,
    .createAppointmentPopup select {
      width: 100%;
      padding: 10px 12px;
      background-color: #0f172a;
      border: 1px solid var(--border-color);
      border-radius: var(--radius-sm);
      color: var(--text-main);
      font-size: 0.9rem;
      box-sizing: border-box;
      margin-top: 4px;
      margin-bottom: 8px;
    }

    .editAppointmentPopup input:focus,
    .editAppointmentPopup select:focus,
    .createAppointmentPopup input:focus,
    .createAppointmentPopup select:focus {
      outline: none;
      border-color: var(--primary-color);
      box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
    }

    .editAppointmentPopup label,
    .createAppointmentPopup label {
      font-size: 0.85rem;
      color: var(--text-muted);
      font-weight: 500;
    }

    .editAppointmentPopup,
    .createAppointmentPopup,
    .textLogsPopup {
      display: flex;
      flex-direction: column;
      align-items: stretch;
      padding: 32px;
      position: fixed;
      background-color: var(--bg-surface);
      border-radius: var(--radius-md);
      box-shadow: var(--shadow-lg);
      width: 100%;
      max-width: 600px;
      height: auto;
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
    
    .deleteAppointmentPopup {
      display: flex;
      flex-direction: column;
      align-items: stretch;
      padding: 32px;
      position: fixed;
      background-color: var(--bg-surface);
      border-radius: var(--radius-md);
      box-shadow: var(--shadow-lg);
      width: 100%;
      max-width: 400px;
      height: auto;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      box-sizing: border-box;
      border: 1px solid var(--border-color);
      z-index: 1000;
      color: var(--text-main);
    }
    
    .deleteAppointmentPopup .deleteButton {
      background-color: var(--danger-color);
      color: var(--text-light);
    }

    .removeVarBtn {
      background-color: var(--danger-color);
      color: var(--text-light);
      border: none;
      border-radius: var(--radius-sm);
      padding: 0 12px;
      cursor: pointer;
      font-weight: bold;
      height: 41px;
      margin-top: 4px;
      transition: background-color 0.2s ease;
    }

    .removeVarBtn:hover {
      background-color: var(--danger-hover);
    }

    .user-search-results {
      max-height: 150px;
      overflow-y: auto;
      background: var(--bg-main);
      border: 1px solid var(--border-color);
      border-radius: var(--radius-sm);
      margin-top: -4px;
      margin-bottom: 8px;
    }

    .user-search-item {
      padding: 8px 12px;
      cursor: pointer;
      font-size: 0.9rem;
      border-bottom: 1px solid var(--border-color);
    }

    .user-search-item:hover {
      background-color: var(--secondary-color);
    }

    .chat-container {
      display: flex;
      flex-direction: column;
      gap: 12px;
      max-height: 400px;
      overflow-y: auto;
      background-color: var(--bg-main);
      border: 1px solid var(--border-color);
      border-radius: var(--radius-sm);
      padding: 16px;
      margin-top: 16px;
      margin-bottom: 16px;
    }

    .chat-bubble {
      max-width: 75%;
      padding: 10px 14px;
      border-radius: var(--radius-md);
      font-size: 0.9rem;
      line-height: 1.4;
      word-break: break-word;
    }

    .chat-bubble.outbound {
      background-color: var(--primary-color);
      color: var(--text-light);
      align-self: flex-end;
      border-bottom-right-radius: 2px;
    }

    .chat-bubble.inbound {
      background-color: var(--secondary-color);
      color: var(--text-main);
      align-self: flex-start;
      border-bottom-left-radius: 2px;
      border: 1px solid var(--border-color);
    }

    .chat-timestamp {
      font-size: 0.7rem;
      color: var(--text-muted);
      margin-top: 4px;
      text-align: right;
    }
  `;

  static properties = {
    appointmentId: {type: Number},
    userId: {type: Number},
    name: {type: String},
    time: {type: String},
    confirmed: {type: Boolean},
    cancelled: {type: Boolean},
    vars: {type: Array},
    showEditPopup: {state: true},
    showDeletePopup: {state: true},
    showCreatePopup: {state: true},
    formUserId: {state: true},
    formUserName: {state: true},
    formTime: {state: true},
    newVarsArray: {state: true},
    userSearchResults: {state: true},
    showTextLogPopup: {state: true},
    smsLogs: {state: true}
  };

  constructor() {
    super();
    this.appointmentId = null;
    this.userId = null;
    this.name = 'N/A';
    this.time = 'N/A';
    this.confirmed = false;
    this.cancelled = false;
    this.vars = [];
    this.showEditPopup = false;
    this.showDeletePopup = false;
    this.showCreatePopup = false;
    this.showTextLogPopup = false;
    this.formUserId = null;
    this.formUserName = '';
    this.formTime = '';
    this.newVarsArray = [];
    this.userSearchResults = [];
    this.updateInterval = null;
    this.smsLogs = [];
  }

  connectedCallback() {
    super.connectedCallback();
    
    if (this.appointmentId != null && (!this.name || this.name === 'N/A')) {
      this.updateAppointment();
    }

    if (this.appointmentId != null) {
      this.updateInterval = setInterval(() => {
        this.updateAppointment();
      }, 1000);
    }
  }

  disconnectedCallback() {
    super.disconnectedCallback();
    if (this.updateInterval) {
      clearInterval(this.updateInterval);
    }
  }

  async updateAppointment() {
    if (this.appointmentId == null) {
      return;
    }

    try {
      let response = await fetch(`/api/appointments/get_appointment_by_id?id=${this.appointmentId}`);

      if (response.status === 404) {
        this.remove();
        return;
      }

      if (!response.ok) {
        throw new Error("Server error");
      }

      let appt = await response.json();

      this.userId = appt.userId ?? null;
      this.time = appt.time ?? "N/A";
      this.confirmed = Boolean(appt.confirmed);
      this.cancelled = Boolean(appt.cancelled);
      this.vars = appt.varList ?? [];

      if (this.userId != null) {
        let userResponse = await fetch(`/api/users/get_user_by_id?id=${this.userId}`);
        if (userResponse.ok) {
          let user = await userResponse.json();
          this.name = user.name ?? "N/A";
        } else {
          this.name = "N/A";
        }
      } else {
        this.name = "N/A";
      }
    } catch(error) {
      console.log(error);
    }
  }

  formatReadableTime(isoString) {
    if (!isoString || isoString === 'N/A') return 'N/A';
    try {
      const date = new Date(isoString);
      return date.toLocaleString(undefined, {
        dateStyle: 'medium',
        timeStyle: 'short'
      });
    } catch (e) {
      return isoString;
    }
  }

  toLocalDatetimeInput(isoString) {
    if (!isoString) return '';
    try {
      const date = new Date(isoString);
      const tzOffset = date.getTimezoneOffset() * 60000;
      const localISOTime = (new Date(date.getTime() - tzOffset)).toISOString().slice(0, 16);
      return localISOTime;
    } catch (e) {
      return '';
    }
  }

  renderStatusIndicator() {
    let statusClass = 'pending';
    let statusText = 'Pending';

    if (this.cancelled) {
      statusClass = 'cancelled';
      statusText = 'Cancelled';
    } else if (this.confirmed) {
      statusClass = 'confirmed';
      statusText = 'Confirmed';
    }

    return html`<div class="status-badge ${statusClass}">${statusText}</div>`;
  }

  varResolver() {
    if (this.vars.length === 0) {
      return html`<p><br></p>`;
    }

    return html`
      <p>Variables:</p>
      ${this.vars.map(variable => html`
        <p>Name: ${variable.varName}, Value: ${variable.varContents}</p>
      `)}
    `;
  }

  renderEditPopup() {
    if (!this.showEditPopup) return html``;

    return html`
      <div class="editAppointmentPopup">
        <p style="font-size: 1.15rem; font-weight: 600; border-bottom: 1px solid var(--border-color); padding-bottom: 12px;">Edit Appointment</p>
        
        <label>Search User (Last, First):</label>
        <input 
          type="text" 
          .value="${this.formUserName}" 
          @input="${this.handleUserSearchInput}" 
          placeholder="Type user name to search..."
        >
        ${this.userSearchResults.length > 0 ? html`
          <div class="user-search-results">
            ${this.userSearchResults.map(user => html`
              <div class="user-search-item" @click="${() => this.selectUser(user)}">
                ${user.name} (ID: ${user.id})
              </div>
            `)}
          </div>
        ` : ''}

        <label>Selected User ID: ${this.formUserId ? this.formUserId : 'None'}</label>

        <label style="margin-top: 12px;">Appointment Time:</label>
        <input 
          type="datetime-local" 
          .value="${this.formTime}" 
          @input="${e => this.formTime = e.target.value}"
        >

        ${this.editAppointmentPopupVars()}

        <button @click=${this.editAppointmentAddVariable} class="btn-secondary" style="margin-top: 8px;">
          Add Variable
        </button>

        <div class="buttonContainer">
          <button @click="${this.cancelEditAppointment}" class="cancelButton">Cancel</button>
          <button @click="${this.saveAppointment}" class="saveButton">Save</button>
        </div>
      </div>
    `;
  }

  renderCreatePopup() {
    if (!this.showCreatePopup) return html``;

    return html`
      <div class="createAppointmentPopup">
        <p style="font-size: 1.15rem; font-weight: 600; border-bottom: 1px solid var(--border-color); padding-bottom: 12px;">Create Appointment</p>
        
        <label>Search User (Last, First):</label>
        <input 
          type="text" 
          .value="${this.formUserName}" 
          @input="${this.handleUserSearchInput}" 
          placeholder="Type user name to search..."
        >
        ${this.userSearchResults.length > 0 ? html`
          <div class="user-search-results">
            ${this.userSearchResults.map(user => html`
              <div class="user-search-item" @click="${() => this.selectUser(user)}">
                ${user.name} (ID: ${user.id})
              </div>
            `)}
          </div>
        ` : ''}

        <label>Selected User ID: ${this.formUserId ? this.formUserId : 'None'}</label>

        <label style="margin-top: 12px;">Appointment Time:</label>
        <input 
          type="datetime-local" 
          .value="${this.formTime}" 
          @input="${e => this.formTime = e.target.value}"
        >

        ${this.editAppointmentPopupVars()}

        <button @click=${this.editAppointmentAddVariable} class="btn-secondary" style="margin-top: 8px;">
          Add Variable
        </button>

        <div class="buttonContainer">
          <button @click="${this.cancelCreateAppointment}" class="cancelButton">Cancel</button>
          <button @click="${this.createAppointment}" class="saveButton">Create</button>
        </div>
      </div>
    `;
  }

  renderDeletePopup() {
    if (!this.showDeletePopup) return html``;

    return html`
      <div class="deleteAppointmentPopup">
        <p style="font-size: 1.15rem; font-weight: 600; border-bottom: 1px solid var(--border-color); padding-bottom: 12px;">Delete Appointment?</p>
        <div class="buttonContainer">
          <button @click="${this.cancelDeleteAppointment}" class="cancelButton">Cancel</button>
          <button @click="${this.deleteAppointment}" class="deleteButton">Delete</button>
        </div>
      </div>
    `;
  }

  async handleUserSearchInput(e) {
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
      console.error("User search error:", err);
    }
  }

  selectUser(user) {
    this.formUserId = user.id;
    this.formUserName = user.name;
    this.userSearchResults = [];
  }

  editAppointmentAddVariable() {
    let newVariable = {
      varName: "",
      varContents: ""
    };
    this.newVarsArray.push(newVariable);
    this.requestUpdate();
  }

  removeVariable(index) {
    this.newVarsArray.splice(index, 1);
    this.requestUpdate();
  }

  editAppointmentPopupVars() {
    let htmlString = [];

    for (let i = 0; i < this.newVarsArray.length; i++) {
      htmlString.push(html`
        <div style="display: flex; gap: 8px; margin-top: 8px; align-items: flex-end;">
          <div style="flex: 1;">
            <label>Variable Name</label>
            <input
              class="newVarName"
              placeholder="Variable name"
              value="${this.newVarsArray[i].varName}"
              @input=${(e) => this.updateVarName(i, e)}
            >
          </div>
          <div style="flex: 1;">
            <label>Variable Contents</label>
            <input
              class="newVarContents"
              placeholder="Variable contents"
              value="${this.newVarsArray[i].varContents}"
              @input=${(e) => this.updateVarContents(i, e)}
            >
          </div>
          <button class="removeVarBtn" @click=${() => this.removeVariable(i)}>X</button>
        </div>
      `);
    }

    return html`${htmlString}`;
  }

  updateVarName(index, event) {
    this.newVarsArray[index].varName = event.target.value;
  }

  updateVarContents(index, event) {
    this.newVarsArray[index].varContents = event.target.value;
  }

  editAppointmentPopup() {
    this.formUserId = this.userId;
    this.formUserName = this.name;
    this.formTime = this.toLocalDatetimeInput(this.time);
    this.newVarsArray = this.vars.map(v => ({ ...v }));
    this.userSearchResults = [];
    this.showEditPopup = true;
  }

  cancelEditAppointment() {
    this.showEditPopup = false;
  }

  openCreatePopup() {
    this.formUserId = null;
    this.formUserName = '';
    this.formTime = '';
    this.newVarsArray = [];
    this.userSearchResults = [];
    this.showCreatePopup = true;
  }

  cancelCreateAppointment() {
    this.showCreatePopup = false;
  }

  async createAppointment() {
    if (!this.formUserId || !this.formTime) {
      return;
    }

    const instantTime = new Date(this.formTime).toISOString();

    let newAppointment = {
      userId: this.formUserId,
      time: instantTime,
      varList: this.newVarsArray
    };

    try {
      let response = await fetch("/api/appointments/create_appointment", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(newAppointment)
      });

      if (response.ok) {
        this.showCreatePopup = false;
        this.dispatchEvent(new CustomEvent('appointment-created', { bubbles: true, composed: true }));
      } else {
        console.error("Failed to create appointment");
      }
    } catch (error) {
      console.error("Error creating appointment:", error);
    }
  }

  async saveAppointment() {
    if (!this.formUserId || !this.formTime) {
      return;
    }

    const instantTime = new Date(this.formTime).toISOString();

    let updatedAppointment = {
      id: this.appointmentId,
      userId: this.formUserId,
      time: instantTime,
      varList: this.newVarsArray
    };

    try {
      let response = await fetch("/api/appointments/update_appointment", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(updatedAppointment)
      });

      if (response.ok) {
        let data = await response.json();
        this.showEditPopup = false;
        if (data.userId) this.userId = data.userId;
        if (data.time) this.time = data.time;
        if (data.varList) this.vars = data.varList;
        this.updateAppointment();
      } else {
        console.error("Failed to update appointment");
      }
    } catch (error) {
      console.error("Error updating appointment:", error);
    }
  }

  deleteAppointmentPopup() {
    this.showDeletePopup = true;
  }

  cancelDeleteAppointment() {
    this.showDeletePopup = false;
  }

  async deleteAppointment() {
    if (this.appointmentId == null) return;

    try {
      let response = await fetch(`/api/appointments/delete_appointment?id=${this.appointmentId}`, {
        method: "DELETE"
      });

      if (response.ok) {
        this.remove();
      } else {
        console.error("Failed to delete appointment");
      }
    } catch (error) {
      console.error("Error deleting appointment:", error);
    }
  }

  async textLogsPopup() {
    if (this.appointmentId == null) return;
    try {
      let response = await fetch(`/api/sms-logs/appointment?appointmentId=${this.appointmentId}`);
      if (response.ok) {
        this.smsLogs = await response.json();
      } else {
        this.smsLogs = [];
      }
    } catch (error) {
      console.error("Error fetching SMS logs:", error);
      this.smsLogs = [];
    }
    this.showTextLogPopup = true;
  }

  closeTextLogsPopup() {
    this.showTextLogPopup = false;
  }

  renderTextLogsPopup() {
    if (!this.showTextLogPopup) return html``;

    return html`
      <div class="textLogsPopup">
        <p style="font-size: 1.15rem; font-weight: 600; border-bottom: 1px solid var(--border-color); padding-bottom: 12px;">SMS Chat Logs</p>
        
        <div class="chat-container">
          ${this.smsLogs.length === 0 ? html`<p style="text-align: center; color: var(--text-muted); margin: auto;">No message logs found.</p>` : 
            this.smsLogs.map(log => html`
              <div class="chat-bubble ${log.direction ? log.direction.toLowerCase() : 'outbound'}">
                <div>${log.messageContent || html`<span style="font-style: italic; opacity: 0.7;">[Empty Message]</span>`}</div>
                <div class="chat-timestamp">${this.formatReadableTime(log.timestamp)}</div>
              </div>
            `)
          }
        </div>

        <div class="buttonContainer">
          <button @click="${this.closeTextLogsPopup}" class="cancelButton">Close</button>
        </div>
      </div>
    `;
  }

  render() {
    return html`
      <div class="appointmentContainer">
        ${this.renderDeletePopup()}
        ${this.renderEditPopup()}
        ${this.renderCreatePopup()}
        ${this.renderTextLogsPopup()}
        ${this.renderStatusIndicator()}
        <p><strong>Name:</strong> ${this.name}</p>
        <p><strong>Time:</strong> ${this.formatReadableTime(this.time)}</p>
        ${this.varResolver()}
        <div class="buttonContainer">
          <button @click="${this.textLogsPopup}" class="showTextLogs">Text Logs</button>
          <button @click="${this.editAppointmentPopup}" class="editAppointment">Edit</button>
          <button @click="${this.deleteAppointmentPopup}" class="deleteAppointment">Delete</button>
        </div>
      </div>
    `;
  }
}

customElements.define('appointment-card', AppointmentCard);
