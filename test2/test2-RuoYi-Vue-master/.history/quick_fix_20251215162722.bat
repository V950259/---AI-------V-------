@echo off
chcp 65001 >nul
echo ========================================
echo RuoYi-Vue 一键修复脚本
echo ========================================
echo.

echo [当前时间] %date% %time%
echo.

echo [步骤1] 检查当前环境状态...
call check_services.bat
echo.

echo [步骤2] 尝试启动Redis服务...
net start redis >nul 2>&1
if errorlevel 1 (
    echo ❌ Redis服务启动失败，尝试使用redis-server命令...
    redis-server --service-start >nul 2>&1
    if errorlevel 1 (
        echo ❌ Redis服务未安装或无法启动
        echo 🔧 将执行临时修复方案（禁用验证码）
        goto :FIX_CAPTCHA
    ) else (
        echo ✅ Redis服务启动成功
        goto :CHECK_AGAIN
    )
) else (
    echo ✅ Redis服务已启动
    goto :CHECK_AGAIN
)

:FIX_CAPTCHA
echo.
echo [步骤3] 执行临时修复（禁用验证码）...
echo.
echo 请输入MySQL root用户密码（如果有的话）：
set /p mysql_password=

if "%mysql_password%"=="" (
    echo 尝试无密码连接MySQL...
    mysql -u root ruoyi < fix_captcha.sql
) else (
    echo 尝试使用密码连接MySQL...
    mysql -u root -p%mysql_password% ruoyi < fix_captcha.sql
)

if errorlevel 1 (
    echo ❌ MySQL连接失败，请手动执行以下SQL：
    echo.
    echo UPDATE sys_config SET config_value = 'false' WHERE config_key = 'sys.account.captchaEnabled';
    echo.
    echo 或使用Navicat等工具执行 fix_captcha.sql 文件
    goto :MANUAL_STEPS
) else (
    echo ✅ 验证码已禁用，重启后端服务后生效
    goto :RESTART_BACKEND
)

:CHECK_AGAIN
echo.
echo [步骤4] 验证Redis启动后的状态...
call check_services.bat
echo.

:RESTART_BACKEND
echo [步骤5] 重启后端服务说明...
echo.
echo 请手动重启后端服务：
echo 1. 关闭当前运行的后端服务（Ctrl+C）
echo 2. 重新启动：
echo    - 在命令行中运行：cd ruoyi-admin && mvn spring-boot:run
echo    - 或双击运行：ry.bat
echo.
echo 重启完成后，再次运行以下命令验证：
echo check_services.bat
echo.

:MANUAL_STEPS
echo ========================================
echo 手动操作步骤
echo ========================================
echo.
echo 1. 重启后端服务
echo 2. 重启前端服务（如果需要）
echo 3. 打开浏览器测试登录
echo 4. 默认用户名：admin，密码：admin123
echo.

echo 📋 参考文档：
echo - 详细问题分析：SOLUTION.md
echo - 脚本执行指南：HOW_TO_RUN_SCRIPTS.md
echo - 故障排除：TROUBLESHOOTING.md
echo.

echo ========================================
echo 修复脚本执行完成！
echo ========================================
pause