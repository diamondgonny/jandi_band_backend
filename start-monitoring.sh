#!/bin/bash

# Jandi Band Backend 모니터링 시스템 시작 스크립트

echo "🔍 Jandi Band Backend 모니터링 시스템을 시작합니다..."
echo "=============================================="

# 현재 디렉토리 확인
if [ ! -f "docker-compose.monitoring.yml" ]; then
    echo "❌ docker-compose.monitoring.yml 파일을 찾을 수 없습니다."
    echo "프로젝트 루트 디렉토리에서 실행해주세요."
    exit 1
fi

# Docker 및 Docker Compose 확인
if ! command -v docker &> /dev/null; then
    echo "❌ Docker가 설치되어 있지 않습니다."
    echo "Docker를 설치한 후 다시 시도해주세요."
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose가 설치되어 있지 않습니다."
    echo "Docker Compose를 설치한 후 다시 시도해주세요."
    exit 1
fi

# Spring Boot 애플리케이션 상태 확인
echo "📡 Spring Boot 애플리케이션 상태 확인 중..."
if curl -s http://localhost:8080/health > /dev/null 2>&1; then
    echo "✅ Spring Boot 애플리케이션이 실행 중입니다."
else
    echo "⚠️  Spring Boot 애플리케이션이 실행되지 않았습니다."
    echo "다음 명령어로 애플리케이션을 먼저 실행해주세요:"
    echo "  ./gradlew bootRun"
    echo ""
    echo "계속해서 모니터링 스택만 실행하시겠습니까? (y/N)"
    read -r response
    if [[ ! "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
        exit 1
    fi
fi

# 기존 컨테이너 정리
echo "🧹 기존 모니터링 컨테이너 정리 중..."
docker-compose -f docker-compose.monitoring.yml down

# 모니터링 스택 시작
echo "🚀 모니터링 스택 시작 중..."
docker-compose -f docker-compose.monitoring.yml up -d

# 컨테이너 상태 확인
echo "⏳ 컨테이너 시작 대기 중..."
sleep 10

# Prometheus 상태 확인
echo "📊 Prometheus 상태 확인 중..."
if curl -s http://localhost:9090/-/healthy > /dev/null 2>&1; then
    echo "✅ Prometheus가 정상적으로 실행 중입니다."
else
    echo "❌ Prometheus 실행에 문제가 있습니다."
fi

# Grafana 상태 확인
echo "📈 Grafana 상태 확인 중..."
if curl -s http://localhost:3000/api/health > /dev/null 2>&1; then
    echo "✅ Grafana가 정상적으로 실행 중입니다."
else
    echo "❌ Grafana 실행에 문제가 있습니다."
fi

echo ""
echo "🎉 모니터링 시스템이 성공적으로 시작되었습니다!"
echo "=============================================="
echo "📍 접속 정보:"
echo "  • Spring Boot App: http://localhost:8080"
echo "  • Prometheus:      http://localhost:9090"
echo "  • Grafana:         http://localhost:3000"
echo "    - 사용자명: admin"
echo "    - 비밀번호: admin123"
echo ""
echo "📋 유용한 명령어:"
echo "  • 로그 확인: docker-compose -f docker-compose.monitoring.yml logs -f"
echo "  • 중지:     docker-compose -f docker-compose.monitoring.yml down"
echo "  • 재시작:   docker-compose -f docker-compose.monitoring.yml restart"
echo ""
echo "📚 자세한 사용법은 MONITORING_GUIDE.md 파일을 참고하세요." 