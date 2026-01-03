# 生成测试汇总报告
$reportDir = "tests\reports"
$backendReportDir = "$reportDir\backend"
$frontendReportDir = "$reportDir\frontend"
$summaryFile = "$reportDir\test_summary.html"

# 读取后端测试结果
$backendPassed = 0
$backendFailed = 0
$backendTotal = 0
$backendFailures = @()

if (Test-Path "$backendReportDir\surefire-reports\TEST-*.xml") {
    $xmlFiles = Get-ChildItem "$backendReportDir\surefire-reports\TEST-*.xml"
    foreach ($xmlFile in $xmlFiles) {
        [xml]$xml = Get-Content $xmlFile
        $testsuite = $xml.testsuite
        if ($testsuite) {
            $backendTotal += [int]$testsuite.tests
            $backendPassed += [int]$testsuite.tests - [int]$testsuite.failures - [int]$testsuite.errors
            $backendFailed += [int]$testsuite.failures + [int]$testsuite.errors
            
            if ($testsuite.failure) {
                foreach ($failure in $testsuite.failure) {
                    $backendFailures += @{
                        Test = $failure.name
                        Message = $failure.message
                        Type = $failure.type
                    }
                }
            }
        }
    }
}

# 读取前端测试结果
$frontendPassed = 0
$frontendFailed = 0
$frontendTotal = 0
$frontendFailures = @()

if (Test-Path "$frontendReportDir\junit.xml") {
    [xml]$xml = Get-Content "$frontendReportDir\junit.xml"
    $testsuites = $xml.testsuites
    if ($testsuites) {
        $frontendTotal = [int]$testsuites.tests
        $frontendPassed = [int]$testsuites.tests - [int]$testsuites.failures
        $frontendFailed = [int]$testsuites.failures
    }
}

# 计算总计
$totalTests = $backendTotal + $frontendTotal
$totalPassed = $backendPassed + $frontendPassed
$totalFailed = $backendFailed + $frontendFailed
$passRate = if ($totalTests -gt 0) { [math]::Round(($totalPassed / $totalTests) * 100, 2) } else { 0 }

# 生成HTML报告
$html = @"
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>测试报告汇总</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            border-bottom: 3px solid #4CAF50;
            padding-bottom: 10px;
        }
        .summary {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin: 20px 0;
        }
        .card {
            background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
            color: white;
            padding: 20px;
            border-radius: 8px;
            text-align: center;
        }
        .card h3 {
            margin: 0 0 10px 0;
            font-size: 18px;
        }
        .card .number {
            font-size: 36px;
            font-weight: bold;
            margin: 10px 0;
        }
        .pass-rate {
            background: linear-gradient(135deg, #4CAF50 0%%, #45a049 100%%);
        }
        .failures {
            margin-top: 30px;
        }
        .failure-item {
            background-color: #ffebee;
            border-left: 4px solid #f44336;
            padding: 15px;
            margin: 10px 0;
            border-radius: 4px;
        }
        .failure-item h4 {
            color: #d32f2f;
            margin: 0 0 10px 0;
        }
        .failure-item pre {
            background-color: #fff;
            padding: 10px;
            border-radius: 4px;
            overflow-x: auto;
            font-size: 12px;
        }
        .timestamp {
            color: #666;
            font-size: 14px;
            margin-top: 20px;
            text-align: right;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>测试报告汇总</h1>
        <div class="timestamp">生成时间: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")</div>
        
        <div class="summary">
            <div class="card">
                <h3>总测试用例数</h3>
                <div class="number">$totalTests</div>
            </div>
            <div class="card" style="background: linear-gradient(135deg, #4CAF50 0%%, #45a049 100%%);">
                <h3>通过用例数</h3>
                <div class="number">$totalPassed</div>
            </div>
            <div class="card" style="background: linear-gradient(135deg, #f44336 0%%, #d32f2f 100%%);">
                <h3>失败用例数</h3>
                <div class="number">$totalFailed</div>
            </div>
            <div class="card pass-rate">
                <h3>通过率</h3>
                <div class="number">$passRate%%</div>
            </div>
        </div>
        
        <h2>后端测试统计</h2>
        <div class="summary">
            <div class="card">
                <h3>总用例数</h3>
                <div class="number">$backendTotal</div>
            </div>
            <div class="card" style="background: linear-gradient(135deg, #4CAF50 0%%, #45a049 100%%);">
                <h3>通过</h3>
                <div class="number">$backendPassed</div>
            </div>
            <div class="card" style="background: linear-gradient(135deg, #f44336 0%%, #d32f2f 100%%);">
                <h3>失败</h3>
                <div class="number">$backendFailed</div>
            </div>
        </div>
        
        <h2>前端测试统计</h2>
        <div class="summary">
            <div class="card">
                <h3>总用例数</h3>
                <div class="number">$frontendTotal</div>
            </div>
            <div class="card" style="background: linear-gradient(135deg, #4CAF50 0%%, #45a049 100%%);">
                <h3>通过</h3>
                <div class="number">$frontendPassed</div>
            </div>
            <div class="card" style="background: linear-gradient(135deg, #f44336 0%%, #d32f2f 100%%);">
                <h3>失败</h3>
                <div class="number">$frontendFailed</div>
            </div>
        </div>
"@

if ($backendFailures.Count -gt 0 -or $frontendFailed -gt 0) {
    $html += @"
        <div class="failures">
            <h2>失败用例详情</h2>
"@
    
    foreach ($failure in $backendFailures) {
        $html += @"
            <div class="failure-item">
                <h4>后端测试 - $($failure.Test)</h4>
                <p><strong>错误类型:</strong> $($failure.Type)</p>
                <pre>$($failure.Message)</pre>
            </div>
"@
    }
    
    if ($frontendFailed -gt 0) {
        $html += @"
            <div class="failure-item">
                <h4>前端测试失败</h4>
                <p>共有 $frontendFailed 个前端测试用例失败，请查看详细报告。</p>
            </div>
"@
    }
    
    $html += @"
        </div>
"@
}

$html += @"
    </div>
</body>
</html>
"@

$html | Out-File -FilePath $summaryFile -Encoding UTF8
Write-Host "测试汇总报告已生成: $summaryFile"

