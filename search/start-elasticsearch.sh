#!/bin/bash

# 엘라스틱서치 환경 시작 스크립트

echo "🔍 잔디밴드 엘라스틱서치 환경 시작 중..."

# Docker Compose로 엘라스틱서치와 키바나 시작
echo "📦 Docker Compose로 엘라스틱서치 시작..."
docker-compose -f docker-compose.elasticsearch.yml up -d

# 엘라스틱서치가 준비될 때까지 대기
echo "⏳ 엘라스틱서치 시작 대기 중..."
sleep 30

# 엘라스틱서치 상태 확인
echo "🔍 엘라스틱서치 상태 확인 중..."
curl -s http://localhost:9200/_cluster/health?pretty

echo ""
echo "✅ 엘라스틱서치 환경이 시작되었습니다!"
echo ""
echo "📊 접속 정보:"
echo "   • 엘라스틱서치: http://localhost:9200"
echo "   • 키바나: http://localhost:5601"
echo ""
echo "🚀 다음 단계:"
echo "   1. 프로젝트 루트로 이동: cd .."
echo "   2. application.properties 파일 생성 (application.properties.example 참고)"
echo "   3. Spring Boot 애플리케이션 실행: ./gradlew bootRun"
echo "   4. 샘플 데이터 생성: POST http://localhost:8080/api/admin/search/teams/sample-data"
echo "   5. Swagger UI 확인: http://localhost:8080/swagger-ui.html"
echo "" 