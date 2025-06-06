@echo off
REM 로컬 모니터링 시작 스크립트 (Windows)

echo 🚀 Starting local monitoring environment...

REM 현재 디렉토리 확인
if not exist "docker-compose.local.yml" (
    echo ❌ Error: docker-compose.local.yml not found.
    echo Please run this script from monitoring-local directory.
    pause
    exit /b 1
)

REM 기존 컨테이너 중지
echo 🛑 Stopping existing containers...
docker-compose -f docker-compose.local.yml down

REM 최신 이미지 다운로드
echo 📥 Pulling latest images...
docker-compose -f docker-compose.local.yml pull

REM 컨테이너 시작
echo 🔄 Starting containers...
docker-compose -f docker-compose.local.yml up -d

REM 상태 확인
echo 📊 Checking container status...
timeout /t 5 >nul
docker-compose -f docker-compose.local.yml ps

echo.
echo 🎉 Local monitoring started successfully!
echo.
echo 📍 Access URLs:
echo    Prometheus: http://localhost:9090
echo    Grafana:    http://localhost:3000
echo.
echo 🔐 Grafana Login:
echo    Username: admin
echo    Password: admin123
echo.
echo ℹ️  Make sure your Spring Boot application is running on port 8080 or 8081
echo    and has actuator endpoints enabled.
echo.
pause 