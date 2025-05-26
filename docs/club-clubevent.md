# Club Event API 명세서

## Base URL
`/api/clubs/{clubId}/events`

## 인증
JWT 인증 필요 (Spring Security + @AuthenticationPrincipal CustomUserDetails)

## 권한 관리
- **조회**: 로그인한 모든 사용자
- **생성**: 로그인한 모든 사용자  
- **삭제**: 일정을 생성한 사용자만 가능

---

## 1. 동아리 일정 생성
### POST `/api/clubs/{clubId}/events`

#### 경로 파라미터
- `clubId` (integer, 필수): 동아리 ID

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
- `name` (string, 필수): 이벤트 이름 (최대 255자)
- `startDatetime` (string, 필수): 시작 일시 (ISO 8601 형식: YYYY-MM-DDTHH:mm:ss)
- `endDatetime` (string, 필수): 종료 일시 (시작 일시 이후여야 함)
- `location` (string, 선택): 장소명 (최대 255자)
- `address` (string, 선택): 상세 주소 (최대 255자)
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

#### 경로 파라미터
- `clubId` (integer, 필수): 동아리 ID
- `eventId` (integer, 필수): 일정 ID

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

#### 경로 파라미터
- `clubId` (integer, 필수): 동아리 ID
- `year` (integer, 필수): 조회할 연도 (예: 2024)
- `month` (integer, 필수): 조회할 월 (1-12)

#### 요청
```bash
curl -X GET "http://localhost:8080/api/clubs/1/events/list/2024/3" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

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

#### 경로 파라미터
- `clubId` (integer, 필수): 동아리 ID
- `eventId` (integer, 필수): 삭제할 일정 ID

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

#### 접근 권한
- 일정을 생성한 사용자만 삭제 가능
- 권한이 없는 경우 403 Forbidden 응답

---

## 공통 에러 응답

### 400 Bad Request - 잘못된 요청
```json
{
  "success": false,
  "message": "필수 필드가 누락되었습니다.",
  "errorCode": "BAD_REQUEST"
}
```
**발생 케이스**: 필수 필드 누락, 잘못된 날짜 형식, 종료일시가 시작일시보다 빠른 경우

### 401 Unauthorized - 인증 실패
```json
{
  "success": false,
  "message": "유효하지 않은 토큰입니다.",
  "errorCode": "INVALID_TOKEN"
}
```
**발생 케이스**: JWT 토큰이 없거나 유효하지 않은 경우

### 403 Forbidden - 권한 없음
```json
{
  "success": false,
  "message": "일정을 삭제할 권한이 없습니다.",
  "errorCode": "FORBIDDEN"
}
```
**발생 케이스**: 일정 삭제 시 생성자가 아닌 경우

### 404 Not Found - 리소스 없음
```json
{
  "success": false,
  "message": "해당 동아리에 속한 일정을 찾을 수 없습니다.",
  "errorCode": "NOT_FOUND"
}
```
**발생 케이스**: 존재하지 않는 동아리 ID 또는 일정 ID

---

## 데이터 모델

### ClubEventReqDTO (요청)
```typescript
interface ClubEventReqDTO {
  name: string;           // 일정 이름 (필수, 최대 255자)
  startDatetime: string;  // 시작 시간 (필수, LocalDateTime)
  endDatetime: string;    // 종료 시간 (필수, LocalDateTime, 시작시간 이후)
  location?: string;      // 장소명 (선택, 최대 255자)
  address?: string;       // 주소 (선택, 최대 255자)
  description?: string;   // 설명 (선택)
}
```

### ClubEventRespDTO (응답)
```typescript
interface ClubEventRespDTO {
  id: number;            // 일정 ID (Long 타입)
  name: string;          // 일정 이름
  startDatetime: string; // 시작 시간 (LocalDateTime)
  endDatetime: string;   // 종료 시간 (LocalDateTime)
  location: string;      // 장소명 (null 가능)
  address: string;       // 주소 (null 가능)
  description: string;   // 설명 (null 가능)
}
```

## 참고사항

### 소프트 삭제
- 삭제된 일정은 DB에서 물리적으로 제거되지 않고 `deletedAt` 필드로 관리
- 조회 시 삭제된 일정은 자동으로 제외됨

### 데이터 타입
- **ID**: Entity는 Integer, 응답 DTO는 Long으로 변환
- **날짜시간**: LocalDateTime 타입 사용 (JSON 직렬화 시 ISO 8601 형식)
