# Jandi Band Backend - Windows Monitoring Start Script
# PowerShell Script for Windows

# 한글 인코딩 설정
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8
chcp 65001 | Out-Null

Write-Host "===========================================" -ForegroundColor Green
Write-Host "Jandi Band Backend Monitoring System" -ForegroundColor Green
Write-Host "===========================================" -ForegroundColor Green

# Check Docker installation
Write-Host "`n1. Checking Docker installation..." -ForegroundColor Yellow
try {
    docker --version | Out-Host
    Write-Host "Docker is properly installed." -ForegroundColor Green
} catch {
    Write-Host "Docker is not installed. Please install Docker Desktop." -ForegroundColor Red
    exit 1
}

# Check Docker daemon
Write-Host "`n2. Checking Docker daemon status..." -ForegroundColor Yellow
try {
    docker ps | Out-Null
    Write-Host "Docker daemon is running." -ForegroundColor Green
} catch {
    Write-Host "Please start Docker Desktop." -ForegroundColor Red
    exit 1
}

# Check Spring Boot application
Write-Host "`n3. Checking Spring Boot application..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method Get -TimeoutSec 5
    if ($response.status -eq "UP") {
        Write-Host "Spring Boot application is running." -ForegroundColor Green
    } else {
        throw "Application is not in healthy state."
    }
} catch {
    Write-Host "Spring Boot application is not running." -ForegroundColor Red
    Write-Host "Please start the application on port 8080 first." -ForegroundColor Red
    exit 1
}

# Check Prometheus metrics
Write-Host "`n4. Checking Prometheus metrics..." -ForegroundColor Yellow
try {
    $metrics = Invoke-RestMethod -Uri "http://localhost:8080/actuator/prometheus" -Method Get -TimeoutSec 5
    $metricsSize = $metrics.Length
    Write-Host "Prometheus metrics are available. (Size: $metricsSize bytes)" -ForegroundColor Green
} catch {
    Write-Host "Cannot fetch Prometheus metrics." -ForegroundColor Red
    Write-Host "Please check if Prometheus metrics are enabled in application.properties." -ForegroundColor Red
    exit 1
}

# Navigate to monitoring directory
Write-Host "`n5. Navigating to monitoring directory..." -ForegroundColor Yellow
Set-Location -Path "monitoring"

# Clean up existing monitoring containers
Write-Host "`n6. Cleaning up existing monitoring containers..." -ForegroundColor Yellow
docker-compose -f docker-compose.yml down -v 2>$null
Write-Host "Existing containers cleaned up." -ForegroundColor Green

# Create Docker network
Write-Host "`n7. Creating Docker network..." -ForegroundColor Yellow
docker network create jandi_band_backend_monitoring 2>$null
Write-Host "Docker network is ready." -ForegroundColor Green

# Start monitoring stack
Write-Host "`n8. Starting monitoring stack..." -ForegroundColor Yellow
Write-Host "Starting Prometheus, Grafana, and Alertmanager..." -ForegroundColor Cyan

docker-compose -f docker-compose.yml up -d

if ($LASTEXITCODE -eq 0) {
    Write-Host "`nMonitoring stack started successfully!" -ForegroundColor Green
    
    # Wait for container initialization
    Write-Host "`nWaiting for container initialization..." -ForegroundColor Yellow
    Start-Sleep -Seconds 30
    
    Write-Host "`n9. Checking container status..." -ForegroundColor Yellow
    docker-compose -f docker-compose.yml ps
    
    Write-Host "`n===========================================" -ForegroundColor Green
    Write-Host "Monitoring Dashboard Access Information" -ForegroundColor Green
    Write-Host "===========================================" -ForegroundColor Green
    Write-Host "Spring Boot App : http://localhost:8080" -ForegroundColor Cyan
    Write-Host "Prometheus      : http://localhost:9090" -ForegroundColor Cyan
    Write-Host "Grafana         : http://localhost:3000" -ForegroundColor Cyan
    Write-Host "  - Username: admin" -ForegroundColor White
    Write-Host "  - Password: admin123" -ForegroundColor White
    Write-Host "===========================================" -ForegroundColor Green
    
    Write-Host "`n10. Performing service health checks..." -ForegroundColor Yellow
    
    # Prometheus health check
    Start-Sleep -Seconds 10
    try {
        $prometheusHealth = Invoke-RestMethod -Uri "http://localhost:9090/-/healthy" -Method Get -TimeoutSec 10
        Write-Host "✓ Prometheus is running properly." -ForegroundColor Green
    } catch {
        Write-Host "⚠ Prometheus is still initializing or has issues." -ForegroundColor Yellow
    }
    
    # Grafana health check
    try {
        $grafanaHealth = Invoke-RestMethod -Uri "http://localhost:3000/api/health" -Method Get -TimeoutSec 10
        Write-Host "✓ Grafana is running properly." -ForegroundColor Green
    } catch {
        Write-Host "⚠ Grafana is still initializing or has issues." -ForegroundColor Yellow
    }
    
    Write-Host "`nThe monitoring system may need 1-2 more minutes to be fully ready." -ForegroundColor Yellow
    Write-Host "Please access the URLs above in your browser!" -ForegroundColor Green
    
} else {
    Write-Host "`nFailed to start monitoring stack." -ForegroundColor Red
    Write-Host "Please check docker-compose logs:" -ForegroundColor Red
    Write-Host "docker-compose -f docker-compose.yml logs" -ForegroundColor White
    exit 1
}

# Return to project root
Set-Location -Path ".."

Write-Host "`nUseful commands:" -ForegroundColor Cyan
Write-Host "View logs: cd monitoring && docker-compose logs -f" -ForegroundColor White
Write-Host "Stop monitoring: cd monitoring && docker-compose down" -ForegroundColor White 