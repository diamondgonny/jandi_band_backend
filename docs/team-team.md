# Team API 명세서

## Base URL
`/api`

## 인증
JWT 인증 필요 (Spring Security + @AuthenticationPrincipal CustomUserDetails)

## 페이지네이션 응답 구조
팀 목록 조회 API는 다음과 같은 페이지네이션 구조를 사용합니다:

```json
{
  "success": true,
  "message": "응답 메시지",
  "data": {
    "content": [...],  // 실제 팀 데이터 배열
    "pageInfo": {
      "page": 0,           // 현재 페이지 번호 (0부터 시작)
      "size": 5,           // 페이지 크기
      "totalElements": 100, // 총 팀 수
      "totalPages": 20,    // 총 페이지 수
      "first": true,       // 첫 번째 페이지 여부
      "last": false,       // 마지막 페이지 여부
      "empty": false       // 비어있는 페이지 여부
    }
  }
}
```

---

## 🎵 팀 관리
JWT 인증 필요

---

## 1. 팀 생성
### POST `/api/clubs/{clubId}/teams`

#### 요청
```bash
curl -X POST "http://localhost:8080/api/clubs/1/teams" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "밴드 팀"
  }'
```

#### 요청 필드
- `name`: 팀 이름

#### 응답 (201 Created)
```json
{
  "success": true,
  "message": "곡 팀이 성공적으로 생성되었습니다.",
  "data": {
    "id": 1,
    "name": "밴드 팀",
    "clubId": 1,
    "clubName": "락밴드 동아리",
    "creatorId": 1,
    "creatorName": "홍길동",
    "createdAt": "2024-03-15T10:30:00",
    "memberCount": 1,
    "members": [
      {
        "userId": 1,
        "nickname": "홍길동",
        "profilePhoto": "https://example.com/profile.jpg",
        "position": "GUITAR",
        "joinedAt": "2024-03-15T10:30:00"
      }
    ]
  }
}
```

### 실패 응답
- **400**: 중복된 팀 이름
- **403**: 동아리 멤버가 아님

---

## 2. 동아리 팀 목록 조회
### GET `/api/clubs/{clubId}/teams`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/clubs/1/teams?page=0&size=5" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 쿼리 파라미터
- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 5)

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "곡 팀 목록을 성공적으로 조회했습니다.",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "밴드 팀",
        "clubId": 1,
        "clubName": "락밴드 동아리",
        "creatorId": 1,
        "creatorName": "홍길동",
        "createdAt": "2024-03-15T10:30:00",
        "memberCount": 4
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

## 3. 팀 상세 정보 조회
### GET `/api/teams/{teamId}`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/teams/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "곡 팀 정보를 성공적으로 조회했습니다.",
  "data": {
    "id": 1,
    "name": "밴드 팀",
    "clubId": 1,
    "clubName": "락밴드 동아리",
    "creatorId": 1,
    "creatorName": "홍길동",
    "createdAt": "2024-03-15T10:30:00",
    "memberCount": 4,
    "members": [
      {
        "userId": 1,
        "nickname": "홍길동",
        "profilePhoto": "https://example.com/profile.jpg",
        "position": "GUITAR",
        "joinedAt": "2024-03-15T10:30:00"
      },
      {
        "userId": 2,
        "nickname": "김철수",
        "profilePhoto": "https://example.com/profile2.jpg",
        "position": "VOCAL",
        "joinedAt": "2024-03-16T14:20:00"
      }
    ]
  }
}
```

### 실패 응답
- **403**: 동아리 멤버가 아님
- **404**: 존재하지 않는 팀

---

## 4. 팀 이름 수정
### PATCH `/api/teams/{teamId}`

#### 요청
```bash
curl -X PATCH "http://localhost:8080/api/teams/1" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "수정된 팀 이름"
  }'
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "곡 팀 이름이 성공적으로 수정되었습니다.",
  "data": {
    "id": 1,
    "name": "수정된 팀 이름",
    "clubId": 1,
    "clubName": "락밴드 동아리",
    "creatorId": 1,
    "creatorName": "홍길동",
    "createdAt": "2024-03-15T10:30:00",
    "memberCount": 4
  }
}
```

### 실패 응답
- **403**: 팀 생성자가 아님

---

## 5. 팀 삭제
### DELETE `/api/teams/{teamId}`

#### 요청
```bash
curl -X DELETE "http://localhost:8080/api/teams/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "곡 팀이 성공적으로 삭제되었습니다.",
  "data": null
}
```

### 실패 응답
- **403**: 팀 생성자가 아님
- **404**: 존재하지 않는 팀

#### 삭제 동작
팀 삭제 시 다음 리소스들이 함께 소프트 삭제됩니다:
- **팀 자체** (`Team`)
- **팀 멤버 관계** (`TeamMember`): 해당 팀의 모든 멤버 관계
- **팀 연습 일정** (`TeamEvent`): 해당 팀의 모든 연습 스케줄

모든 삭제 작업은 하나의 트랜잭션으로 처리되어 데이터 일관성을 보장합니다.

---

## 6. 팀 탈퇴
### DELETE `/api/teams/{teamId}/members/me`

#### 요청
```bash
curl -X DELETE "http://localhost:8080/api/teams/1/members/me" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "팀에서 성공적으로 탈퇴했습니다.",
  "data": null
}
```

### 실패 응답
- **400**: 팀 생성자는 탈퇴 불가 (팀 삭제 필요)
- **404**: 팀 멤버가 아님

---

## 7. 팀 연습 일정 생성
### POST `/api/teams/{teamId}/practice-schedules`

#### 요청
```bash
curl -X POST "http://localhost:8080/api/teams/1/practice-schedules" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "주간 연습",
    "description": "이번 주 연습 일정입니다",
    "startDatetime": "2024-03-20T19:00:00",
    "endDatetime": "2024-03-20T21:00:00",
    "location": "연습실 A"
  }'
```

#### 요청 필드
- `title`: 연습 일정 제목
- `description`: 연습 일정 설명
- `startDatetime`: 연습 일정 시작 시간
- `endDatetime`: 연습 일정 종료 시간
- `location`: 연습 일정 장소

#### 응답 (201 Created)
```json
{
  "success": true,
  "message": "연습 일정이 성공적으로 생성되었습니다.",
  "data": {
    "id": 1,
    "title": "주간 연습",
    "description": "이번 주 연습 일정입니다",
    "startDatetime": "2024-03-20T19:00:00",
    "endDatetime": "2024-03-20T21:00:00",
    "location": "연습실 A",
    "teamId": 1,
    "teamName": "밴드 팀",
    "creatorId": 1,
    "creatorName": "홍길동",
    "createdAt": "2024-03-15T10:30:00"
  }
}
```

---

## 8. 팀 연습 일정 목록 조회
### GET `/api/teams/{teamId}/practice-schedules?page=0&size=10`

#### 요청
```bash
curl "http://localhost:8080/api/teams/1/practice-schedules?page=0&size=10" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 쿼리 파라미터
- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 10)

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "연습 일정 목록을 성공적으로 조회했습니다.",
  "data": {
    "content": [
      {
        "id": 1,
        "title": "주간 연습",
        "description": "이번 주 연습 일정입니다",
        "startDatetime": "2024-03-20T19:00:00",
        "endDatetime": "2024-03-20T21:00:00",
        "location": "연습실 A",
        "teamId": 1,
        "teamName": "밴드 팀",
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
      "size": 10,
      "number": 0
    }
  }
}
```

---

## 9. 팀 연습 일정 상세 조회
### GET `/api/practice-schedules/{scheduleId}`

#### 요청
```bash
curl "http://localhost:8080/api/practice-schedules/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "연습 일정을 성공적으로 조회했습니다.",
  "data": {
    "id": 1,
    "title": "주간 연습",
    "description": "이번 주 연습 일정입니다",
    "startDatetime": "2024-03-20T19:00:00",
    "endDatetime": "2024-03-20T21:00:00",
    "location": "연습실 A",
    "teamId": 1,
    "teamName": "밴드 팀",
    "creatorId": 1,
    "creatorName": "홍길동",
    "createdAt": "2024-03-15T10:30:00"
  }
}
```

---

## 10. 팀 연습 일정 수정
### PATCH `/api/practice-schedules/{scheduleId}`

#### 요청
```bash
curl -X PATCH "http://localhost:8080/api/practice-schedules/1" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "수정된 연습",
    "location": "연습실 B"
  }'
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "연습 일정이 성공적으로 수정되었습니다.",
  "data": null
}
```

---

## 11. 팀 연습 일정 삭제
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
  "message": "연습 일정이 성공적으로 삭제되었습니다.",
  "data": null
}
```

## 에러 응답
```json
{
  "success": false,
  "message": "에러 메시지",
  "errorCode": "ERROR_CODE",
  "data": null
}
```

### 주요 에러 코드
- `TEAM_LEAVE_NOT_ALLOWED`: 팀 탈퇴 불가 (마지막 멤버인 경우)
- `RESOURCE_NOT_FOUND`: 팀 또는 리소스를 찾을 수 없음
- `UNAUTHORIZED_CLUB_ACCESS`: 권한 없음
- `BAD_REQUEST`: 잘못된 요청

### HTTP 상태 코드
- `200 OK`: 성공
- `201 Created`: 생성 성공
- `400 Bad Request`: 잘못된 요청
- `401 Unauthorized`: 인증 실패
- `403 Forbidden`: 권한 없음
- `404 Not Found`: 리소스 없음

## 참고사항
- **권한**:
  - **팀 생성**: 동아리 멤버만 가능
  - **동아리 팀 목록 조회**: 권한 제한 없음 (동아리 메인 페이지 접속용)
  - **팀 상세 조회**: 동아리 멤버만 가능
  - **팀 수정/삭제**: 팀 멤버라면 누구나 가능
  - **팀 탈퇴**: 팀 멤버만 가능 (단, 마지막 멤버는 제외)
- **팀 탈퇴 제한**: 마지막 남은 팀원은 탈퇴 불가 (팀 삭제 필요)
- **자동 멤버 추가**: 팀 생성자는 자동으로 첫 번째 멤버 등록
- **페이지네이션**: 기본 크기 5개, PagedRespDTO 구조 사용, 최신 생성순으로 정렬
- **소프트 삭제 시스템**:
  - **팀 삭제**: 실제 삭제가 아닌 `deleted_at` 설정으로 소프트 삭제
    - **연쇄 삭제**: 팀 삭제 시 관련 리소스들도 함께 소프트 삭제
      - `TeamMember`: 해당 팀의 모든 멤버 관계 소프트 삭제
      - `TeamEvent`: 해당 팀의 모든 연습 일정 소프트 삭제
    - **트랜잭션 보장**: 모든 관련 리소스가 동일한 시점에 일괄 처리
    - **데이터 일관성**: 동일한 `deleted_at` 시간으로 데이터 무결성 보장
  - **팀 멤버 탈퇴**: 실제 삭제가 아닌 `deleted_at` 설정으로 소프트 삭제
  - **자동 필터링**: 모든 조회 API에서 삭제된 팀/멤버는 자동으로 제외
  - **멤버 수 계산**: 삭제되지 않은 활성 멤버만 카운트
- **시간표 통합**: 팀 상세 조회 시 팀원들의 시간표 정보(`timetableData`, `isSubmitted` 등) 포함
- **스케줄 조율**: 팀 상세 조회 시 `suggestedScheduleAt`과 `submissionProgress`로 시간표 제출 현황 추적
