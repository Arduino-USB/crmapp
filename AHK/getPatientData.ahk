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

targetPatient := Trim(A_Args[1])

; Strip any middle name/initial from "Last, First Middle"
parts := StrSplit(targetPatient, ",")

if (parts.Length >= 2) {
    last := Trim(parts[1])
    firstParts := StrSplit(Trim(parts[2]), A_Space)
    targetPatient := last ", " firstParts[1]
}
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

; Position the Select Patient dialog off-screen unless --debug mode is active
if (!isDebugMode) {
    LogDebug("Hiding Select Patient dialog off-screen...")
    if (WinGetMinMax(dlgHwnd) = -1)
        WinRestore(dlgHwnd)
    WinMove(-10000, -10000, , , dlgHwnd)
    Sleep 500
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
; STEP 5: Read DataGridView Results Instead of Selecting Patient
; ==============================================================================

LogDebug("STEP 5: Reading patient grid data directly...")

NormalizeName(str) {
    ; remove whitespace and lowercase
    str := RegExReplace(str, "\s+", "")
    return StrLower(str)
}

ParseTargetName(input) {
    input := Trim(input)

    parts := StrSplit(input, ",")

    if (parts.Length < 2)
        return ""

    last := NormalizeName(parts[1])

    ; Take only the first word after the comma (ignore middle names/initials)
    firstParts := StrSplit(Trim(parts[2]), A_Space)
    first := NormalizeName(firstParts[1])

    return last "," first
}
GetRowValue(row, label) {
    try {
        el := row.FindElement({
            Name: label,
            LocalizedType: "item"
        })

        return el.CurrentValue
    }
    catch {
        return ""
    }
}


targetNormalized := ParseTargetName(targetPatient)

if (targetNormalized = "") {
    FileAppend("Error: Invalid patient format. Expected Last, First.`n", "*")
    ExitApp 107
}

LogDebug("Normalized target: " . targetNormalized)


try {

    ; locate search result grid
    gridTable := selectPatientDlg.WaitElement(
        {AutomationId:"dtxPatientGrid", LocalizedType:"table"},
        5000
    )

    if (!gridTable) {
        throw Error("Patient grid not found")
    }


    matched := false
    patient := Map()


    rows := gridTable.GetChildren()

    LogDebug("Grid children count: " . rows.Length)


    for row in rows {

        try {

            if (row.CurrentName = "")
                continue


            if (!InStr(row.CurrentName, "Row"))
                continue


            lastName :=
                GetRowValue(row, "Last Name " . row.CurrentName)

            firstName :=
                GetRowValue(row, "First Name " . row.CurrentName)


            if (lastName = "" || firstName = "")
                continue


            rowNormalized :=
                NormalizeName(lastName)
                . ","
                . NormalizeName(firstName)


            LogDebug(
                "Checking row: "
                . rowNormalized
            )


            if (rowNormalized = targetNormalized) {

                LogDebug("MATCH FOUND")

                patient["lastName"] := lastName
                patient["firstName"] := firstName

                patient["preferredName"] :=
                    GetRowValue(
                        row,
                        "Preferred Name " . row.CurrentName
                    )

                patient["phone"] :=
                    GetRowValue(
                        row,
                        "Phone " . row.CurrentName
                    )

                patient["chart"] :=
                    GetRowValue(
                        row,
                        "Chart # " . row.CurrentName
                    )

                patient["dob"] :=
                    GetRowValue(
                        row,
                        "Birthday " . row.CurrentName
                    )


                matched := true
                break
            }

        }
        catch as err {
            LogDebug(
                "Row parsing failed: "
                . err.Message
            )
        }
    }


    if (!matched) {

        FileAppend(
            "Error: Patient not found: "
            . targetPatient
            . "`n",
            "*"
        )

        ExitApp 108
    }


}
catch as err {

    FileAppend(
        "Error reading patient grid: "
        . err.Message
        . "`n",
        "*"
    )

    ExitApp 109
}



; ==============================================================================
; STEP 6: Output JSON
; ==============================================================================


resultPayload := Map(
    "patient", targetPatient,
    "lastName", patient["lastName"],
    "firstName", patient["firstName"],
    "preferredName", patient["preferredName"],
    "phone", patient["phone"],
    "chart", patient["chart"],
    "dob", patient["dob"]
)


try {

    jsonOutput := JSON.Dump(resultPayload)

    FileAppend(
        jsonOutput,
        "*",
        "UTF-8"
    )

}
catch as err {

    FileAppend(
        "Error serializing JSON: "
        . err.Message
        . "`n",
        "*"
    )

    ExitApp 110
}


ExitApp 0