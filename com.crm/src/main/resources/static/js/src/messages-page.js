import {html, css, LitElement} from 'lit';
import './message-card.js';

export class MessagesPage extends LitElement {
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
      max-width: 500px;
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

    .response-checkboxes {
      max-height: 120px;
      overflow-y: auto;
      background: #0f172a;
      border: 1px solid #475569;
      border-radius: 4px;
      padding: 8px;
      margin-top: 4px;
    }

    .response-checkbox-item {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 0.85rem;
      padding: 4px 0;
    }

    .block-container {
      background: #0f172a;
      border: 1px solid #475569;
      border-radius: 4px;
      padding: 10px;
      margin-bottom: 10px;
    }

    .block-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 6px;
    }

    .remove-block-btn {
      background: #ef4444;
      color: #fff;
      border: none;
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 0.75rem;
      cursor: pointer;
    }

    .add-block-btn {
      background: #334155;
      color: #fff;
      border: 1px solid #475569;
      padding: 6px 10px;
      border-radius: 4px;
      font-size: 0.8rem;
      cursor: pointer;
      margin-bottom: 12px;
      width: 100%;
    }

    .add-block-btn:hover {
      background: #1e293b;
    }
  `;

  static properties = {
    messages: {state: true},
    allResponses: {state: true},
    varTypes: {state: true},
    isModalOpen: {state: true},
    formMilestone: {state: true},
    formIsBefore: {state: true},
    formTextList: {state: true},
    formResponseIds: {state: true},
    formError: {state: true}
  };

  constructor() {
    super();
    this.messages = [];
    this.allResponses = [];
    this.varTypes = [];
    this.isModalOpen = false;
    this.formMilestone = 0;
    this.formIsBefore = true;
    this.formTextList = [];
    this.formResponseIds = [];
    this.formError = '';
  }

  async connectedCallback() {
    super.connectedCallback();
    await this.loadData();
  }

  async loadData() {
    try {
      let [msgRes, respRes, varRes] = await Promise.all([
        fetch('/api/messages/get_all_messages'),
        fetch('/api/messages/responses/get_all_responses'),
        fetch('/api/messages/get_vartypes')
      ]);
      if (msgRes.ok) this.messages = await msgRes.json();
      if (respRes.ok) this.allResponses = await respRes.json();
      if (varRes.ok) this.varTypes = await varRes.json();
    } catch (e) {
      console.error("Failed to load data", e);
    }
  }

  openModal() {
    this.formMilestone = 0;
    this.formIsBefore = true;
    this.formTextList = [
      { mode: 'TEXT', plainText: '', varType: '', varName: '', ifText: [], insertIndexes: [], elseText: '' }
    ];
    this.formResponseIds = [];
    this.formError = '';
    this.isModalOpen = true;
  }

  addFormBlock() {
    this.formTextList = [
      ...this.formTextList,
      { mode: 'TEXT', plainText: '', varType: '', varName: '', ifText: [], insertIndexes: [], elseText: '' }
    ];
  }

  removeFormBlock(index) {
    this.formTextList = this.formTextList.filter((_, i) => i !== index);
  }

  updateBlockField(index, field, value) {
    const updated = [...this.formTextList];
    updated[index] = { ...updated[index], [field]: value };
    this.formTextList = updated;
  }

  handleCheckbox(id, checked) {
    if (checked) {
      if (!this.formResponseIds.includes(id)) {
        this.formResponseIds = [...this.formResponseIds, id];
      }
    } else {
      this.formResponseIds = this.formResponseIds.filter(rId => rId !== id);
    }
  }

  async submitNewMessage(e) {
    e.preventDefault();
    this.formError = '';

    let payload = {
      dayMileStone: parseInt(this.formMilestone, 10) || 0,
      isBefore: this.formIsBefore,
      textList: this.formTextList,
      responseIds: this.formResponseIds
    };

    try {
      let res = await fetch('/api/messages/create_message', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(payload)
      });

      if (res.ok) {
        this.isModalOpen = false;
        this.loadData();
      } else {
        this.formError = 'Failed to create message.';
      }
    } catch (e) {
      console.error(e);
      this.formError = 'Network error.';
    }
  }

  render() {
    return html`
      <div class="top-bar">
        <button class="create-btn" @click="${this.openModal}">Create New Message</button>
      </div>

      <div class="cardContainer">
        ${this.messages.length === 0 ? html`<p>No messages found.</p>` : this.messages.map(m => html`
          <message-card 
            messageId="${m.id}" 
            dayMileStone="${m.dayMileStone}" 
            ?isBefore="${m.isBefore}"
          ></message-card>
        `)}
      </div>

      ${this.isModalOpen ? html`
        <div class="modal-overlay">
          <div class="modal">
            <h3>Create New Message</h3>
            <form @submit="${this.submitNewMessage}">
              <div class="form-group">
                <label>Milestone (Days):</label>
                <input type="number" .value="${this.formMilestone}" @input="${e => this.formMilestone = e.target.value}" required />
              </div>

              <div class="form-group">
                <label>Timing:</label>
                <select .value="${String(this.formIsBefore)}" @change="${e => this.formIsBefore = e.target.value === 'true'}">
                  <option value="true">Before Appointment</option>
                  <option value="false">After Appointment</option>
                </select>
              </div>

              <label style="margin-top: 8px; display: block; font-weight: 600; color: #94a3b8; font-size: 0.85rem;">Text Blocks:</label>
              ${this.formTextList.map((block, index) => html`
                <div class="block-container">
                  <div class="block-header">
                    <label style="font-size: 0.8rem; color: #94a3b8;">Block #${index + 1}</label>
                    <button type="button" class="remove-block-btn" @click="${() => this.removeFormBlock(index)}">Remove</button>
                  </div>
                  
                  <div class="form-group" style="margin-bottom: 8px;">
                    <label>Mode:</label>
                    <select .value="${block.mode}" @change="${e => this.updateBlockField(index, 'mode', e.target.value)}">
                      <option value="TEXT">TEXT</option>
                      <option value="IFELSE">IFELSE</option>
                    </select>
                  </div>

                  ${block.mode === 'TEXT' ? html`
                    <div class="form-group" style="margin-bottom: 8px;">
                      <label>Plain Text:</label>
                      <textarea rows="2" .value="${block.plainText || ''}" @input="${e => this.updateBlockField(index, 'plainText', e.target.value)}" required></textarea>
                    </div>
                  ` : html`
                    <div class="form-group" style="margin-bottom: 8px;">
                      <label>Var Type:</label>
                      <select .value="${block.varType || ''}" @change="${e => this.updateBlockField(index, 'varType', e.target.value)}">
                        <option value="">Select VarType</option>
                        ${this.varTypes.map(vt => html`<option value="${vt}">${vt}</option>`)}
                      </select>
                    </div>

                    <div class="form-group" style="margin-bottom: 8px;">
                      <label>Var Name:</label>
                      <input type="text" .value="${block.varName || ''}" @input="${e => this.updateBlockField(index, 'varName', e.target.value)}" required />
                    </div>

                    <div class="form-group" style="margin-bottom: 8px;">
                      <label>If Text (comma separated options):</label>
                      <input type="text" .value="${(block.ifText || []).join(', ')}" @input="${e => this.updateBlockField(index, 'ifText', e.target.value.split(',').map(s => s.trim()))}" />
                    </div>

                    <div class="form-group" style="margin-bottom: 8px;">
                      <label>Else Text (Fallback):</label>
                      <input type="text" .value="${block.elseText || ''}" @input="${e => this.updateBlockField(index, 'elseText', e.target.value)}" />
                    </div>
                  `}
                </div>
              `)}

              <button type="button" class="add-block-btn" @click="${this.addFormBlock}">+ Add Text Block</button>

              <div class="form-group">
                <label>Associated Responses:</label>
                <div class="response-checkboxes">
                  ${this.allResponses.map(resp => html`
                    <div class="response-checkbox-item">
                      <input 
                        type="checkbox" 
                        .checked="${this.formResponseIds.includes(resp.id)}"
                        @change="${e => this.handleCheckbox(resp.id, e.target.checked)}"
                      >
                      <span>[${resp.mode}] "${resp.text}"</span>
                    </div>
                  `)}
                </div>
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

customElements.define('messages-page', MessagesPage);
