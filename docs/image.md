# Image API 명세서

## Base URL
`/api/images`

## 인증
JWT 인증 필요 + 관리자(ADMIN) 권한 필요

---

## 1. 이미지 업로드 (관리자 전용)
### POST `/api/images/upload`

#### Content-Type
`multipart/form-data`

#### 요청
```bash
curl -X POST "http://localhost:8080/api/images/upload" \
  -H "Authorization: Bearer {ADMIN_JWT_TOKEN}" \
  -F "file=@/path/to/image.jpg" \
  -F "dirName=profiles"
```

#### 요청 파라미터
- `file` (file, 필수): 업로드할 이미지 파일
- `dirName` (string, 필수): 저장할 디렉토리 이름

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "이미지 업로드 성공",
  "data": "https://bucket-name.s3.amazonaws.com/profiles/uuid-filename.jpg"
}
```

#### 디렉토리 이름 예시
- `profiles`: 프로필 사진
- `clubs`: 동아리 대표 사진
- `promos`: 공연 홍보 이미지

---

## 2. 이미지 삭제 (관리자 전용)
### DELETE `/api/images`

#### 요청
```bash
curl -X DELETE "http://localhost:8080/api/images?fileUrl=https://bucket-name.s3.amazonaws.com/profiles/uuid-filename.jpg" \
  -H "Authorization: Bearer {ADMIN_JWT_TOKEN}"
```

#### 쿼리 파라미터
- `fileUrl` (string, 필수): 삭제할 이미지의 전체 URL

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "이미지 삭제 성공",
  "data": null
}
```

---

## 에러 응답
```json
{
  "success": false,
  "message": "에러 메시지",
  "data": null
}
```

### HTTP 상태 코드
- `200 OK`: 성공
- `400 Bad Request`: 잘못된 요청 (파일 형식 오류 등)
- `401 Unauthorized`: 인증 실패
- `403 Forbidden`: 권한 없음 (관리자가 아님)
- `500 Internal Server Error`: 서버 오류

### 주요 에러 케이스
- **인증 실패**: JWT 토큰이 없거나 유효하지 않음
- **권한 없음**: 관리자(ADMIN) 권한이 없음
- **파일 형식 오류**: 지원하지 않는 파일 형식

## 참고사항
- **관리자 전용**: ADMIN 권한을 가진 사용자만 사용 가능
- **파일 형식**: JPG, PNG 등 일반적인 이미지 형식 지원
- **파일 크기**: 서버 설정에 따른 제한
- **S3 업로드**: AWS S3에 이미지 저장
- **UUID 파일명**: 업로드 시 고유한 파일명으로 변환
- **일반 사용자 이미지 업로드**: 다른 API(공연 홍보 생성, 프로필 사진 업로드 등)에서는 일반 사용자도 이미지 첨부 가능 (내부적으로 S3Service 직접 사용) 