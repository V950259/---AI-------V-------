@echo off
echo ========================================
echo RuoYi-Vue 环境检查脚本
echo ========================================
echo.

echo [1] 检查后端服务状态...
curl -s -o nul -w "%%{http_code}" http://localhost:8080/dev-api/captchaImage
if errorlevel 1 (
    echo ❌ 后端服务未启动或无法访问
    echo 请确保后端服务在8080端口正常运行
) else (
    echo ✅ 后端服务正常运行
)
echo.

echo [2] 检查Redis服务状态...
redis-cli ping >nul 2>&1
if errorlevel 1 (
    echo ❌ Redis服务未启动
    echo 请启动Redis服务: redis-server
) else (
    echo ✅ Redis服务正常运行
)
echo.

echo [3] 检查前端服务状态...
curl -s -o nul -w "%%{http_code}" http://localhost:80
if errorlevel 1 (
    echo ❌ 前端服务未启动或端口不是80
    echo 请启动前端服务: npm run dev
) else (
    echo ✅ 前端服务正常运行
)
echo.

echo [4] 测试验证码接口...
curl -s -H "Accept: application/json" http://localhost:8080/dev-api/captchaImage > temp_captcha.json
findstr "captchaEnabled" temp_captcha.json >nul
if errorlevel 1 (
    echo ❌ 验证码接口异常
    echo 请检查Redis和数据库连接
) else (
    echo ✅ 验证码接口正常
)
del temp_captcha.json 2>nul
echo.

echo [5] 检查端口占用情况...
echo 8080端口占用:
netstat -ano | findstr :8080
echo.
echo 80端口占用:
netstat -ano | findstr :80
echo.

echo ========================================
echo 检查完成！
echo ========================================
pause