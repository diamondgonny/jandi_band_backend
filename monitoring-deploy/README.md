# 운영 환경 모니터링 (Ubuntu EC2)

Prometheus + Grafana를 사용한 운영 환경 모니터링 설정입니다.

## 🏗️ 구성요소

- **Prometheus**: 메트릭 수집 및 저장
- **Grafana**: 데이터 시각화 및 대시보드

## 📋 사전 요구사항

- Ubuntu EC2 서버
- Docker & Docker Compose 설치
- 기존 Spring Boot 앱 (`rhythmeet-be` 컨테이너) 실행 중
- Jenkins 컨테이너 실행 중 (선택사항)

## 🚀 배포 방법

### 1. 파일 업로드
```bash
# 로컬에서 EC2로 파일 전송
scp -r -i "jandi-band.pem" monitoring-deploy/ ubuntu@54.180.215.226:~/monitoring/
```

### 2. 서버에서 배포
```bash
# EC2 서버 접속
ssh -i "jandi-band.pem" ubuntu@54.180.215.226

# 모니터링 디렉토리로 이동
cd ~/monitoring

# 배포 스크립트 실행 권한 부여
chmod +x scripts/deploy.sh

# 배포 실행
./scripts/deploy.sh
```

### 3. 수동 배포 (스크립트 없이)
```bash
cd ~/monitoring
docker-compose down
docker-compose up -d
```

## 📊 접속 정보

### 직접 포트 접속
- Prometheus: http://54.180.215.226:9090
- Grafana: http://54.180.215.226:3000

### HTTPS 서브패스 접속 (Nginx 설정 필요)
- Grafana: https://rhythmeet-be.yeonjae.kr/grafana/
- Prometheus: https://rhythmeet-be.yeonjae.kr/prometheus/

### Grafana 로그인
- Username: `admin`
- Password: `admin123`

## 🔧 설정 확인

### Spring Boot 메트릭 확인
```bash
# Prometheus 타겟 상태 확인
curl http://localhost:9090/api/v1/targets

# Spring Boot 메트릭 직접 확인
curl http://localhost:8081/actuator/prometheus
```

### 컨테이너 로그 확인
```bash
docker logs jandi-prometheus-deploy
docker logs jandi-grafana-deploy
```

## 📁 파일 구조

```
monitoring-deploy/
├── docker-compose.yml          # 메인 컴포즈 파일
├── config/
│   ├── prometheus/
│   │   └── prometheus.yml      # Prometheus 설정
│   └── grafana/
│       ├── provisioning/       # 자동 설정
│       └── dashboards/         # 대시보드 파일
├── scripts/
│   └── deploy.sh              # 배포 스크립트
└── README.md                  # 이 파일
```

## 🛠️ 트러블슈팅

### 네트워크 문제
```bash
# 네트워크 확인
docker network ls
docker network inspect spring-app_spring-network
```

### 컨테이너 재시작
```bash
docker-compose restart prometheus
docker-compose restart grafana
``` 