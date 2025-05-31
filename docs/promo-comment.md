# Promo Comment API 명세서

## Base URL
`/api/promos`

## 인증
댓글 생성, 수정, 삭제는 JWT 인증 필요 (Spring Security + @AuthenticationPrincipal CustomUserDetails). 조회는 인증 불필요.

## 페이지네이션 응답 구조
댓글 목록 조회 API는 다음과 같은 페이지네이션 구조를 사용합니다:

```json
{
  "success": true,
  "message": "응답 메시지",
  "data": {
    "content": [...],  // 실제 댓글 데이터 배열
    "pageInfo": {
      "page": 0,           // 현재 페이지 번호 (0부터 시작)
      "size": 20,          // 페이지 크기
      "totalElements": 100, // 총 댓글 수
      "totalPages": 5,     // 총 페이지 수
      "first": true,       // 첫 번째 페이지 여부
      "last": false,       // 마지막 페이지 여부
      "empty": false       // 비어있는 페이지 여부
    }
  }
}
```

---

## 1. 공연 홍보 댓글 목록 조회
### GET `/api/promos/{promoId}/comments`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/promos/1/comments?page=0&size=20&sort=createdAt,desc"
```

#### 쿼리 파라미터
- `page` (integer): 페이지 번호 (기본값: 0)
- `size` (integer): 페이지 크기 (기본값: 20)
- `sort` (string): 정렬 기준 (기본값: "createdAt,desc")

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "공연 홍보 댓글 목록을 조회했습니다.",
  "data": {
    "content": [
      {
        "id": 1,
        "promoId": 1,
        "description": "정말 기대되는 공연이네요!",
        "creatorId": 1,
        "creatorName": "홍길동",
        "creatorProfilePhoto": "https://example.com/profile.jpg",
        "likeCount": 5,
        "isLikedByUser": true,
        "createdAt": "2024-03-15T10:30:00",
        "updatedAt": "2024-03-15T10:30:00"
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

## 2. 공연 홍보 댓글 생성
### POST `/api/promos/{promoId}/comments`

#### 요청
```bash
curl -X POST "http://localhost:8080/api/promos/1/comments" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "description": "정말 기대되는 공연이네요!"
  }'
```

#### 요청 필드
- `description` (string, 필수): 댓글 내용

#### 응답 (201 Created)
```json
{
  "success": true,
  "message": "공연 홍보 댓글이 성공적으로 생성되었습니다.",
  "data": {
    "id": 1,
    "promoId": 1,
    "description": "정말 기대되는 공연이네요!",
    "creatorId": 1,
    "creatorName": "홍길동",
    "creatorProfilePhoto": "https://example.com/profile.jpg",
    "likeCount": 0,
    "isLikedByUser": null,
    "createdAt": "2024-03-15T10:30:00",
    "updatedAt": "2024-03-15T10:30:00"
  }
}
```

---

## 3. 공연 홍보 댓글 수정
### PATCH `/api/promos/comments/{commentId}`

#### 요청
```bash
curl -X PATCH "http://localhost:8080/api/promos/comments/1" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "description": "수정된 댓글 내용입니다."
  }'
```

#### 요청 필드
- `description` (string, 필수): 수정할 댓글 내용

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "공연 홍보 댓글이 성공적으로 수정되었습니다.",
  "data": {
    "id": 1,
    "promoId": 1,
    "description": "수정된 댓글 내용입니다.",
    "creatorId": 1,
    "creatorName": "홍길동",
    "creatorProfilePhoto": "https://example.com/profile.jpg",
    "likeCount": 5,
    "isLikedByUser": true,
    "createdAt": "2024-03-15T10:30:00",
    "updatedAt": "2024-03-15T11:00:00"
  }
}
```

---

## 4. 공연 홍보 댓글 삭제
### DELETE `/api/promos/comments/{commentId}`

#### 요청
```bash
curl -X DELETE "http://localhost:8080/api/promos/comments/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "공연 홍보 댓글이 성공적으로 삭제되었습니다.",
  "data": null
}
```

---

## 5. 공연 홍보 댓글 좋아요 추가/취소
### POST `/api/promos/comments/{commentId}/like`

#### 요청
```bash
curl -X POST "http://localhost:8080/api/promos/comments/1/like" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (200 OK) - 좋아요 추가 시
```json
{
  "success": true,
  "message": "댓글 좋아요가 추가되었습니다.",
  "data": "liked"
}
```

#### 응답 (200 OK) - 좋아요 취소 시
```json
{
  "success": true,
  "message": "댓글 좋아요가 취소되었습니다.",
  "data": "unliked"
}
```

---

## 6. 공연 홍보 댓글 좋아요 상태 확인
### GET `/api/promos/comments/{commentId}/like/status`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/promos/comments/1/like/status" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "댓글 좋아요 상태 조회 성공",
  "data": true
}
```

#### 응답 필드
- `data` (boolean): 사용자의 좋아요 상태 (true: 좋아요 누름, false: 좋아요 안 누름)

---

## 7. 공연 홍보 댓글 좋아요 수 조회
### GET `/api/promos/comments/{commentId}/like/count`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/promos/comments/1/like/count"
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "댓글 좋아요 수 조회 성공",
  "data": 15
}
```

#### 응답 필드
- `data` (integer): 댓글의 총 좋아요 수

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
- `201 Created`: 생성 성공
- `400 Bad Request`: 잘못된 요청
- `401 Unauthorized`: 인증 실패
- `403 Forbidden`: 권한 없음
- `404 Not Found`: 리소스 없음

### 주요 에러 케이스
- **공연 홍보 없음**: `ResourceNotFoundException` - 공연 홍보를 찾을 수 없습니다
- **댓글 없음**: `ResourceNotFoundException` - 댓글을 찾을 수 없습니다
- **권한 없음**: `IllegalStateException` - 댓글을 수정/삭제할 권한이 없습니다
- **사용자 없음**: `ResourceNotFoundException` - 사용자를 찾을 수 없습니다

## 참고사항
- **권한**: 댓글 생성은 모든 인증된 사용자, 수정/삭제는 댓글 작성자만 가능
- **소프트 삭제**: 실제 삭제가 아닌 deletedAt 설정
- **페이지네이션**: 기본 크기 20개, 정렬 기본값은 생성 시간 내림차순 (`createdAt,desc`)
- **정렬 옵션**: `sort` 파라미터로 정렬 기준과 방향 지정 가능 (예: `createdAt,asc`, `likeCount,desc`)
- **자동 계산**: 댓글 생성/삭제 시 공연 홍보의 commentCount 자동 업데이트
- **프로필 사진**: 댓글 작성자의 현재 프로필 사진 URL 포함 (없으면 null)
- **좋아요**: 토글 방식으로 동작 (같은 API로 추가/취소), 중복 좋아요 방지
- **좋아요 상태**: 댓글 목록 조회 시 `isLikedByUser` 필드로 현재 사용자의 좋아요 상태 포함 (true: 좋아요 누름, false: 좋아요 안 누름, null: 인증되지 않은 사용자)
- **응답 구조**: 실제 `PromoCommentRespDTO` 구조 반영, `isLikedByUser` 필드 포함 