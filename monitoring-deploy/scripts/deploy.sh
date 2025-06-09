#!/bin/bash

# 운영 환경 모니터링 배포 스크립트 (Ubuntu EC2)
# Prometheus + Grafana 배포

set -e

echo "Starting monitoring deployment for production environment..."

# 현재 디렉토리 확인
if [ ! -f "docker-compose.yml" ]; then
    echo "Error: docker-compose.yml not found. Please run this script from monitoring-deploy directory."
    exit 1
fi

# 기존 컨테이너 완전 제거
echo "Removing existing containers and volumes..."
docker-compose down -v
docker system prune -f

# config 폴더 권한 설정
echo "Setting up permissions..."
sudo chown -R 1000:1000 config/
sudo chmod -R 755 config/

# 도커 이미지 업데이트
echo "Pulling latest images..."
docker-compose pull

# 컨테이너 시작
echo "Starting containers..."
docker-compose up -d

# 컨테이너 상태 확인
echo "Checking container status..."
sleep 10
docker-compose ps

# 헬스체크
echo "Performing health checks..."

# Prometheus 헬스체크
for i in {1..30}; do
    if curl -s http://localhost:9090/-/healthy > /dev/null; then
        echo "Prometheus is healthy"
        break
    fi
    echo "Waiting for Prometheus to be ready... ($i/30)"
    sleep 2
done

# Grafana 헬스체크
for i in {1..30}; do
    if curl -s http://localhost:3000/api/health > /dev/null; then
        echo "Grafana is healthy"
        break
    fi
    echo "Waiting for Grafana to be ready... ($i/30)"
    sleep 2
done

# 대시보드 확인
echo "Checking dashboard provisioning..."
sleep 5
if docker exec jandi-grafana-deploy ls /var/lib/grafana/dashboards > /dev/null 2>&1; then
    echo "Dashboard files are accessible"
else
    echo "Dashboard files may need manual import"
fi

echo ""
echo "Deployment completed successfully!"
echo "" 