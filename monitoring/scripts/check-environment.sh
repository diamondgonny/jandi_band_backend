#!/bin/bash

# 현재 Docker 환경 확인 스크립트
echo "========================================="
echo "현재 Docker 환경 분석"
echo "========================================="

echo "1. 실행 중인 모든 컨테이너:"
echo "----------------------------------------"
docker ps --format "table {{.Names}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}" || echo "Docker가 실행되지 않았습니다."

echo ""
echo "2. Docker 네트워크 목록:"
echo "----------------------------------------"
docker network ls || echo "Docker 네트워크 정보를 가져올 수 없습니다."

echo ""
echo "3. Spring Boot 애플리케이션 후보:"
echo "----------------------------------------"
SPRINGBOOT_CANDIDATES=$(docker ps --filter "expose=8080" --format "{{.Names}}\t{{.Image}}" | head -5)
if [ -z "$SPRINGBOOT_CANDIDATES" ]; then
    echo "8080 포트를 사용하는 컨테이너를 찾을 수 없습니다."
    echo "다른 포트를 사용하는 Spring Boot 애플리케이션:"
    docker ps --format "{{.Names}}\t{{.Image}}\t{{.Ports}}" | grep -E "(spring|boot|java|openjdk)" || echo "Spring Boot 관련 컨테이너를 찾을 수 없습니다."
else
    echo "포트 8080을 사용하는 컨테이너들:"
    echo "$SPRINGBOOT_CANDIDATES"
fi

echo ""
echo "4. Jenkins 컨테이너 후보:"
echo "----------------------------------------"
JENKINS_CANDIDATES=$(docker ps --format "{{.Names}}\t{{.Image}}\t{{.Ports}}" | grep -i jenkins)
if [ -z "$JENKINS_CANDIDATES" ]; then
    echo "Jenkins 컨테이너를 찾을 수 없습니다."
else
    echo "$JENKINS_CANDIDATES"
fi

echo ""
echo "5. Nginx 컨테이너 후보:"
echo "----------------------------------------"
NGINX_CANDIDATES=$(docker ps --format "{{.Names}}\t{{.Image}}\t{{.Ports}}" | grep -E "(nginx|proxy)")
if [ -z "$NGINX_CANDIDATES" ]; then
    echo "Nginx/Proxy 컨테이너를 찾을 수 없습니다."
else
    echo "$NGINX_CANDIDATES"
fi

echo ""
echo "6. 추천 설정:"
echo "----------------------------------------"

# Spring Boot 컨테이너 추천
SPRINGBOOT_MAIN=$(docker ps --filter "expose=8080" --format "{{.Names}}" | head -1)
if [ ! -z "$SPRINGBOOT_MAIN" ]; then
    echo "Spring Boot 컨테이너명: $SPRINGBOOT_MAIN"
    
    # 네트워크 확인
    NETWORK=$(docker inspect $SPRINGBOOT_MAIN | jq -r '.[0].NetworkSettings.Networks | keys[0]' 2>/dev/null || echo "bridge")
    echo "사용 중인 네트워크: $NETWORK"
    
    # 포트 확인
    EXPOSED_PORTS=$(docker inspect $SPRINGBOOT_MAIN | jq -r '.[0].NetworkSettings.Ports | keys[]' 2>/dev/null || echo "")
    echo "노출된 포트들: $EXPOSED_PORTS"
else
    echo "Spring Boot 컨테이너를 자동으로 감지할 수 없습니다."
fi

# Jenkins 컨테이너 추천
JENKINS_MAIN=$(docker ps --format "{{.Names}}" | grep -i jenkins | head -1)
if [ ! -z "$JENKINS_MAIN" ]; then
    echo "Jenkins 컨테이너명: $JENKINS_MAIN"
fi

echo ""
echo "7. 모니터링 설정 준비:"
echo "----------------------------------------"
echo "다음 명령어로 모니터링 스택을 배포할 수 있습니다:"
echo "cd monitoring"
echo "./scripts/deploy-ec2.sh"
echo ""
echo "또는 수동으로 설정하려면:"
echo "1. docker-compose.deploy.yml에서 네트워크명 수정"
echo "2. config/prometheus/prometheus.deploy.yml에서 컨테이너명 수정"
echo "3. docker-compose -f docker-compose.deploy.yml up -d"

echo ""
echo "=========================================" 