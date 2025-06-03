# Jandi Band Backend - 로컬 모니터링 가이드

## 개요

이 가이드는 Jandi Band Backend 프로젝트의 로컬 개발 환경에서 Prometheus와 Grafana를 사용한 모니터링 시스템을 구현하는 방법을 설명합니다.

## 아키텍처

```
Spring Boot App → Micrometer → Prometheus → Grafana
     ↓              ↓           ↓          ↓
   메트릭 생성    메트릭 수집   데이터 저장   시각화
```

## 실행 방법

### 1. 사전 준비
- Docker 및 Docker Compose 설치 필요
- Java 21 및 Gradle 설치 필요

### 2. 애플리케이션 빌드 및 실행
```bash
# 프로젝트 빌드
./gradlew build

# Spring Boot 애플리케이션 실행
./gradlew bootRun
```

### 3. 모니터링 스택 실행
```bash
# 자동 시작 스크립트 사용 (권장)
./start-monitoring.sh

# 또는 직접 Docker Compose 실행
docker-compose -f docker-compose.monitoring.yml up -d

# 로그 확인
docker-compose -f docker-compose.monitoring.yml logs -f
```

### 4. 접속 URL
- **Spring Boot App**: http://localhost:8080
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000
  - 사용자명: `admin`
  - 비밀번호: `admin123`
- **Alertmanager**: http://localhost:9093

## 모니터링 엔드포인트

### Spring Boot Actuator 엔드포인트
- `/actuator/health` - 애플리케이션 헬스체크
- `/actuator/prometheus` - Prometheus 메트릭
- `/actuator/metrics` - 상세 메트릭 정보

### 커스텀 헬스체크 엔드포인트
- `/health` - 개선된 헬스체크 (커스텀 메트릭 포함)

## 수집되는 메트릭

### 1. 애플리케이션 메트릭
- `http_server_requests_seconds` - HTTP 요청 처리 시간
- `http_server_requests_seconds_count` - 총 HTTP 요청 수
- Spring Boot Actuator에서 제공하는 기본 메트릭

### 2. JVM 메트릭 (자동 수집)
- `jvm_memory_used_bytes` - JVM 메모리 사용량
- `jvm_memory_max_bytes` - JVM 최대 메모리
- `jvm_gc_collection_seconds` - GC 수행 시간
- `jvm_threads_current` - 현재 스레드 수

### 3. 시스템 메트릭
- `system_cpu_usage` - CPU 사용률
- `hikaricp_connections_active` - 데이터베이스 연결 수
- `hikaricp_connections_max` - 최대 데이터베이스 연결 수

### 4. 캘린더 API 메트릭
- 캘린더 API 응답 시간 추적
- 인증 실패율 (401 에러) 모니터링

## Grafana 대시보드

### 기본 제공 대시보드
1. **HTTP 요청 메트릭** - 응답 시간, 요청 수, 에러율
2. **JVM 메모리 사용량** - 힙/논힙 메모리 사용량
3. **데이터베이스 연결 상태** - 활성 DB 연결 수
4. **시스템 리소스** - CPU, 메모리 사용률
5. **캘린더 API 성능** - 캘린더 전용 메트릭

## 설정 상세

### Prometheus 설정 (`monitoring/prometheus/prometheus.yml`)
```yaml
scrape_configs:
  - job_name: 'jandi-band-backend'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['host.docker.internal:8080']
```

### Spring Boot 설정 (application.properties에 추가 필요)
```properties
# Actuator 엔드포인트 활성화
management.endpoints.web.exposure.include=health,info,prometheus,metrics
management.endpoint.prometheus.enabled=true

# Prometheus 메트릭 활성화
management.metrics.export.prometheus.enabled=true
management.metrics.tags.application=${spring.application.name}
```

## 알림 설정

### 포함된 알림 규칙
- **애플리케이션 다운**: 1분 이상 응답 없음
- **높은 응답 시간**: 95th 백분위수가 1초 초과 (5분간)
- **높은 에러율**: 5% 초과 (2분간)
- **높은 메모리 사용률**: JVM 메모리 85% 초과 (5분간)
- **캘린더 API 문제**: 응답 시간 2초 초과 (3분간)
- **인증 실패**: 분당 5건 이상 401 에러

## 테스트 방법

### 1. 메트릭 수집 확인
```bash
# Prometheus 메트릭 엔드포인트 확인
curl http://localhost:8080/actuator/prometheus

# 커스텀 헬스체크 확인
curl http://localhost:8080/health
```

### 2. API 부하 테스트
```bash
# 여러 번 호출하여 메트릭 변화 확인
for i in {1..10}; do curl http://localhost:8080/health; done
```

### 3. 캘린더 API 테스트
```bash
# 캘린더 API 호출 (인증 토큰 필요)
curl -H "Authorization: Bearer YOUR_TOKEN" \
     "http://localhost:8080/api/clubs/1/calendar?year=2024&month=3"
```

## PromQL 쿼리 예제

### 기본 쿼리
```promql
# HTTP 요청 비율 (5분 평균)
rate(http_server_requests_seconds_count[5m])

# 응답 시간 95th 백분위수
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))

# 에러율
rate(http_server_requests_seconds_count{status=~"5.."}[5m]) / 
rate(http_server_requests_seconds_count[5m]) * 100

# JVM 메모리 사용률
jvm_memory_used_bytes / jvm_memory_max_bytes * 100
```

### 캘린더 API 전용 쿼리
```promql
# 캘린더 API 응답 시간
histogram_quantile(0.95, 
  rate(http_server_requests_seconds_bucket{uri=~".*/calendar.*"}[5m]))

# 인증 실패율
rate(http_server_requests_seconds_count{status="401"}[5m])
```

## 트러블슈팅

### 자주 발생하는 문제

#### 1. Prometheus가 메트릭을 수집하지 못하는 경우
```bash
# 애플리케이션이 실행 중인지 확인
curl http://localhost:8080/actuator/prometheus

# Docker 네트워크 확인
docker network ls
docker network inspect jandi_band_backend_monitoring
```

#### 2. Grafana 대시보드가 로드되지 않는 경우
```bash
# Grafana 로그 확인
docker logs jandi-grafana

# Grafana 컨테이너 재시작
docker restart jandi-grafana
```

#### 3. 알림이 발생하지 않는 경우
- Prometheus에서 타겟 상태 확인: http://localhost:9090/targets
- Alertmanager 상태 확인: http://localhost:9093
- 알림 규칙 확인: http://localhost:9090/rules

## 유용한 명령어

```bash
# 모니터링 스택 중지
docker-compose -f docker-compose.monitoring.yml down

# 볼륨 포함 완전 삭제
docker-compose -f docker-compose.monitoring.yml down -v

# 컨테이너 상태 확인
docker-compose -f docker-compose.monitoring.yml ps

# 실시간 로그 확인
docker-compose -f docker-compose.monitoring.yml logs -f

# 특정 서비스 재시작
docker-compose -f docker-compose.monitoring.yml restart grafana
```

## 다음 단계

1. **커스텀 메트릭 추가**: 비즈니스 로직 관련 메트릭 구현
2. **대시보드 커스터마이징**: 프로젝트 특화 대시보드 생성
3. **알림 채널 설정**: 이메일, Slack 등 실제 알림 채널 구성
4. **성능 최적화**: 메트릭 기반 애플리케이션 성능 개선 