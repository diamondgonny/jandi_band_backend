# Poll API 명세서

## Base URL
`/api/polls`

## 인증
JWT 인증 필요 (Spring Security + @AuthenticationPrincipal CustomUserDetails)

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
### PUT `/api/polls/{pollId}/songs/{songId}/votes/{emoji}`

#### 요청
```bash
curl -X PUT "http://localhost:8080/api/polls/1/songs/1/votes/LIKE" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 투표 타입 (emoji)
영어명과 한국어명 모두 지원:
- `LIKE` 또는 `좋아요`: 좋아요 👍
- `DISLIKE` 또는 `별로에요`: 별로에요 👎
- `CANT` 또는 `실력부족`: 실력부족 😅
- `HAJJ` 또는 `하고싶지_않은데_존중해요`: 하고싶지_않은데_존중해요 🔥

#### 투표 로직
- **같은 타입 재투표**: 이미 같은 타입의 투표가 있으면 에러 응답 (400 Bad Request)
- **다른 타입 투표**: 기존 투표가 다른 타입이면 새로운 타입으로 덮어씀
- **신규 투표**: 기존 투표가 없으면 새로 생성

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
    "suggesterProfilePhoto": "https://example.com/profile.jpg",
    "createdAt": "2024-03-15T11:00:00",
    "likeCount": 6,
    "dislikeCount": 1,
    "cantCount": 2,
    "hajjCount": 0,
    "userVoteType": "LIKE"
  }
}
```

#### 에러 응답 (400 Bad Request) - 같은 타입 재투표 시
```json
{
  "success": false,
  "message": "이미 이 노래에 대한 'LIKE' 투표가 존재합니다. 취소하려면 DELETE 요청을 사용하세요.",
  "data": null
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
    "suggesterProfilePhoto": "https://example.com/profile.jpg",
    "createdAt": "2024-03-15T11:00:00",
    "likeCount": 5,
    "dislikeCount": 1,
    "cantCount": 2,
    "hajjCount": 0,
    "userVoteType": null
  }
}
```

#### 에러 응답 (404 Not Found) - 해당 타입의 투표가 없을 시
```json
{
  "success": false,
  "message": "사용자의 해당 노래에 대한 LIKE 타입의 투표를 찾을 수 없습니다.",
  "data": null
}
```

---

## 응답 필드 설명

### PollSongRespDTO 필드
- `suggesterProfilePhoto` (string): 곡 제안자의 프로필 사진 URL (없으면 null)
- `userVoteType` (string): 현재 사용자의 투표 타입 ("LIKE", "DISLIKE", "CANT", "HAJJ" 중 하나, 투표하지 않았으면 null)

---

## 에러 응답
```json
{
  "success": false,
  "message": "에러 메시지",
  "data": null,
  "errorCode": "ERROR_CODE"
}
```

### HTTP 상태 코드
- `200 OK`: 성공
- `201 Created`: 생성 성공
- `400 Bad Request`: 잘못된 요청 (중복 투표 등)
- `401 Unauthorized`: 인증 실패
- `404 Not Found`: 리소스 없음

### 주요 에러 케이스
- **투표 없음**: `PollNotFoundException` - 해당 투표를 찾을 수 없습니다
- **곡 없음**: `PollSongNotFoundException` - 해당 노래를 찾을 수 없습니다
- **중복 투표**: `VoteAlreadyExistsException` - 이미 같은 타입의 투표가 존재합니다
- **투표 없음**: `VoteNotFoundException` - 해당 타입의 투표를 찾을 수 없습니다
- **잘못된 투표 타입**: `IllegalArgumentException` - 유효하지 않은 투표 타입입니다

## 참고사항
- **투표 교체**: 다른 타입의 기존 투표가 있으면 새로운 타입으로 덮어씀
- **같은 타입 재투표**: 에러 응답 후 DELETE로 취소 후 다시 투표해야 함
- **페이지네이션**: 기본 크기 5개
- **인증**: 투표 상세 조회는 인증 없이도 가능하지만 사용자 투표 상태(`userVoteType`)는 보이지 않음
- **투표 타입**: 영어명과 한국어명 모두 지원 (대소문자 구분 없음)
