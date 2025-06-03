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

# Stop and remove monitoring containers
docker-compose -f docker-compose.yml down

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n✓ Monitoring stack stopped successfully." -ForegroundColor Green
    
    # Ask about removing volumes
    $removeVolumes = Read-Host "`nDo you want to remove data volumes as well? (y/N)"
    
    if ($removeVolumes -eq "y" -or $removeVolumes -eq "Y") {
        Write-Host "`nRemoving data volumes..." -ForegroundColor Yellow
        docker-compose -f docker-compose.yml down -v
        docker volume prune -f
        Write-Host "✓ Data volumes removed." -ForegroundColor Green
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