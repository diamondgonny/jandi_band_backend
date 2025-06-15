#!/bin/bash

echo "Elasticsearch와 Kibana를 시작합니다..."

# 현재 디렉토리가 search 폴더인지 확인
if [ ! -f "docker-compose.elasticsearch.yml" ]; then
    echo "docker-compose.elasticsearch.yml 파일을 찾을 수 없습니다."
    echo "search 폴더에서 실행해주세요."
    exit 1
fi

# Docker가 실행 중인지 확인
if ! docker info > /dev/null 2>&1; then
    echo "Docker가 실행되지 않았습니다. Docker를 먼저 시작해주세요."
    exit 1
fi

# 기존 컨테이너가 실행 중인지 확인
if docker ps | grep -q "jandi-elasticsearch"; then
    echo "Elasticsearch가 이미 실행 중입니다."
    echo "재시작하시겠습니까? (y/n)"
    read -r response
    if [[ "$response" =~ ^[Yy]$ ]]; then
        echo "기존 컨테이너를 중지합니다..."
        docker-compose -f docker-compose.elasticsearch.yml down
    else
        echo "기존 서비스를 유지합니다."
        exit 0
    fi
fi

# Elasticsearch와 Kibana 시작
echo "Elasticsearch와 Kibana를 시작합니다..."
docker-compose -f docker-compose.elasticsearch.yml up -d

# 시작 대기
echo "서비스가 시작되는 동안 잠시 기다려주세요..."
sleep 30

# 상태 확인
echo "서비스 상태를 확인합니다..."

# Elasticsearch 상태 확인
if curl -s http://localhost:9200/_cluster/health > /dev/null; then
    echo "Elasticsearch가 정상적으로 시작되었습니다."
    echo "URL: http://localhost:9200"
else
    echo "Elasticsearch 시작에 실패했습니다."
    echo "로그를 확인해주세요:"
    docker logs jandi-elasticsearch
    exit 1
fi

# Kibana 상태 확인
if curl -s http://localhost:5601 > /dev/null; then
    echo "Kibana가 정상적으로 시작되었습니다."
    echo "URL: http://localhost:5601"
else
    echo "Kibana가 아직 시작되지 않았습니다. 잠시 후 다시 확인해주세요."
    echo "URL: http://localhost:5601"
fi

echo ""
echo "Elasticsearch 환경 설정이 완료되었습니다!"
echo ""
echo "다음 단계:"
echo "1. Spring Boot 애플리케이션 시작: ./gradlew bootRun"
echo "2. 데이터 동기화: curl -X POST http://localhost:8080/api/admin/promos/sync-all"
echo "3. 검색 테스트: curl -X GET 'http://localhost:8080/api/promos/search-v2?keyword=락밴드'"
echo ""
echo "유용한 명령어:"
echo "- 서비스 중지: docker-compose -f docker-compose.elasticsearch.yml down"
echo "- 로그 확인: docker logs jandi-elasticsearch"
echo "- Kibana 접속: http://localhost:5601" 