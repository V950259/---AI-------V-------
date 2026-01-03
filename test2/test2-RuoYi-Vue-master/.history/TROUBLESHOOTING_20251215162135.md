# RuoYi-Vue Token获取失败问题诊断

## 问题描述
- 后端服务正常运行，显示"欢迎使用RuoYi后台管理框架，当前版本：v3.9.0"
- 前端无法获取Token，提示"无法获取Token，可能后端服务未启动"

## 问题分析

### 1. API路径匹配问题
根据配置文件分析：

**后端配置：**
- 服务器端口：8080
- Context Path：/
- Swagger路径映射：/dev-api

**前端配置：**
- 开发环境API基础路径：/dev-api
- 代理目标：http://localhost:8080

### 2. 可能的原因

#### 原因1：Redis服务未启动
验证码和Token存储依赖Redis，如果Redis未启动会导致：
- 验证码生成失败
- Token无法存储和验证

#### 原因2：数据库连接问题
验证码开关配置从数据库读取，如果数据库连接异常会导致验证码功能异常

#### 原因3：跨域配置问题
虽然前端有代理配置，但可能存在跨域配置问题

## 解决方案

### 步骤1：检查Redis服务
```bash
# 检查Redis是否启动
redis-cli ping

# 如果未启动，启动Redis服务
redis-server
```

### 步骤2：检查数据库连接
确认MySQL数据库正常运行，并且RuoYi数据库已创建。

### 步骤3：验证API接口
直接访问以下接口测试：

1. 验证码接口：http://localhost:8080/dev-api/captchaImage
2. 登录接口：http://localhost:8080/dev-api/login

### 步骤4：检查系统配置
在数据库中检查验证码开关配置：
```sql
SELECT * FROM sys_config WHERE config_key = 'sys.account.captchaEnabled';
```

### 步骤5：前端代理配置检查
确认vue.config.js中的代理配置正确：
```javascript
proxy: {
  ['/dev-api']: {
    target: 'http://localhost:8080',
    changeOrigin: true,
    pathRewrite: {
      ['^' + process.env.VUE_APP_BASE_API]: ''
    }
  }
}
```

## 常见错误及解决方法

### 错误1：Redis连接超时
**症状：** 控制台显示Redis连接错误
**解决：** 启动Redis服务，检查application.yml中的Redis配置

### 错误2：数据库连接失败
**症状：** 数据库相关错误日志
**解决：** 检查application-druid.yml中的数据库配置

### 错误3：验证码接口502错误
**症状：** 验证码无法加载
**解决：** 检查后端服务是否正常启动，端口是否被占用

## 验证步骤

1. **后端验证**
   ```bash
   curl http://localhost:8080/dev-api/captchaImage
   ```

2. **前端验证**
   - 启动前端服务：npm run dev
   - 打开浏览器控制台
   - 查看网络请求是否正常

3. **完整登录流程测试**
   - 访问验证码接口
   - 获取验证码图片和UUID
   - 提交登录请求
   - 检查Token返回

## 环境要求

- Java 8+
- MySQL 5.7+
- Redis 3.0+
- Node.js 8.9+
- npm 3.0+

## 快速检查脚本

创建一个简单的健康检查脚本来验证所有服务：

```bash
# 检查后端服务
curl -f http://localhost:8080/dev-api/captchaImage || echo "后端服务异常"

# 检查Redis
redis-cli ping || echo "Redis服务异常"

# 检查数据库（需要根据实际情况修改）
mysql -h localhost -u root -p -e "SELECT 1" ruoyi || echo "数据库连接异常"