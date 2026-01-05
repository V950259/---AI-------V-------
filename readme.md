通过调试costrict，对ruoyi管理系统生成四种测试方案（含测试用例、运行脚本及测试日志），展示多种形式生成报告，同时相互印证增加准确性，四种均可通过将test文件夹拖入VScode中让costrict运行.bat脚本或直接在cmd里以管理员身份运行。
每个test文件夹下面均有readme.md说明mode以及提示词，总体采用架构如下：
flowchart LR
    A[用户 / 开发者] -->|触发| B[/auto_test_start]

    B --> C[CoStrict Orchestrator<br/>任务编排中枢]

    C --> D1[仓库结构分析模块<br/>Repo Analyzer]
    C --> D2[技术栈识别模块<br/>Stack Detector]

    D1 --> E[测试方案生成<br/>Test Plan Generator]
    D2 --> E

    E --> F1[后端测试生成 Agent<br/>API Test Agent]
    E --> F2[前端测试生成 Agent<br/>UI Test Agent]

    F1 --> G1[后端测试代码<br/>JUnit / Go test / Jest]
    F2 --> G2[前端测试代码<br/>Playwright]

    G1 --> H[一键执行脚本生成<br/>run_test.sh / bat]
    G2 --> H

    H --> I[自动执行测试<br/>Test Runner]

    I -->|失败| J[失败分析模块<br/>Failure Analyzer]
    J --> K[自动修复 Agent<br/>Self-Healing]

    K --> I

    I -->|成功| L[测试报告生成<br/>Report Generator]

    L --> M[最终输出<br/>测试代码 + 报告 + 脚本]
