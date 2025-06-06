# EC2에서 프로메테우스와 그라파나 설정 가이드

## 사전 준비사항

### 1. EC2 보안 그룹 설정
다음 포트들을 인바운드 규칙에 추가하세요:
- **3000** (Grafana)
- **9090** (Prometheus) 
- **9093** (Alertmanager)
- **80, 443** (Nginx)

### 2. 스프링부트 애플리케이션 설정 확인
스프링부트 애플리케이션이 Prometheus 메트릭을 노출하도록 설정되어 있는지 확인:

```yaml
# application.yml
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

## 설치 및 설정 과정

### 1. 모니터링 파일 업로드
로컬에서 EC2로 monitoring 디렉토리 전체를 업로드:

```bash
# 로컬 터미널에서 실행
scp -r -i your-key.pem monitoring/ ubuntu@your-ec2-ip:/home/ubuntu/
```

### 2. EC2에서 Docker 설치
```bash
# EC2 인스턴스에 SSH 접속 후 실행
sudo apt update
sudo apt install -y docker.io docker-compose
sudo usermod -aG docker $USER
sudo systemctl start docker
sudo systemctl enable docker

# 로그아웃 후 재로그인 또는 다음 명령어 실행
newgrep docker
```

### 3. 모니터링 스택 배포
```bash
cd monitoring
chmod +x scripts/deploy-ec2.sh
./scripts/deploy-ec2.sh
```

### 4. Nginx 리버스 프록시 설정 (선택사항)

#### 4.1 Nginx 설치
```bash
sudo apt install -y nginx
```

#### 4.2 설정 파일 추가
```bash
# 기존 default 사이트 설정에 추가하거나 새 설정 파일 생성
sudo cp /home/ubuntu/monitoring/docs/nginx-monitoring.conf /etc/nginx/sites-available/monitoring

# 기존 설정에 추가하는 경우
sudo nano /etc/nginx/sites-available/default
# monitoring/docs/nginx-monitoring.conf 내용을 server 블록 안에 복사
```

#### 4.3 Nginx 재시작
```bash
sudo nginx -t  # 설정 검증
sudo systemctl reload nginx
```

## 접속 및 확인

### 1. 서비스 상태 확인
```bash
# 컨테이너 상태 확인
docker ps

# 로그 확인
docker-compose -f docker-compose.deploy.yml logs -f
```

### 2. 웹 인터페이스 접속

#### 직접 접속 (포트 기반)
- **Prometheus**: `http://your-ec2-ip:9090`
- **Grafana**: `http://your-ec2-ip:3000`
  - 계정: `admin` / `admin123`
- **Alertmanager**: `http://your-ec2-ip:9093`

#### Nginx 리버스 프록시 사용 시
- **Prometheus**: `http://your-domain/prometheus/`
- **Grafana**: `http://your-domain/grafana/`
- **Alertmanager**: `http://your-domain/alertmanager/`

### 3. 메트릭 수집 확인

#### Prometheus에서 확인
1. Prometheus 웹 UI → Status → Targets
2. `jandi-band-backend` job의 상태가 UP인지 확인

#### 스프링부트 애플리케이션 확인
```bash
# 메트릭 엔드포인트 직접 확인
curl http://localhost:8080/actuator/prometheus
curl http://localhost:8080/actuator/health
```

## 문제 해결

### 1. 컨테이너가 시작되지 않는 경우
```bash
# 로그 확인
docker-compose -f docker-compose.deploy.yml logs

# 개별 컨테이너 로그 확인
docker logs jandi-prometheus-deploy
docker logs jandi-grafana-deploy
```

### 2. 메트릭 수집이 안 되는 경우
- 스프링부트 애플리케이션이 실행 중인지 확인
- 포트 8080이 열려있는지 확인
- `host.docker.internal` 연결 확인:
  ```bash
  # 프로메테우스 컨테이너 내부에서 테스트
  docker exec -it jandi-prometheus-deploy sh
  wget -qO- http://host.docker.internal:8080/actuator/health
  ```

### 3. Grafana 대시보드가 로드되지 않는 경우
```bash
# Grafana 데이터 볼륨 권한 확인
sudo chown -R 472:472 /var/lib/docker/volumes/monitoring_grafana-data/_data
```

## 유지보수

### 1. 업데이트
```bash
cd monitoring
docker-compose -f docker-compose.deploy.yml pull
docker-compose -f docker-compose.deploy.yml up -d
```

### 2. 백업
```bash
# Prometheus 데이터 백업
docker run --rm -v monitoring_prometheus-data:/data -v $(pwd):/backup alpine tar czf /backup/prometheus-backup.tar.gz /data

# Grafana 데이터 백업
docker run --rm -v monitoring_grafana-data:/data -v $(pwd):/backup alpine tar czf /backup/grafana-backup.tar.gz /data
```

### 3. 모니터링 스택 중지
```bash
cd monitoring
docker-compose -f docker-compose.deploy.yml down
``` 