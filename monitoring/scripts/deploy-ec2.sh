#!/bin/bash

# 원격 서버에서 모니터링 스택 배포 스크립트
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

echo "1. 현재 Docker 환경 확인 중..."
echo "========================================="
echo "현재 실행 중인 컨테이너:"
docker ps --format "table {{.Names}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}"
echo ""
echo "현재 Docker 네트워크:"
docker network ls
echo ""

# Spring Boot 컨테이너 확인
echo "2. Spring Boot 컨테이너 확인..."
SPRINGBOOT_CONTAINER=$(docker ps --filter "expose=8080" --format "{{.Names}}" | head -1)
if [ -z "$SPRINGBOOT_CONTAINER" ]; then
    echo "Warning: 8080 포트를 사용하는 컨테이너를 찾을 수 없습니다."
    read -p "Spring Boot 컨테이너명을 입력하세요: " SPRINGBOOT_CONTAINER
fi
echo "Spring Boot 컨테이너: $SPRINGBOOT_CONTAINER"

# Jenkins 컨테이너 확인
echo "3. Jenkins 컨테이너 확인..."
JENKINS_CONTAINER=$(docker ps --filter "expose=8080" --format "{{.Names}}" | grep -i jenkins | head -1)
if [ -z "$JENKINS_CONTAINER" ]; then
    read -p "Jenkins 컨테이너명을 입력하세요 (없으면 Enter): " JENKINS_CONTAINER
fi
if [ ! -z "$JENKINS_CONTAINER" ]; then
    echo "Jenkins 컨테이너: $JENKINS_CONTAINER"
fi

# 네트워크 확인
echo "4. Docker 네트워크 확인..."
NETWORK_NAME=$(docker inspect $SPRINGBOOT_CONTAINER | jq -r '.[0].NetworkSettings.Networks | keys[0]' 2>/dev/null || echo "")
if [ -z "$NETWORK_NAME" ]; then
    echo "기본 네트워크를 사용하거나 네트워크를 찾을 수 없습니다."
    read -p "사용할 네트워크명을 입력하세요 (기본값: bridge): " NETWORK_NAME
    NETWORK_NAME=${NETWORK_NAME:-bridge}
fi
echo "사용할 네트워크: $NETWORK_NAME"

# 도메인 설정
echo "5. 도메인 설정..."
read -p "도메인 또는 IP 주소를 입력하세요 (예: your-domain.com 또는 1.2.3.4): " DOMAIN
if [ -z "$DOMAIN" ]; then
    echo "Error: 도메인 또는 IP를 입력해야 합니다."
    exit 1
fi
echo "설정된 도메인: $DOMAIN"

# 설정 파일 업데이트
echo "6. 설정 파일 업데이트 중..."
# docker-compose.deploy.yml 업데이트
cp docker-compose.deploy.yml docker-compose.deploy.yml.backup
sed -i "s/YOUR_DOMAIN/$DOMAIN/g" docker-compose.deploy.yml
sed -i "s/jandi_backend_network/$NETWORK_NAME/g" docker-compose.deploy.yml

# prometheus.deploy.yml 업데이트
cp config/prometheus/prometheus.deploy.yml config/prometheus/prometheus.deploy.yml.backup
sed -i "s/springboot-container/$SPRINGBOOT_CONTAINER/g" config/prometheus/prometheus.deploy.yml
if [ ! -z "$JENKINS_CONTAINER" ]; then
    sed -i "s/jenkins-container/$JENKINS_CONTAINER/g" config/prometheus/prometheus.deploy.yml
fi

echo "7. 기존 모니터링 컨테이너 중지 및 제거..."
docker-compose -f docker-compose.deploy.yml down --remove-orphans || true

echo "8. 최신 이미지 다운로드..."
docker-compose -f docker-compose.deploy.yml pull

echo "9. 모니터링 스택 시작..."
docker-compose -f docker-compose.deploy.yml up -d

echo "10. 컨테이너 상태 확인..."
sleep 15
docker-compose -f docker-compose.deploy.yml ps

echo "11. 헬스체크..."
echo "Spring Boot 헬스체크..."
if docker exec -it $SPRINGBOOT_CONTAINER curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "✓ Spring Boot 헬스체크 성공"
else
    echo "⚠ Spring Boot 헬스체크 실패 - 수동으로 확인하세요"
fi

echo "Prometheus 타겟 확인..."
sleep 10
if curl -s http://localhost:9090/api/v1/targets > /dev/null 2>&1; then
    echo "✓ Prometheus 접근 성공"
else
    echo "⚠ Prometheus 접근 실패 - 포트 확인이 필요합니다"
fi

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
echo "감지된 컨테이너:"
echo "- Spring Boot: $SPRINGBOOT_CONTAINER"
if [ ! -z "$JENKINS_CONTAINER" ]; then
    echo "- Jenkins: $JENKINS_CONTAINER"
fi
echo "- 네트워크: $NETWORK_NAME"
echo ""
echo "Note: docs/nginx-monitoring.conf 파일을 참고하여"
echo "      Nginx 설정을 추가해주세요."
echo "=========================================" 