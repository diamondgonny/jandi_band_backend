# 🔍 Jandi Band Backend - Prometheus & Grafana 모니터링 가이드

## 📖 개요

이 가이드는 Jandi Band Backend 프로젝트에 Prometheus와 Grafana를 사용한 모니터링 시스템을 구현하는 방법을 설명합니다.

## 🏗️ 아키텍처

```
Spring Boot App → Micrometer → Prometheus → Grafana
     ↓              ↓           ↓          ↓
   메트릭 생성    메트릭 수집   데이터 저장   시각화
```

## 🚀 실행 방법

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
# Prometheus와 Grafana 실행
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

## 📊 모니터링 엔드포인트

### Spring Boot Actuator 엔드포인트
- `/actuator/health` - 애플리케이션 헬스체크
- `/actuator/prometheus` - Prometheus 메트릭
- `/actuator/metrics` - 상세 메트릭 정보

### 커스텀 헬스체크 엔드포인트
- `/health` - 개선된 헬스체크 (커스텀 메트릭 포함)
- `/health/metrics` - 상세 서버 메트릭
- `/health/users/increment` - 활성 사용자 수 증가 (테스트용)
- `/health/users/decrement` - 활성 사용자 수 감소 (테스트용)

## 📈 수집되는 메트릭

### 1. 애플리케이션 메트릭
- `jandi_active_users` - 활성 사용자 수
- `jandi_api_calls_total` - 총 API 호출 횟수
- `jandi_business_logic_duration` - 비즈니스 로직 처리 시간
- `jandi_errors_total` - 에러 발생 횟수

### 2. HTTP 메트릭
- `jandi_http_requests_duration` - HTTP 요청 처리 시간
- `jandi_http_requests_total` - 총 HTTP 요청 수
- `jandi_http_errors_total` - HTTP 에러 수

### 3. JVM 메트릭 (자동 수집)
- `jvm_memory_used_bytes` - JVM 메모리 사용량
- `jvm_memory_max_bytes` - JVM 최대 메모리
- `jvm_gc_collection_seconds` - GC 수행 시간
- `jvm_threads_current` - 현재 스레드 수

### 4. 시스템 메트릭 (자동 수집)
- `system_cpu_usage` - CPU 사용률
- `disk_free_bytes` - 디스크 여유 공간
- `hikaricp_connections_active` - 데이터베이스 연결 수

## 🎯 Grafana 대시보드

### 기본 제공 대시보드: "Jandi Band Backend 모니터링"

#### 패널 구성:
1. **활성 사용자 수** (Gauge) - 현재 활성 사용자 수 표시
2. **API 호출 비율** (Time Series) - 초당 API 호출 수
3. **HTTP 응답 시간** (Time Series) - 50th, 95th, 99th 백분위수
4. **JVM 메모리 사용량** (Time Series) - 힙/논힙 메모리 사용량
5. **HTTP 상태 코드별 요청 비율** (Time Series) - 2xx, 4xx, 5xx 별 요청 수
6. **데이터베이스 연결 상태** (Stat) - 활성 DB 연결 수

## 🔧 설정 상세

### Prometheus 설정 (`prometheus.yml`)
```yaml
scrape_configs:
  - job_name: 'jandi-band-backend'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['host.docker.internal:8080']
```

### Spring Boot 설정 (`application.properties`)
```properties
# Actuator 엔드포인트 활성화
management.endpoints.web.exposure.include=health,info,prometheus,metrics
management.endpoint.prometheus.enabled=true

# Prometheus 메트릭 활성화
management.metrics.export.prometheus.enabled=true
management.metrics.tags.application=${spring.application.name}
```

## 🧪 테스트 방법

### 1. 메트릭 수집 확인
```bash
# Prometheus 메트릭 엔드포인트 확인
curl http://localhost:8080/actuator/prometheus

# 커스텀 헬스체크 확인
curl http://localhost:8080/health
```

### 2. 활성 사용자 수 테스트
```bash
# 사용자 수 증가
curl http://localhost:8080/health/users/increment

# 사용자 수 감소
curl http://localhost:8080/health/users/decrement
```

### 3. API 부하 테스트
```bash
# 여러 번 호출하여 메트릭 변화 확인
for i in {1..10}; do curl http://localhost:8080/health; done
```

## 📝 PromQL 쿼리 예제

### 기본 쿼리
```promql
# 활성 사용자 수
jandi_active_users

# API 호출 비율 (5분 평균)
rate(jandi_api_calls_total[5m])

# 응답 시간 95th 백분위수
histogram_quantile(0.95, rate(jandi_http_requests_duration_seconds_bucket[5m]))

# 에러율
rate(jandi_http_errors_total[5m]) / rate(jandi_http_requests_total[5m]) * 100
```

### 고급 쿼리
```promql
# 엔드포인트별 평균 응답 시간
avg(rate(jandi_http_requests_duration_seconds_sum[5m])) by (uri) / 
avg(rate(jandi_http_requests_duration_seconds_count[5m])) by (uri)

# JVM 메모리 사용률
jvm_memory_used_bytes / jvm_memory_max_bytes * 100
```

## 🚨 알림 설정 (고급)

### Grafana 알림 규칙 예제
```yaml
# 응답 시간이 1초를 초과할 때
- alert: HighResponseTime
  expr: histogram_quantile(0.95, rate(jandi_http_requests_duration_seconds_bucket[5m])) > 1
  for: 5m
  labels:
    severity: warning
  annotations:
    summary: "응답 시간이 느립니다"

# 에러율이 5%를 초과할 때
- alert: HighErrorRate
  expr: rate(jandi_http_errors_total[5m]) / rate(jandi_http_requests_total[5m]) * 100 > 5
  for: 2m
  labels:
    severity: critical
  annotations:
    summary: "에러율이 높습니다"
```

## 🛠️ 트러블슈팅

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

# 대시보드 파일 권한 확인
ls -la monitoring/grafana/dashboards/
```

#### 3. 메트릭이 표시되지 않는 경우
- Prometheus에서 타겟 상태 확인: http://localhost:9090/targets
- Grafana에서 데이터 소스 연결 확인
- 시간 범위 설정 확인

## 📚 추가 리소스

### 학습 자료
- [Prometheus 공식 문서](https://prometheus.io/docs/)
- [Grafana 공식 문서](https://grafana.com/docs/)
- [Micrometer 문서](https://micrometer.io/docs)
- [Spring Boot Actuator 가이드](https://spring.io/guides/gs/actuator-service/)

### 유용한 링크
- [PromQL 튜토리얼](https://prometheus.io/docs/prometheus/latest/querying/basics/)
- [Grafana 대시보드 예제](https://grafana.com/grafana/dashboards/)
- [Spring Boot 메트릭 커스터마이징](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.metrics.customizing)

## 🎯 다음 단계

1. **알림 시스템 구축**: Alertmanager 추가하여 이메일/슬랙 알림 설정
2. **로그 수집**: ELK Stack 또는 Loki 추가하여 로그 중앙화
3. **분산 추적**: Jaeger 또는 Zipkin 추가하여 마이크로서비스 추적
4. **성능 최적화**: 수집된 메트릭을 기반으로 애플리케이션 성능 개선

---

## 📞 문의사항

모니터링 시스템 관련 문의사항이 있으시면 언제든지 연락주세요! 