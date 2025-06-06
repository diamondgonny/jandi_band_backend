#!/bin/bash

# 운영 환경 모니터링 배포 스크립트 (Ubuntu EC2)
# Prometheus + Grafana 배포

set -e

echo "🚀 Starting monitoring deployment for production environment..."

# 현재 디렉토리 확인
if [ ! -f "docker-compose.yml" ]; then
    echo "❌ Error: docker-compose.yml not found. Please run this script from monitoring-deploy directory."
    exit 1
fi

# 기존 컨테이너 중지 및 제거
echo "🛑 Stopping existing containers..."
docker-compose down

# 도커 이미지 업데이트
echo "📥 Pulling latest images..."
docker-compose pull

# 컨테이너 시작
echo "🔄 Starting containers..."
docker-compose up -d

# 컨테이너 상태 확인
echo "📊 Checking container status..."
sleep 5
docker-compose ps

# 헬스체크
echo "🔍 Performing health checks..."

# Prometheus 헬스체크
for i in {1..30}; do
    if curl -s http://localhost:9090/-/healthy > /dev/null; then
        echo "✅ Prometheus is healthy"
        break
    fi
    echo "⏳ Waiting for Prometheus to be ready... ($i/30)"
    sleep 2
done

# Grafana 헬스체크
for i in {1..30}; do
    if curl -s http://localhost:3000/api/health > /dev/null; then
        echo "✅ Grafana is healthy"
        break
    fi
    echo "⏳ Waiting for Grafana to be ready... ($i/30)"
    sleep 2
done

echo ""
echo "🎉 Deployment completed successfully!"
echo ""
echo "📍 Access URLs:"
echo "   Prometheus: http://54.180.215.226:9090"
echo "   Grafana:    http://54.180.215.226:3000"
echo "   HTTPS Grafana: https://rhythmeet-be.yeonjae.kr/grafana/"
echo ""
echo "🔐 Grafana Login:"
echo "   Username: admin"
echo "   Password: admin123"
echo "" 