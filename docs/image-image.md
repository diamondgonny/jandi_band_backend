# Image API

## 이미지 관리
JWT 인증 + ADMIN 권한 필요

---

## 1. 이미지 업로드
```
POST /api/images/upload
Authorization: Bearer {JWT_TOKEN}
Content-Type: multipart/form-data
```

### 요청 예시
```bash
curl -X POST "http://localhost:8080/api/images/upload" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -F "file=@/path/to/image.jpg" \
  -F "dirName=profiles"
```

### 요청 필드
- `file`: 업로드할 이미지 파일
- `dirName`: 저장할 디렉토리 이름

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "이미지 업로드 성공",
  "data": "https://s3.amazonaws.com/bucket/profiles/image-uuid.jpg"
}
```

### 응답 필드
- `data`: 업로드된 이미지의 URL

### 실패 응답
- **401**: 인증 실패
- **403**: ADMIN 권한 없음
- **400**: 지원하지 않는 파일 형식
- **413**: 파일 크기 초과

---

## 2. 이미지 삭제
```
DELETE /api/images?fileUrl={IMAGE_URL}
Authorization: Bearer {JWT_TOKEN}
```

### 요청 예시
```bash
curl -X DELETE "http://localhost:8080/api/images?fileUrl=https://s3.amazonaws.com/bucket/profiles/image-uuid.jpg" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 쿼리 파라미터
- `fileUrl`: 삭제할 이미지 URL

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "이미지 삭제 성공",
  "data": null
}
```

### 실패 응답
- **401**: 인증 실패
- **403**: ADMIN 권한 없음
- **404**: 존재하지 않는 이미지

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
- `400 Bad Request`: 잘못된 요청
- `401 Unauthorized`: 인증 실패
- `403 Forbidden`: 권한 없음 (ADMIN 아님)
- `404 Not Found`: 리소스 없음
- `413 Payload Too Large`: 파일 크기 초과

## 지원 형식
- JPG, JPEG, PNG, GIF, WebP
- 최대 크기: 10MB
- 용도: 프로필 사진, 동아리 사진, 홍보글 이미지

## 참고사항
- **권한**: ADMIN 권한을 가진 사용자만 접근 가능
- **디렉토리**: dirName 파라미터로 S3 내 저장 경로 지정
- **파일명**: UUID로 자동 생성되어 중복 방지
- **CDN**: S3 + CloudFront를 통한 빠른 이미지 전송 