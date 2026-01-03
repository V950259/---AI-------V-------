@echo off
chcp 65001 >nul
echo ========================================
echo 自动化测试执行脚本
echo ========================================
echo.

set BACKEND_TEST_DIR=tests\backend
set FRONTEND_TEST_DIR=ruoyi-ui\tests\frontend
set REPORT_DIR=tests\reports
set BACKEND_REPORT_DIR=%REPORT_DIR%\backend
set FRONTEND_REPORT_DIR=%REPORT_DIR%\frontend

:: 创建报告目录
if not exist "%REPORT_DIR%" mkdir "%REPORT_DIR%"
if not exist "%BACKEND_REPORT_DIR%" mkdir "%BACKEND_REPORT_DIR%"
if not exist "%FRONTEND_REPORT_DIR%" mkdir "%FRONTEND_REPORT_DIR%"

echo [1/3] 开始执行后端测试...
echo.
cd ruoyi-admin
call mvn clean test -Dtest=com.ruoyi.test.api.*Test -DtestSourceDirectory=../tests/backend
if %ERRORLEVEL% NEQ 0 (
    echo 后端测试执行失败！
    cd ..
    goto :error
)
cd ..
echo 后端测试执行完成！
echo.

echo [2/3] 开始执行前端测试...
echo.
cd ruoyi-ui
call npm run test:unit
if %ERRORLEVEL% NEQ 0 (
    echo 前端测试执行失败！
    cd ..
    goto :error
)
cd ..
echo 前端测试执行完成！
echo.

echo [3/3] 生成测试报告...
echo.

:: 复制后端测试报告
if exist "ruoyi-admin\target\surefire-reports" (
    xcopy /E /I /Y "ruoyi-admin\target\surefire-reports" "%BACKEND_REPORT_DIR%\surefire-reports"
)

:: 复制前端测试报告
if exist "ruoyi-ui\tests\frontend\reports" (
    xcopy /E /I /Y "ruoyi-ui\tests\frontend\reports" "%FRONTEND_REPORT_DIR%"
)

:: 生成汇总报告
echo 正在生成测试汇总报告...
powershell -ExecutionPolicy Bypass -File "generate_test_report.ps1"

echo.
echo ========================================
echo 测试执行完成！
echo ========================================
echo 测试报告位置：
echo   后端报告: %BACKEND_REPORT_DIR%
echo   前端报告: %FRONTEND_REPORT_DIR%
echo   汇总报告: %REPORT_DIR%\test_summary.html
echo ========================================
goto :end

:error
echo.
echo ========================================
echo 测试执行过程中出现错误！
echo ========================================
exit /b 1

:end
exit /b 0

