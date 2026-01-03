# 自动化测试验收方案

## 项目概述

本项目为 RuoYi-Vue 前后端分离管理系统，已配置完整的自动化测试框架。

## 技术栈

### 后端测试
- **框架**: JUnit 5
- **工具**: RestAssured (API测试)
- **构建工具**: Maven
- **报告**: Surefire Reports (XML) + HTML汇总报告

### 前端测试
- **框架**: Jest
- **工具**: Vue Test Utils
- **构建工具**: npm
- **报告**: Jest Coverage + JUnit XML

## 目录结构

```
tests/
├── backend/                    # 后端测试
│   ├── api/                   # API接口测试
│   │   ├── LoginApiTest.java
│   │   ├── UserApiTest.java
│   │   ├── RoleApiTest.java
│   │   ├── MenuApiTest.java
│   │   ├── DeptApiTest.java
│   │   ├── PostApiTest.java
│   │   ├── NoticeApiTest.java
│   │   ├── ConfigApiTest.java
│   │   ├── DictApiTest.java
│   │   └── MonitorApiTest.java
│   └── config/                # 测试配置
│       ├── TestConfig.java
│       └── TestUtils.java
├── frontend/                   # 前端测试
│   ├── unit/                  # 单元测试
│   │   ├── components/        # 组件测试
│   │   ├── views/            # 页面测试
│   │   ├── utils/            # 工具函数测试
│   │   └── router/           # 路由测试
│   └── e2e/                  # 端到端测试（可选）
└── reports/                   # 测试报告
    ├── backend/              # 后端测试报告
    ├── frontend/             # 前端测试报告
    └── test_summary.html     # 汇总报告

ruoyi-ui/
├── jest.config.js            # Jest配置
└── tests/
    └── frontend/
        └── unit/             # 前端单元测试
```

## 测试用例覆盖

### 后端API测试（10+个接口）

1. **登录接口** (`/login`)
   - ✅ 成功场景：正常登录
   - ✅ 异常场景：用户名错误、密码错误、参数为空
   - ✅ 鉴权场景：获取用户信息、获取路由信息
   - ✅ 无权限场景：未登录访问

2. **用户管理接口** (`/system/user/*`)
   - ✅ 成功场景：获取列表、根据ID查询、新增、修改
   - ✅ 异常场景：参数错误、用户名已存在、不存在的用户
   - ✅ 无权限场景：未登录访问、无效Token

3. **角色管理接口** (`/system/role/*`)
   - ✅ 成功场景：获取列表、查询、新增、修改
   - ✅ 异常场景：参数错误、角色名称已存在
   - ✅ 无权限场景：未登录访问

4. **菜单管理接口** (`/system/menu/*`)
   - ✅ 成功场景：获取列表、查询、新增
   - ✅ 异常场景：参数错误、菜单名称已存在
   - ✅ 无权限场景：未登录访问

5. **部门管理接口** (`/system/dept/*`)
   - ✅ 成功场景：获取列表、查询、新增
   - ✅ 异常场景：参数错误、部门名称已存在
   - ✅ 无权限场景：未登录访问

6. **岗位管理接口** (`/system/post/*`)
   - ✅ 成功场景：获取列表、查询、新增
   - ✅ 异常场景：参数错误
   - ✅ 无权限场景：未登录访问

7. **通知公告接口** (`/system/notice/*`)
   - ✅ 成功场景：获取列表、查询、新增
   - ✅ 异常场景：参数错误、超长标题
   - ✅ 无权限场景：未登录访问

8. **参数配置接口** (`/system/config/*`)
   - ✅ 成功场景：获取列表、查询
   - ✅ 异常场景：不存在的配置
   - ✅ 无权限场景：未登录访问

9. **字典管理接口** (`/system/dict/*`)
   - ✅ 成功场景：获取字典类型列表、字典数据列表
   - ✅ 异常场景：不存在的字典
   - ✅ 无权限场景：未登录访问

10. **监控接口** (`/monitor/*`)
    - ✅ 成功场景：在线用户、登录日志、操作日志、服务器信息
    - ✅ 无权限场景：未登录访问

### 前端功能测试（10+个功能）

1. **登录页面** (`login.test.js`)
   - ✅ 正常场景：表单渲染、输入用户名密码、点击登录
   - ✅ 边界场景：空用户名、空密码、超长输入、特殊字符

2. **分页组件** (`Pagination.test.js`)
   - ✅ 正常场景：组件渲染、显示总记录数、页码变化
   - ✅ 边界场景：总记录数为0、超大数值、每页条数边界

3. **面包屑组件** (`Breadcrumb.test.js`)
   - ✅ 正常场景：组件渲染、有数据
   - ✅ 边界场景：空数据

4. **用户管理页面** (`user.test.js`)
   - ✅ 正常场景：页面渲染、查询表单
   - ✅ 边界场景：空查询条件、超长查询条件

5. **角色管理页面** (`role.test.js`)
   - ✅ 正常场景：页面渲染、角色列表
   - ✅ 边界场景：空列表

6. **菜单管理页面** (`menu.test.js`)
   - ✅ 正常场景：页面渲染、菜单树
   - ✅ 边界场景：空菜单树

7. **部门管理页面** (`dept.test.js`)
   - ✅ 正常场景：页面渲染、部门树
   - ✅ 边界场景：空部门树

8. **通知公告页面** (`notice.test.js`)
   - ✅ 正常场景：页面渲染、公告列表
   - ✅ 边界场景：空列表、超长标题

9. **表单验证工具** (`validate.test.js`)
   - ✅ 正常场景：有效用户名、密码、邮箱、手机号
   - ✅ 边界场景：空值、超短、超长、无效格式

10. **路由权限控制** (`permission.test.js`)
    - ✅ 正常场景：路由初始化、已登录访问
    - ✅ 边界场景：未登录访问受保护路由

## 执行方式

### Windows系统

```bash
# 一键执行所有测试
run_test.bat
```

### 手动执行

#### 后端测试
```bash
cd ruoyi-admin
mvn clean test -Dtest=com.ruoyi.test.api.*Test
```

#### 前端测试
```bash
cd ruoyi-ui
npm run test:unit
```

## 测试报告

### 报告位置

- **后端报告**: `tests/reports/backend/surefire-reports/`
- **前端报告**: `tests/reports/frontend/`
- **汇总报告**: `tests/reports/test_summary.html`

### 报告内容

汇总报告包含以下信息：
- ✅ 总测试用例数
- ✅ 通过用例数
- ✅ 失败用例数
- ✅ 整体通过率
- ✅ 后端测试统计
- ✅ 前端测试统计
- ✅ 失败用例详情（包含错误信息）

## 测试场景说明

### 成功请求 (200 OK)
- 正常数据返回
- 接口功能正常

### 异常请求 (400/500)
- 参数错误
- 缺少必需参数
- 数据格式错误
- 业务逻辑错误

### 鉴权通过 (有效Token)
- 携带有效Token
- 权限验证通过
- 正常访问接口

### 无权限验证 (401/403)
- 未登录访问
- Token无效或过期
- 权限不足

### 边界场景
- 空数据
- 超长文本
- 极端数值
- 特殊字符
- 网络延迟（前端模拟）

## 注意事项

1. **环境要求**
   - Java 1.8+
   - Maven 3.6+
   - Node.js 8.9+
   - npm 3.0+

2. **测试前准备**
   - 确保后端服务已启动（默认端口8080）
   - 确保数据库已配置
   - 确保Redis已启动（如需要）

3. **测试数据**
   - 默认使用 `admin/admin123` 作为测试账号
   - 测试数据会自动清理（使用@Transactional）

4. **报告查看**
   - 打开 `tests/reports/test_summary.html` 查看汇总报告
   - 详细报告在各子目录中

## 持续集成

可以将测试集成到CI/CD流程中：

```yaml
# 示例：GitHub Actions
- name: Run Backend Tests
  run: |
    cd ruoyi-admin
    mvn clean test

- name: Run Frontend Tests
  run: |
    cd ruoyi-ui
    npm run test:unit
```

## 维护说明

1. **添加新测试用例**
   - 后端：在 `tests/backend/api/` 下创建新的测试类
   - 前端：在 `tests/frontend/unit/` 下创建新的测试文件

2. **更新测试配置**
   - 后端：修改 `ruoyi-admin/pom.xml`
   - 前端：修改 `ruoyi-ui/jest.config.js`

3. **查看测试覆盖率**
   - 后端：`mvn test jacoco:report`
   - 前端：`npm run test:unit` (自动生成覆盖率报告)

## 联系方式

如有问题，请联系测试团队。

