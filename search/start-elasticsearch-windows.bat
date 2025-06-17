@echo off
echo Windows용 Elasticsearch 시작 스크립트
echo.

REM Git Bash가 있는지 확인
where bash >nul 2>&1
if %errorlevel% equ 0 (
    echo Git Bash를 사용하여 Elasticsearch를 시작합니다...
    bash start-elasticsearch.sh
) else (
    echo Git Bash를 찾을 수 없습니다.
    echo Docker Compose를 직접 사용합니다...
    echo.
    
    REM 현재 디렉토리가 search 폴더인지 확인
    if not exist "docker-compose.elasticsearch.windows.yml" (
        echo docker-compose.elasticsearch.windows.yml 파일을 찾을 수 없습니다.
        echo search 폴더에서 실행해주세요.
        pause
        exit /b 1
    )
    
    REM Docker가 실행 중인지 확인
    docker info >nul 2>&1
    if errorlevel 1 (
        echo Docker가 실행되지 않았습니다. Docker Desktop을 먼저 시작해주세요.
        pause
        exit /b 1
    )
    
    echo Elasticsearch와 Kibana를 시작합니다...
    docker-compose -f docker-compose.elasticsearch.windows.yml up -d
    
    echo.
    echo 서비스가 시작되었습니다.
    echo Elasticsearch: http://localhost:9200
    echo Kibana: http://localhost:5601
    echo.
    echo 다음 단계:
    echo 1. Spring Boot 애플리케이션 시작: gradlew.bat bootRun
    echo 2. 데이터 동기화: curl -X POST http://localhost:8080/api/admin/promos/sync-all
    echo.
)

pause 