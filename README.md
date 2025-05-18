# Jandi Band Backend

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

## API 명세서

### 이미지 업로드 API

AWS S3를 사용하여 이미지를 업로드하고 관리하는 API입니다.

#### 1. 이미지 업로드

이미지 파일을 S3 버킷에 업로드합니다.

- **URL**: `/api/v1/images/upload`
- **Method**: `POST`
- **Content-Type**: `multipart/form-data`

**Request Parameters:**
| 파라미터 | 타입 | 필수 여부 | 설명 |
|---------|------|-----------|------|
| file | File | Yes | 업로드할 이미지 파일 (최대 10MB) |
| dirName | String | Yes | S3 버킷 내 저장할 디렉토리 이름 (예: "profile", "post") |

**Response:**
```json
{
    "status": 200,
    "data": "https://{bucket-name}.s3.ap-northeast-2.amazonaws.com/{dirName}/{fileName}"
}
```

**Error Response:**
```json
{
    "status": 400,
    "message": "잘못된 형식의 파일입니다."
}
```

#### 2. 이미지 삭제

S3 버킷에서 이미지를 삭제합니다.

- **URL**: `/api/v1/images`
- **Method**: `DELETE`
- **Content-Type**: `application/x-www-form-urlencoded`

**Request Parameters:**
| 파라미터 | 타입 | 필수 여부 | 설명 |
|---------|------|-----------|------|
| fileUrl | String | Yes | 삭제할 이미지의 전체 URL |

**Response:**
```json
{
    "status": 200
}
```

**Error Response:**
```json
{
    "status": 400,
    "message": "이미지를 찾을 수 없습니다."
}
```

### 제한사항

1. 파일 크기 제한
   - 최대 파일 크기: 10MB
   - 지원하는 이미지 형식: JPG, JPEG, PNG, GIF

2. 디렉토리 구조
   - 모든 이미지는 지정된 디렉토리(`dirName`) 아래에 저장됩니다.
   - 파일명은 UUID를 사용하여 고유성을 보장합니다.

3. 보안
   - 업로드된 이미지는 public-read 권한으로 설정됩니다.
   - AWS 자격 증명은 환경 변수를 통해 관리됩니다.

### 사용 예시

#### cURL을 사용한 이미지 업로드
```bash
curl -X POST http://localhost:8080/api/v1/images/upload \
  -F "file=@profile.jpg" \
  -F "dirName=profile"
```

#### cURL을 사용한 이미지 삭제
```bash
curl -X DELETE "http://localhost:8080/api/v1/images?fileUrl=https://{bucket-name}.s3.ap-northeast-2.amazonaws.com/profile/{fileName}"
```
