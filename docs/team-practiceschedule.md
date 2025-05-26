# Practice Schedule API 명세서

## Base URL
`/api`

## 인증
생성, 삭제는 JWT 인증 필요. 조회는 인증 불필요.

---

## 1. 팀별 연습 일정 목록 조회
### GET `/api/teams/{teamId}/practice-schedules`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/teams/1/practice-schedules?page=0&size=20"
```

#### 쿼리 파라미터
- `page` (integer): 페이지 번호 (기본값: 0)
- `size` (integer): 페이지 크기 (기본값: 20)

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "팀별 곡 연습 일정 목록 조회 성공",
  "data": {
    "content": [
      {
        "id": 1,
        "teamId": 1,
        "teamName": "락밴드 A팀",
        "songName": "Bohemian Rhapsody",
        "artistName": "Queen",
        "youtubeUrl": "https://www.youtube.com/watch?v=fJ9rUzIMcZQ",
        "startDatetime": "2024-03-15T19:00:00",
        "endDatetime": "2024-03-15T21:00:00",
        "location": "연습실 1",
        "address": "서울시 강남구 테헤란로 123",
        "additionalDescription": "보컬 파트 중점 연습",
        "creatorId": 1,
        "creatorName": "홍길동",
        "createdAt": "2024-03-15T10:30:00",
        "updatedAt": "2024-03-15T10:30:00",
        "participants": [
          {
            "id": 1,
            "userId": 1,
            "userName": "홍길동"
          }
        ]
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

## 2. 연습 일정 상세 조회
### GET `/api/practice-schedules/{scheduleId}`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/practice-schedules/1"
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "곡 연습 일정 상세 조회 성공",
  "data": {
    "id": 1,
    "teamId": 1,
    "teamName": "락밴드 A팀",
    "songName": "Bohemian Rhapsody",
    "artistName": "Queen",
    "youtubeUrl": "https://www.youtube.com/watch?v=fJ9rUzIMcZQ",
    "startDatetime": "2024-03-15T19:00:00",
    "endDatetime": "2024-03-15T21:00:00",
    "location": "연습실 1",
    "address": "서울시 강남구 테헤란로 123",
    "additionalDescription": "보컬 파트 중점 연습",
    "creatorId": 1,
    "creatorName": "홍길동",
    "createdAt": "2024-03-15T10:30:00",
    "updatedAt": "2024-03-15T10:30:00",
    "participants": [
      {
        "id": 1,
        "userId": 1,
        "userName": "홍길동"
      }
    ]
  }
}
```

---

## 3. 연습 일정 생성
### POST `/api/teams/{teamId}/practice-schedules`

#### 요청
```bash
curl -X POST "http://localhost:8080/api/teams/1/practice-schedules" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "songName": "Bohemian Rhapsody",
    "artistName": "Queen",
    "youtubeUrl": "https://www.youtube.com/watch?v=fJ9rUzIMcZQ",
    "startDatetime": "2024-03-15T19:00:00",
    "endDatetime": "2024-03-15T21:00:00",
    "location": "연습실 1",
    "address": "서울시 강남구 테헤란로 123",
    "additionalDescription": "보컬 파트 중점 연습"
  }'
```

#### 요청 필드
- `songName` (string, 필수): 곡명 (최대 100자)
- `artistName` (string, 선택): 아티스트명 (최대 100자)
- `youtubeUrl` (string, 선택): YouTube URL (최대 500자)
- `startDatetime` (string, 필수): 연습 시작 일시 (ISO 8601)
- `endDatetime` (string, 필수): 연습 종료 일시 (ISO 8601)
- `location` (string, 선택): 장소명 (최대 255자)
- `address` (string, 선택): 상세 주소 (최대 255자)
- `additionalDescription` (string, 선택): 추가 설명 (최대 길이 제한 없음)

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "곡 연습 일정 생성 성공",
  "data": {
    "id": 1,
    "teamId": 1,
    "teamName": "락밴드 A팀",
    "songName": "Bohemian Rhapsody",
    "artistName": "Queen",
    "youtubeUrl": "https://www.youtube.com/watch?v=fJ9rUzIMcZQ",
    "startDatetime": "2024-03-15T19:00:00",
    "endDatetime": "2024-03-15T21:00:00",
    "location": "연습실 1",
    "address": "서울시 강남구 테헤란로 123",
    "additionalDescription": "보컬 파트 중점 연습",
    "creatorId": 1,
    "creatorName": "홍길동",
    "createdAt": "2024-03-15T10:30:00",
    "updatedAt": "2024-03-15T10:30:00",
    "participants": []
  }
}
```

---

## 4. 연습 일정 삭제
### DELETE `/api/practice-schedules/{scheduleId}`

#### 요청
```bash
curl -X DELETE "http://localhost:8080/api/practice-schedules/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "곡 연습 일정 삭제 성공",
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
- `400 Bad Request`: 잘못된 요청
- `401 Unauthorized`: 인증 실패
- `403 Forbidden`: 권한 없음
- `404 Not Found`: 리소스 없음

## 참고사항
- **권한**: 연습 일정 생성은 JWT 인증 필요, 삭제는 생성자만 가능
- **데이터 저장**: 곡명과 아티스트명은 "곡명 - 아티스트명" 형태로 name 필드에 저장
- **URL 파싱**: YouTube URL과 추가 설명을 description 필드에 저장 후 응답 시 파싱
- **페이지네이션**: 기본 크기 20개 (Spring Boot 기본값)
- **정렬**: 연습 시작 일시(startDatetime) 오름차순 정렬
- **소프트 삭제**: 실제 삭제가 아닌 deletedAt 설정
- **연습 일정 구분**: TeamEvent의 name 필드에 " - "가 포함된 경우를 연습 일정으로 판별
