# Poll API 명세서

## Base URL
`/api/polls`

## 인증
JWT 인증 필요 (투표 상세 조회는 선택적)

---

## 1. 투표 생성
### POST `/api/polls`

#### 요청
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

#### 요청 필드
- `title` (string, 필수): 투표 제목
- `clubId` (integer, 필수): 동아리 ID  
- `endDatetime` (string, 필수): 투표 마감 시간 (미래 시간)

#### 응답 (201 Created)
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

---

## 2. 클럽별 투표 목록 조회
### GET `/api/polls/clubs/{clubId}`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/polls/clubs/1?page=0&size=5"
```

#### 쿼리 파라미터
- `page` (integer): 페이지 번호 (기본값: 0)
- `size` (integer): 페이지 크기 (기본값: 5)

#### 응답 (200 OK)
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
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true
  }
}
```

---

## 3. 투표 상세 조회
### GET `/api/polls/{pollId}`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/polls/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (200 OK)
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
### POST `/api/polls/{pollId}/songs`

#### 요청
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

#### 요청 필드
- `songName` (string, 필수): 곡 제목
- `artistName` (string, 필수): 아티스트명
- `youtubeUrl` (string, 필수): YouTube URL
- `description` (string, 선택): 곡 설명

#### 응답 (201 Created)
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
### PUT `/api/polls/{pollId}/songs/{songId}/votes/{emoji}`

#### 요청
```bash
curl -X PUT "http://localhost:8080/api/polls/1/songs/1/votes/LIKE" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 투표 타입 (emoji)
- `LIKE`: 좋아요 👍
- `DISLIKE`: 별로에요 👎
- `CANT`: 실력부족 😅
- `HAJJ`: 하고싶지_않은데_존중해요 🔥

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "투표가 설정되었습니다.",
  "data": {
    "id": 1,
    "pollId": 1,
    "songName": "Bohemian Rhapsody",
    "artistName": "Queen",
    "youtubeUrl": "https://www.youtube.com/watch?v=fJ9rUzIMcZQ",
    "description": "클래식한 록 명곡입니다",
    "suggesterId": 1,
    "suggesterName": "홍길동",
    "createdAt": "2024-03-15T11:00:00",
    "likeCount": 6,
    "dislikeCount": 1,
    "cantCount": 2,
    "hajjCount": 0,
    "userVoteType": "LIKE"
  }
}
```

---

## 6. 곡 투표 취소
### DELETE `/api/polls/{pollId}/songs/{songId}/votes/{emoji}`

#### 요청
```bash
curl -X DELETE "http://localhost:8080/api/polls/1/songs/1/votes/LIKE" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "투표가 취소되었습니다.",
  "data": {
    "id": 1,
    "pollId": 1,
    "songName": "Bohemian Rhapsody",
    "artistName": "Queen",
    "youtubeUrl": "https://www.youtube.com/watch?v=fJ9rUzIMcZQ",
    "description": "클래식한 록 명곡입니다",
    "suggesterId": 1,
    "suggesterName": "홍길동",
    "createdAt": "2024-03-15T11:00:00",
    "likeCount": 5,
    "dislikeCount": 1,
    "cantCount": 2,
    "hajjCount": 0,
    "userVoteType": null
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
- `201 Created`: 생성 성공
- `400 Bad Request`: 잘못된 요청
- `401 Unauthorized`: 인증 실패
- `404 Not Found`: 리소스 없음

## 참고사항
- **투표 교체**: 기존 투표가 있으면 덮어씀
- **페이지네이션**: 기본 크기 5개
- **인증**: 투표 상세 조회는 인증 없이도 가능하지만 사용자 투표 상태는 보이지 않음
