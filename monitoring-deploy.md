# Jandi Band Backend - 프로덕션 배포 환경 모니터링 가이드

## 개요

이 가이드는 EC2 Ubuntu 서버에서 Jenkins CI/CD와 Docker를 사용하여 배포되는 Jandi Band Backend 프로젝트의 프로덕션 모니터링 시스템 구축 방법을 설명합니다.

## 배포 아키텍처

```
GitHub → Jenkins (Docker) → Docker Registry → EC2 Host Docker
                                                     ↓
                                    [App Container] [Prometheus] [Grafana] [Alertmanager]
                                            ↓           ↓          ↓           ↓
                                       메트릭 생성   데이터 수집   시각화    알림 발송
```

## 사전 준비

### 1. EC2 서버 설정
```bash
# Docker 설치 (Ubuntu 22.04 기준)
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
# 프로젝트 디렉토리 생성
mkdir -p /home/ubuntu/jandi-monitoring/{config,data,logs}
cd /home/ubuntu/jandi-monitoring

# 데이터 디렉토리 권한 설정
sudo mkdir -p data/{prometheus,grafana,alertmanager}
sudo chown -R 472:472 data/grafana  # Grafana 컨테이너 UID
sudo chown -R 65534:65534 data/prometheus  # Prometheus 컨테이너 UID
sudo chown -R 65534:65534 data/alertmanager  # Alertmanager 컨테이너 UID
```

## 설정 파일 생성

### 1. Docker Compose 설정 (`docker-compose.monitoring.yml`)
```yaml
version: '3.8'

networks:
  monitoring:
    driver: bridge
  app-network:
    external: true  # 애플리케이션 네트워크에 연결

volumes:
  prometheus-data:
    driver: local
  grafana-data:
    driver: local
  alertmanager-data:
    driver: local

services:
  prometheus:
    image: prom/prometheus:v2.45.0
    container_name: jandi-prometheus
    restart: unless-stopped
    ports:
      - "9090:9090"
    volumes:
      - ./config/prometheus.yml:/etc/prometheus/prometheus.yml:ro
      - ./config/alert-rules.yml:/etc/prometheus/alert-rules.yml:ro
      - prometheus-data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--storage.tsdb.retention.time=30d'
      - '--storage.tsdb.retention.size=10GB'
      - '--web.console.libraries=/etc/prometheus/console_libraries'
      - '--web.console.templates=/etc/prometheus/consoles'
      - '--web.enable-lifecycle'
      - '--web.enable-admin-api'
      - '--alertmanager.url=http://alertmanager:9093'
    networks:
      - monitoring
      - app-network
    healthcheck:
      test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:9090/-/healthy"]
      interval: 30s
      timeout: 10s
      retries: 3

  grafana:
    image: grafana/grafana:10.0.0
    container_name: jandi-grafana
    restart: unless-stopped
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin123
      - GF_SECURITY_ADMIN_USER=admin
      - GF_INSTALL_PLUGINS=grafana-clock-panel,grafana-simple-json-datasource
      - GF_SMTP_ENABLED=true
      - GF_SMTP_HOST=smtp.gmail.com:587
      - GF_SMTP_USER=${SMTP_USER}
      - GF_SMTP_PASSWORD=${SMTP_PASSWORD}
      - GF_SMTP_FROM_ADDRESS=${SMTP_FROM}
    volumes:
      - grafana-data:/var/lib/grafana
      - ./config/grafana/provisioning:/etc/grafana/provisioning:ro
      - ./config/grafana/dashboards:/var/lib/grafana/dashboards:ro
    networks:
      - monitoring
    depends_on:
      - prometheus
    healthcheck:
      test: ["CMD-SHELL", "curl -f http://localhost:3000/api/health || exit 1"]
      interval: 30s
      timeout: 10s
      retries: 3

  alertmanager:
    image: prom/alertmanager:v0.25.0
    container_name: jandi-alertmanager
    restart: unless-stopped
    ports:
      - "9093:9093"
    volumes:
      - ./config/alertmanager.yml:/etc/alertmanager/alertmanager.yml:ro
      - alertmanager-data:/alertmanager
    command:
      - '--config.file=/etc/alertmanager/alertmanager.yml'
      - '--storage.path=/alertmanager'
      - '--web.external-url=http://localhost:9093'
      - '--cluster.advertise-address=0.0.0.0:9093'
    networks:
      - monitoring
    healthcheck:
      test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:9093/-/healthy"]
      interval: 30s
      timeout: 10s
      retries: 3

  node-exporter:
    image: prom/node-exporter:v1.6.0
    container_name: jandi-node-exporter
    restart: unless-stopped
    ports:
      - "9100:9100"
    volumes:
      - /proc:/host/proc:ro
      - /sys:/host/sys:ro
      - /:/rootfs:ro
    command:
      - '--path.procfs=/host/proc'
      - '--path.rootfs=/rootfs'
      - '--path.sysfs=/host/sys'
      - '--collector.filesystem.mount-points-exclude=^/(sys|proc|dev|host|etc)($$|/)'
    networks:
      - monitoring

  cadvisor:
    image: gcr.io/cadvisor/cadvisor:v0.47.0
    container_name: jandi-cadvisor
    restart: unless-stopped
    ports:
      - "8080:8080"
    volumes:
      - /:/rootfs:ro
      - /var/run:/var/run:rw
      - /sys:/sys:ro
      - /var/lib/docker/:/var/lib/docker:ro
      - /dev/disk/:/dev/disk:ro
    privileged: true
    networks:
      - monitoring
```

### 2. Prometheus 설정 (`config/prometheus.yml`)
```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

rule_files:
  - "alert-rules.yml"

alerting:
  alertmanagers:
    - static_configs:
        - targets:
          - alertmanager:9093

scrape_configs:
  # Spring Boot 애플리케이션
  - job_name: 'jandi-band-backend'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['jandi-app:8080']  # Docker 네트워크 내 컨테이너 이름
    scrape_interval: 5s
    scrape_timeout: 5s

  # Node Exporter (시스템 메트릭)
  - job_name: 'node-exporter'
    static_configs:
      - targets: ['node-exporter:9100']

  # cAdvisor (컨테이너 메트릭)
  - job_name: 'cadvisor'
    static_configs:
      - targets: ['cadvisor:8080']

  # Prometheus 자체 모니터링
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']

  # Grafana 모니터링
  - job_name: 'grafana'
    static_configs:
      - targets: ['grafana:3000']
```

### 3. 알림 규칙 설정 (`config/alert-rules.yml`)
```yaml
groups:
  - name: jandi-backend-alerts
    rules:
      # 애플리케이션 다운
      - alert: ApplicationDown
        expr: up{job="jandi-band-backend"} == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Jandi Band Backend 애플리케이션이 다운되었습니다"
          description: "{{ $labels.instance }}에서 애플리케이션이 1분 이상 응답하지 않습니다."

      # 높은 응답 시간
      - alert: HighResponseTime
        expr: histogram_quantile(0.95, rate(http_server_requests_seconds_bucket{job="jandi-band-backend"}[5m])) > 1
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "높은 응답 시간 감지"
          description: "95th 백분위수 응답 시간이 {{ $value }}초로 1초를 초과했습니다."

      # 높은 에러율
      - alert: HighErrorRate
        expr: rate(http_server_requests_seconds_count{job="jandi-band-backend",status=~"5.."}[5m]) / rate(http_server_requests_seconds_count{job="jandi-band-backend"}[5m]) * 100 > 5
        for: 2m
        labels:
          severity: critical
        annotations:
          summary: "높은 에러율 감지"
          description: "에러율이 {{ $value }}%로 5%를 초과했습니다."

      # 높은 메모리 사용률
      - alert: HighMemoryUsage
        expr: (jvm_memory_used_bytes{job="jandi-band-backend"} / jvm_memory_max_bytes{job="jandi-band-backend"}) * 100 > 85
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "높은 JVM 메모리 사용률"
          description: "JVM 메모리 사용률이 {{ $value }}%로 85%를 초과했습니다."

      # 디스크 공간 부족
      - alert: LowDiskSpace
        expr: (node_filesystem_avail_bytes / node_filesystem_size_bytes) * 100 < 10
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "디스크 공간 부족"
          description: "{{ $labels.mountpoint }}의 디스크 공간이 {{ $value }}% 남았습니다."

      # 높은 CPU 사용률
      - alert: HighCPUUsage
        expr: 100 - (avg(rate(node_cpu_seconds_total{mode="idle"}[5m])) * 100) > 80
        for: 10m
        labels:
          severity: warning
        annotations:
          summary: "높은 CPU 사용률"
          description: "CPU 사용률이 {{ $value }}%로 80%를 초과했습니다."
```

### 4. Alertmanager 설정 (`config/alertmanager.yml`)
```yaml
global:
  smtp_smarthost: 'smtp.gmail.com:587'
  smtp_from: '${SMTP_FROM}'
  smtp_auth_username: '${SMTP_USER}'
  smtp_auth_password: '${SMTP_PASSWORD}'

route:
  group_by: ['alertname']
  group_wait: 10s
  group_interval: 10s
  repeat_interval: 1h
  receiver: 'web.hook'
  routes:
    - match:
        severity: critical
      receiver: 'critical-alerts'
    - match:
        severity: warning
      receiver: 'warning-alerts'

receivers:
  - name: 'web.hook'
    webhook_configs:
      - url: 'http://localhost:5001/'

  - name: 'critical-alerts'
    email_configs:
      - to: 'admin@example.com'
        subject: '[CRITICAL] Jandi Backend Alert'
        body: |
          {{ range .Alerts }}
          Alert: {{ .Annotations.summary }}
          Description: {{ .Annotations.description }}
          {{ end }}
    slack_configs:
      - api_url: 'YOUR_SLACK_WEBHOOK_URL'
        channel: '#alerts'
        title: '[CRITICAL] Jandi Backend Alert'
        text: '{{ range .Alerts }}{{ .Annotations.summary }}{{ end }}'

  - name: 'warning-alerts'
    email_configs:
      - to: 'team@example.com'
        subject: '[WARNING] Jandi Backend Alert'
        body: |
          {{ range .Alerts }}
          Alert: {{ .Annotations.summary }}
          Description: {{ .Annotations.description }}
          {{ end }}

inhibit_rules:
  - source_match:
      severity: 'critical'
    target_match:
      severity: 'warning'
    equal: ['alertname', 'dev', 'instance']
```

### 5. Grafana 프로비저닝 설정

#### 데이터소스 설정 (`config/grafana/provisioning/datasources/prometheus.yml`)
```yaml
apiVersion: 1

datasources:
  - name: Prometheus
    type: prometheus
    access: proxy
    url: http://prometheus:9090
    isDefault: true
    editable: true
```

#### 대시보드 설정 (`config/grafana/provisioning/dashboards/dashboard.yml`)
```yaml
apiVersion: 1

providers:
  - name: 'default'
    orgId: 1
    folder: ''
    type: file
    disableDeletion: false
    editable: true
    options:
      path: /var/lib/grafana/dashboards
```

## Jenkins CI/CD 통합

### 1. Jenkinsfile 수정
```groovy
pipeline {
    agent any
    
    environment {
        DOCKER_REGISTRY = 'your-registry.com'
        IMAGE_NAME = 'jandi-band-backend'
        MONITORING_COMPOSE = '/home/ubuntu/jandi-monitoring/docker-compose.monitoring.yml'
    }
    
    stages {
        stage('Build') {
            steps {
                script {
                    sh './gradlew clean build'
                }
            }
        }
        
        stage('Docker Build') {
            steps {
                script {
                    def image = docker.build("${DOCKER_REGISTRY}/${IMAGE_NAME}:${BUILD_NUMBER}")
                    docker.withRegistry("https://${DOCKER_REGISTRY}", 'docker-registry-credentials') {
                        image.push()
                        image.push('latest')
                    }
                }
            }
        }
        
        stage('Deploy') {
            steps {
                script {
                    sh """
                        # 기존 컨테이너 중지 및 제거
                        docker stop jandi-app || true
                        docker rm jandi-app || true
                        
                        # 네트워크 생성 (이미 존재하면 무시)
                        docker network create app-network || true
                        
                        # 새 컨테이너 실행
                        docker run -d \
                            --name jandi-app \
                            --network app-network \
                            -p 8080:8080 \
                            -e SPRING_PROFILES_ACTIVE=prod \
                            ${DOCKER_REGISTRY}/${IMAGE_NAME}:${BUILD_NUMBER}
                        
                        # 모니터링 스택 업데이트 (필요시)
                        cd /home/ubuntu/jandi-monitoring
                        docker-compose -f docker-compose.monitoring.yml up -d
                    """
                }
            }
        }
        
        stage('Health Check') {
            steps {
                script {
                    sh """
                        # 애플리케이션 헬스체크
                        for i in {1..30}; do
                            if curl -f http://localhost:8080/health; then
                                echo "Application is healthy"
                                break
                            fi
                            echo "Waiting for application to start... (\$i/30)"
                            sleep 10
                        done
                        
                        # Prometheus 타겟 확인
                        sleep 30
                        curl -s http://localhost:9090/api/v1/targets | jq '.data.activeTargets[] | select(.labels.job=="jandi-band-backend") | .health'
                    """
                }
            }
        }
    }
    
    post {
        success {
            slackSend(
                channel: '#deployments',
                color: 'good',
                message: "Deployment successful: ${env.JOB_NAME} - ${env.BUILD_NUMBER}"
            )
        }
        failure {
            slackSend(
                channel: '#deployments',
                color: 'danger',
                message: "Deployment failed: ${env.JOB_NAME} - ${env.BUILD_NUMBER}"
            )
        }
    }
}
```

### 2. 환경 변수 설정 파일 (`.env`)
```bash
# SMTP 설정 (Grafana 알림용)
SMTP_USER=your-email@gmail.com
SMTP_PASSWORD=your-app-password
SMTP_FROM=noreply@yourdomain.com

# Slack 설정
SLACK_WEBHOOK_URL=https://hooks.slack.com/services/YOUR/SLACK/WEBHOOK

# 도메인 설정
DOMAIN=yourdomain.com
```

## 배포 및 실행

### 1. 모니터링 스택 배포
```bash
# 설정 파일들을 서버에 업로드
scp -r config/ ubuntu@your-ec2-instance:/home/ubuntu/jandi-monitoring/
scp docker-compose.monitoring.yml ubuntu@your-ec2-instance:/home/ubuntu/jandi-monitoring/
scp .env ubuntu@your-ec2-instance:/home/ubuntu/jandi-monitoring/

# 서버에 접속하여 실행
ssh ubuntu@your-ec2-instance
cd /home/ubuntu/jandi-monitoring

# 환경 변수 로드 및 실행
source .env
docker-compose -f docker-compose.monitoring.yml up -d

# 로그 확인
docker-compose -f docker-compose.monitoring.yml logs -f
```

### 2. 포트 및 보안 그룹 설정
```bash
# EC2 보안 그룹에서 다음 포트 허용:
# - 3000: Grafana Web UI
# - 9090: Prometheus Web UI (선택사항, 보안상 제한 권장)
# - 9093: Alertmanager Web UI (선택사항, 보안상 제한 권장)
# - 8080: Spring Boot Application
```

### 3. Nginx 리버스 프록시 설정 (선택사항)
```nginx
# /etc/nginx/sites-available/jandi-monitoring
server {
    listen 80;
    server_name monitoring.yourdomain.com;
    
    location /grafana/ {
        proxy_pass http://localhost:3000/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
    
    location /prometheus/ {
        proxy_pass http://localhost:9090/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # 보안: IP 제한
        allow 10.0.0.0/8;
        allow 172.16.0.0/12;
        allow 192.168.0.0/16;
        deny all;
    }
}
```

## 모니터링 대시보드 접속

### 접속 URL
- **Grafana**: http://your-ec2-ip:3000 (admin/admin123)
- **Prometheus**: http://your-ec2-ip:9090
- **Alertmanager**: http://your-ec2-ip:9093

### 기본 대시보드 임포트
```bash
# Grafana에서 다음 대시보드 ID 임포트:
# - 1860: Node Exporter Full
# - 893: Docker and system monitoring
# - 12900: Spring Boot 2.1 Statistics
```

## 알림 테스트

### 1. 애플리케이션 다운 테스트
```bash
# 애플리케이션 컨테이너 중지
docker stop jandi-app

# 1분 후 알림 확인
```

### 2. 메모리 부하 테스트
```bash
# 메모리 부하 생성 (테스트용)
docker exec jandi-app java -jar stress-test.jar --memory
```

## 운영 가이드

### 1. 정기 점검 항목
```bash
# 디스크 사용량 확인
df -h

# 메트릭 데이터 크기 확인
du -sh /home/ubuntu/jandi-monitoring/data/prometheus

# 로그 파일 로테이션 확인
docker logs jandi-prometheus --tail 100
docker logs jandi-grafana --tail 100
```

### 2. 백업 스크립트
```bash
#!/bin/bash
# backup-monitoring.sh

BACKUP_DIR="/home/ubuntu/backups/monitoring"
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p $BACKUP_DIR

# Grafana 설정 백업
docker exec jandi-grafana grafana-cli admin export-dash > $BACKUP_DIR/grafana-dashboards-$DATE.json

# Prometheus 데이터 백업 (스냅샷)
curl -XPOST http://localhost:9090/api/v1/admin/tsdb/snapshot
tar -czf $BACKUP_DIR/prometheus-data-$DATE.tar.gz /home/ubuntu/jandi-monitoring/data/prometheus

# 설정 파일 백업
tar -czf $BACKUP_DIR/monitoring-config-$DATE.tar.gz /home/ubuntu/jandi-monitoring/config

# 7일 이상 된 백업 파일 삭제
find $BACKUP_DIR -name "*.tar.gz" -mtime +7 -delete
find $BACKUP_DIR -name "*.json" -mtime +7 -delete
```

### 3. 장애 복구 절차
```bash
# 1. 모니터링 스택 재시작
cd /home/ubuntu/jandi-monitoring
docker-compose -f docker-compose.monitoring.yml restart

# 2. 데이터 복구 (필요시)
docker-compose -f docker-compose.monitoring.yml down
tar -xzf prometheus-data-backup.tar.gz
docker-compose -f docker-compose.monitoring.yml up -d

# 3. 설정 복구 (필요시)
tar -xzf monitoring-config-backup.tar.gz
docker-compose -f docker-compose.monitoring.yml restart
```

## 트러블슈팅

### 자주 발생하는 문제들

#### 1. Prometheus가 애플리케이션을 찾지 못하는 경우
```bash
# 네트워크 연결 확인
docker network ls
docker network inspect app-network

# 컨테이너 이름 확인
docker ps --format "table {{.Names}}\t{{.Networks}}"

# 애플리케이션 메트릭 엔드포인트 확인
curl http://localhost:8080/actuator/prometheus
```

#### 2. Grafana 대시보드가 데이터를 표시하지 않는 경우
```bash
# Prometheus 연결 상태 확인
curl http://localhost:9090/api/v1/targets

# Grafana 데이터소스 테스트
curl -u admin:admin123 http://localhost:3000/api/datasources/proxy/1/api/v1/query?query=up
```

#### 3. 알림이 발송되지 않는 경우
```bash
# Alertmanager 설정 확인
curl http://localhost:9093/api/v1/status

# SMTP 설정 테스트
docker exec jandi-grafana grafana-cli admin test-email admin@example.com
```

## 성능 최적화

### 1. Prometheus 데이터 보존 정책
```yaml
# prometheus.yml 추가 설정
global:
  scrape_interval: 15s  # 운영환경에서는 30s 권장
  evaluation_interval: 15s

# 스토리지 설정
storage:
  tsdb:
    retention.time: 15d  # 보존 기간 조정
    retention.size: 5GB  # 최대 크기 제한
```

### 2. 메트릭 필터링
```yaml
# 불필요한 메트릭 제외
metric_relabel_configs:
  - source_labels: [__name__]
    regex: 'jvm_gc_.*|jvm_buffer_.*'
    action: drop
```

## 다음 단계

1. **로그 중앙화**: ELK Stack 또는 Loki 추가
2. **분산 추적**: Jaeger 또는 Zipkin 통합
3. **자동 스케일링**: Kubernetes 마이그레이션 고려
4. **보안 강화**: TLS/SSL 인증서 적용
5. **비용 최적화**: 클라우드 모니터링 서비스 검토

---

## 문의사항

프로덕션 모니터링 시스템 관련 문의사항이 있으시면 언제든지 연락주세요!
