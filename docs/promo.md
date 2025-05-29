# Promo API 명세서

## Base URL
`/api/promos`

## 인증
생성, 수정, 삭제, 이미지 업로드는 JWT 인증 필요 (Spring Security + @AuthenticationPrincipal CustomUserDetails). 조회/검색은 인증 불필요.

## 페이지네이션 응답 구조
모든 목록 조회 API는 다음과 같은 페이지네이션 구조를 사용합니다:

```json
{
  "success": true,
  "message": "응답 메시지",
  "data": {
    "content": [...],  // 실제 데이터 배열
    "pageInfo": {
      "page": 0,           // 현재 페이지 번호 (0부터 시작)
      "size": 20,          // 페이지 크기
      "totalElements": 100, // 총 요소 수
      "totalPages": 5,     // 총 페이지 수
      "first": true,       // 첫 번째 페이지 여부
      "last": false,       // 마지막 페이지 여부
      "empty": false       // 비어있는 페이지 여부
    }
  }
}
```

---

## 1. 공연 홍보 목록 조회
### GET `/api/promos`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/promos?page=0&size=20"
```

#### 쿼리 파라미터
- `page` (integer): 페이지 번호 (기본값: 0)
- `size` (integer): 페이지 크기 (기본값: 20)
- `sort` (string): 정렬 기준

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "공연 홍보 목록 조회 성공",
  "data": {
    "content": [
      {
        "id": 1,
        "teamName": "락밴드 팀",
        "creatorId": 1,
        "creatorName": "홍길동",
        "title": "락밴드 정기공연",
        "admissionFee": 10000,
        "eventDatetime": "2024-03-15T19:00:00",
        "location": "홍대 클럽",
        "address": "서울시 마포구 홍익로 123",
        "description": "락밴드 팀의 정기 공연입니다.",
        "viewCount": 100,
        "commentCount": 5,
        "likeCount": 20,
        "isLikedByUser": true,
        "createdAt": "2024-03-01T10:00:00",
        "updatedAt": "2024-03-01T10:00:00",
        "photoUrls": ["https://example.com/photo1.jpg"]
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

## 2. 공연 홍보 상세 조회
### GET `/api/promos/{promoId}`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/promos/1"
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "공연 홍보 상세 조회 성공",
  "data": {
    "id": 1,
    "teamName": "락밴드 팀",
    "creatorId": 1,
    "creatorName": "홍길동",
    "title": "락밴드 정기공연",
    "admissionFee": 10000,
    "eventDatetime": "2024-03-15T19:00:00",
    "location": "홍대 클럽",
    "address": "서울시 마포구 홍익로 123",
    "description": "락밴드 팀의 정기 공연입니다.",
    "viewCount": 100,
    "commentCount": 5,
    "likeCount": 20,
    "isLikedByUser": true,
    "createdAt": "2024-03-01T10:00:00",
    "updatedAt": "2024-03-01T10:00:00",
    "photoUrls": ["https://example.com/photo1.jpg"]
  }
}
```

#### 응답 필드
- `teamName` (string): 팀명 (모든 팀에 있음)
- `isLikedByUser` (boolean): 현재 사용자의 좋아요 상태 (true: 좋아요 누름, false: 좋아요 안 누름, null: 인증되지 않은 사용자)

---

## 3. 공연 홍보 생성
### POST `/api/promos`

#### 요청
```bash
curl -X POST "http://localhost:8080/api/promos" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "teamName": "락밴드 팀",
    "title": "락밴드 정기공연",
    "admissionFee": 10000,
    "eventDatetime": "2024-03-15T19:00:00",
    "location": "홍대 클럽",
    "address": "서울시 마포구 홍익로 123",
    "description": "락밴드 팀의 정기 공연입니다."
  }'
```

#### 요청 필드
- `teamName` (string, 필수): 팀명 (최대 255자)
- `title` (string, 필수): 공연 제목 (최대 255자)
- `admissionFee` (number, 선택): 입장료
- `eventDatetime` (string, 선택): 공연 일시 (ISO 8601)
- `location` (string, 선택): 장소명 (최대 255자)
- `address` (string, 선택): 상세 주소 (최대 255자)
- `description` (string, 선택): 공연 설명

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "공연 홍보 생성 성공",
  "data": {
    "id": 1,
    "teamName": "락밴드 팀",
    "creatorId": 1,
    "creatorName": "홍길동",
    "title": "락밴드 정기공연",
    "admissionFee": 10000,
    "eventDatetime": "2024-03-15T19:00:00",
    "location": "홍대 클럽",
    "address": "서울시 마포구 홍익로 123",
    "description": "락밴드 팀의 정기 공연입니다.",
    "viewCount": 0,
    "commentCount": 0,
    "likeCount": 0,
    "createdAt": "2024-03-01T10:00:00",
    "updatedAt": "2024-03-01T10:00:00",
    "photoUrls": []
  }
}
```

---

## 4. 공연 홍보 수정
### PUT `/api/promos/{promoId}`

#### 요청
```bash
curl -X PUT "http://localhost:8080/api/promos/1" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "teamName": "수정된 팀명",
    "title": "수정된 공연 제목",
    "admissionFee": 12000,
    "eventDatetime": "2024-03-15T19:30:00",
    "location": "새로운 장소",
    "address": "새로운 주소",
    "description": "수정된 설명"
  }'
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "공연 홍보 수정 성공",
  "data": {
    "id": 1,
    "teamName": "수정된 팀명",
    "creatorId": 1,
    "creatorName": "홍길동",
    "title": "수정된 공연 제목",
    "admissionFee": 12000,
    "eventDatetime": "2024-03-15T19:30:00",
    "location": "새로운 장소",
    "address": "새로운 주소",
    "description": "수정된 설명",
    "viewCount": 100,
    "commentCount": 5,
    "likeCount": 20,
    "createdAt": "2024-03-01T10:00:00",
    "updatedAt": "2024-03-01T11:00:00",
    "photoUrls": ["https://example.com/photo1.jpg"]
  }
}
```

---

## 5. 공연 홍보 삭제
### DELETE `/api/promos/{promoId}`

#### 요청
```bash
curl -X DELETE "http://localhost:8080/api/promos/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "공연 홍보 삭제 성공",
  "data": null
}
```

---

## 6. 공연 홍보 이미지 업로드
### POST `/api/promos/{promoId}/images`

#### 요청
```bash
curl -X POST "http://localhost:8080/api/promos/1/images" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -F "image=@/path/to/image.jpg"
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "공연 홍보 이미지 업로드 성공",
  "data": "https://example.com/uploaded-image.jpg"
}
```

---

## 7. 공연 홍보 이미지 삭제
### DELETE `/api/promos/{promoId}/images`

#### 요청
```bash
curl -X DELETE "http://localhost:8080/api/promos/1/images?imageUrl=https://example.com/image.jpg" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "공연 홍보 이미지 삭제 성공",
  "data": null
}
```

---

## 8. 공연 홍보 검색
### GET `/api/promos/search`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/promos/search?keyword=락밴드&page=0&size=20"
```

#### 쿼리 파라미터
- `keyword` (string, 필수): 검색 키워드
- `page`, `size`, `sort`: 페이지네이션 옵션

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "공연 홍보 검색 성공",
  "data": {
    "content": [
      {
        "id": 1,
        "teamName": "락밴드 팀",
        "creatorId": 1,
        "creatorName": "홍길동",
        "title": "락밴드 정기공연",
        "admissionFee": 10000,
        "eventDatetime": "2024-03-15T19:00:00",
        "location": "홍대 클럽",
        "address": "서울시 마포구 홍익로 123",
        "description": "락밴드 팀의 정기 공연입니다.",
        "viewCount": 100,
        "commentCount": 5,
        "likeCount": 20,
        "isLikedByUser": true,
        "createdAt": "2024-03-01T10:00:00",
        "updatedAt": "2024-03-01T10:00:00",
        "photoUrls": ["https://example.com/photo1.jpg"]
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

## 9. 공연 홍보 필터링
### GET `/api/promos/filter`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/promos/filter?teamName=락밴드&page=0&size=20"
```

#### 쿼리 파라미터
- `startDate` (string, 선택): 시작 날짜 (ISO 8601)
- `endDate` (string, 선택): 종료 날짜 (ISO 8601)
- `teamName` (string, 선택): 팀명
- `page`, `size`, `sort`: 페이지네이션 옵션

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "공연 홍보 필터링 성공",
  "data": {
    "content": [
      {
        "id": 1,
        "teamName": "락밴드 팀",
        "creatorId": 1,
        "creatorName": "홍길동",
        "title": "락밴드 정기공연",
        "admissionFee": 10000,
        "eventDatetime": "2024-03-15T19:00:00",
        "location": "홍대 클럽",
        "address": "서울시 마포구 홍익로 123",
        "description": "락밴드 팀의 정기 공연입니다.",
        "viewCount": 100,
        "commentCount": 5,
        "likeCount": 20,
        "isLikedByUser": true,
        "createdAt": "2024-03-01T10:00:00",
        "updatedAt": "2024-03-01T10:00:00",
        "photoUrls": ["https://example.com/photo1.jpg"]
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
- `403 Forbidden`: 권한 없음
- `404 Not Found`: 리소스 없음

---

## 10. 공연 홍보 댓글 목록 조회
### GET `/api/promos/{promoId}/comments`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/promos/1/comments?page=0&size=20"
```

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
        "createdAt": "2024-03-15T10:30:00",
        "updatedAt": "2024-03-15T10:30:00"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true
  }
}
```

---

## 11. 공연 홍보 댓글 생성
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

---

## 12. 공연 홍보 댓글 수정
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

---

## 13. 공연 홍보 댓글 삭제
### DELETE `/api/promos/comments/{commentId}`

#### 요청
```bash
curl -X DELETE "http://localhost:8080/api/promos/comments/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

---

## 14. 공연 홍보 좋아요 추가/취소
### POST `/api/promos/{promoId}/like`

#### 요청
```bash
curl -X POST "http://localhost:8080/api/promos/1/like" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (200 OK) - 좋아요 추가 시
```json
{
  "success": true,
  "message": "공연 홍보 좋아요가 추가되었습니다.",
  "data": "liked"
}
```

#### 응답 (200 OK) - 좋아요 취소 시
```json
{
  "success": true,
  "message": "공연 홍보 좋아요가 취소되었습니다.",
  "data": "unliked"
}
```

---

## 15. 공연 홍보 좋아요 상태 확인
### GET `/api/promos/{promoId}/like/status`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/promos/1/like/status" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "공연 홍보 좋아요 상태 조회 성공",
  "data": true
}
```

#### 응답 필드
- `data` (boolean): 사용자의 좋아요 상태 (true: 좋아요 누름, false: 좋아요 안 누름)

---

## 16. 공연 홍보 좋아요 수 조회
### GET `/api/promos/{promoId}/like/count`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/promos/1/like/count"
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "공연 홍보 좋아요 수 조회 성공",
  "data": 25
}
```

#### 응답 필드
- `data` (integer): 공연 홍보의 총 좋아요 수

---

## 참고사항
- **권한**: 생성은 인증된 사용자만, 수정/삭제는 작성자만 가능
- **팀명**: teamName만 사용하며 모든 공연 홍보에 필수
- **이미지**: 여러 이미지 업로드 가능, 개별 삭제 가능
- **자동 계산**: viewCount, commentCount, likeCount 자동 관리
- **소프트 삭제**: 실제 삭제가 아닌 deletedAt 설정
- **댓글**: 댓글 생성/삭제 시 공연 홍보의 commentCount 자동 업데이트
- **좋아요**: 토글 방식으로 동작 (같은 API로 추가/취소), 중복 좋아요 방지
- **좋아요 상태**: 공연 홍보 목록/상세 조회 시 `isLikedByUser` 필드로 현재 사용자의 좋아요 상태 포함 (true: 좋아요 누름, false: 좋아요 안 누름, null: 인증되지 않은 사용자)
