import {html, css, LitElement} from 'lit';

export class UserCard extends LitElement {
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

    .userContainer p {
      margin: 0 0 12px 0;
      color: var(--text-main);
      font-size: 0.95rem;
      line-height: 1.5;
    }   
    
    .userContainer {
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

    .userContainer:hover {
      box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.4), 0 4px 6px -2px rgba(0, 0, 0, 0.2);
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

    .buttonContainer .editUser,
    .buttonContainer .btn-primary {
      background-color: var(--primary-color);
      color: var(--text-light);
    }

    .buttonContainer .editUser:hover,
    .buttonContainer .btn-primary:hover {
      background-color: var(--primary-hover);
    }

    .buttonContainer .deleteUser,
    .buttonContainer .btn-danger {
      background-color: var(--danger-color);
      color: var(--text-light);
    }

    .buttonContainer .deleteUser:hover,
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

    .editUserPopup input {
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

    .editUserPopup input:focus {
      outline: none;
      border-color: var(--primary-color);
      box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
    }

    .editUserPopup label {
      font-size: 0.85rem;
      color: var(--text-muted);
      font-weight: 500;
    }

    .editUserPopup {
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
    
    .deleteUserPopup {
      display: flex;
      flex-direction: column;
      align-items: stretch;
      padding: 32px;
      position: fixed;
      background-color: var(--bg-surface);
      border-radius: var(--radius-md);
      box-shadow: var(--shadow-lg);
      width: 100%;
      max-width: 60vw;
      height: auto;
      max-height: 20vh;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      box-sizing: border-box;
      border: 1px solid var(--border-color);
      z-index: 1000;
      color: var(--text-main);
    }
    
    .deleteUserPopup .deleteButton {
        background-color: var(--danger-color);
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
  `;

  static properties = {
    userId: {type: Number},
    name: {type: String},
    phone: {type: String},
    prefName: {type: String},
    vars: {type: Array},
    showEditPopup: {state: true},
    showDeletePopup: {state: true},
    newVarsArray: {state: true}
  };

  constructor() {
    super();
    this.name = 'N/A';
    this.phone = 'N/A';
    this.prefName = 'N/A';
    this.vars = [];
    this.showEditPopup = false;
    this.showDeletePopup = false;
    this.newNameInput = null;
    this.newPrefNameInput = null;
    this.newPhoneInput = null;
    this.newVarsArray = [];
    this.userId = null;
    this.updateInterval = null;
  }

  connectedCallback() {
    super.connectedCallback();
    
    if (!this.name || this.name === 'N/A') {
      this.updateUser();
    }

    this.updateInterval = setInterval(() => {
      this.updateUser();
    }, 1000);
  }

  disconnectedCallback() {
    super.disconnectedCallback();
    clearInterval(this.updateInterval);
  }

  async updateUser() {
    if (this.userId == null) {
      return;
    }

    try {
      let response = await fetch(`/api/users/get_user_by_id?id=${this.userId}`);

      if (response.status === 404) {
        this.remove();
        return;
      }

      if (!response.ok) {
        throw new Error("Server error");
      }

      let user = await response.json();

      this.name = user.name ?? "N/A";
      this.phone = user.phone ?? "N/A";
      this.prefName = user.prefName ?? "N/A";
      this.vars = user.varList ?? [];
    } catch(error) {
      console.log(error);
    }
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
    if (this.showEditPopup === true) {
      return html`<div class="editUserPopup">
                    <p style="font-size: 1.15rem; font-weight: 600; border-bottom: 1px solid var(--border-color); padding-bottom: 12px;">Edit ${this.name}</p>
                    <label>Enter new name in Last, First format</label>
                    <input @input=${e => this.newNameInput = e.target.value} class="nameInput" placeholder="${this.name}">
                    
                    <label>Enter new phone # in +1XXXXXXXXXX format</label>
                    <input @input=${e => this.newPhoneInput = e.target.value} class="phoneInput" placeholder="${this.phone}">
                    
                    <label>Enter new preferred name</label>
                    <input @input=${e => this.newPrefNameInput = e.target.value} class="prefNameInput" placeholder="${this.prefName}">
                    
                    ${this.editUserPopupVars()}
                    
                    <button @click=${this.editUserAddVariable} class="btn-secondary" style="margin-top: 8px;">
                      Add Variable
                    </button>
                    
                    <div class="buttonContainer">
                        <button @click=${this.CancelEditUser} class="cancelButton">Cancel</button>
                        <button @click=${this.editUser} class="saveButton">Save</button>
                    </div>
                  </div>`;
    }
  }

  renderDeletePopup() {
    if (this.showDeletePopup === true) {
      return html`<div class="deleteUserPopup">
                    <p style="font-size: 1.15rem; font-weight: 600; border-bottom: 1px solid var(--border-color); padding-bottom: 12px;">Delete ${this.name}?</p>
                    
                    <div class="buttonContainer">
                        <button @click=${this.cancelDeleteUser} class="cancelButton">Cancel</button>
                        <button @click=${this.deleteUser} class="deleteButton">Delete</button>
                    </div>
                  </div>`;
    }
  }

  cancelDeleteUser() {
    this.showDeletePopup = false;    
  }    

  async deleteUser() {
    if (this.userId == null) {
      return;
    }

    try {
      let response = await fetch(`/api/users/delete_user?id=${this.userId}`, {
        method: "DELETE"
      });

      if (response.ok) {
        this.remove();
      } else {
        console.error("Failed to delete user on the server.");
      }
    } catch (error) {
      console.error("Error deleting user:", error);
    }
  } 
 
  editUserAddVariable() {
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
  
  editUserPopupVars() {
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
  
  editUserPopup() {
    // Populate current vars into the edit array (deep copy to prevent mutating live state directly)
    this.newVarsArray = this.vars.map(v => ({ ...v }));
    this.newNameInput = this.name;
    this.newPhoneInput = this.phone;
    this.newPrefNameInput = this.prefName;
    this.showEditPopup = true;
  }

  editUser() {
    let updatedUser = {
        id: this.userId,
        name: this.newNameInput,
        phone: this.newPhoneInput,
        prefName: this.newPrefNameInput,
        varList: this.newVarsArray
    };

    Object.keys(updatedUser).forEach(key => {
        if (updatedUser[key] == null) {
            delete updatedUser[key];
        }
    });

    fetch("/api/users/update_user", {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(updatedUser)
    })
    .then(response => response.json())
    .then(data => {
        console.log(data);
        this.showEditPopup = false;
        if (data.name) this.name = data.name;
        if (data.phone) this.phone = data.phone;
        if (data.prefName) this.prefName = data.prefName;
        if (data.varList) this.vars = data.varList;
    });
  }

  CancelEditUser() {
      this.showEditPopup = false;
  }
  
  deleteUserPopup() {
    this.showDeletePopup = true;
  }

  render() {
    return html`
      <div class="userContainer">
        ${this.renderDeletePopup()}
        ${this.renderEditPopup()}
        <p><strong>Name:</strong> ${this.name}</p>
        <p><strong>Phone:</strong> ${this.phone}</p>
        ${this.varResolver()}
        <div class="buttonContainer">
          <button @click=${this.editUserPopup} class="editUser">Edit</button>
          <button @click=${this.deleteUserPopup} class="deleteUser">Delete</button>
        </div>
      </div>
    `;
  }
}

customElements.define('user-card', UserCard);
