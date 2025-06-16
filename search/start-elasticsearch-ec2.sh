#!/bin/bash

echo "EC2 Ubuntu 서버에서 Elasticsearch와 Kibana를 시작합니다..."

# 현재 디렉토리가 search 폴더인지 확인
if [ ! -f "docker-compose.elasticsearch.ec2.yml" ]; then
    echo "docker-compose.elasticsearch.ec2.yml 파일을 찾을 수 없습니다."
    echo "search 폴더에서 실행해주세요."
    exit 1
fi

# Docker가 실행 중인지 확인
if ! docker info > /dev/null 2>&1; then
    echo "Docker가 실행되지 않았습니다. Docker를 먼저 설치하고 시작해주세요."
    echo "Docker 설치 명령어:"
    echo "curl -fsSL https://get.docker.com -o get-docker.sh"
    echo "sudo sh get-docker.sh"
    echo "sudo usermod -aG docker $USER"
    echo "sudo systemctl start docker"
    echo "sudo systemctl enable docker"
    exit 1
fi

# Docker Compose가 설치되어 있는지 확인
if ! command -v docker-compose &> /dev/null; then
    echo "Docker Compose가 설치되지 않았습니다. 설치해주세요."
    echo "Docker Compose 설치 명령어:"
    echo "sudo curl -L \"https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)\" -o /usr/local/bin/docker-compose"
    echo "sudo chmod +x /usr/local/bin/docker-compose"
    exit 1
fi

# 시스템 리소스 확인
echo "시스템 리소스를 확인합니다..."

# 메모리 확인 (최소 2GB 필요)
total_mem=$(free -m | awk 'NR==2{printf "%.0f", $2}')
if [ "$total_mem" -lt 2048 ]; then
    echo "경고: 시스템 메모리가 2GB 미만입니다. (현재: ${total_mem}MB)"
    echo "Elasticsearch 실행에 문제가 있을 수 있습니다."
    echo "계속 진행하시겠습니까? (y/n)"
    read -r response
    if [[ ! "$response" =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# 디스크 공간 확인 (최소 5GB 필요)
free_space=$(df -BG . | awk 'NR==2{print $4}' | sed 's/G//')
if [ "$free_space" -lt 5 ]; then
    echo "경고: 디스크 공간이 5GB 미만입니다. (현재: ${free_space}GB)"
    echo "계속 진행하시겠습니까? (y/n)"
    read -r response
    if [[ ! "$response" =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# 시스템 설정 최적화
echo "시스템 설정을 최적화합니다..."

# vm.max_map_count 설정
if [ "$(sysctl vm.max_map_count | awk '{print $3}')" -lt 262144 ]; then
    echo "vm.max_map_count를 262144로 설정합니다..."
    sudo sysctl -w vm.max_map_count=262144
    echo "vm.max_map_count=262144" | sudo tee -a /etc/sysctl.conf
fi

# 기존 컨테이너가 실행 중인지 확인
if docker ps | grep -q "jandi-elasticsearch"; then
    echo "Elasticsearch가 이미 실행 중입니다."
    echo "재시작하시겠습니까? (y/n)"
    read -r response
    if [[ "$response" =~ ^[Yy]$ ]]; then
        echo "기존 컨테이너를 중지합니다..."
        docker-compose -f docker-compose.elasticsearch.ec2.yml down
    else
        echo "기존 서비스를 유지합니다."
        exit 0
    fi
fi

# Elasticsearch와 Kibana 시작
echo "Elasticsearch와 Kibana를 시작합니다..."
docker-compose -f docker-compose.elasticsearch.ec2.yml up -d

# 시작 대기
echo "서비스가 시작되는 동안 잠시 기다려주세요..."
sleep 45

# 상태 확인
echo "서비스 상태를 확인합니다..."

# Elasticsearch 상태 확인
max_attempts=10
attempt=1
while [ $attempt -le $max_attempts ]; do
    if curl -s http://localhost:9200/_cluster/health > /dev/null; then
        echo "Elasticsearch가 정상적으로 시작되었습니다."
        echo "URL: http://localhost:9200"
        break
    else
        echo "Elasticsearch 시작 대기 중... (시도 $attempt/$max_attempts)"
        sleep 10
        attempt=$((attempt + 1))
    fi
done

if [ $attempt -gt $max_attempts ]; then
    echo "Elasticsearch 시작에 실패했습니다."
    echo "로그를 확인해주세요:"
    docker logs jandi-elasticsearch
    exit 1
fi

# Kibana 상태 확인
max_attempts=10
attempt=1
while [ $attempt -le $max_attempts ]; do
    if curl -s http://localhost:5601 > /dev/null; then
        echo "Kibana가 정상적으로 시작되었습니다."
        echo "URL: http://localhost:5601"
        break
    else
        echo "Kibana 시작 대기 중... (시도 $attempt/$max_attempts)"
        sleep 10
        attempt=$((attempt + 1))
    fi
done

if [ $attempt -gt $max_attempts ]; then
    echo "Kibana가 아직 시작되지 않았습니다. 잠시 후 다시 확인해주세요."
    echo "URL: http://localhost:5601"
fi

# 방화벽 설정 안내
echo ""
echo "방화벽 설정이 필요합니다:"
echo "sudo ufw allow 9200/tcp  # Elasticsearch"
echo "sudo ufw allow 5601/tcp  # Kibana"
echo ""

# 서비스 상태 출력
echo "Elasticsearch 환경 설정이 완료되었습니다!"
echo ""
echo "서비스 상태:"
docker-compose -f docker-compose.elasticsearch.ec2.yml ps
echo ""
echo "다음 단계:"
echo "1. Spring Boot 애플리케이션 시작: ./gradlew bootRun"
echo "2. 데이터 동기화: curl -X POST http://localhost:8080/api/admin/promos/sync-all"
echo "3. 검색 테스트: curl -X GET 'http://localhost:8080/api/promos/search-v2?keyword=락밴드'"
echo ""
echo "유용한 명령어:"
echo "- 서비스 중지: docker-compose -f docker-compose.elasticsearch.ec2.yml down"
echo "- 로그 확인: docker logs jandi-elasticsearch"
echo "- 서비스 재시작: docker-compose -f docker-compose.elasticsearch.ec2.yml restart"
echo "- Kibana 접속: http://YOUR_EC2_PUBLIC_IP:5601" 