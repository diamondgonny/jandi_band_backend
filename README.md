# 잔디밴드 백엔드

## 🚀 프로젝트 구조

```
jandi_band_backend/
├── src/                    # Spring Boot 소스코드
│   └── main/java/com/jandi/band_backend/
│       ├── auth/           # 사용자 인증 및 권한 관리
│       ├── club/           # 동아리 관리
│       ├── config/         # 애플리케이션 설정
│       ├── global/         # 전역 설정 및 공통 기능
│       ├── health/         # 헬스체크
│       ├── image/          # 이미지 관리
│       ├── invite/         # 초대 관리
│       ├── manage/         # 관리 기능
│       ├── poll/           # 투표 기능
│       ├── promo/          # 프로모션 관리
│       ├── search/         # 검색 기능 (Elasticsearch)
│       ├── security/       # 보안 설정
│       ├── team/           # 팀 관리
│       ├── univ/           # 대학교 관리
│       └── user/           # 사용자 관리
├── search/                 # 엘라스틱서치 관련 파일
│   ├── docker-compose.elasticsearch.yml
│   └── start-elasticsearch.sh
├── monitoring-local/       # 로컬 개발환경 모니터링 (Prometheus + Grafana)
├── monitoring-deploy/      # 운영환경 모니터링 (Ubuntu EC2)
├── docs/                   # 프로젝트 문서
├── build.gradle           # Gradle 빌드 설정
├── Dockerfile             # Docker 이미지 빌드
└── README.md              # 이 파일
```

## 📚 상세 가이드

- **검색 기능**: [search/README.md](search/README.md)
- **로컬 모니터링**: [monitoring-local/README.md](monitoring-local/README.md)
- **운영 모니터링**: [monitoring-deploy/README.md](monitoring-deploy/README.md)

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