# Jandi Band Backend - Windows Monitoring Stop Script
# PowerShell Script for Windows

# 한글 인코딩 설정
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8
chcp 65001 | Out-Null

Write-Host "===========================================" -ForegroundColor Red
Write-Host "Jandi Band Backend Monitoring Stop" -ForegroundColor Red
Write-Host "===========================================" -ForegroundColor Red

Write-Host "`nStopping monitoring stack..." -ForegroundColor Yellow

# Navigate to monitoring directory
Set-Location -Path "monitoring"

# 컨테이너 정지
Write-Host "Stopping monitoring containers..." -ForegroundColor Cyan
docker-compose -f docker-compose.local.yml down

if ($LASTEXITCODE -eq 0) {
    Write-Host "Monitoring containers stopped successfully." -ForegroundColor Green
    
    # 볼륨 삭제 옵션
    $removeVolumes = Read-Host "`nRemove data volumes as well? (y/N)"
    if ($removeVolumes -eq "y" -or $removeVolumes -eq "Y") {
        Write-Host "Removing volumes..." -ForegroundColor Yellow
        docker-compose -f docker-compose.local.yml down -v
        Write-Host "Volumes removed." -ForegroundColor Green
    }
    
    # Clean up unused networks
    Write-Host "`nCleaning up unused networks..." -ForegroundColor Yellow
    docker network prune -f
    
    Write-Host "`n===========================================" -ForegroundColor Green
    Write-Host "Monitoring stack completely stopped." -ForegroundColor Green
    Write-Host "===========================================" -ForegroundColor Green
    
} else {
    Write-Host "`n⚠ Error occurred while stopping monitoring stack." -ForegroundColor Red
    Write-Host "Please check containers manually: docker ps -a" -ForegroundColor Yellow
}

# Return to project root
Set-Location -Path ".." 