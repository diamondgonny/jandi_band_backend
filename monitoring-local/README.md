# 로컬 개발환경 모니터링 (Windows)

Windows 개발환경에서 Prometheus + Grafana를 사용한 모니터링 설정입니다.

## 🏗️ 구성요소

- **Prometheus**: 메트릭 수집 및 저장
- **Grafana**: 데이터 시각화 및 대시보드

## 📋 사전 요구사항

- Windows 10/11
- Docker Desktop 설치 및 실행 중
- Spring Boot 애플리케이션 (포트 8080 또는 8081)
- Spring Boot Actuator 의존성 추가

## 🚀 시작 방법

### 1. 자동 시작 (추천)
```cmd
# monitoring-local 폴더에서 실행
start-local.bat
```

### 2. 수동 시작
```cmd
# PowerShell 또는 CMD에서 실행
cd monitoring-local
docker-compose -f docker-compose.local.yml up -d
```

### 3. 중지
```cmd
docker-compose -f docker-compose.local.yml down
```

## 📊 접속 정보

- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000

### Grafana 로그인
- Username: `admin`
- Password: `admin123`

## 🔧 Spring Boot 설정

### 1. Actuator 의존성 추가
```gradle
// build.gradle
implementation 'org.springframework.boot:spring-boot-starter-actuator'
implementation 'io.micrometer:micrometer-registry-prometheus'
```

### 2. application.yml 설정
```yaml
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

### 3. 메트릭 확인
- 애플리케이션 실행 후 http://localhost:8080/actuator/prometheus 접속
- 메트릭 데이터가 표시되면 정상

## 🔍 모니터링 확인

### Prometheus 타겟 확인
1. http://localhost:9090 접속
2. Status > Targets 메뉴 클릭
3. `jandi-band-backend-local` 타겟이 UP 상태인지 확인

### Grafana 대시보드
1. http://localhost:3000 접속
2. 로그인 (admin/admin123)
3. Dashboards > Browse 에서 대시보드 확인

## 📁 파일 구조

```
monitoring-local/
├── docker-compose.local.yml    # 로컬용 컴포즈 파일
├── config/
│   ├── prometheus/
│   │   └── prometheus.local.yml # 로컬 Prometheus 설정
│   └── grafana/
│       ├── provisioning/        # 자동 설정
│       └── dashboards/          # 대시보드 파일
├── scripts/
│   └── start-local.bat         # Windows 시작 스크립트
└── README.md                   # 이 파일
```

## 🛠️ 트러블슈팅

### Spring Boot 연결 안됨
```cmd
# Spring Boot가 실행중인지 확인
curl http://localhost:8080/actuator/health

# 다른 포트에서 실행중인 경우 prometheus.local.yml 수정
# targets: ['host.docker.internal:8081'] 
```

### Docker Desktop 문제
- Docker Desktop이 실행중인지 확인
- WSL2 백엔드 사용 권장

### 포트 충돌
- 기존에 실행중인 서비스가 있는지 확인
- 필요시 docker-compose.local.yml에서 포트 변경

## 💡 팁

- 개발 중에는 `docker-compose logs -f` 로 실시간 로그 확인
- Grafana에서 쿼리 연습: `up`, `jvm_memory_used_bytes` 등
- Prometheus 웹UI에서 PromQL 쿼리 테스트 가능 