# 원격 서버(Ubuntu)에 모니터링 스택 통합 가이드

> 이미 Docker, Jenkins, Spring Boot, Nginx가 구성된 환경에 Prometheus와 Grafana를 추가하는 방법을 설명합니다.

## 사전 확인사항

### 1. 현재 환경 확인
현재 실행 중인 Docker 환경을 확인하세요:

```bash
# 현재 환경 분석 스크립트 실행
cd monitoring
chmod +x scripts/check-environment.sh
./scripts/check-environment.sh
```

### 2. 보안 그룹 설정 (AWS EC2인 경우)
다음 포트들을 인바운드 규칙에 추가하세요:
- **3000** (Grafana)
- **9090** (Prometheus) 
- **9093** (Alertmanager)
- **9118** (Jenkins Exporter, 선택사항)

### 3. Spring Boot 애플리케이션 메트릭 설정 확인
Spring Boot 애플리케이션이 Prometheus 메트릭을 노출하도록 설정되어 있는지 확인:

```yaml
# application.yml 또는 application.properties
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
    prometheus:
      enabled: true
  metrics:
    export:
      prometheus:
        enabled: true
```

## 빠른 시작 (자동 설치)

### 1. 모니터링 파일 업로드
로컬에서 원격 서버로 monitoring 디렉토리를 업로드:

```bash
# 로컬 터미널에서 실행
scp -r -i your-key.pem monitoring/ ubuntu@your-server-ip:/home/ubuntu/
```

### 2. 자동 배포 실행
```bash
# 원격 서버에 SSH 접속 후
cd monitoring
chmod +x scripts/deploy-ec2.sh
./scripts/deploy-ec2.sh
```

스크립트가 자동으로:
- 현재 Docker 환경을 분석
- Spring Boot, Jenkins 컨테이너 감지
- 네트워크 설정 확인
- 모니터링 스택 배포

## 수동 설정 (고급 사용자용)

### 1. 현재 환경 분석
```bash
# 컨테이너 목록 확인
docker ps

# 네트워크 확인
docker network ls

# Spring Boot 컨테이너 정보 확인
docker inspect <springboot-container-name>
```

### 2. 설정 파일 수정

#### 2.1 docker-compose.deploy.yml 수정
```bash
# 네트워크명 변경 (실제 사용 중인 네트워크명으로)
sed -i 's/jandi_backend_network/your-actual-network/g' docker-compose.deploy.yml

# 도메인 설정
sed -i 's/YOUR_DOMAIN/your-domain.com/g' docker-compose.deploy.yml
```

#### 2.2 prometheus.deploy.yml 수정
```bash
# Spring Boot 컨테이너명 변경
sed -i 's/springboot-container/your-springboot-container/g' config/prometheus/prometheus.deploy.yml

# Jenkins 컨테이너명 변경 (있는 경우)
sed -i 's/jenkins-container/your-jenkins-container/g' config/prometheus/prometheus.deploy.yml
```

### 3. 모니터링 스택 시작
```bash
docker-compose -f docker-compose.deploy.yml up -d
```

## 접속 및 확인

### 1. 서비스 상태 확인
```bash
# 컨테이너 상태 확인
docker-compose -f docker-compose.deploy.yml ps

# 로그 확인
docker-compose -f docker-compose.deploy.yml logs -f
```

### 2. 웹 인터페이스 접속

#### 직접 접속 (포트 기반)
- **Prometheus**: `http://server-ip:9090`
- **Grafana**: `http://server-ip:3000`
  - 계정: `admin` / `admin123`
- **Alertmanager**: `http://server-ip:9093`

#### Nginx 리버스 프록시 사용 시
기존 Nginx 설정에 다음 location 블록을 추가:

```nginx
# /etc/nginx/sites-available/default 또는 해당 설정 파일에 추가

# Prometheus
location /prometheus/ {
    proxy_pass http://localhost:9090/;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
}

# Grafana
location /grafana/ {
    proxy_pass http://localhost:3000/;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
}
```

설정 후 Nginx 재시작:
```bash
sudo nginx -t
sudo systemctl reload nginx
```

### 3. 메트릭 수집 확인

#### Prometheus 대시보드에서 확인
1. `http://server-ip:9090` 접속
2. Status → Targets 메뉴
3. 다음 job들이 UP 상태인지 확인:
   - `jandi-band-backend` (Spring Boot)
   - `jenkins` (Jenkins, 설정한 경우)

#### Spring Boot 메트릭 직접 확인
```bash
# Spring Boot 컨테이너 내부에서 확인
docker exec -it <springboot-container> curl http://localhost:8080/actuator/prometheus

# 또는 호스트에서 확인 (포트가 노출된 경우)
curl http://localhost:8080/actuator/prometheus
```

## Grafana 대시보드 설정

### 1. 데이터 소스 추가
1. Grafana 접속 (`http://server-ip:3000`)
2. Configuration → Data Sources
3. Add data source → Prometheus
4. URL: `http://prometheus:9090`
5. Save & Test

### 2. 추천 대시보드
- **Spring Boot Dashboard**: ID `6756`
- **Jenkins Dashboard**: ID `9964`
- **Docker Container Dashboard**: ID `193`

Import 방법:
1. Dashboard → Import
2. 대시보드 ID 입력
3. Prometheus 데이터 소스 선택

## 문제 해결

### 1. 컨테이너 간 통신 문제
```bash
# 네트워크 연결 확인
docker network inspect <network-name>

# 컨테이너 간 연결 테스트
docker exec -it jandi-prometheus-deploy ping springboot-container
```

### 2. 메트릭 수집이 안 되는 경우
```bash
# Spring Boot 애플리케이션 상태 확인
docker logs <springboot-container>

# Prometheus 타겟 상태 확인
curl http://localhost:9090/api/v1/targets | jq '.data.activeTargets'
```

### 3. 포트 충돌 해결
기존 서비스와 포트가 충돌하는 경우 `docker-compose.deploy.yml`에서 포트 번호 변경:
```yaml
ports:
  - "19090:9090"  # 9090 대신 19090 사용
```

## 모니터링 스택 관리

### 1. 업데이트
```bash
cd monitoring
docker-compose -f docker-compose.deploy.yml pull
docker-compose -f docker-compose.deploy.yml up -d
```

### 2. 중지
```bash
docker-compose -f docker-compose.deploy.yml down
```

### 3. 데이터 백업
```bash
# Prometheus 데이터 백업
docker run --rm -v monitoring_prometheus-data:/data -v $(pwd):/backup alpine tar czf /backup/prometheus-backup-$(date +%Y%m%d).tar.gz /data

# Grafana 데이터 백업  
docker run --rm -v monitoring_grafana-data:/data -v $(pwd):/backup alpine tar czf /backup/grafana-backup-$(date +%Y%m%d).tar.gz /data
``` 