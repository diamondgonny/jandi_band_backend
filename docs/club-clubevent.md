# Club Event API 명세서

## Base URL
`/api/clubs/{clubId}/events`

## 인증
- JWT 인증 필요 (Spring Security의 @AuthenticationPrincipal 사용)
- Authorization 헤더: `Bearer {JWT_TOKEN}`

---

## 1. 동아리 일정 생성
### POST `/api/clubs/{clubId}/events`

#### 요청
```bash
curl -X POST "http://localhost:8080/api/clubs/1/events" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "2024년 정기 공연",
    "startDatetime": "2024-03-15T19:00:00",
    "endDatetime": "2024-03-15T21:30:00",
    "location": "대학교 대강당",
    "address": "서울특별시 강남구 테헤란로 123",
    "description": "우리 밴드의 첫 번째 정기 공연입니다."
  }'
```

#### 요청 필드
- `name` (string, 필수): 이벤트 이름
- `startDatetime` (string, 필수): 시작 일시 (ISO 8601 형식)
- `endDatetime` (string, 필수): 종료 일시 (시작 일시 이후)
- `location` (string, 선택): 장소명
- `address` (string, 선택): 상세 주소
- `description` (string, 선택): 이벤트 설명

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "동아리 일정이 생성되었습니다.",
  "data": {
    "id": 15,
    "name": "2024년 정기 공연",
    "startDatetime": "2024-03-15T19:00:00",
    "endDatetime": "2024-03-15T21:30:00",
    "location": "대학교 대강당",
    "address": "서울특별시 강남구 테헤란로 123",
    "description": "우리 밴드의 첫 번째 정기 공연입니다."
  }
}
```

---

## 2. 동아리 일정 상세 조회
### GET `/api/clubs/{clubId}/events/{eventId}`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/clubs/1/events/15" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "동아리 일정 상세 조회 성공",
  "data": {
    "id": 15,
    "name": "2024년 정기 공연",
    "startDatetime": "2024-03-15T19:00:00",
    "endDatetime": "2024-03-15T21:30:00",
    "location": "대학교 대강당",
    "address": "서울특별시 강남구 테헤란로 123",
    "description": "우리 밴드의 첫 번째 정기 공연입니다."
  }
}
```

---

## 3. 동아리 일정 목록 조회 (월별)
### GET `/api/clubs/{clubId}/events/list/{year}/{month}`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/clubs/1/events/list/2024/3" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 경로 파라미터
- `clubId` (integer): 동아리 ID
- `year` (integer): 조회할 연도 (예: 2024)
- `month` (integer): 조회할 월 (1-12)

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "동아리 일정 목록 조회 성공",
  "data": [
    {
      "id": 15,
      "name": "2024년 정기 공연",
      "startDatetime": "2024-03-15T19:00:00",
      "endDatetime": "2024-03-15T21:30:00",
      "location": "대학교 대강당",
      "address": "서울특별시 강남구 테헤란로 123",
      "description": "우리 밴드의 첫 번째 정기 공연입니다."
    },
    {
      "id": 16,
      "name": "합주 연습",
      "startDatetime": "2024-03-20T18:00:00",
      "endDatetime": "2024-03-20T20:00:00",
      "location": "연습실 A",
      "address": null,
      "description": "정기 공연 준비 연습"
    }
  ]
}
```

---

## 4. 동아리 일정 삭제
### DELETE `/api/clubs/{clubId}/events/{eventId}`

#### 요청
```bash
curl -X DELETE "http://localhost:8080/api/clubs/1/events/15" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "동아리 일정이 삭제되었습니다.",
  "data": null
}
```

#### 권한
- 일정을 생성한 사용자만 삭제 가능

---

## 공통 에러 응답

### 400 Bad Request
```json
{
  "success": false,
  "message": "필수 필드가 누락되었습니다.",
  "errorCode": "BAD_REQUEST"
}
```

### 401 Unauthorized
```json
{
  "success": false,
  "message": "유효하지 않은 토큰입니다.",
  "errorCode": "INVALID_TOKEN"
}
```

### 403 Forbidden
```json
{
  "success": false,
  "message": "일정을 삭제할 권한이 없습니다.",
  "errorCode": "FORBIDDEN"
}
```

### 404 Not Found
```json
{
  "success": false,
  "message": "해당 동아리에 속한 일정을 찾을 수 없습니다.",
  "errorCode": "NOT_FOUND"
}
```

---

## 주요 변경사항 (2024.12 업데이트)

### 인증 방식
- **이전**: Authorization 헤더 직접 파싱
- **현재**: Spring Security의 @AuthenticationPrincipal 사용 (자동 JWT 처리)

### 응답 형식
- **이전**: 직접 DTO 반환
- **현재**: CommonResponse 래퍼로 통일된 응답 형식

### API 경로
- **이전**: `/api/clubs/{clubId}/events/add`
- **현재**: `/api/clubs/{clubId}/events` (RESTful)

### 시간대 처리
- **변경**: 전역 KST 사용으로 시간대 파라미터 제거

## 데이터 모델

### ClubEventReqDTO (요청)
```typescript
interface ClubEventReqDTO {
  name: string;           // 일정 이름
  startDatetime: string;  // 시작 시간 (ISO 8601)
  endDatetime: string;    // 종료 시간 (ISO 8601)
  location?: string;      // 장소명 (선택)
  address?: string;       // 주소 (선택)
  description?: string;   // 설명 (선택)
}
```

### ClubEventRespDTO (응답)
```typescript
interface ClubEventRespDTO {
  id: number;            // 일정 ID
  name: string;          // 일정 이름
  startDatetime: string; // 시작 시간
  endDatetime: string;   // 종료 시간
  location: string;      // 장소명
  address: string;       // 주소
  description: string;   // 설명
}
```
