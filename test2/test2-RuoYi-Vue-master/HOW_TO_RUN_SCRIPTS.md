# 脚本执行指南

## 📋 脚本清单

我已经为你创建了4个关键脚本，以下是详细的执行方法：

### 1. 🔍 环境检查脚本 - [`check_services.bat`](check_services.bat)
**用途**：检查所有服务状态，诊断问题根源
**执行方法**：
```bash
# 方法1：双击文件
在文件资源管理器中双击 check_services.bat

# 方法2：命令行执行
check_services.bat

# 方法3：在VSCode终端中执行
.\check_services.bat
```

### 2. 🛠️ 快速修复脚本 - [`fix_captcha.sql`](fix_captcha.sql)
**用途**：临时禁用验证码，解决Redis未启动问题
**执行方法**：

#### 方法A：使用MySQL命令行
```bash
mysql -u root -p ruoyi < fix_captcha.sql
```

#### 方法B：使用Navicat等图形化工具
1. 打开Navicat/phpMyAdmin等工具
2. 连接到mysql数据库
3. 选择 `ruoyi` 数据库
4. 打开SQL查询窗口
5. 复制 [`fix_captcha.sql`](fix_captcha.sql) 中的内容
6. 执行SQL语句

#### 方法C：使用批处理脚本（推荐）
我为你创建一个一键修复脚本：
<execute_command>
<command>echo @echo off > quick_fix.bat</command>
</execute_command>

### 3. 🚀 一键启动脚本 - [`quick_fix.bat`](quick_fix.bat) (即将创建)
**用途**：自动执行所有修复步骤
**执行方法**：
```bash
quick_fix.bat
```

## 🎯 推荐执行顺序

### 方案1：完整修复（推荐Redis）

1. **先检查环境**：
   ```bash
   check_services.bat
   ```

2. **启动Redis服务**：
   ```bash
   # 如果已安装Redis
   net start redis
   
   # 或直接运行
   redis-server
   ```

3. **重启后端服务**：
   - 停止当前后端服务
   - 重新启动：`mvn spring-boot:run` 或运行 [`ry.bat`](ry.bat)

4. **再次检查**：
   ```bash
   check_services.bat
   ```

### 方案2：临时修复（无Redis）

1. **执行SQL修复**：
   ```bash
   mysql -u root -p ruoyi < fix_captcha.sql
   ```

2. **重启后端服务**

3. **测试登录**

## 🔧 详细操作步骤

### 步骤1：打开命令提示符
- 按 `Win + R`
- 输入 `cmd`
- 按 Enter

### 步骤2：进入项目目录
```bash
cd /d e:\RuoYi-Vue-master
```

### 步骤3：运行检查脚本
```bash
check_services.bat
```

### 步骤4：根据结果选择修复方案

#### 如果显示Redis未启动：
```bash
# 尝试启动Redis（如果已安装）
net start redis

# 或者下载安装Redis
# 访问：https://github.com/microsoftarchive/redis/releases
```

#### 如果想临时禁用验证码：
```bash
# 执行SQL脚本
mysql -u root -p ruoyi < fix_captcha.sql
```

### 步骤5：重启服务
```bash
# 重启后端
# 在新的命令行窗口中：
cd /d e:\RuoYi-Vue-master\ruoyi-admin
mvn spring-boot:run

# 如果前端也重启
cd /d e:\RuoYi-Vue-master\ruoyi-ui
npm run dev
```

### 步骤6：验证修复
```bash
# 再次运行检查
check_services.bat

# 或直接测试API
curl http://localhost:8080/dev-api/captchaImage
```

## 🆘 常见问题

### Q: 提示"mysql不是内部或外部命令"
**A**: 需要将MySQL的bin目录添加到系统PATH，或使用完整路径：
```bash
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p ruoyi < fix_captcha.sql
```

### Q: 提示"redis不是内部或外部命令"
**A**: 需要安装Redis或添加到PATH：
```bash
# 下载地址：https://github.com/microsoftarchive/redis/releases
# 或使用Chocolatey安装：choco install redis-64
```

### Q: 执行脚本没反应
**A**: 确保有管理员权限，右键以管理员身份运行命令提示符

### Q: 不知道MySQL密码
**A**: 检查 [`ruoyi-admin/src/main/resources/application-druid.yml`](ruoyi-admin/src/main/resources/application-druid.yml) 文件中的数据库配置

## 📞 如果仍然有问题

1. 查看控制台错误信息
2. 检查防火墙设置
3. 确认端口没有被占用
4. 重启计算机后重试

---

**提示**：建议先运行 [`check_services.bat`](check_services.bat) 确认问题，然后根据提示选择合适的修复方案。