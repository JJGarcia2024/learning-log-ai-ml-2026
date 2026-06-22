@echo off
REM ===================================================================
REM  Register a Windows Task Scheduler task that launches
REM  Spacesmith's Timer at exactly 9:00 AM every day.
REM
REM  Uses "Start the task as soon as available" so that if the PC was
REM  off at 9:00, it launches the moment the PC is turned on.
REM
REM  This works even when the app is fully closed (unlike the Startup
REM  shortcut), because Task Scheduler launches it for you.
REM ===================================================================
setlocal EnableDelayedExpansion
cd /d "%~dp0"

set "TASKNAME=Spacesmith's Timer 9AM"
set "TEMPLATE=%~dp0SpacesmithTimer-9AM.xml"
set "GENERATED=%TEMP%\SpacesmithTimer-9AM.generated.xml"

REM --- Decide what to launch: built EXE if present, else Python script ---
if exist "%~dp0dist\SpacesmithTimer.exe" (
    set "TARGET=%~dp0dist\SpacesmithTimer.exe"
    set "ARGS="
) else (
    set "PYW="
    for /f "delims=" %%P in ('where pythonw 2^>nul') do if not defined PYW set "PYW=%%P"
    if not defined PYW for /f "delims=" %%P in ('where python 2^>nul') do if not defined PYW set "PYW=%%P"
    if not defined PYW (
        echo Could not find SpacesmithTimer.exe or Python.
        echo Build the EXE first ^(build_exe.bat^) or install Python, then re-run this.
        pause
        exit /b 1
    )
    set "TARGET=!PYW!"
    set "ARGS=%~dp0spacesmith_timer.py"
)

set "WORKDIR=%~dp0"
if "%WORKDIR:~-1%"=="\" set "WORKDIR=%WORKDIR:~0,-1%"

echo Launch target : !TARGET!
echo Arguments     : !ARGS!
echo.

REM --- Fill the template placeholders with the resolved paths ---
powershell -NoProfile -Command ^
  "$xml = Get-Content -Raw -LiteralPath '%TEMPLATE%';" ^
  "$xml = $xml.Replace('__TARGET__', [Security.SecurityElement]::Escape('!TARGET!'));" ^
  "$xml = $xml.Replace('__ARGS__',   [Security.SecurityElement]::Escape('!ARGS!'));" ^
  "$xml = $xml.Replace('__WORKDIR__',[Security.SecurityElement]::Escape('!WORKDIR!'));" ^
  "[IO.File]::WriteAllText('%GENERATED%', $xml, [Text.Encoding]::Unicode)"

if not exist "%GENERATED%" (
    echo Failed to generate the task XML.
    pause
    exit /b 1
)

REM --- Register (or replace) the scheduled task ---
schtasks /Create /TN "%TASKNAME%" /XML "%GENERATED%" /F
if errorlevel 1 (
    echo.
    echo Failed to register the task.
    del "%GENERATED%" >nul 2>&1
    pause
    exit /b 1
)

del "%GENERATED%" >nul 2>&1

echo.
echo ============================================================
echo  Done! Task "%TASKNAME%" is registered.
echo  Spacesmith's Timer will launch daily at 9:00 AM (or as soon
echo  as the PC is on afterwards).
echo.
echo  Test it now:   schtasks /Run /TN "%TASKNAME%"
echo  Remove it:     schtasks /Delete /TN "%TASKNAME%" /F
echo  Inspect it:    open "Task Scheduler" from the Start menu.
echo ============================================================
echo.
pause
