#Requires AutoHotkey v2.0
;@Ahk2Exe-ConsoleApp
#Include UIA.ahk
#Include cJson.ahk

; ==============================================================================
; CONFIGURATION & CLI ARGUMENT PARSING
; ==============================================================================

DENTRIX_FAMFILE := "C:\Program Files (x86)\Dentrix\FamFile.exe"

; Ensure target patient argument is supplied
if (A_Args.Length < 1) {
    FileAppend("Error: Missing target patient name argument.`nUsage: DentrixExport.exe `"LastName, FirstName`" [--debug]`n", "*")
    ExitApp 100
}

targetPatient := A_Args[1]
isDebugMode := false

for arg in A_Args {
    if (StrLower(arg) = "--debug") {
        isDebugMode := true
        break
    }
}

LogDebug(msg) {
    global isDebugMode
    if (isDebugMode) {
        FileAppend("[DEBUG] " . msg . "`n", "*")
    }
}

LogDebug("Starting execution for patient: " . targetPatient)
LogDebug("Debug mode is active.")

; ==============================================================================
; STEP 1: Launch & Attach Family File Main Window
; ==============================================================================

LogDebug("STEP 1: Launching or attaching to FamFile.exe...")
famFileHwnd := 0

Loop 10 {
    LogDebug("Attempt " . A_Index . " to launch/find FamFile.exe")
    Run '"' DENTRIX_FAMFILE '"'
    if WinWait("ahk_exe FamFile.exe", , 10) {
        famFileHwnd := WinExist("ahk_exe FamFile.exe")
        if famFileHwnd {
            LogDebug("FamFile.exe window found with HWND: " . famFileHwnd)
            break
        }
    }
    LogDebug("FamFile.exe process did not respond in time. Terminating and retrying...")
    ProcessClose("FamFile.exe")
    Sleep 1000
}

if !famFileHwnd {
    FileAppend("Error: Failed to launch or locate FamFile.exe window.`n", "*")
    ExitApp 101
}

; Position window off-screen unless --debug mode is active
if (!isDebugMode) {
    LogDebug("Hiding window off-screen...")
    if (WinGetMinMax(famFileHwnd) = -1)
        WinRestore(famFileHwnd)
    WinMove(-10000, -10000, , , famFileHwnd)
    Sleep 500
} else {
    LogDebug("Debug mode active: Keeping FamFile window visible.")
}

mainRoot := UIA.ElementFromHandle(famFileHwnd)
if !mainRoot {
    FileAppend("Error: Failed to attach UIA to FamFile main window.`n", "*")
    ExitApp 102
}
LogDebug("Successfully attached UIA to FamFile main window.")


; ==============================================================================
; STEP 2: Locate or Trigger "Select Patient" Dialog Window
; ==============================================================================

LogDebug("STEP 2: Checking for 'Select Patient' dialog...")
selectPatientDlg := 0

dlgHwnd := WinExist("Select Patient ahk_exe FamFile.exe")

if (!dlgHwnd) {
    LogDebug("'Select Patient' dialog not open. Triggering via MenuSelect...")
    try {
        MenuSelect(famFileHwnd, , "File", "Select Patient...")
    } catch as err {
        FileAppend("Error: Failed opening 'Select Patient' menu - " err.Message "`n", "*")
        ExitApp(103)
    }

    dlgHwnd := WinWait("Select Patient ahk_exe FamFile.exe", , 5)
} else {
    LogDebug("'Select Patient' dialog was already open from previous session.")
}

if (dlgHwnd) {
    try selectPatientDlg := UIA.ElementFromHandle(dlgHwnd)
}

if (!selectPatientDlg) {
    FileAppend("Error: Could not locate 'Select Patient' window.`n", "*")
    ExitApp(104)
}

LogDebug("Successfully attached UIA directly to 'Select Patient' dialog!")

; ==============================================================================
; STEP 3: Switch to "Search By" Tab
; ==============================================================================

LogDebug("STEP 3: Switching to 'Search By' tab...")
try {
    searchByTab := selectPatientDlg.WaitElement({Name: "Search By", LocalizedType: "tab item"}, 3000)
    
    try {
        searchByTab.SelectionItemPattern.Select()
    } catch {
        searchByTab.Click()
    }
    Sleep 300
} catch as err {
    FileAppend("Error: Failed selecting 'Search By' tab - " err.Message "`n", "*")
    ExitApp(105)
}


; ==============================================================================
; STEP 4: Select Radio Button & Input Search Parameters
; ==============================================================================

LogDebug("STEP 4: Selecting radio button and entering search query...")
try {
    try {
        radioEl := selectPatientDlg.FindElement({Name: "Last Name (Last, First)", LocalizedType: "radio button"})
        radioEl.SelectionItemPattern.Select()
        Sleep 200
    } catch {
        LogDebug("Warning: Could not select 'Last Name' radio button via pattern. Continuing...")
    }

    LogDebug("Searching for Edit Box element...")
    editEl := selectPatientDlg.WaitElement({LocalizedType: "edit"}, 3000)
    
    ; Force clear previous value if dialog was already open
    try {
        editEl.ValuePattern.SetValue("") 
        editEl.ValuePattern.SetValue(targetPatient)
        LogDebug("Set search query '" . targetPatient . "' via ValuePattern.")
    } catch {
        LogDebug("ValuePattern unavailable. Executing ControlSend fallback...")
        if (eHwnd := editEl.NativeWindowHandle) {
            ControlSend("^a{BackSpace}" . targetPatient, eHwnd)
        }
    }
    Sleep 200

    LogDebug("Searching for 'Search' button...")
    btnEl := selectPatientDlg.WaitElement({Name: "Search", LocalizedType: "button"}, 3000)
    
    try {
        btnEl.InvokePattern.Invoke()
    } catch {
        if (bHwnd := btnEl.NativeWindowHandle) {
            PostMessage(0x00F5, 0, 0, bHwnd) ; BM_CLICK
        }
    }
    LogDebug("Executed 'Search' button action.")

    ; Wait briefly for DataGridView to repaint after query execution
    Sleep 800
} catch as err {
    FileAppend("Error: Failed populating search filters - " err.Message "`n", "*")
    ExitApp(106)
}


; ==============================================================================
; STEP 5: Select Result from DataGridView & Confirm OK
; ==============================================================================

LogDebug("STEP 5: Extracting search results from DataGridView...")
try {
    gridTable := selectPatientDlg.WaitElement({Name: "DataGridView", LocalizedType: "table"}, 5000)
    
    rowItem := 0
    Loop 5 {
        try {
            rowItem := gridTable.FindElement({Name: "Row 0", Type: "Custom"})
            if rowItem
                break
        } catch {
            try {
                rowItem := gridTable.FindElement({Type: "Custom"})
                if rowItem
                    break
            }
        }
        Sleep 300
    }

    if !rowItem {
        FileAppend("Error: Search results row not found in DataGridView.`n", "*")
        ExitApp(107)
    }

    ; Select & Set focus to the row item explicitly
    try {
        rowItem.SelectionItemPattern.Select()
    } catch {
        try rowItem.SetFocus()
    }
    LogDebug("Selected patient row in DataGridView.")
    Sleep 400

    LogDebug("Confirming selection with OK...")
    okBtn := selectPatientDlg.WaitElement({Name: "OK", LocalizedType: "button"}, 3000)
    
    confirmSuccess := false
    
    ; Attempt 1: Invoke Pattern
    try {
        okBtn.InvokePattern.Invoke()
        confirmSuccess := true
    } catch {
        ; Attempt 2: BM_CLICK message
        if (okHwnd := okBtn.NativeWindowHandle) {
            PostMessage(0x00F5, 0, 0, okHwnd)
            confirmSuccess := true
        }
    }

    ; Fallback: Send ENTER directly to dialog handle to submit standard modal dialog
    if (!confirmSuccess || WinExist("ahk_id " . dlgHwnd)) {
        LogDebug("Fallback: Sending Enter keystroke to 'Select Patient' dialog...")
        ControlSend("{Enter}", , "ahk_id " . dlgHwnd)
    }
    
    ; Wait up to 5 seconds for dialog window to close
    if WinWaitClose("ahk_id " . dlgHwnd, , 5) {
        LogDebug("Confirmed 'OK'. 'Select Patient' dialog closed successfully.")
    } else {
        FileAppend("Error: 'Select Patient' dialog failed to close after OK.`n", "*")
        ExitApp(108)
    }
} catch as err {
    FileAppend("Error: Failed selecting patient row or confirming OK - " err.Message "`n", "*")
    ExitApp(108)
}

; ==============================================================================
; STEP 6: Locate PFPersonal Pane & Trigger 'Patient Information'
; ==============================================================================
PostClickRel(hwnd, x, y, doubleClick := false) {
    ; Pack X and Y coordinates into a single 32-bit integer (LPARAM)
    ; Low Word = X (16-bit), High Word = Y (16-bit)
    lparam := (y << 16) | (x & 0xFFFF)
    
    ; 1. First Click
    PostMessage(0x0201, 0x0001, lparam, hwnd) ; WM_LBUTTONDOWN
    PostMessage(0x0202, 0x0000, lparam, hwnd) ; WM_LBUTTONUP
    
    if (doubleClick) {
        Sleep 50 ; Small delay to simulate double-click cadence
        PostMessage(0x0203, 0x0001, lparam, hwnd) ; WM_LBUTTONDBLCLK
        PostMessage(0x0202, 0x0000, lparam, hwnd) ; WM_LBUTTONUP
    }
}

LogDebug("STEP 6: Refreshing main handle and locating PFPersonal pane...")
; ==============================================================================
; STEP 6: Locate PFPersonal Pane & Trigger 'Patient Information'
; ==============================================================================

LogDebug("STEP 6: Pressing Enter")
Sleep 1000
; Restore a minimized window using its handle (HWND)
if (WinGetMinMax(famFileHwnd) = -1) { ; -1 means minimized
    WinRestore(famFileHwnd)
}
ControlSend("{Enter}", , "ahk_id " . famFileHwnd)


; ==============================================================================
; STEP 7: Waiting for 'Patient Information' Dialog Window
; ==============================================================================
LogDebug("STEP 7: Waiting for 'Patient Information' dialog window...")
patientInfoDlg := 0

Loop 10 {
	try {
		if (piHwnd := WinWait("Patient Information ahk_exe FamFile.exe", , 2)) {
			patientInfoDlg := UIA.ElementFromHandle(piHwnd)
			if patientInfoDlg {
				LogDebug("Attached UIA to 'Patient Information' dialog (HWND: " . piHwnd . ").")
				break
			}
		}
	}
	LogDebug("Waiting for 'Patient Information' window... (Attempt " . A_Index . ")")
	Sleep 500
}

patientInfoHwnd := 0
patientInfoDlg := 0

; Wait up to 5 seconds for the top-level window handle to appear
if (patientInfoHwnd := WinWait("Patient Information ahk_exe FamFile.exe", , 5)) {
    try {
        patientInfoDlg := UIA.ElementFromHandle(patientInfoHwnd)
    } catch as err {
        LogDebug("Failed binding UIA to Patient Information handle: " err.Message)
    }
}

if (!patientInfoDlg) {
    FileAppend("Error: Could not locate 'Patient Information' window.`n", "*")
    ExitApp 107
}

LogDebug("Successfully opened and attached UIA to 'Patient Information' dialog!")

if !patientInfoDlg {
    FileAppend("Error: Patient Information dialog failed to appear.`n", "*")
    ExitApp 109
}

GetValueTwoOffsetFromGroup(parentDlg, groupName) {
    LogDebug("Scanning group offset for target group: '" . groupName . "'")
    
    ; Get direct children cleanly using UIA.ahk built-in method
    children := parentDlg.GetChildren()
    LogDebug("Retrieved " . children.Length . " direct child elements from parent dialog.")
    
    groupIdx := 0
    for idx, child in children {
        try {
            if (child.CurrentLocalizedType = "group") {
                cName := child.CurrentName
                if (groupName = "Phone" && cName = "Phone") {
                    groupIdx := idx
                    LogDebug("Matched group 'Phone' at child index: " . idx)
                    break
                }
                if (groupName = "Personal" && InStr(cName, "Personal")) {
                    groupIdx := idx
                    LogDebug("Matched group 'Personal' (" . cName . ") at child index: " . idx)
                    break
                }
            }
        }
    }
    
    if (groupIdx = 0 || (groupIdx + 2) > children.Length) {
        LogDebug("Failed to find valid target offset element for group: " . groupName)
        return ""
    }
        
    targetEl := children[groupIdx + 2]
    LogDebug("Inspecting offset target element (+2) at index " . (groupIdx + 2) . "...")
    
    try {
        val := targetEl.ValuePattern.Value
        LogDebug("Extracted value via ValuePattern: '" . val . "'")
        return val
    }
    try {
        val := targetEl.CurrentName
        LogDebug("Extracted value via CurrentName: '" . val . "'")
        return val
    }
    LogDebug("Could not retrieve value from target element.")
    return ""
}

extractedPhone := GetValueTwoOffsetFromGroup(patientInfoDlg, "Phone")
extractedDOB   := GetValueTwoOffsetFromGroup(patientInfoDlg, "Personal")

LogDebug("Extracted Phone: " . (extractedPhone != "" ? extractedPhone : "[EMPTY]"))
LogDebug("Extracted DOB:   " . (extractedDOB != "" ? extractedDOB : "[EMPTY]"))



; ==============================================================================
; CLEANUP: Close 'Patient Information' Dialog Window
; ==============================================================================

LogDebug("Closing 'Patient Information' window...")

if (patientInfoHwnd && WinExist("ahk_id " . patientInfoHwnd)) {
    ; Send WM_CLOSE directly to the window handle
    WinClose("ahk_id " . patientInfoHwnd)
    
    ; Wait up to 3 seconds for the window to vanish
    if WinWaitClose("ahk_id " . patientInfoHwnd, , 3) {
        LogDebug("'Patient Information' dialog closed successfully.")
    } else {
        LogDebug("Warning: 'Patient Information' dialog did not close in time. Force killing dialog...")
        WinKill("ahk_id " . patientInfoHwnd)
    }
}

; ==============================================================================
; STEP 8: Serialize Output to STDOUT & Exit Cleanly
; ==============================================================================

LogDebug("STEP 8: Serializing final payload to JSON...")
resultPayload := Map(
    "patient", targetPatient,
    "phone", extractedPhone,
    "dob", extractedDOB
)

try {
    jsonOutput := JSON.Dump(resultPayload)
    LogDebug("JSON Output successfully generated.")
    FileAppend(jsonOutput, "*", "UTF-8")
} catch as err {
    FileAppend("Error: Failed serializing JSON output - " err.Message "`n", "*")
    ExitApp 110
}

LogDebug("Script completed successfully.")
ExitApp 0