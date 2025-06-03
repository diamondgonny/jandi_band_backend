# Jandi Band Backend - 프로덕션 배포 환경 모니터링 가이드

## 개요

이 가이드는 Ubuntu 서버에서 Spring Boot 애플리케이션과 함께 Prometheus + Grafana 모니터링 시스템을 구축하는 방법을 설명합니다.

## 배포 경로

```
~/home/spring-app/
├── jandi-band-backend.jar          # Spring Boot 애플리케이션
├── monitoring/                     # 모니터링 시스템
│   ├── docker-compose.production.yml
│   ├── config/
│   │   ├── prometheus/
│   │   │   └── prometheus.production.yml
│   │   └── grafana/
│   │       ├── provisioning/
│   │       └── dashboards/
│   └── scripts/
│       └── start-production.sh
└── logs/                           # 애플리케이션 로그
```

## 사전 준비

### 1. 서버 환경 설정

```bash
# Docker 설치 (Ubuntu 22.04)
sudo apt update
sudo apt install -y apt-transport-https ca-certificates curl gnupg lsb-release
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io

# Docker Compose 설치
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 사용자를 docker 그룹에 추가
sudo usermod -aG docker $USER
newgrp docker
```

### 2. 디렉토리 구조 생성

```bash
# 애플리케이션 디렉토리 생성
mkdir -p ~/home/spring-app/monitoring/config/{prometheus,grafana/{provisioning/{datasources,dashboards},dashboards}}
mkdir -p ~/home/spring-app/monitoring/scripts
mkdir -p ~/home/spring-app/logs

cd ~/home/spring-app
```

## 배포 과정

### 1. 모니터링 설정 파일 업로드

모니터링 관련 파일들을 서버의 `~/home/spring-app/monitoring/` 디렉토리에 수동으로 업로드:

```bash
# 로컬에서 서버로 파일 전송
scp -r monitoring/ user@your-server:~/home/spring-app/

# 또는 개별 파일 전송
scp monitoring/docker-compose.production.yml user@your-server:~/home/spring-app/monitoring/
scp monitoring/config/prometheus/prometheus.production.yml user@your-server:~/home/spring-app/monitoring/config/prometheus/
scp monitoring/scripts/start-production.sh user@your-server:~/home/spring-app/monitoring/scripts/
```

### 2. 스크립트 실행 권한 부여

```bash
chmod +x ~/home/spring-app/monitoring/scripts/start-production.sh
```

### 3. Spring Boot 애플리케이션 시작

```bash
cd ~/home/spring-app
java -jar jandi-band-backend.jar --spring.profiles.active=prod &
```

### 4. 모니터링 시스템 시작

```bash
cd ~/home/spring-app/monitoring
./scripts/start-production.sh
```

또는 직접 Docker Compose 실행:

```bash
cd ~/home/spring-app/monitoring
docker-compose -f docker-compose.production.yml up -d
```

## 접속 정보

- **Grafana**: http://your-server-ip:3000 (admin/admin123)
- **Prometheus**: http://your-server-ip:9090
- **Spring Boot App**: http://your-server-ip:8080

## 모니터링 중지

```bash
cd ~/home/spring-app/monitoring
docker-compose -f docker-compose.production.yml down

# 데이터까지 삭제하려면
docker-compose -f docker-compose.production.yml down -v
```

## 백업 및 복구

### 백업

```bash
# 모니터링 데이터 백업
cd ~/home/spring-app
tar -czf monitoring-backup-$(date +%Y%m%d).tar.gz monitoring/
docker run --rm -v monitoring_prometheus-data:/data -v $(pwd):/backup alpine tar czf /backup/prometheus-data-$(date +%Y%m%d).tar.gz -C /data .
```

### 복구

```bash
# 모니터링 설정 복구
tar -xzf monitoring-backup-*.tar.gz

# Prometheus 데이터 복구
docker run --rm -v monitoring_prometheus-data:/data -v $(pwd):/backup alpine tar xzf /backup/prometheus-data-*.tar.gz -C /data
```

## 자동화 (선택사항)

### systemd 서비스 등록

```bash
# 모니터링 서비스 파일 생성
sudo nano /etc/systemd/system/jandi-monitoring.service
```

```ini
[Unit]
Description=Jandi Band Backend Monitoring
Requires=docker.service
After=docker.service

[Service]
Type=oneshot
RemainAfterExit=yes
WorkingDirectory=/home/ubuntu/home/spring-app/monitoring
ExecStart=/usr/local/bin/docker-compose -f docker-compose.production.yml up -d
ExecStop=/usr/local/bin/docker-compose -f docker-compose.production.yml down
TimeoutStartSec=0

[Install]
WantedBy=multi-user.target
```

```bash
# 서비스 활성화
sudo systemctl enable jandi-monitoring.service
sudo systemctl start jandi-monitoring.service
```

## 트러블슈팅

### 1. 컨테이너가 시작되지 않는 경우

```bash
# 로그 확인
docker-compose -f docker-compose.production.yml logs

# 개별 컨테이너 로그 확인
docker logs jandi-prometheus
docker logs jandi-grafana
```

### 2. 메트릭 수집이 되지 않는 경우

```bash
# Spring Boot 애플리케이션 상태 확인
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/prometheus

# Prometheus 타겟 상태 확인
curl http://localhost:9090/api/v1/targets
```

### 3. 포트 충돌 문제

```bash
# 사용 중인 포트 확인
sudo netstat -tulpn | grep :3000
sudo netstat -tulpn | grep :9090

# 포트 변경이 필요한 경우 docker-compose.production.yml 수정
```

## 보안 설정

### 1. 방화벽 설정

```bash
# UFW 방화벽 설정
sudo ufw allow 22        # SSH
sudo ufw allow 8080      # Spring Boot
sudo ufw allow 3000      # Grafana
sudo ufw allow 9090      # Prometheus (선택사항, 내부 네트워크만)
sudo ufw enable
```

### 2. Grafana 보안 강화

```bash
# Grafana 설정 파일에서 admin 비밀번호 변경
# docker-compose.production.yml 파일의 GF_SECURITY_ADMIN_PASSWORD 수정
```

---

이 가이드를 따라하면 프로덕션 환경에서 안정적인 모니터링 시스템을 구축할 수 있습니다.
