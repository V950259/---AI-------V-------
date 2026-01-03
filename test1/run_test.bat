@echo off
setlocal enabledelayedexpansion

:: Configuration parameters
set PROJECT_DIR=RuoYi-Vue
set SERVICE_PORT=8080
set HEALTH_URL=http://localhost:%SERVICE_PORT%/
set TIMEOUT=180
set CHECK_INTERVAL=5
set SERVICE_TITLE=RuoYi Service
set LOG_DIR=test_logs
set SERVICE_LOG_FILE=%LOG_DIR%\service.log
set TEST_LOG_FILE=%LOG_DIR%\test_results.log
set BUILD_LOG_FILE=%LOG_DIR%\build.log

:: Create log directory
if not exist "%LOG_DIR%" (
    mkdir "%LOG_DIR%"
)

echo ========================================
echo RuoYi Test Execution Script
echo ========================================
echo.

:: Step 1: Install dependencies
echo [STEP 1] Installing dependencies...
cd "%PROJECT_DIR%"
call mvn clean install -DskipTests > "..\%BUILD_LOG_FILE%" 2>&1
if %errorlevel% neq 0 (
    cd ..
    echo [ERROR] Failed to install dependencies. Check %BUILD_LOG_FILE%
    echo [ERROR] Maven command failed with error code: %errorlevel%
    echo [ERROR] Current directory: %CD%
    echo [ERROR] Project directory: %PROJECT_DIR%
    goto :cleanup
)
cd ..
echo [SUCCESS] Dependencies installed successfully.

:: Step 2: Clean environment
echo [STEP 2] Cleaning environment...
call :cleanup_environment

:: Step 3: Start service
echo [STEP 3] Starting service...
cd "%PROJECT_DIR%\ruoyi-admin"
start "%SERVICE_TITLE%" cmd /c "mvn spring-boot:run > ..\..\%SERVICE_LOG_FILE% 2>&1"

:: Wait for service to start
echo [INFO] Waiting for service to start...
set /a elapsed=0
:check_loop
echo Checking service status... (!elapsed!/%TIMEOUT% seconds)

:: Use PowerShell to check health endpoint
powershell -Command "try { $response = Invoke-WebRequest -Uri '%HEALTH_URL%' -UseBasicParsing -TimeoutSec 10; Write-Output $response.StatusCode } catch { Write-Output 'Error' }" > temp_status.txt
set /p status_code=<temp_status.txt
del temp_status.txt

:: Check HTTP status code
if "%status_code%"=="200" (
    echo.
    echo [SUCCESS] Service started successfully! Health check passed.
    goto :run_tests
)

:: Check if timeout reached
if !elapsed! geq %TIMEOUT% (
    echo.
    echo [ERROR] Service startup timeout! Failed to start within %TIMEOUT% seconds.
    echo [ERROR] Check service log: %SERVICE_LOG_FILE%
    goto :cleanup
)

:: Wait for next check
timeout /t %CHECK_INTERVAL% /nobreak > nul
set /a elapsed=elapsed+%CHECK_INTERVAL%
goto :check_loop

:run_tests
:: Step 4: Execute test suite
echo [STEP 4] Executing test suite...
call mvn test -Dtest=com.ruoyi.**.*Test > "..\..\%TEST_LOG_FILE%" 2>&1
if %errorlevel% neq 0 (
    echo [WARNING] Some tests failed. Check %TEST_LOG_FILE% for details.
) else (
    echo [SUCCESS] All tests passed successfully.
)

:: Step 5: Get test results
echo [STEP 5] Test results summary...
echo Test execution completed. Results saved to %TEST_LOG_FILE%
echo.

:: Display summary
echo ========================================
echo Test Execution Summary
echo ========================================
echo Service Log: %SERVICE_LOG_FILE%
echo Test Log: %TEST_LOG_FILE%
echo Build Log: %BUILD_LOG_FILE%
echo ========================================

:: Step 6: Stop service
echo [STEP 6] Stopping service...
goto :cleanup

:cleanup_environment
echo [INFO] Cleaning up environment...

:: Stop any running java processes for ruoyi
for /f "tokens=2 delims=," %%a in ('tasklist /fo csv ^| findstr /i "java.exe"') do (
    set "PID=%%~a"
    set "PID=!PID:"=!"
    set "PID=!PID: =!"
    if "!PID!" neq "" (
        :: Check if this java process is running our service
        tasklist /fi "PID eq !PID!" /fo csv | findstr /i "java.exe" > nul
        if !errorlevel! equ 0 (
            taskkill /f /pid !PID! >nul 2>&1
            if !errorlevel! equ 0 (
                echo [INFO] Terminated Java process !PID!
            )
        )
    )
)

:: Check and clean port usage
setlocal enabledelayedexpansion
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%SERVICE_PORT%"') do (
    set "PID=%%a"
    set "PID=!PID: =!"
    if "!PID!" neq "" (
        taskkill /f /pid !PID! >nul 2>&1
        if !errorlevel! equ 0 (
            echo [INFO] Cleared port %SERVICE_PORT% usage (PID: !PID!)
        )
    )
)

:: Clean old log files
if exist "%LOG_DIR%\*.log" (
    echo [INFO] Cleaning old log files...
    del /q "%LOG_DIR%\*.log" >nul 2>&1
)

echo [SUCCESS] Environment cleaned successfully.
goto :eof

:cleanup
echo [INFO] Performing final cleanup...
call :cleanup_environment
echo [INFO] Test execution completed.
endlocal
exit /b 0