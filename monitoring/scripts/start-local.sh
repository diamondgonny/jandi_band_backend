#!/bin/bash

# 🔍 Jandi Band Backend - 모니터링 시스템 시작 스크립트
# Prometheus + Grafana + Alertmanager를 Docker Compose로 실행합니다.

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
echo "🔍 Jandi Band Backend - 모니터링 시스템 시작"
echo "=================================================================="
echo -e "${NC}"

# Docker 및 Docker Compose 확인
log_info "Docker 및 Docker Compose 확인 중..."
if ! command -v docker &> /dev/null; then
    log_error "Docker가 설치되지 않았습니다."
    exit 1
fi

if ! command -v docker-compose &> /dev/null && ! command -v docker compose &> /dev/null; then
    log_error "Docker Compose가 설치되지 않았습니다."
    exit 1
fi

# Docker 서비스 확인
if ! docker info &> /dev/null; then
    log_error "Docker 서비스가 실행되지 않았습니다. Docker를 시작해주세요."
    exit 1
fi

log_success "Docker 환경 확인 완료"

# 필요한 디렉토리 생성
log_info "필요한 디렉토리 생성 중..."
mkdir -p monitoring/{prometheus,grafana/{provisioning/{datasources,dashboards},dashboards},alertmanager}

# 권한 설정 (Grafana용)
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    log_info "Linux 환경에서 Grafana 권한 설정 중..."
    sudo chown -R 472:472 monitoring/grafana 2>/dev/null || log_warning "Grafana 권한 설정에 실패했습니다. 수동으로 설정이 필요할 수 있습니다."
fi

# Spring Boot 애플리케이션 확인
log_info "Spring Boot 애플리케이션 상태 확인 중..."
if curl -sf http://localhost:8080/health &> /dev/null; then
    log_success "Spring Boot 애플리케이션이 실행 중입니다."
elif curl -sf http://localhost:8080/actuator/health &> /dev/null; then
    log_success "Spring Boot 애플리케이션이 실행 중입니다."
else
    log_warning "Spring Boot 애플리케이션이 실행되지 않았거나 헬스체크에 실패했습니다."
    log_warning "모니터링 스택은 시작되지만 애플리케이션 메트릭을 수집할 수 없습니다."
    
    read -p "계속 진행하시겠습니까? (y/N): " continue_choice
    if [[ ! $continue_choice =~ ^[Yy]$ ]]; then
        log_info "모니터링 시작을 취소했습니다."
        exit 0
    fi
fi

# 기존 컨테이너 정리 (선택사항)
read -p "Clean up existing monitoring containers? (y/N): " cleanup_choice
if [[ $cleanup_choice =~ ^[Yy]$ ]]; then
    log_info "Cleaning up existing monitoring containers..."
    docker-compose -f docker-compose.local.yml down -v 2>/dev/null || true
    log_success "Existing containers cleaned up"
fi

# 모니터링 스택 시작
log_info "Starting monitoring stack..."
if command -v docker-compose &> /dev/null; then
    docker-compose -f docker-compose.local.yml up -d
else
    docker compose -f docker-compose.local.yml up -d
fi

# 컨테이너 상태 확인
log_info "컨테이너 상태 확인 중..."
sleep 10

# 각 서비스 헬스체크
services=("prometheus:9090" "grafana:3000" "alertmanager:9093")
all_healthy=true

for service in "${services[@]}"; do
    name=$(echo $service | cut -d: -f1)
    port=$(echo $service | cut -d: -f2)
    
    if curl -sf http://localhost:$port &> /dev/null; then
        log_success "$name이 정상적으로 실행 중입니다 (포트: $port)"
    else
        log_error "$name이 실행되지 않았습니다 (포트: $port)"
        all_healthy=false
    fi
done

# 결과 출력
echo -e "\n${BLUE}=================================================================="
echo "🔍 모니터링 시스템 시작 완료"
echo "=================================================================="
echo -e "${NC}"

if $all_healthy; then
    log_success "모든 모니터링 서비스가 정상적으로 시작되었습니다!"
else
    log_warning "일부 서비스에 문제가 있습니다. 로그를 확인해주세요."
fi

echo -e "\n📊 접속 정보:"
echo -e "• Grafana:      ${GREEN}http://localhost:3000${NC} (admin/admin123)"
echo -e "• Prometheus:   ${GREEN}http://localhost:9090${NC}"
echo -e "• Alertmanager: ${GREEN}http://localhost:9093${NC}"

echo -e "\n🔧 Useful Commands:"
echo -e "• View logs:    ${YELLOW}docker-compose -f docker-compose.local.yml logs -f${NC}"
echo -e "• Container status: ${YELLOW}docker-compose -f docker-compose.local.yml ps${NC}"
echo -e "• Stop monitoring: ${YELLOW}docker-compose -f docker-compose.local.yml down${NC}"

echo -e "\n📈 모니터링 메트릭 테스트:"
echo -e "• API 호출 테스트: ${YELLOW}curl http://localhost:8080/health${NC}"
echo -e "• 메트릭 확인:     ${YELLOW}curl http://localhost:8080/actuator/prometheus${NC}"

echo -e "\n✅ 모니터링 시스템이 준비되었습니다!" 