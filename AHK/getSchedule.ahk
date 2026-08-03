#Requires AutoHotkey v2.0
;@Ahk2Exe-ConsoleApp
#Include UIA.ahk
#Include cJson.ahk

; ============================================
; CONFIGURATION
; ============================================

TARGET_AUTOMATION_ID := "_daveControl"
DENTRIX_APP          := "C:\Program Files (x86)\Dentrix\ApptList.exe"
TARGET_COMBO_ITEM    := "DataExtract"

; ============================================
; Usage:
;     ApptExport.exe "7/18/2026"
;
; Output:
;     Outputs JSON data directly to STDOUT
; ============================================

if (A_Args.Length < 1)
{
    FileAppend("Error: Missing date argument.`nUsage: ApptExport.exe `"7/18/2026`"`n", "*")
    ExitApp 1
}

inputDate := A_Args[1]
dateParts := StrSplit(inputDate, "/")

if (dateParts.Length != 3)
{
    FileAppend("Error: Invalid date format. Use M/D/YYYY.`n", "*")
    ExitApp 1
}

passedDate := Integer(dateParts[1]) "/" Integer(dateParts[2]) "/" dateParts[3]


; ============================================
; Launch ApptList & Prepare Hidden State
; ============================================

hwnd := 0

Loop 10
{
    Run '"' DENTRIX_APP '"'

    if WinWait("ahk_exe ApptList.exe", , 10)
    {
        hwnd := WinExist("ahk_exe ApptList.exe")
        if hwnd
            break
    }

    ; Kill any half-started copy
    ProcessClose("ApptList.exe")
    Sleep 1000
}

if !hwnd
    ExitApp 1

; Ensure window is un-minimized and pushed off-screen to keep controls rendered safely
if (WinGetMinMax(hwnd) = -1)
    WinRestore(hwnd)

WinMove(-10000, -10000, , , hwnd)
Sleep 1000


; ============================================
; Attach UIA
; ============================================

root := UIA.ElementFromHandle(hwnd)

if !root
{
    FileAppend("Error: UIA attach failed.`n", "*")
    
    ExitApp 1
}


; ============================================
; Set toolStrip2 ComboBox to DataExtract
; ============================================

try toolStrip := root.FindElement({AutomationId:"toolStrip2"})
catch
    toolStrip := false

if !toolStrip
{
    FileAppend("Error: toolStrip2 not found.`n", "*")
    
    ExitApp 3
}

try combo := toolStrip.FindElement({ControlType:50003}) ; ComboBox
catch
    combo := false

if !combo
{
    FileAppend("Error: ComboBox not found.`n", "*")
    
    ExitApp 3
}

; Grab ComboBox HWND to execute background selection
comboHwnd := 0
try comboHwnd := combo.NativeWindowHandle
catch
    comboHwnd := 0

comboSuccess := false

if comboHwnd 
{
    try 
    {
        ControlChooseString(TARGET_COMBO_ITEM, comboHwnd)
        Sleep 300
        comboSuccess := true
    }
}

if !comboSuccess 
{
    try 
    {
        combo.ValuePattern.SetValue(TARGET_COMBO_ITEM)
        Sleep 300
        comboSuccess := true
    }
}

if !comboSuccess 
{
    Loop 10
    {
        try items := combo.FindElements({ControlType:50007}) ; ListItem
        catch
            items := []

        for item in items
        {
            try
            {
                if (item.CurrentName = TARGET_COMBO_ITEM)
                {
                    try
                        item.SelectionItemPattern.Select()
                    catch
                        item.Select()

                    comboSuccess := true
                    break
                }
            }
        }

        if comboSuccess
            break

        Sleep 500
    }
}

if !comboSuccess
{
    FileAppend("Error: Failed to set ComboBox to DataExtract.`n", "*")
    
    ExitApp 2
}

Sleep 1500


; ============================================
; Set date (10-Iteration Loop)
; ============================================

try datePicker := root.FindElement({AutomationId:"dateTimePicker1"})
catch
    datePicker := false

if !datePicker
{
    FileAppend("Error: dateTimePicker1 not found.`n", "*")
    
    ExitApp 4
}

ctrlHwnd := 0
try ctrlHwnd := datePicker.NativeWindowHandle
catch
    ctrlHwnd := 0

if !ctrlHwnd
{
    FileAppend("Error: Could not retrieve NativeWindowHandle for dateTimePicker1.`n", "*")
    
    ExitApp 4
}

mm   := Format("{:02d}", Integer(dateParts[1]))
dd   := Format("{:02d}", Integer(dateParts[2]))
yyyy := dateParts[3]

dateSuccess := false

GetCurrentDateValue(dp) {
    try return dp.ValuePattern.Value
    try return dp.LegacyIAccessiblePattern.CurrentValue
    return ""
}

Loop 10
{
    try
    {
        ControlFocus(ctrlHwnd)
        Sleep 100

        ControlSend("{Home}", ctrlHwnd)
        Sleep 100

        ControlSend(mm "{Right}" dd "{Right}" yyyy "{Enter}", ctrlHwnd)
        Sleep 500

        currentDate := GetCurrentDateValue(datePicker)

        if (InStr(currentDate, passedDate) || InStr(currentDate, mm "/" dd "/" yyyy))
        {
            dateSuccess := true
            break
        }
    }
    catch
    {
        Sleep 500
    }

    Sleep 500
}

if !dateSuccess
{
    FileAppend("Error: Failed to set target date after 10 attempts.`n", "*")
    
    ExitApp 5
}

Sleep 1500


; ============================================
; Find appointment pane
; ============================================

try targetPane := root.FindElement({AutomationId:TARGET_AUTOMATION_ID})
catch
    targetPane := false

if !targetPane
{
    FileAppend("Error: Target pane not found.`n", "*")
    
    ExitApp 6
}


; ============================================
; Extract Tree Structure via Spatial Positioning
; ============================================

try listCtrl := targetPane.FindElement({ControlType:50008}) ; List
catch
    listCtrl := targetPane

if !listCtrl
{
    FileAppend("Error: List element not found in target pane.`n", "*")
    
    ExitApp 7
}

; --------------------------------------------
; 1. Map & Sort Header Columns Left-to-Right
; --------------------------------------------
headers := []

try 
{
    headerControl := listCtrl.FindElement({ControlType:50034}) ; Header Control
    headerItems   := headerControl.FindElements({ControlType:50035}) ; Header Items

    for hItem in headerItems 
    {
        keyName := Trim(hItem.CurrentName)
        if (keyName != "")
        {
            rect := hItem.BoundingRectangle
            headers.Push({ name: keyName, x: rect.l, w: (rect.r - rect.l) })
        }
    }
}

if (headers.Length = 0)
{
    FileAppend("Error: Failed to parse header keys from list control.`n", "*")
    
    ExitApp 8
}

; Sort headers array numerically by X coordinate
SortByX(arr) {
    Loop arr.Length - 1 {
        i := A_Index
        Loop arr.Length - i {
            j := i + A_Index
            if (arr[i].x > arr[j].x) {
                temp := arr[i]
                arr[i] := arr[j]
                arr[j] := temp
            }
        }
    }
    return arr
}

headers := SortByX(headers)


; --------------------------------------------
; 2. Iterate Groups and Map Text Elements by X Coordinates
; --------------------------------------------
extractedRecords := []

try 
{
    groups := listCtrl.FindElements({ControlType:50026}) ; Group

    for grp in groups 
    {
        try listItem := grp.FindElement({ControlType:50007}) ; ListItem
        catch
            continue

        textElements := listItem.FindElements({ControlType:50020}) ; Text
        
        ; Initialize row map with default blank strings for every header column
        rowMap := Map()
        for h in headers
            rowMap[h.name] := ""

        for txtEl in textElements 
        {
            txtValue := Trim(txtEl.CurrentName)
            if (txtValue == "")
                continue

            rect := txtEl.BoundingRectangle
            txtMidX := rect.l + ((rect.r - rect.l) / 2)

            ; Match the text element to whichever header column spans across its X centerpoint
            for h in headers 
            {
                if (txtMidX >= h.x && txtMidX <= (h.x + h.w)) 
                {
                    rowMap[h.name] := txtValue
                    break
                }
            }
        }

        extractedRecords.Push(rowMap)
    }
}
catch as err
{
    FileAppend("Error: Failed while parsing tree items - " err.Message "`n", "*")
    
    ExitApp 9
}


; ============================================
; Convert to JSON & Output via cJson
; ============================================

try 
{
    jsonOutput := JSON.Dump(extractedRecords)
    FileAppend(jsonOutput, "*", "UTF-8")
}
catch as err
{
    FileAppend("Error: JSON serialization failed - " err.Message "`n", "*")
    
    ExitApp 11
}

; Clean up app window before exiting


ExitApp 0
