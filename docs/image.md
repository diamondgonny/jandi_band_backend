# Image API

## 이미지 업로드
JWT 인증 필요

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
  -F "image=@/path/to/image.jpg"
```

### 요청 필드
- `image`: 업로드할 이미지 파일

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "이미지 업로드 성공",
  "data": "https://example.com/images/uploaded-image.jpg"
}
```

### 실패 응답
- **400**: 지원하지 않는 파일 형식
- **413**: 파일 크기 초과

---

## 2. 이미지 삭제
```
DELETE /api/images?imageUrl={IMAGE_URL}
Authorization: Bearer {JWT_TOKEN}
```

### 요청 예시
```bash
curl -X DELETE "http://localhost:8080/api/images?imageUrl=https://example.com/images/image.jpg" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 쿼리 파라미터
- `imageUrl`: 삭제할 이미지 URL

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "이미지 삭제 성공",
  "data": null
}
```

### 실패 응답
- **404**: 존재하지 않는 이미지
- **403**: 삭제 권한 없음

---

## 지원 형식
- JPG, JPEG, PNG, GIF, WebP
- 최대 크기: 10MB
- 용도: 프로필 사진, 동아리 사진, 홍보글 이미지 