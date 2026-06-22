@echo off
REM ===================================================================
REM  Make Spacesmith's Timer launch automatically at every Windows
REM  login, so the 9 AM auto-start fires as long as the PC is on.
REM
REM  It drops a shortcut into your Startup folder pointing at the
REM  built EXE (dist\SpacesmithTimer.exe) if present, otherwise at
REM  the Python script.
REM ===================================================================
setlocal
cd /d "%~dp0"

set TARGET=
if exist "%~dp0dist\SpacesmithTimer.exe" (
    set "TARGET=%~dp0dist\SpacesmithTimer.exe"
    set "ARGS="
) else (
    for /f "delims=" %%P in ('where pythonw 2^>nul') do set "PYW=%%P"
    if not defined PYW for /f "delims=" %%P in ('where python 2^>nul') do set "PYW=%%P"
    if not defined PYW (
        echo Could not find SpacesmithTimer.exe or Python. Build first or install Python.
        pause
        exit /b 1
    )
    set "TARGET=%PYW%"
    set "ARGS=%~dp0spacesmith_timer.py"
)

set "STARTUP=%APPDATA%\Microsoft\Windows\Start Menu\Programs\Startup"
set "LINK=%STARTUP%\Spacesmith's Timer.lnk"

powershell -NoProfile -Command ^
  "$s=(New-Object -ComObject WScript.Shell).CreateShortcut('%LINK%');" ^
  "$s.TargetPath='%TARGET%';" ^
  "$s.Arguments='%ARGS%';" ^
  "$s.WorkingDirectory='%~dp0';" ^
  "$s.WindowStyle=1;" ^
  "$s.Description='Spacesmith''s Timer';" ^
  "$s.Save()"

if exist "%LINK%" (
    echo.
    echo Installed! Spacesmith's Timer will now start when you log in.
    echo Shortcut: "%LINK%"
    echo.
    echo To undo, just delete that shortcut (run: shell:startup ).
) else (
    echo Failed to create the startup shortcut.
)
echo.
pause
