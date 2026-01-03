# 问题解决方案

## 🔍 问题诊断结果

✅ **后端服务正常运行** (端口8080)
✅ **前端服务正常运行** (端口80)  
❌ **Redis服务未启动** - 这是主要问题！
❌ **验证码接口异常** - 由于Redis未启动导致

## 🚨 核心问题

RuoYi-Vue系统依赖Redis来存储：
- 验证码信息
- 用户Token
- 会话信息
- 缓存数据

当Redis未启动时，验证码生成失败，进而导致登录流程无法完成。

## 🛠️ 解决步骤

### 方法1：安装并启动Redis (推荐)

#### Windows环境：
```bash
# 1. 下载Redis for Windows
# 访问：https://github.com/microsoftarchive/redis/releases
# 下载Redis-x64-3.0.504.msi

# 2. 安装后启动Redis服务
net start redis

# 或者手动启动
redis-server
```

#### 使用Docker (如果有Docker)：
```bash
docker run -d -p 6379:6379 --name redis redis:latest
```

### 方法2：修改配置禁用验证码 (临时方案)

如果暂时无法安装Redis，可以禁用验证码功能：

1. **修改数据库配置**
   ```sql
   -- 连接到ruoyi数据库
   UPDATE sys_config SET config_value = 'false' WHERE config_key = 'sys.account.captchaEnabled';
   ```

2. **或者修改配置文件**
   编辑 `ruoyi-admin/src/main/resources/application.yml`：
   ```yaml
   ruoyi:
     # 临时禁用验证码
     captchaType: disabled
   ```

### 方法3：检查Redis配置

确认Redis配置正确：
```yaml
# application.yml 中的Redis配置
spring:
  redis:
    host: localhost
    port: 6379
    database: 0
    password:  # 如果有密码请填写
    timeout: 10s
```

## ✅ 验证步骤

1. **启动Redis服务**
2. **重启后端服务**
3. **测试验证码接口**：
   ```bash
   curl http://localhost:8080/dev-api/captchaImage
   ```
4. **测试登录功能**

## 📋 完整环境检查清单

- [ ] Redis服务启动 (端口6379)
- [ ] MySQL数据库运行
- [ ] 后端服务启动 (端口8080)
- [ ] 前端服务启动 (端口80)
- [ ] 数据库表已创建 (执行ry_20250522.sql)
- [ ] 验证码接口正常返回

## 🚀 快速启动脚本

创建一个启动脚本：

```batch
@echo off
echo 启动RuoYi-Vue环境...

echo [1] 启动Redis...
redis-server --service-start

echo [2] 启动后端...
cd ruoyi-admin
mvn spring-boot:run

echo [3] 启动前端...
cd ../ruoyi-ui
npm run dev
```

## 📞 如果仍有问题

1. 检查Redis日志
2. 查看后端控制台错误信息
3. 确认数据库连接配置
4. 验证防火墙设置

---

**重要提示：** Redis是RuoYi-Vue的核心依赖，必须正确配置才能正常运行系统。