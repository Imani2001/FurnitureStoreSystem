@echo off
title Furniture Store - Setup
color 0B

echo ============================================
echo    FURNITURE STORE SALES SYSTEM - SETUP
echo ============================================
echo.

REM ---- Step 1: Check Java ----
echo [1/3] Checking Java installation...
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed on this machine.
    echo Please install Java JDK 8 or higher, then rerun this setup.
    pause
    exit /b 1
)
echo     Java found.
echo.

REM ---- Step 2: Load Database ----
echo [2/3] Setting up the database...
echo     Connecting to MySQL with XAMPP default settings...
echo.
mysql -u root < database_setup.sql
if errorlevel 1 (
    echo ERROR: Database setup failed. Check your MySQL credentials.
    pause
    exit /b 1
)
echo     Database ready.
echo.

REM ---- Step 3: Launch Application ----
echo [3/3] Starting the application...
echo.
java -jar FurnituresStoreSystem.jar

pause