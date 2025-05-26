# Promo API 명세서

## Base URL
`/api/promos`

## 인증
생성, 수정, 삭제, 이미지 업로드는 JWT 인증 필요 (Spring Security + @AuthenticationPrincipal CustomUserDetails). 조회/검색은 인증 불필요.

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
        "clubId": 1,
        "clubName": "락밴드 동아리",
        "creatorId": 1,
        "creatorName": "홍길동",
        "title": "락밴드 정기공연",
        "admissionFee": 10000,
        "eventDatetime": "2024-03-15T19:00:00",
        "location": "홍대 클럽",
        "address": "서울시 마포구 홍익로 123",
        "description": "락밴드 동아리의 정기 공연입니다.",
        "status": "UPCOMING",
        "viewCount": 100,
        "commentCount": 5,
        "likeCount": 20,
        "createdAt": "2024-03-01T10:00:00",
        "updatedAt": "2024-03-01T10:00:00",
        "photoUrls": ["https://example.com/photo1.jpg"]
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

## 2. 클럽별 공연 홍보 목록 조회
### GET `/api/promos/club/{clubId}`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/promos/club/1?page=0&size=20"
```

#### 쿼리 파라미터
- `page` (integer): 페이지 번호 (기본값: 0)
- `size` (integer): 페이지 크기 (기본값: 20)
- `sort` (string): 정렬 기준

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "클럽별 공연 홍보 목록 조회 성공",
  "data": {
    "content": [
      {
        "id": 1,
        "clubId": 1,
        "clubName": "락밴드 동아리",
        "creatorId": 1,
        "creatorName": "홍길동",
        "title": "락밴드 정기공연",
        "admissionFee": 10000,
        "eventDatetime": "2024-03-15T19:00:00",
        "location": "홍대 클럽",
        "address": "서울시 마포구 홍익로 123",
        "description": "락밴드 동아리의 정기 공연입니다.",
        "status": "UPCOMING",
        "viewCount": 100,
        "commentCount": 5,
        "likeCount": 20,
        "createdAt": "2024-03-01T10:00:00",
        "updatedAt": "2024-03-01T10:00:00",
        "photoUrls": ["https://example.com/photo1.jpg"]
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

## 3. 공연 홍보 상세 조회
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
    "clubId": 1,
    "clubName": "락밴드 동아리",
    "creatorId": 1,
    "creatorName": "홍길동",
    "title": "락밴드 정기공연",
    "admissionFee": 10000,
    "eventDatetime": "2024-03-15T19:00:00",
    "location": "홍대 클럽",
    "address": "서울시 마포구 홍익로 123",
    "description": "락밴드 동아리의 정기 공연입니다.",
    "status": "UPCOMING",
    "viewCount": 100,
    "commentCount": 5,
    "likeCount": 20,
    "createdAt": "2024-03-01T10:00:00",
    "updatedAt": "2024-03-01T10:00:00",
    "photoUrls": ["https://example.com/photo1.jpg"]
  }
}
```

---

## 4. 공연 홍보 생성
### POST `/api/promos`

#### 요청
```bash
curl -X POST "http://localhost:8080/api/promos" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "clubId": 1,
    "title": "락밴드 정기공연",
    "admissionFee": 10000,
    "eventDatetime": "2024-03-15T19:00:00",
    "location": "홍대 클럽",
    "address": "서울시 마포구 홍익로 123",
    "description": "락밴드 동아리의 정기 공연입니다.",
    "status": "UPCOMING"
  }'
```

#### 요청 필드
- `clubId` (integer, 필수): 동아리 ID
- `title` (string, 필수): 공연 제목 (최대 255자)
- `admissionFee` (number, 선택): 입장료
- `eventDatetime` (string, 선택): 공연 일시 (ISO 8601)
- `location` (string, 선택): 장소명 (최대 255자)
- `address` (string, 선택): 상세 주소 (최대 255자)
- `description` (string, 선택): 공연 설명
- `status` (string, 선택): 상태 (UPCOMING/ONGOING/COMPLETED, 기본값: UPCOMING)

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "공연 홍보 생성 성공",
  "data": {
    "id": 1,
    "clubId": 1,
    "clubName": "락밴드 동아리",
    "creatorId": 1,
    "creatorName": "홍길동",
    "title": "락밴드 정기공연",
    "admissionFee": 10000,
    "eventDatetime": "2024-03-15T19:00:00",
    "location": "홍대 클럽",
    "address": "서울시 마포구 홍익로 123",
    "description": "락밴드 동아리의 정기 공연입니다.",
    "status": "UPCOMING",
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

## 5. 공연 홍보 수정
### PUT `/api/promos/{promoId}`

#### 요청
```bash
curl -X PUT "http://localhost:8080/api/promos/1" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "clubId": 1,
    "title": "수정된 공연 제목",
    "admissionFee": 12000,
    "eventDatetime": "2024-03-15T19:30:00",
    "location": "새로운 장소",
    "address": "새로운 주소",
    "description": "수정된 설명",
    "status": "ONGOING"
  }'
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "공연 홍보 수정 성공",
  "data": {
    "id": 1,
    "clubId": 1,
    "clubName": "락밴드 동아리",
    "creatorId": 1,
    "creatorName": "홍길동",
    "title": "수정된 공연 제목",
    "admissionFee": 12000,
    "eventDatetime": "2024-03-15T19:30:00",
    "location": "새로운 장소",
    "address": "새로운 주소",
    "description": "수정된 설명",
    "status": "ONGOING",
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

## 6. 공연 홍보 삭제
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

## 7. 공연 홍보 이미지 업로드
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

## 8. 공연 홍보 이미지 삭제
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

## 9. 공연 홍보 검색
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
        "clubId": 1,
        "clubName": "락밴드 동아리",
        "creatorId": 1,
        "creatorName": "홍길동",
        "title": "락밴드 정기공연",
        "admissionFee": 10000,
        "eventDatetime": "2024-03-15T19:00:00",
        "location": "홍대 클럽",
        "address": "서울시 마포구 홍익로 123",
        "description": "락밴드 동아리의 정기 공연입니다.",
        "status": "UPCOMING",
        "viewCount": 100,
        "commentCount": 5,
        "likeCount": 20,
        "createdAt": "2024-03-01T10:00:00",
        "updatedAt": "2024-03-01T10:00:00",
        "photoUrls": ["https://example.com/photo1.jpg"]
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

## 10. 공연 홍보 필터링
### GET `/api/promos/filter`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/promos/filter?status=UPCOMING&clubId=1&page=0&size=20"
```

#### 쿼리 파라미터
- `status` (string, 선택): 공연 상태 (UPCOMING/ONGOING/COMPLETED)
- `startDate` (string, 선택): 시작 날짜 (ISO 8601)
- `endDate` (string, 선택): 종료 날짜 (ISO 8601)
- `clubId` (integer, 선택): 동아리 ID
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
        "clubId": 1,
        "clubName": "락밴드 동아리",
        "creatorId": 1,
        "creatorName": "홍길동",
        "title": "락밴드 정기공연",
        "admissionFee": 10000,
        "eventDatetime": "2024-03-15T19:00:00",
        "location": "홍대 클럽",
        "address": "서울시 마포구 홍익로 123",
        "description": "락밴드 동아리의 정기 공연입니다.",
        "status": "UPCOMING",
        "viewCount": 100,
        "commentCount": 5,
        "likeCount": 20,
        "createdAt": "2024-03-01T10:00:00",
        "updatedAt": "2024-03-01T10:00:00",
        "photoUrls": ["https://example.com/photo1.jpg"]
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

## 상태 값
- `UPCOMING`: 예정된 공연
- `ONGOING`: 진행 중인 공연
- `COMPLETED`: 완료된 공연

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

## 11. 공연 홍보 댓글 목록 조회
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

## 12. 공연 홍보 댓글 생성
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

## 13. 공연 홍보 댓글 수정
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

## 14. 공연 홍보 댓글 삭제
### DELETE `/api/promos/comments/{commentId}`

#### 요청
```bash
curl -X DELETE "http://localhost:8080/api/promos/comments/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

---

## 참고사항
- **권한**: 생성은 클럽 멤버만, 수정/삭제는 작성자만 가능
- **이미지**: 여러 이미지 업로드 가능, 개별 삭제 가능
- **자동 계산**: viewCount, commentCount, likeCount 자동 관리
- **소프트 삭제**: 실제 삭제가 아닌 deletedAt 설정
- **댓글**: 댓글 생성/삭제 시 공연 홍보의 commentCount 자동 업데이트
