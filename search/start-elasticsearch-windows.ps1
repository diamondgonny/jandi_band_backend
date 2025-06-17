# Windows용 Elasticsearch 시작 스크립트 (PowerShell)
param(
    [switch]$Force,
    [switch]$SkipChecks
)

Write-Host "Windows용 Elasticsearch 시작 스크립트" -ForegroundColor Green
Write-Host ""

# Git Bash가 있는지 확인
$bashPath = Get-Command bash -ErrorAction SilentlyContinue
if ($bashPath) {
    Write-Host "Git Bash를 사용하여 Elasticsearch를 시작합니다..." -ForegroundColor Cyan
    
    # 매개변수를 bash 스크립트로 전달
    $bashArgs = @("start-elasticsearch.sh")
    if ($Force) { $bashArgs += "--force" }
    if ($SkipChecks) { $bashArgs += "--skip-checks" }
    
    & bash $bashArgs
} else {
    Write-Host "Git Bash를 찾을 수 없습니다." -ForegroundColor Yellow
    Write-Host "Docker Compose를 직접 사용합니다..." -ForegroundColor Cyan
    Write-Host ""
    
    # 현재 디렉토리가 search 폴더인지 확인
    if (-not (Test-Path "docker-compose.elasticsearch.windows.yml")) {
        Write-Host "docker-compose.elasticsearch.windows.yml 파일을 찾을 수 없습니다." -ForegroundColor Red
        Write-Host "search 폴더에서 실행해주세요." -ForegroundColor Yellow
        Read-Host "계속하려면 아무 키나 누르세요"
        exit 1
    }
    
    # Docker가 실행 중인지 확인
    try {
        docker info | Out-Null
    } catch {
        Write-Host "Docker가 실행되지 않았습니다. Docker Desktop을 먼저 시작해주세요." -ForegroundColor Red
        Read-Host "계속하려면 아무 키나 누르세요"
        exit 1
    }
    
    # 기존 컨테이너가 실행 중인지 확인
    $runningContainers = docker ps --format "table {{.Names}}" | Select-String "jandi-elasticsearch"
    if ($runningContainers) {
        if (-not $Force) {
            Write-Host "Elasticsearch가 이미 실행 중입니다." -ForegroundColor Yellow
            $response = Read-Host "재시작하시겠습니까? (y/n)"
            if ($response -eq "y" -or $response -eq "Y") {
                Write-Host "기존 컨테이너를 중지합니다..." -ForegroundColor Yellow
                docker-compose -f docker-compose.elasticsearch.windows.yml down
            } else {
                Write-Host "기존 서비스를 유지합니다." -ForegroundColor Green
                Read-Host "계속하려면 아무 키나 누르세요"
                exit 0
            }
        } else {
            Write-Host "기존 컨테이너를 중지합니다..." -ForegroundColor Yellow
            docker-compose -f docker-compose.elasticsearch.windows.yml down
        }
    }
    
    # 시스템 리소스 확인 (선택사항)
    if (-not $SkipChecks) {
        Write-Host "시스템 리소스를 확인합니다..." -ForegroundColor Cyan
        
        # 메모리 확인
        $memory = Get-CimInstance -ClassName Win32_OperatingSystem
        $totalMemoryGB = [math]::Round($memory.TotalVisibleMemorySize / 1MB, 2)
        if ($totalMemoryGB -lt 4) {
            Write-Host "경고: 시스템 메모리가 4GB 미만입니다. (현재: ${totalMemoryGB}GB)" -ForegroundColor Yellow
            Write-Host "Elasticsearch 실행에 문제가 있을 수 있습니다." -ForegroundColor Yellow
            $response = Read-Host "계속 진행하시겠습니까? (y/n)"
            if ($response -ne "y" -and $response -ne "Y") {
                exit 1
            }
        }
    }
    
    Write-Host "Elasticsearch와 Kibana를 시작합니다..." -ForegroundColor Green
    docker-compose -f docker-compose.elasticsearch.windows.yml up -d
    
    Write-Host ""
    Write-Host "서비스가 시작되었습니다." -ForegroundColor Green
    Write-Host "Elasticsearch: http://localhost:9200" -ForegroundColor Cyan
    Write-Host "Kibana: http://localhost:5601" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "다음 단계:" -ForegroundColor Cyan
    Write-Host "1. Spring Boot 애플리케이션 시작: .\gradlew.bat bootRun" -ForegroundColor White
    Write-Host "2. 데이터 동기화: curl -X POST http://localhost:8080/api/admin/promos/sync-all" -ForegroundColor White
    Write-Host ""
    Write-Host "유용한 명령어:" -ForegroundColor Cyan
    Write-Host "- 서비스 중지: docker-compose -f docker-compose.elasticsearch.windows.yml down" -ForegroundColor White
    Write-Host "- 로그 확인: docker logs jandi-elasticsearch" -ForegroundColor White
    Write-Host ""
}

Read-Host "계속하려면 아무 키나 누르세요" 