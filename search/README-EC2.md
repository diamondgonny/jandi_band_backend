# EC2 Ubuntu 서버에서 Elasticsearch 설정 가이드

이 문서는 AWS EC2 Ubuntu 서버에서 Elasticsearch와 Kibana를 설정하는 완전한 가이드입니다.

## 사전 요구사항

### 1. EC2 인스턴스 사양
- **최소 사양**: t3.medium (2 vCPU, 4GB RAM)
- **권장 사양**: t3.large (2 vCPU, 8GB RAM) 또는 t3.xlarge (4 vCPU, 16GB RAM)
- **스토리지**: 최소 20GB (Elasticsearch 데이터용)

### 2. 보안 그룹 설정
다음 포트들을 열어주세요:
- **22**: SSH 접속
- **80**: HTTP (웹 서버용)
- **443**: HTTPS (웹 서버용)
- **8080**: Spring Boot 애플리케이션
- **9200**: Elasticsearch
- **5601**: Kibana

## 1. EC2 인스턴스 초기 설정

### 1.1 시스템 업데이트
```bash
sudo apt update && sudo apt upgrade -y
```

### 1.2 Java 설치 (Spring Boot용)
```bash
# OpenJDK 21 설치
sudo apt install openjdk-21-jdk -y

# Java 버전 확인
java -version
```

### 1.3 Docker 설치
```bash
# Docker 설치
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# 현재 사용자를 docker 그룹에 추가
sudo usermod -aG docker $USER

# Docker 서비스 시작 및 자동 시작 설정
sudo systemctl start docker
sudo systemctl enable docker

# Docker 설치 확인
docker --version
```

### 1.4 Docker Compose 설치
```bash
# Docker Compose 설치
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Docker Compose 설치 확인
docker-compose --version
```

### 1.5 시스템 설정 최적화
```bash
# vm.max_map_count 설정 (Elasticsearch 필수)
sudo sysctl -w vm.max_map_count=262144
echo "vm.max_map_count=262144" | sudo tee -a /etc/sysctl.conf

# 파일 디스크립터 제한 설정
echo "* soft nofile 65536" | sudo tee -a /etc/security/limits.conf
echo "* hard nofile 65536" | sudo tee -a /etc/security/limits.conf
```

## 2. 프로젝트 배포

### 2.1 프로젝트 다운로드
```bash
# Git으로 프로젝트 클론 (또는 파일 업로드)
git clone <your-repository-url>
cd jandi_band_backend

# 또는 파일을 직접 업로드한 경우
# 프로젝트 디렉토리로 이동
```

### 2.2 Elasticsearch 시작
```bash
# search 폴더로 이동
cd search

# 실행 권한 부여
chmod +x start-elasticsearch-ec2.sh

# Elasticsearch 시작
./start-elasticsearch-ec2.sh
```

### 2.3 방화벽 설정
```bash
# UFW 방화벽 활성화
sudo ufw enable

# 필요한 포트 열기
sudo ufw allow ssh
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 8080/tcp
sudo ufw allow 9200/tcp
sudo ufw allow 5601/tcp

# 방화벽 상태 확인
sudo ufw status
```

## 3. Spring Boot 애플리케이션 설정

### 3.1 application.properties 설정
```properties
# 데이터베이스 설정
spring.datasource.url=jdbc:mysql://localhost:3306/jandi_band?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8
spring.datasource.username=your_username
spring.datasource.password=your_password

# Elasticsearch 설정
spring.elasticsearch.uris=http://localhost:9200
spring.elasticsearch.connection-timeout=10s
spring.elasticsearch.socket-timeout=30s

# 서버 포트 설정
server.port=8080

# 로깅 설정
logging.level.org.springframework.data.elasticsearch=INFO
logging.level.org.elasticsearch=INFO
```

### 3.2 애플리케이션 빌드 및 실행
```bash
# 프로젝트 루트로 이동
cd /path/to/jandi_band_backend

# Gradle 래퍼에 실행 권한 부여
chmod +x gradlew

# 애플리케이션 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun
```

### 3.3 백그라운드 실행 (선택사항)
```bash
# nohup으로 백그라운드 실행
nohup ./gradlew bootRun > app.log 2>&1 &

# 프로세스 확인
ps aux | grep java

# 로그 확인
tail -f app.log
```

## 4. 데이터 동기화 및 테스트

### 4.1 데이터 동기화
```bash
# Elasticsearch에 데이터 동기화
curl -X POST "http://localhost:8080/api/admin/promos/sync-all"
```

### 4.2 서비스 상태 확인
```bash
# Elasticsearch 상태 확인
curl -X GET "http://localhost:9200/_cluster/health?pretty"

# Spring Boot 애플리케이션 상태 확인
curl -X GET "http://localhost:8080/health"
```

### 4.3 검색 테스트
```bash
# 키워드 검색 테스트
curl -X GET "http://localhost:8080/api/promos/search-v2?keyword=락밴드&page=0&size=10"

# 제목 검색 테스트
curl -X GET "http://localhost:8080/api/promos/search-v2/title?title=정기공연&page=0&size=10"
```

## 5. 외부 접속 설정

### 5.1 EC2 퍼블릭 IP 확인
```bash
# EC2 퍼블릭 IP 확인
curl http://169.254.169.254/latest/meta-data/public-ipv4
```

### 5.2 외부에서 접속 테스트
```bash
# Elasticsearch 접속 테스트 (EC2 퍼블릭 IP 사용)
curl -X GET "http://YOUR_EC2_PUBLIC_IP:9200/_cluster/health?pretty"

# Spring Boot 애플리케이션 접속 테스트
curl -X GET "http://YOUR_EC2_PUBLIC_IP:8080/health"

# 검색 API 테스트
curl -X GET "http://YOUR_EC2_PUBLIC_IP:8080/api/promos/search-v2?keyword=락밴드"
```

### 5.3 Kibana 접속
브라우저에서 다음 URL로 접속:
```
http://YOUR_EC2_PUBLIC_IP:5601
```

## 6. 모니터링 및 관리

### 6.1 서비스 상태 확인
```bash
# Docker 컨테이너 상태 확인
docker ps

# Elasticsearch 로그 확인
docker logs jandi-elasticsearch

# Kibana 로그 확인
docker logs jandi-kibana
```

### 6.2 리소스 사용량 모니터링
```bash
# 시스템 리소스 확인
htop

# 메모리 사용량 확인
free -h

# 디스크 사용량 확인
df -h

# Docker 리소스 사용량 확인
docker stats
```

### 6.3 서비스 재시작
```bash
# Elasticsearch 재시작
docker-compose -f docker-compose.elasticsearch.ec2.yml restart

# 특정 서비스만 재시작
docker-compose -f docker-compose.elasticsearch.ec2.yml restart elasticsearch
docker-compose -f docker-compose.elasticsearch.ec2.yml restart kibana
```

## 7. 문제 해결

### 7.1 Elasticsearch 시작 실패
```bash
# 로그 확인
docker logs jandi-elasticsearch

# 메모리 부족 문제 해결
# EC2 인스턴스 타입을 더 큰 것으로 변경하거나
# Elasticsearch 메모리 설정 조정
```

### 7.2 포트 충돌
```bash
# 포트 사용 중인 프로세스 확인
sudo netstat -tulpn | grep :9200
sudo netstat -tulpn | grep :5601

# 충돌하는 프로세스 종료
sudo kill -9 <PID>
```

### 7.3 디스크 공간 부족
```bash
# 디스크 사용량 확인
df -h

# 불필요한 파일 정리
sudo apt autoremove -y
docker system prune -f
```

### 7.4 방화벽 문제
```bash
# 방화벽 상태 확인
sudo ufw status

# 포트 다시 열기
sudo ufw allow 9200/tcp
sudo ufw allow 5601/tcp
```

## 8. 백업 및 복구

### 8.1 Elasticsearch 데이터 백업
```bash
# 인덱스 백업
curl -X PUT "localhost:9200/_snapshot/backup_repo" -H 'Content-Type: application/json' -d'
{
  "type": "fs",
  "settings": {
    "location": "/usr/share/elasticsearch/data/backup"
  }
}'

# 스냅샷 생성
curl -X PUT "localhost:9200/_snapshot/backup_repo/snapshot_1?wait_for_completion=true"
```

### 8.2 데이터 복구
```bash
# 스냅샷에서 복구
curl -X POST "localhost:9200/_snapshot/backup_repo/snapshot_1/_restore"
```

## 9. 성능 최적화

### 9.1 Elasticsearch 설정 최적화
```bash
# 인덱스 설정 최적화
curl -X PUT "localhost:9200/promos/_settings" -H 'Content-Type: application/json' -d'
{
  "index": {
    "number_of_shards": 1,
    "number_of_replicas": 0,
    "refresh_interval": "1s"
  }
}'
```

### 9.2 시스템 최적화
```bash
# 스왑 비활성화 (메모리 충분한 경우)
sudo swapoff -a

# 파일 시스템 캐시 최적화
echo 'vm.swappiness=1' | sudo tee -a /etc/sysctl.conf
```

## 10. 보안 고려사항

### 10.1 기본 보안 설정
```bash
# SSH 키 기반 인증만 허용
sudo nano /etc/ssh/sshd_config
# PasswordAuthentication no
sudo systemctl restart sshd

# 정기적인 보안 업데이트
sudo apt update && sudo apt upgrade -y
```

### 10.2 Elasticsearch 보안 (프로덕션 환경)
프로덕션 환경에서는 Elasticsearch 보안을 활성화하는 것을 권장합니다:
```yaml
# docker-compose.elasticsearch.ec2.yml에서
environment:
  - xpack.security.enabled=true
  - ELASTIC_PASSWORD=your_secure_password
```

## 11. 유용한 명령어 모음

```bash
# 서비스 시작
./start-elasticsearch-ec2.sh

# 서비스 중지
docker-compose -f docker-compose.elasticsearch.ec2.yml down

# 서비스 상태 확인
docker-compose -f docker-compose.elasticsearch.ec2.yml ps

# 로그 확인
docker logs jandi-elasticsearch -f
docker logs jandi-kibana -f

# 데이터 동기화
curl -X POST "http://localhost:8080/api/admin/promos/sync-all"

# 검색 테스트
curl -X GET "http://localhost:8080/api/promos/search-v2?keyword=락밴드"

# 시스템 리소스 확인
htop
free -h
df -h
```

## 12. 다음 단계

1. **모니터링 설정**: Prometheus, Grafana 등으로 시스템 모니터링
2. **로그 관리**: ELK 스택으로 로그 수집 및 분석
3. **백업 자동화**: 정기적인 데이터 백업 스케줄링
4. **보안 강화**: SSL/TLS 인증서 설정, 방화벽 규칙 세분화
5. **성능 튜닝**: 실제 사용량에 따른 Elasticsearch 설정 최적화 