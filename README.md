# 잔디밴드 백엔드

## 🚀 프로젝트 구조

```
jandi_band_backend/
├── src/                    # Spring Boot 소스코드
├── monitoring-local/       # 로컬 개발환경 모니터링
├── monitoring-deploy/      # 운영환경 모니터링 (Ubuntu EC2)
├── docs/                   # 프로젝트 문서
├── build.gradle           # Gradle 빌드 설정
├── Dockerfile             # Docker 이미지 빌드
└── README.md              # 이 파일
```

## 📊 모니터링 설정

### 🏠 로컬 개발환경 (Windows)
- **위치**: `monitoring-local/`
- **용도**: Windows 개발환경에서의 Spring Boot 애플리케이션 모니터링
- **구성**: Prometheus + Grafana
- **시작방법**: `monitoring-local/start-local.bat` 실행

### 🚀 운영환경 (Ubuntu EC2)
- **위치**: `monitoring-deploy/`
- **용도**: Ubuntu EC2 서버에서의 프로덕션 모니터링
- **구성**: Prometheus + Grafana
- **배포방법**: `monitoring-deploy/scripts/deploy.sh` 실행

### 📈 모니터링 기능
- Spring Boot 애플리케이션 메트릭
- JVM 메모리 사용량
- CPU 사용률
- HTTP 요청 통계
- 애플리케이션 상태 모니터링

## 🔧 개발 환경 설정

### Spring Boot Actuator 설정
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

### 의존성 추가
```gradle
// build.gradle
implementation 'org.springframework.boot:spring-boot-starter-actuator'
implementation 'io.micrometer:micrometer-registry-prometheus'
```

## 📚 상세 가이드

- [로컬 모니터링 가이드](monitoring-local/README.md)
- [운영 모니터링 가이드](monitoring-deploy/README.md)

## 🌐 접속 정보

### 로컬 환경
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000

### 운영 환경
- Prometheus: http://54.180.215.226:9090
- Grafana: http://54.180.215.226:3000
- HTTPS Grafana: https://rhythmeet-be.yeonjae.kr/grafana/

## 환경 설정

### application.properties 설정

프로젝트를 실행하기 전에 `src/main/resources` 디렉토리에 `application.properties` 파일을 생성해야 합니다.

1. `application.properties.example` 파일을 `application.properties`로 복사합니다.
2. 아래 값들을 본인의 환경에 맞게 수정합니다:
   - `{DB_HOST}`: 데이터베이스 호스트 주소
   - `{DB_PORT}`: 데이터베이스 포트
   - `{DB_NAME}`: 데이터베이스 이름
   - `{DB_USERNAME}`: 데이터베이스 사용자 이름
   - `{DB_PASSWORD}`: 데이터베이스 비밀번호
   - `{AWS_ACCESS_KEY}`: AWS 액세스 키
   - `{AWS_SECRET_KEY}`: AWS 시크릿 키
   - `{AWS_S3_BUCKET}`: S3 버킷 이름

> **주의**: `application.properties` 파일은 개인 정보를 포함하므로 Git에 커밋하지 마세요. 이 파일은 이미 `.gitignore`에 등록되어 있습니다.