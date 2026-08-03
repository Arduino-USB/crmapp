import {html, css, LitElement} from 'lit';

export class MessageCard extends LitElement {
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

    .messageContainer p {
      margin: 0 0 8px 0;
      color: var(--text-main);
      font-size: 0.9rem;
      line-height: 1.4;
    }   
    
    .messageContainer {
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

    .messageContainer:hover {
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

    .editMessage {
      background-color: var(--primary-color);
      color: var(--text-light);
    }

    .editMessage:hover {
      background-color: var(--primary-hover);
    }

    .deleteMessage {
      background-color: var(--danger-color);
      color: var(--text-light);
    }

    .deleteMessage:hover {
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

    .editMessagePopup input,
    .editMessagePopup select,
    .editMessagePopup textarea {
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

    .editMessagePopup input:focus,
    .editMessagePopup select:focus,
    .editMessagePopup textarea:focus {
      outline: none;
      border-color: var(--primary-color);
      box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
    }

    .editMessagePopup label {
      font-size: 0.8rem;
      color: var(--text-muted);
      font-weight: 500;
    }

    .editMessagePopup {
      display: flex;
      flex-direction: column;
      align-items: stretch;
      padding: 24px;
      position: fixed;
      background-color: var(--bg-surface);
      border-radius: var(--radius-md);
      box-shadow: var(--shadow-lg);
      width: 100%;
      max-width: 550px;
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
    
    .deleteMessagePopup {
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
    
    .deleteMessagePopup .deleteButton {
      background-color: var(--danger-color);
      color: var(--text-light);
    }

    .block-container {
      background: #0f172a;
      border: 1px solid var(--border-color);
      border-radius: var(--radius-sm);
      padding: 12px;
      margin-bottom: 12px;
    }

    .block-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;
    }

    .remove-block-btn {
      background: var(--danger-color);
      color: #fff;
      border: none;
      padding: 3px 8px;
      border-radius: 4px;
      font-size: 0.75rem;
      cursor: pointer;
    }

    .add-block-btn {
      background: var(--secondary-color);
      color: #fff;
      border: 1px solid var(--border-color);
      padding: 8px 10px;
      border-radius: var(--radius-sm);
      font-size: 0.8rem;
      cursor: pointer;
      margin-bottom: 16px;
      width: 100%;
    }

    .add-block-btn:hover {
      background: var(--secondary-hover);
    }

    /* IFELSE Component Row Layout Styles */
    .ifelse-row {
      display: flex;
      align-items: center;
      gap: 6px;
      margin-bottom: 6px;
    }

    .ifelse-row input[type="text"] {
      margin: 0;
      flex: 1;
    }

    .pill {
      background-color: var(--primary-color);
      color: var(--text-light);
      padding: 4px 10px;
      border-radius: 16px;
      font-size: 0.75rem;
      font-weight: 500;
      display: inline-flex;
      align-items: center;
      gap: 6px;
      flex: 1;
      justify-content: center;
    }

    .row-delete-btn {
      background: var(--danger-color);
      color: #fff;
      border: none;
      width: 24px;
      height: 24px;
      border-radius: 4px;
      cursor: pointer;
      font-size: 0.8rem;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .ifelse-actions {
      display: flex;
      gap: 8px;
      margin-top: 8px;
      margin-bottom: 12px;
    }

    .ifelse-actions button {
      background: var(--secondary-color);
      color: var(--text-main);
      border: 1px solid var(--border-color);
      padding: 4px 8px;
      border-radius: var(--radius-sm);
      font-size: 0.75rem;
      cursor: pointer;
    }

    .ifelse-actions button:hover {
      background: var(--secondary-hover);
      color: var(--text-light);
    }

    .preview-box {
      background: #1e293b;
      border: 1px dashed var(--border-color);
      padding: 8px;
      border-radius: var(--radius-sm);
      font-size: 0.8rem;
      color: var(--text-muted);
      margin-top: 8px;
    }

    .preview-box span {
      color: var(--text-main);
    }

    .linked-responses-container {
      display: flex;
      flex-direction: column;
      gap: 8px;
      margin-bottom: 16px;
    }

    .linked-response-row {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .linked-response-row select {
      margin: 0;
      flex: 1;
    }

    .link-new-btn {
      background: var(--secondary-color);
      color: var(--text-main);
      border: 1px solid var(--border-color);
      padding: 6px 12px;
      border-radius: var(--radius-sm);
      font-size: 0.8rem;
      cursor: pointer;
      align-self: flex-start;
    }

    .link-new-btn:hover {
      background: var(--secondary-hover);
      color: var(--text-light);
    }
  `;

  static properties = {
    messageId: {type: Number},
    dayMileStone: {type: Number},
    isBefore: {type: Boolean},
    textList: {type: Array},
    responseIds: {type: Array},
    allResponses: {type: Array},
    varTypes: {type: Array},
    showEditPopup: {state: true},
    showDeletePopup: {state: true},
    formMilestone: {state: true},
    formIsBefore: {state: true},
    formTextList: {state: true},
    formResponseIds: {state: true},
    previewStates: {state: true}
  };

  constructor() {
    super();
    this.messageId = null;
    this.dayMileStone = 0;
    this.isBefore = true;
    this.textList = [];
    this.responseIds = [];
    this.allResponses = [];
    this.varTypes = [];
    this.showEditPopup = false;
    this.showDeletePopup = false;
    this.formMilestone = 0;
    this.formIsBefore = true;
    this.formTextList = [];
    this.formResponseIds = [];
    this.previewStates = {};
    this.updateInterval = null;
  }

  connectedCallback() {
    super.connectedCallback();
    this.updateMessage();
    this.fetchAllResponses();
    this.fetchVarTypes();

    this.updateInterval = setInterval(() => {
      this.updateMessage();
    }, 1000);
  }

  disconnectedCallback() {
    super.disconnectedCallback();
    clearInterval(this.updateInterval);
  }

  async fetchAllResponses() {
    try {
      let res = await fetch('/api/messages/responses/get_all_responses');
      if (res.ok) {
        this.allResponses = await res.json();
      }
    } catch (e) {
      console.error("Failed to load responses list", e);
    }
  }

  async fetchVarTypes() {
    try {
      let res = await fetch('/api/messages/get_vartypes');
      if (res.ok) {
        this.varTypes = await res.json();
      }
    } catch (e) {
      console.error("Failed to load var types", e);
    }
  }

  async updateMessage() {
    if (this.messageId == null) return;

    try {
      let response = await fetch(`/api/messages/get_message_by_id?id=${this.messageId}`);
      if (response.status === 404) {
        this.remove();
        return;
      }
      if (!response.ok) throw new Error("Server error");

      let msg = await response.json();
      this.dayMileStone = msg.dayMileStone ?? 0;
      this.isBefore = msg.isBefore ?? true;
      this.textList = msg.textList || [];
      this.responseIds = msg.responseIds || [];
    } catch (error) {
      console.log(error);
    }
  }

  getSummaryText() {
    if (!this.textList || this.textList.length === 0) return 'No blocks content';
    return this.textList.map((block) => {
      if (block.mode === 'TEXT') {
        return `[Text: ${block.plainText || ''}]`;
      } else {
        return `[IfElse: ${block.varName || 'var'}]`;
      }
    }).join(' ');
  }

  renderEditPopup() {
    if (!this.showEditPopup) return html``;

    return html`
      <div class="editMessagePopup">
        <p style="font-size: 1.05rem; font-weight: 600; border-bottom: 1px solid var(--border-color); padding-bottom: 8px;">Edit Message</p>
        
        <label>Milestone (Days):</label>
        <input 
          type="number" 
          .value="${this.formMilestone}" 
          @input="${e => this.formMilestone = parseInt(e.target.value, 10) || 0}"
        >

        <label>Timing:</label>
        <select .value="${String(this.formIsBefore)}" @change="${e => this.formIsBefore = e.target.value === 'true'}">
          <option value="true">Before Appointment</option>
          <option value="false">After Appointment</option>
        </select>

        <label style="margin-top: 8px; display: block; font-weight: 600;">Text Blocks:</label>
        ${this.formTextList.map((block, index) => html`
          <div class="block-container">
            <div class="block-header">
              <label>Block #${index + 1} (${block.mode})</label>
              <button type="button" class="remove-block-btn" @click="${() => this.removeFormBlock(index)}">Remove</button>
            </div>
            
            <label>Mode:</label>
            <select .value="${block.mode}" @change="${e => this.updateBlockField(index, 'mode', e.target.value)}">
              <option value="TEXT">TEXT</option>
              <option value="IFELSE">IFELSE</option>
            </select>

            ${block.mode === 'TEXT' ? html`
              <label>Plain Text:</label>
              <textarea 
                rows="2" 
                .value="${block.plainText || ''}" 
                @input="${e => this.updateBlockField(index, 'plainText', e.target.value)}"
              ></textarea>
            ` : html`
              <label>Var Type:</label>
              <select .value="${block.varType || ''}" @change="${e => this.updateBlockField(index, 'varType', e.target.value)}">
                <option value="">Select VarType</option>
                ${this.varTypes.map(vt => html`<option value="${vt}">${vt}</option>`)}
              </select>

              <label>Var Name:</label>
              <input 
                type="text" 
                .value="${block.varName || ''}" 
                @input="${e => this.updateBlockField(index, 'varName', e.target.value)}"
              >

              <label>If Text Layout:</label>
              <div style="margin-top: 4px;">
                ${this.renderIfTextRows(block, index)}
              </div>

              <div class="ifelse-actions">
                <button type="button" @click="${() => this.addIfTextSentence(index)}">+ Add Text</button>
                <button type="button" @click="${() => this.addIfTextVariable(index)}">+ Insert Variable</button>
              </div>

              <label>Else Text (Fallback):</label>
              <input 
                type="text" 
                .value="${block.elseText || ''}" 
                @input="${e => this.updateBlockField(index, 'elseText', e.target.value)}"
              >

              <div class="preview-box">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 4px;">
                  <strong>Preview:</strong>
                  <select style="width: auto; margin: 0; padding: 2px 6px; font-size: 0.75rem;" 
                    @change="${e => this.setPreviewState(index, e.target.value)}">
                    <option value="exists">Variable Exists</option>
                    <option value="missing">Variable Missing (Else)</option>
                  </select>
                </div>
                <span>${this.getBlockPreview(block, index)}</span>
              </div>
            `}
          </div>
        `)}

        <button type="button" class="add-block-btn" @click="${this.addFormBlock}">+ Add Text Block</button>

        <label>Associated Responses:</label>
        <div class="linked-responses-container">
          ${this.formResponseIds.map((resId, index) => html`
            <div class="linked-response-row">
              <select @change="${e => this.updateLinkedResponse(index, parseInt(e.target.value, 10))}">
                <option value="">Select Response Trigger</option>
                ${this.allResponses.map(resp => html`
                  <option value="${resp.id}" ?selected="${resp.id === resId}">${resp.text}</option>
                `)}
              </select>
              <button type="button" class="row-delete-btn" @click="${() => this.removeLinkedResponse(index)}">×</button>
            </div>
          `)}
          <button type="button" class="link-new-btn" @click="${this.addLinkedResponse}">Link New Response</button>
        </div>

        <div class="buttonContainer">
          <button @click="${() => this.showEditPopup = false}" class="cancelButton">Cancel</button>
          <button @click="${this.saveMessage}" class="saveButton">Save</button>
        </div>
      </div>
    `;
  }

  renderIfTextRows(block, blockIndex) {
    const ifText = block.ifText || [];
    const insertIndexes = block.insertIndexes || [];
    
    let rows = [];
    let textCursor = 0;
    
    for (let i = 0; i <= ifText.length; i++) {
      if (textCursor < ifText.length) {
        const currentTextIdx = textCursor;
        rows.push(html`
          <div class="ifelse-row">
            <input type="text" .value="${ifText[currentTextIdx]}" 
              @input="${e => this.updateIfTextString(blockIndex, currentTextIdx, e.target.value)}">
            <button type="button" class="row-delete-btn" @click="${() => this.removeIfTextItem(blockIndex, currentTextIdx, 'text')}">×</button>
          </div>
        `);
        textCursor++;
      }
      
      if (insertIndexes.includes(textCursor)) {
        const targetVarIndex = insertIndexes.indexOf(textCursor);
        rows.push(html`
          <div class="ifelse-row">
            <div class="pill">
              <span>{${block.varName || 'variable'}}</span>
            </div>
            <button type="button" class="row-delete-btn" @click="${() => this.removeIfTextItem(blockIndex, targetVarIndex, 'var')}">×</button>
          </div>
        `);
      }
    }
    return html`${rows}`;
  }

  addIfTextSentence(blockIndex) {
    const block = this.formTextList[blockIndex];
    const ifText = [...(block.ifText || []), ''];
    this.updateBlockField(blockIndex, 'ifText', ifText);
  }

  addIfTextVariable(blockIndex) {
    const block = this.formTextList[blockIndex];
    const ifText = block.ifText || [];
    const insertIndexes = [...(block.insertIndexes || []), ifText.length];
    const updated = [...this.formTextList];
    updated[blockIndex] = { ...updated[blockIndex], insertIndexes };
    this.formTextList = updated;
  }

  updateIfTextString(blockIndex, textIndex, value) {
    const block = this.formTextList[blockIndex];
    const ifText = [...(block.ifText || [])];
    ifText[textIndex] = value;
    this.updateBlockField(blockIndex, 'ifText', ifText);
  }

  removeIfTextItem(blockIndex, itemIndex, type) {
    const block = this.formTextList[blockIndex];
    let ifText = [...(block.ifText || [])];
    let insertIndexes = [...(block.insertIndexes || [])];

    if (type === 'text') {
      ifText.splice(itemIndex, 1);
      // Adjust insertIndexes mapping values appropriately
      insertIndexes = insertIndexes.map(idx => idx > itemIndex ? idx - 1 : idx);
    } else {
      insertIndexes.splice(itemIndex, 1);
    }

    const updated = [...this.formTextList];
    updated[blockIndex] = { ...updated[blockIndex], ifText, insertIndexes };
    this.formTextList = updated;
  }

  setPreviewState(blockIndex, state) {
    this.previewStates = { ...this.previewStates, [blockIndex]: state };
  }

  getBlockPreview(block, blockIndex) {
    const state = this.previewStates[blockIndex] || 'exists';
    if (state === 'missing') {
      return block.elseText || '(No fallback text provided)';
    }

    const ifText = block.ifText || [];
    const insertIndexes = block.insertIndexes || [];
    let result = '';
    let textCursor = 0;

    for (let i = 0; i <= ifText.length; i++) {
      if (textCursor < ifText.length) {
        result += (ifText[textCursor] || '') + ' ';
        textCursor++;
      }
      if (insertIndexes.includes(textCursor)) {
        result += `[${block.varName || 'variable'}] `;
      }
    }
    return result.trim() || '(Empty if-text content)';
  }

  addLinkedResponse() {
    this.formResponseIds = [...this.formResponseIds, ''];
  }

  updateLinkedResponse(index, value) {
    const updated = [...this.formResponseIds];
    updated[index] = value;
    this.formResponseIds = updated;
  }

  removeLinkedResponse(index) {
    this.formResponseIds = this.formResponseIds.filter((_, i) => i !== index);
  }

  renderDeletePopup() {
    if (!this.showDeletePopup) return html``;

    return html`
      <div class="deleteMessagePopup">
        <p style="font-size: 1.05rem; font-weight: 600; border-bottom: 1px solid var(--border-color); padding-bottom: 8px;">Delete Message?</p>
        <div class="buttonContainer">
          <button @click="${() => this.showDeletePopup = false}" class="cancelButton">Cancel</button>
          <button @click="${this.deleteMessage}" class="deleteButton">Delete</button>
        </div>
      </div>
    `;
  }

  addFormBlock() {
    this.formTextList = [
      ...this.formTextList,
      { mode: 'TEXT', plainText: '', varType: '', varName: '', ifText: [''], insertIndexes: [], elseText: '' }
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

  openEditPopup() {
    this.formMilestone = this.dayMileStone;
    this.formIsBefore = this.isBefore;
    this.formTextList = JSON.parse(JSON.stringify(this.textList));
    if (this.formTextList.length === 0) {
      this.formTextList = [{ mode: 'TEXT', plainText: '', varType: '', varName: '', ifText: [''], insertIndexes: [], elseText: '' }];
    }
    this.formResponseIds = [...this.responseIds];
    this.fetchAllResponses();
    this.fetchVarTypes();
    this.showEditPopup = true;
  }

  async saveMessage() {
    let updatedMessage = {
      id: this.messageId,
      dayMileStone: this.formMilestone,
      isBefore: this.formIsBefore,
      textList: this.formTextList,
      responseIds: this.formResponseIds.filter(id => id)
    };

    try {
      let response = await fetch("/api/messages/update_message", {
        method: "PUT",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(updatedMessage)
      });

      if (response.ok) {
        this.showEditPopup = false;
        this.updateMessage();
      } else {
        console.error("Failed to update message");
      }
    } catch (e) {
      console.error("Error updating message:", e);
    }
  }

  async deleteMessage() {
    if (this.messageId == null) return;
    try {
      let res = await fetch(`/api/messages/delete_message?id=${this.messageId}`, { method: "DELETE" });
      if (res.ok) {
        this.remove();
      }
    } catch (e) {
      console.error("Error deleting message:", e);
    }
  }

  render() {
    return html`
      <div class="messageContainer">
        ${this.renderDeletePopup()}
        ${this.renderEditPopup()}
        <p><strong>Milestone:</strong> ${this.dayMileStone} days ${this.isBefore ? 'before' : 'after'}</p>
        <p><strong>Content:</strong> ${this.getSummaryText()}</p>
        <p><strong>Responses Linked:</strong> ${this.responseIds.length}</p>
        <div class="buttonContainer">
          <button @click="${this.openEditPopup}" class="editMessage">Edit</button>
          <button @click="${() => this.showDeletePopup = true}" class="deleteMessage">Delete</button>
        </div>
      </div>
    `;
  }
}

if (!customElements.get('message-card')) {
  customElements.define('message-card', MessageCard);
}
