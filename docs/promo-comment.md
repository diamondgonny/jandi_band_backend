# Promo Comment API

## 공연 홍보 댓글
JWT 인증 필요

---

## 1. 댓글 목록 조회
```
GET /api/promos/{promoId}/comments?page=0&size=20&sort=createdAt,desc
```

### 요청 예시
```bash
curl "http://localhost:8080/api/promos/1/comments?page=0&size=20&sort=createdAt,desc"
```

### 쿼리 파라미터
- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 20)
- `sort`: 정렬 (기본값: createdAt,desc)

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "공연 홍보 댓글 목록을 조회했습니다.",
  "data": {
    "content": [
      {
        "id": 1,
        "promoId": 1,
        "content": "정말 기대되는 공연이네요!",
        "authorId": 2,
        "authorName": "김철수",
        "authorProfilePhoto": "https://example.com/profile.jpg",
        "likeCount": 5,
        "isLikedByUser": false,
        "createdAt": "2024-03-15T14:30:00",
        "updatedAt": "2024-03-15T14:30:00"
      }
    ],
    "pageInfo": {
      "page": 0,
      "size": 20,
      "totalElements": 1,
      "totalPages": 1,
      "first": true,
      "last": true,
      "empty": false
    }
  }
}
```

---

## 2. 댓글 생성
```
POST /api/promos/{promoId}/comments
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

### 요청 예시
```bash
curl -X POST "http://localhost:8080/api/promos/1/comments" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "정말 기대되는 공연이네요!"
  }'
```

### 요청 필드
- `content`: 댓글 내용 (필수)

### 성공 응답 (201)
```json
{
  "success": true,
  "message": "공연 홍보 댓글이 성공적으로 생성되었습니다.",
  "data": {
    "id": 1,
    "promoId": 1,
    "content": "정말 기대되는 공연이네요!",
    "authorId": 2,
    "authorName": "김철수",
    "authorProfilePhoto": "https://example.com/profile.jpg",
    "likeCount": 0,
    "isLikedByUser": false,
    "createdAt": "2024-03-15T14:30:00",
    "updatedAt": "2024-03-15T14:30:00"
  }
}
```

---

## 3. 댓글 수정
```
PATCH /api/promos/comments/{commentId}
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

### 요청 예시
```bash
curl -X PATCH "http://localhost:8080/api/promos/comments/1" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "수정된 댓글 내용입니다."
  }'
```

### 요청 필드
- `content`: 수정할 댓글 내용 (필수)

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "공연 홍보 댓글이 성공적으로 수정되었습니다.",
  "data": {
    "id": 1,
    "promoId": 1,
    "content": "수정된 댓글 내용입니다.",
    "authorId": 2,
    "authorName": "김철수",
    "authorProfilePhoto": "https://example.com/profile.jpg",
    "likeCount": 5,
    "isLikedByUser": false,
    "createdAt": "2024-03-15T14:30:00",
    "updatedAt": "2024-03-15T15:00:00"
  }
}
```

### 실패 응답
- **403**: 댓글 작성자가 아님

---

## 4. 댓글 삭제
```
DELETE /api/promos/comments/{commentId}
Authorization: Bearer {JWT_TOKEN}
```

### 요청 예시
```bash
curl -X DELETE "http://localhost:8080/api/promos/comments/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "공연 홍보 댓글이 성공적으로 삭제되었습니다.",
  "data": null
}
```

### 실패 응답
- **403**: 댓글 작성자가 아님
- **404**: 존재하지 않는 댓글

---

## 5. 댓글 좋아요 추가/취소
```
POST /api/promos/comments/{commentId}/like
Authorization: Bearer {JWT_TOKEN}
```

### 요청 예시
```bash
curl -X POST "http://localhost:8080/api/promos/comments/1/like" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "댓글 좋아요가 추가되었습니다.",
  "data": "liked"
}
```

### 좋아요 취소 시
```json
{
  "success": true,
  "message": "댓글 좋아요가 취소되었습니다.",
  "data": "unliked"
}
```

---

## 6. 댓글 좋아요 상태 확인
```
GET /api/promos/comments/{commentId}/like/status
Authorization: Bearer {JWT_TOKEN}
```

### 요청 예시
```bash
curl "http://localhost:8080/api/promos/comments/1/like/status" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "댓글 좋아요 상태 조회 성공",
  "data": true
}
```

### 응답 필드
- `data`: 좋아요 여부 (true/false)

---

## 7. 댓글 좋아요 수 조회
```
GET /api/promos/comments/{commentId}/like/count
```

### 요청 예시
```bash
curl "http://localhost:8080/api/promos/comments/1/like/count"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "댓글 좋아요 수 조회 성공",
  "data": 5
}
```

### 응답 필드
- `data`: 총 좋아요 수

---

## 정렬 옵션
- `createdAt`: 생성일 기준 (기본값)
- `likeCount`: 좋아요 수 기준

### 정렬 방향
- `asc`: 오름차순
- `desc`: 내림차순 (기본값)

## 에러 응답
```