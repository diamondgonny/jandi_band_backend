#!/bin/bash

# Jandi Band Backend - Production Monitoring Start Script
# For Ubuntu Server deployment

set -e

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 로그 함수
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 제목 출력
echo -e "${BLUE}"
echo "=================================================================="
echo "🔍 Jandi Band Backend - Production Monitoring System"
echo "=================================================================="
echo -e "${NC}"

# Docker 및 Docker Compose 확인
log_info "Checking Docker and Docker Compose..."
if ! command -v docker &> /dev/null; then
    log_error "Docker is not installed."
    exit 1
fi

if ! command -v docker-compose &> /dev/null && ! command -v docker compose &> /dev/null; then
    log_error "Docker Compose is not installed."
    exit 1
fi

# Docker 서비스 확인
if ! docker info &> /dev/null; then
    log_error "Docker service is not running. Please start Docker."
    exit 1
fi

log_success "Docker environment check completed"

# Spring Boot 애플리케이션 확인
log_info "Checking Spring Boot application..."
if curl -sf http://localhost:8080/health &> /dev/null; then
    log_success "Spring Boot application is running."
elif curl -sf http://localhost:8080/actuator/health &> /dev/null; then
    log_success "Spring Boot application is running."
else
    log_warning "Spring Boot application is not running or health check failed."
    log_warning "Monitoring stack will start but cannot collect application metrics."
    
    read -p "Continue? (y/N): " continue_choice
    if [[ ! $continue_choice =~ ^[Yy]$ ]]; then
        log_info "Monitoring start cancelled."
        exit 0
    fi
fi

# 기존 컨테이너 정리 (선택사항)
read -p "Clean up existing monitoring containers? (y/N): " cleanup_choice
if [[ $cleanup_choice =~ ^[Yy]$ ]]; then
    log_info "Cleaning up existing monitoring containers..."
    docker-compose -f docker-compose.deploy.yml down -v 2>/dev/null || true
    log_success "Existing containers cleaned up"
fi

# 모니터링 스택 시작
log_info "Starting monitoring stack..."
if command -v docker-compose &> /dev/null; then
    docker-compose -f docker-compose.deploy.yml up -d
else
    docker compose -f docker-compose.deploy.yml up -d
fi

# 컨테이너 상태 확인
log_info "Checking container status..."
sleep 10

# 각 서비스 헬스체크
services=("prometheus:9090" "grafana:3000")
all_healthy=true

for service in "${services[@]}"; do
    name=$(echo $service | cut -d: -f1)
    port=$(echo $service | cut -d: -f2)
    
    if curl -sf http://localhost:$port &> /dev/null; then
        log_success "$name is running properly (port: $port)"
    else
        log_error "$name is not running (port: $port)"
        all_healthy=false
    fi
done

# 결과 출력
echo -e "\n${BLUE}=================================================================="
echo "🔍 Production Monitoring System Started"
echo "=================================================================="
echo -e "${NC}"

if $all_healthy; then
    log_success "All monitoring services started successfully!"
else
    log_warning "Some services have issues. Please check logs."
fi

echo -e "\n📊 Access Information:"
echo -e "• Grafana:      ${GREEN}http://your-server-ip:3000${NC} (admin/admin123)"
echo -e "• Prometheus:   ${GREEN}http://your-server-ip:9090${NC}"

echo -e "\n🔧 Useful Commands:"
echo -e "• View logs:    ${YELLOW}docker-compose -f docker-compose.deploy.yml logs -f${NC}"
echo -e "• Container status: ${YELLOW}docker-compose -f docker-compose.deploy.yml ps${NC}"
echo -e "• Stop monitoring: ${YELLOW}docker-compose -f docker-compose.deploy.yml down${NC}"

echo -e "\n📈 Test Monitoring:"
echo -e "• API test: ${YELLOW}curl http://localhost:8080/health${NC}"
echo -e "• Metrics:  ${YELLOW}curl http://localhost:8080/actuator/prometheus${NC}"

echo -e "\n✅ Production monitoring system is ready!" 