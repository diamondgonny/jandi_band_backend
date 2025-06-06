#!/bin/bash

# EC2 인스턴스에서 프로메테우스와 그라파나 배포 스크립트
set -e

echo "========================================="
echo "Jandi Band Backend 모니터링 스택 배포"
echo "========================================="

# 현재 디렉토리 확인
if [ ! -f "docker-compose.deploy.yml" ]; then
    echo "Error: docker-compose.deploy.yml 파일을 찾을 수 없습니다."
    echo "monitoring 디렉토리에서 이 스크립트를 실행해주세요."
    exit 1
fi

# 환경 변수 설정
read -p "도메인 또는 IP 주소를 입력하세요 (예: your-domain.com 또는 1.2.3.4): " DOMAIN
if [ -z "$DOMAIN" ]; then
    echo "Error: 도메인 또는 IP를 입력해야 합니다."
    exit 1
fi

echo "설정된 도메인: $DOMAIN"

# docker-compose.deploy.yml에서 YOUR_DOMAIN 플레이스홀더 교체
sed -i "s/YOUR_DOMAIN/$DOMAIN/g" docker-compose.deploy.yml

echo "1. 기존 컨테이너 중지 및 제거..."
docker-compose -f docker-compose.deploy.yml down --remove-orphans || true

echo "2. 최신 이미지 다운로드..."
docker-compose -f docker-compose.deploy.yml pull

echo "3. 모니터링 스택 시작..."
docker-compose -f docker-compose.deploy.yml up -d

echo "4. 컨테이너 상태 확인..."
sleep 10
docker-compose -f docker-compose.deploy.yml ps

echo ""
echo "========================================="
echo "배포 완료!"
echo "========================================="
echo "접속 정보:"
echo "- Prometheus: http://$DOMAIN:9090"
echo "- Grafana: http://$DOMAIN:3000"
echo "  - 계정: admin / admin123"
echo "- Alertmanager: http://$DOMAIN:9093"
echo ""
echo "Nginx 리버스 프록시 사용 시:"
echo "- Prometheus: http://$DOMAIN/prometheus/"
echo "- Grafana: http://$DOMAIN/grafana/"
echo "- Alertmanager: http://$DOMAIN/alertmanager/"
echo ""
echo "Note: docs/nginx-monitoring.conf 파일을 참고하여"
echo "      Nginx 설정을 추가해주세요."
echo "=========================================" 