# Jandi Band Backend

## 환경 설정

### application.properties 설정

프로젝트를 실행하기 전에 `src/main/resources` 디렉토리에 `application.properties` 파일을 생성해야 합니다.

1. `application.properties.example` 파일을 `application.properties`로 복사합니다.
2. 아래 값들을 본인의 환경에 맞게 수정합니다:
   - `{DB_HOST}`: 데이터베이스 호스트 주소 (예: `localhost` 또는 `database-1.cpmmkkgy0tui.ap-northeast-2.rds.amazonaws.com`)
   - `{DB_PORT}`: 데이터베이스 포트 (기본값: `3306`)
   - `{DB_NAME}`: 데이터베이스 이름 (예: `rhythmeet`)
   - `{DB_USERNAME}`: 데이터베이스 사용자 이름
   - `{DB_PASSWORD}`: 데이터베이스 비밀번호

예시:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/rhythmeet
spring.datasource.username=root
spring.datasource.password=mypassword
```

> **주의**: `application.properties` 파일은 개인 정보를 포함하므로 Git에 커밋하지 마세요. 이 파일은 이미 `.gitignore`에 등록되어 있습니다.
