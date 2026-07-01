@echo off
REM ===================================================================
REM  Build a standalone SpacesmithTimer.exe (no Python needed to run)
REM  Requires: Python 3 installed once, on this build machine only.
REM ===================================================================
setlocal
cd /d "%~dp0"

echo Installing PyInstaller (one-time)...
python -m pip install --upgrade pyinstaller || goto :err

echo.
echo Building SpacesmithTimer.exe ...
set ICON=
if exist "astronaut.ico" set ICON=--icon astronaut.ico

python -m PyInstaller ^
    --noconfirm ^
    --onefile ^
    --windowed ^
    --name SpacesmithTimer ^
    %ICON% ^
    spacesmith_timer.py || goto :err

echo.
echo ============================================================
echo  Done!  Your app is here:
echo      %~dp0dist\SpacesmithTimer.exe
echo.
echo  Double-click it to run. To make it launch every time you
echo  log in (so the 9 AM auto-start works), run:
echo      install_autostart.bat
echo ============================================================
echo.
pause
exit /b 0

:err
echo.
echo Build failed. Make sure Python 3 is installed and on your PATH.
pause
exit /b 1
