# Poll API

## 🗳️ 투표 관리
JWT 인증 필요

---

## 1. 투표 생성
```
POST /api/polls
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

### 요청 예시
```bash
curl -X POST "http://localhost:8080/api/polls" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "5월 정기공연 곡 선정",
    "clubId": 1,
    "endDatetime": "2024-05-01T23:59:59"
  }'
```

### 요청 필드
- `title`: 투표 제목
- `clubId`: 동아리 ID  
- `endDatetime`: 투표 마감 시간 (미래 시간)

### 성공 응답 (201)
```json
{
  "success": true,
  "message": "투표가 성공적으로 생성되었습니다.",
  "data": {
    "id": 1,
    "title": "5월 정기공연 곡 선정",
    "clubId": 1,
    "clubName": "락밴드 동아리",
    "startDatetime": "2024-03-15T10:30:00",
    "endDatetime": "2024-05-01T23:59:59",
    "creatorId": 1,
    "creatorName": "홍길동",
    "createdAt": "2024-03-15T10:30:00"
  }
}
```

### 실패 응답
- **400**: 과거 시간으로 마감일 설정
- **403**: 동아리 멤버가 아님

---

## 2. 클럽별 투표 목록 조회
```
GET /api/polls/clubs/{clubId}?page=0&size=5
Authorization: Bearer {JWT_TOKEN}
```

### 요청 예시
```bash
curl "http://localhost:8080/api/polls/clubs/1?page=0&size=5" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 쿼리 파라미터
- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 5)

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "투표 목록을 조회했습니다.",
  "data": {
    "content": [
      {
        "id": 1,
        "title": "5월 정기공연 곡 선정",
        "clubId": 1,
        "clubName": "락밴드 동아리",
        "startDatetime": "2024-03-15T10:30:00",
        "endDatetime": "2024-05-01T23:59:59",
        "creatorId": 1,
        "creatorName": "홍길동",
        "createdAt": "2024-03-15T10:30:00"
      }
    ],
    "pageInfo": {
      "totalElements": 1,
      "totalPages": 1,
      "first": true,
      "last": true,
      "size": 5,
      "number": 0
    }
  }
}
```

---

## 3. 투표 상세 조회
```
GET /api/polls/{pollId}
Authorization: Bearer {JWT_TOKEN}
```

### 요청 예시
```bash
curl "http://localhost:8080/api/polls/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "투표 상세 정보를 조회했습니다.",
  "data": {
    "id": 1,
    "title": "5월 정기공연 곡 선정",
    "clubId": 1,
    "clubName": "락밴드 동아리",
    "startDatetime": "2024-03-15T10:30:00",
    "endDatetime": "2024-05-01T23:59:59",
    "creatorId": 1,
    "creatorName": "홍길동",
    "createdAt": "2024-03-15T10:30:00",
    "songs": [
      {
        "id": 1,
        "pollId": 1,
        "songName": "Bohemian Rhapsody",
        "artistName": "Queen",
        "youtubeUrl": "https://www.youtube.com/watch?v=fJ9rUzIMcZQ",
        "description": "클래식한 록 명곡입니다",
        "suggesterId": 1,
        "suggesterName": "홍길동",
        "suggesterProfilePhoto": "https://example.com/profile.jpg",
        "createdAt": "2024-03-15T11:00:00",
        "likeCount": 5,
        "dislikeCount": 1,
        "cantCount": 2,
        "hajjCount": 0,
        "userVoteType": "LIKE"
      }
    ]
  }
}
```

---

## 4. 투표에 곡 추가
```
POST /api/polls/{pollId}/songs
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

### 요청 예시
```bash
curl -X POST "http://localhost:8080/api/polls/1/songs" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "songName": "Bohemian Rhapsody",
    "artistName": "Queen",
    "youtubeUrl": "https://www.youtube.com/watch?v=fJ9rUzIMcZQ",
    "description": "클래식한 록 명곡입니다"
  }'
```

### 요청 필드
- `songName`: 곡 제목
- `artistName`: 아티스트명
- `youtubeUrl`: YouTube URL
- `description`: 곡 설명 (선택)

### 성공 응답 (201)
```json
{
  "success": true,
  "message": "곡이 성공적으로 투표에 추가되었습니다.",
  "data": {
    "id": 1,
    "pollId": 1,
    "songName": "Bohemian Rhapsody",
    "artistName": "Queen",
    "youtubeUrl": "https://www.youtube.com/watch?v=fJ9rUzIMcZQ",
    "description": "클래식한 록 명곡입니다",
    "suggesterId": 1,
    "suggesterName": "홍길동",
    "suggesterProfilePhoto": "https://example.com/profile.jpg",
    "createdAt": "2024-03-15T11:00:00",
    "likeCount": 0,
    "dislikeCount": 0,
    "cantCount": 0,
    "hajjCount": 0,
    "userVoteType": null
  }
}
```

---

## 5. 곡에 투표하기
```
PUT /api/polls/{pollId}/songs/{songId}/votes/{emoji}
Authorization: Bearer {JWT_TOKEN}
```

### 요청 예시
```bash
curl -X PUT "http://localhost:8080/api/polls/1/songs/1/votes/LIKE" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 투표 타입
- `LIKE`: 좋아요 (👍)
- `DISLIKE`: 싫어요 (👎)
- `CANT`: 못해요 (😅)
- `HAJJ`: 하고싶어요 (🔥)

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "투표가 성공적으로 등록되었습니다.",
  "data": null
}
```

### 실패 응답
- **400**: 마감된 투표
- **404**: 존재하지 않는 투표 또는 곡

---

## 6. 투표 삭제
```
DELETE /api/polls/{pollId}
Authorization: Bearer {JWT_TOKEN}
```

### 요청 예시
```bash
curl -X DELETE "http://localhost:8080/api/polls/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "투표가 성공적으로 삭제되었습니다.",
  "data": null
}
```

### 실패 응답
- **403**: 투표 생성자가 아님

---

## 7. 곡 삭제
```
DELETE /api/polls/{pollId}/songs/{songId}
Authorization: Bearer {JWT_TOKEN}
```

### 요청 예시
```bash
curl -X DELETE "http://localhost:8080/api/polls/1/songs/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "곡이 성공적으로 삭제되었습니다.",
  "data": null
}
```

### 실패 응답
- **403**: 곡 제안자가 아님
