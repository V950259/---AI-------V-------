@echo off
chcp 65001 >nul
echo ========================================
echo RuoYi-Vue 验证码修复脚本（使用密码）
echo ========================================
echo.

echo 正在禁用验证码功能以解决Redis未启动问题...
echo.

mysql -u root -p060412yu ruoyi -e "UPDATE sys_config SET config_value = 'false', remark = '是否开启验证码功能（true开启，false关闭）- 临时关闭以解决Redis问题' WHERE config_key = 'sys.account.captchaEnabled';"

if errorlevel 1 (
    echo ❌ MySQL连接失败，请检查：
    echo 1. MySQL服务是否启动
    echo 2. 数据库ruoyi是否存在
    echo 3. 密码是否正确：060412yu
    echo.
    echo 手动执行以下SQL：
    echo UPDATE sys_config SET config_value = 'false' WHERE config_key = 'sys.account.captchaEnabled';
    goto :END
)

echo ✅ 验证码已成功禁用！
echo.

echo 📋 后续步骤：
echo 1. 重启后端服务
echo 2. 打开浏览器测试登录
echo 3. 用户名：admin，密码：admin123（无需验证码）
echo.

echo 🔄 重启后端服务方法：
echo 方法1：Ctrl+C 停止当前服务，然后重新运行 mvn spring-boot:run
echo 方法2：双击运行 ry.bat 文件
echo.

:END
pause