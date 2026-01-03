# Maven 测试配置说明

## 概述

本文档说明了 RuoYi 3.9.0 后端项目中 ruoyi-admin 模块的 Maven 测试配置，该配置允许在本地开发时默认跳过所有 API 测试。

## 配置详情

### Maven Surefire Plugin 配置

在 `ruoyi-admin/pom.xml` 中，maven-surefire-plugin 配置如下：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.22.2</version>
    <configuration>
        <testSourceDirectory>${project.basedir}/../tests/backend</testSourceDirectory>
        <includes>
            <include>**/*Test.java</include>
            <include>**/*Tests.java</include>
        </includes>
        <reportFormat>xml</reportFormat>
        <outputDirectory>${project.build.directory}/surefire-reports</outputDirectory>
        <!-- 使用属性控制是否跳过测试 -->
        <skipTests>${skip.tests}</skipTests>
    </configuration>
</plugin>
```

### Maven Profiles 配置

项目配置了三个 Maven Profiles：

#### 1. 开发环境 (dev) - 默认激活
```xml
<profile>
    <id>dev</id>
    <activation>
        <activeByDefault>true</activeByDefault>
    </activation>
    <properties>
        <skip.tests>true</skip.tests>
    </properties>
</profile>
```

#### 2. 测试环境 (test)
```xml
<profile>
    <id>test</id>
    <properties>
        <skip.tests>false</skip.tests>
    </properties>
</profile>
```

#### 3. CI/CD 环境 (ci)
```xml
<profile>
    <id>ci</id>
    <properties>
        <skip.tests>false</skip.tests>
    </properties>
</profile>
```

## 使用方法

### 默认行为（开发环境）
```bash
# 默认跳过所有测试
mvn clean compile
mvn clean package
mvn clean install
```

### 运行测试

#### 方法 1：激活 test profile
```bash
# 激活 test profile，运行所有测试
mvn clean test -Ptest
mvn clean package -Ptest
mvn clean install -Ptest
```

#### 方法 2：激活 ci profile
```bash
# 激活 ci profile，运行所有测试
mvn clean test -Pci
mvn clean package -Pci
mvn clean install -Pci
```

#### 方法 3：直接覆盖属性
```bash
# 直接设置 skip.tests=false
mvn clean test -Dskip.tests=false
mvn clean package -Dskip.tests=false
mvn clean install -Dskip.tests=false
```

#### 方法 4：强制运行测试
```bash
# 使用 surefire 插件强制运行测试
mvn clean compile surefire:test
```

### 跳过测试（显式）
```bash
# 显式跳过测试
mvn clean package -DskipTests=true
mvn clean install -DskipTests=true
```

## 测试文件位置

测试源代码位置：`tests/backend/`
测试报告输出位置：`ruoyi-admin/target/surefire-reports/`

## 注意事项

1. **默认行为**：在本地开发时，默认跳过所有测试，加快构建速度
2. **CI/CD 环境**：建议在 CI/CD 环境中使用 `-Pci` 激活测试
3. **手动测试**：需要运行测试时，使用 `-Ptest` 或 `-Dskip.tests=false`
4. **测试保留**：所有测试类都保留，只是默认不执行
5. **版本警告**：RestAssured 版本警告不影响功能，可忽略

## 环境变量配置

可以在 `settings.xml` 或环境变量中设置默认 profile：

```xml
<activeProfiles>
    <activeProfile>dev</activeProfile>
</activeProfiles>
```

## IDE 配置

### IntelliJ IDEA
1. 打开 Maven 工具窗口
2. 在 Profiles 中选择 `test` 或 `ci` 来运行测试
3. 默认使用 `dev` profile 跳过测试

### Eclipse
1. 右键项目 -> Run As -> Maven Build
2. 在 Profiles 中输入 `test` 或 `ci`
3. Goals 设置为 `clean test`

## 故障排除

### 测试仍然运行
- 检查是否有其他 Maven 配置覆盖了 skipTests 属性
- 确认 profile 正确激活：`mvn help:active-profiles`

### 测试文件未找到
- 确认测试文件在 `tests/backend/` 目录下
- 检查文件名是否符合 `**/*Test.java` 或 `**/*Tests.java` 模式

### 版本冲突警告
- RestAssured 版本警告不影响功能，可忽略
- 如需解决，可在父 pom.xml 中统一管理版本