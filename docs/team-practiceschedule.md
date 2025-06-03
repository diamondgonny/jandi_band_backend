# Practice Schedule API

## Base URL
`/api/teams/{teamId}/practice-schedules`

## 인증
**모든 API는 JWT 인증 필요** (Spring Security + @AuthenticationPrincipal CustomUserDetails)

## 권한 체계
- **일정 조회 (목록/상세)**: 동아리 멤버 또는 ADMIN
- **일정 생성/삭제**: 팀 멤버 또는 ADMIN

## 페이지네이션 응답 구조
연습 일정 목록 조회 API는 다음과 같은 페이지네이션 구조를 사용합니다:

```json
{
  "success": true,
  "message": "응답 메시지",
  "data": {
    "content": [...],  // 실제 연습 일정 데이터 배열
    "pageInfo": {
      "page": 0,           // 현재 페이지 번호 (0부터 시작)
      "size": 20,          // 페이지 크기
      "totalElements": 100, // 총 연습 일정 수
      "totalPages": 5,     // 총 페이지 수
      "first": true,       // 첫 번째 페이지 여부
      "last": false,       // 마지막 페이지 여부
      "empty": false       // 비어있는 페이지 여부
    }
  }
}
```

---

## 1. 팀별 연습 일정 목록 조회
### GET `/api/teams/{teamId}/practice-schedules`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/teams/1/practice-schedules?page=0&size=20" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 경로 파라미터
- `teamId` (integer, 필수): 팀 ID

#### 쿼리 파라미터
- `page` (integer): 페이지 번호 (기본값: 0)
- `size` (integer): 페이지 크기 (기본값: 20)

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "팀별 연습 일정 목록 조회 성공",
  "data": {
    "content": [
      {
        "id": 1,
        "teamId": 1,
        "teamName": "락밴드 A팀",
        "name": "Bohemian Rhapsody - Queen",
        "startDatetime": "2024-03-15T19:00:00",
        "endDatetime": "2024-03-15T21:00:00",
        "noPosition": "VOCAL",
        "creatorId": 1,
        "creatorName": "홍길동",
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

## 2. 연습 일정 상세 조회
### GET `/api/teams/{teamId}/practice-schedules/{scheduleId}`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/teams/1/practice-schedules/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 경로 파라미터
- `teamId` (integer, 필수): 팀 ID
- `scheduleId` (integer, 필수): 연습 일정 ID

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "곡 연습 일정 상세 조회 성공",
  "data": {
    "id": 1,
    "teamId": 1,
    "teamName": "락밴드 A팀",
    "name": "Bohemian Rhapsody - Queen",
    "startDatetime": "2024-03-15T19:00:00",
    "endDatetime": "2024-03-15T21:00:00",
    "noPosition": "VOCAL",
    "creatorId": 1,
    "creatorName": "홍길동",
    "createdAt": "2024-03-15T10:30:00",
    "updatedAt": "2024-03-15T10:30:00"
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
    "name": "Bohemian Rhapsody - Queen",
    "startDatetime": "2024-03-15T19:00:00",
    "endDatetime": "2024-03-15T21:00:00",
    "noPosition": "VOCAL"
  }'
```

#### 경로 파라미터
- `teamId` (integer, 필수): 팀 ID

#### 요청 필드
- `name` (string, 필수): 연습 일정명 (최대 255자)
- `startDatetime` (string, 필수): 연습 시작 일시 (ISO 8601)
- `endDatetime` (string, 필수): 연습 종료 일시 (ISO 8601)
- `noPosition` (string, 선택): 연습에서 제외되는 포지션 (VOCAL, GUITAR, KEYBOARD, BASS, DRUM, NONE)

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "곡 연습 일정 생성 성공",
  "data": {
    "id": 1,
    "teamId": 1,
    "teamName": "락밴드 A팀",
    "name": "Bohemian Rhapsody - Queen",
    "startDatetime": "2024-03-15T19:00:00",
    "endDatetime": "2024-03-15T21:00:00",
    "noPosition": "VOCAL",
    "creatorId": 1,
    "creatorName": "홍길동",
    "createdAt": "2024-03-15T10:30:00",
    "updatedAt": "2024-03-15T10:30:00"
  }
}
```

---

## 4. 연습 일정 삭제
### DELETE `/api/teams/{teamId}/practice-schedules/{scheduleId}`

#### 요청
```bash
curl -X DELETE "http://localhost:8080/api/teams/1/practice-schedules/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 경로 파라미터
- `teamId` (integer, 필수): 팀 ID
- `scheduleId` (integer, 필수): 삭제할 연습 일정 ID

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
  "errorCode": "ERROR_CODE",
  "data": null
}
```

### HTTP 상태 코드
- `200 OK`: 성공
- `400 Bad Request`: 잘못된 요청
- `401 Unauthorized`: 인증 실패
- `403 Forbidden`: 권한 없음 (UNAUTHORIZED_CLUB_ACCESS)
- `404 Not Found`: 리소스 없음 (TEAM_NOT_FOUND, USER_NOT_FOUND, RESOURCE_NOT_FOUND)

### 주요 에러 코드
- `TEAM_NOT_FOUND`: 존재하지 않는 팀
- `USER_NOT_FOUND`: 존재하지 않는 사용자
- `RESOURCE_NOT_FOUND`: 존재하지 않는 연습 일정
- `UNAUTHORIZED_CLUB_ACCESS`: 권한 없음 (동아리 멤버가 아님)
- `BAD_REQUEST`: 잘못된 요청 (잘못된 시간 형식, 과거 시간 입력 등)

## Position 값
- `VOCAL`: 보컬
- `GUITAR`: 기타
- `KEYBOARD`: 키보드
- `BASS`: 베이스
- `DRUM`: 드럼
- `NONE`: 모든 포지션 참여

## 참고사항
- **권한**: 동아리 멤버만 일정 조회 가능, 팀 멤버만 일정 생성/삭제 가능
- **시간 형식**: ISO 8601 형식 (YYYY-MM-DDTHH:mm:ss)
- **페이지네이션**: 기본 크기 20개, 최신 생성순으로 정렬
- **소프트 삭제**: 실제 삭제가 아닌 deletedAt 설정으로 소프트 삭제
- **자동 필터링**: 모든 조회 API에서 삭제된 일정은 자동으로 제외
- **URL 일관성**: 모든 엔드포인트가 `/api/teams/{teamId}/practice-schedules` 패턴을 따름