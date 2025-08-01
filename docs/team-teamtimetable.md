# Team Timetable API

## Base URL
`/api/teams`

## 인증
JWT 인증 필요 (Spring Security + @AuthenticationPrincipal CustomUserDetails)
- 팀 스케줄 조율 제안 API는 별도 권한 필요
- 팀원의 시간표 관련 API는 본인만 사용 가능

---

## 1. 팀 스케줄 조율 제안 API ('시간 제출 중' 모드 시작)
### POST `/api/teams/{teamId}/schedule-suggestion`

#### 설명
팀 내에서 스케줄 조율을 시작하는 API입니다. `suggestedScheduleAt`을 현재 시간으로 설정하고 팀원이 시간표를 등록하면 해당 팀원의 `isSubmitted`가 true로 바뀝니다.

#### 요청
```bash
curl -X POST "http://localhost:8080/api/teams/1/schedule-suggestion" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 요청 필드
- 요청 바디 없음

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "스케줄 조율 모드가 시작되었습니다",
  "data": {
    "teamId": 1,
    "suggestedScheduleAt": "2024-05-15T10:30:00",
    "suggesterUserId": 123,
    "suggesterName": "김철수"
  }
}
```

---

## 2. 팀원의 시간표 등록 API
### POST `/api/teams/{teamId}/members/me/timetable`

#### 설명
팀원이 기존에 만들어둔 개인 시간표를 팀 시간표로 등록합니다. 본인이 생성한 개인 시간표만 사용할 수 있으며, 시간표 등록 시 `timetableUpdatedAt`이 현재 시간으로 자동 업데이트됩니다.

#### 요청
```bash
curl -X POST "http://localhost:8080/api/teams/1/members/me/timetable" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "userTimetableId": 5
  }'
```

#### 요청 필드
- `userTimetableId` (integer, 필수): 등록할 개인 시간표 ID

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "팀 시간표 등록 성공",
  "data": {
    "userId": 1,
    "teamId": 1,
    "timetableData": {
      "Mon": ["14:00", "15:00", "16:00"],
      "Tue": ["18:00", "19:00"],
      "Wed": ["14:00", "15:00"],
      "Thu": [],
      "Fri": ["17:00", "18:00"],
      "Sat": ["10:00", "11:00", "14:00"],
      "Sun": []
    },
    "updatedTimetableAt": "2024-05-15T11:00:00"
  }
}
```

---

## 3. 팀원의 시간표 수정 API
### PATCH `/api/teams/{teamId}/members/me/timetable`

#### 설명
팀원이 자신의 팀 시간표를 직접 수정합니다. 시간표 수정 시 `timetableUpdatedAt`이 현재 시간으로 자동 업데이트됩니다. (TODO: 만약 모든 수정으로 팀의 일원의 시간표를 제출한다면 팀원들에게 카톡 알림을 보내는 로직 추가)

#### 요청
```bash
curl -X PATCH "http://localhost:8080/api/teams/1/members/me/timetable" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "timetableData": {
      "Mon": ["14:00", "15:00", "16:00"],
      "Tue": ["18:00", "19:00"],
      "Wed": ["14:00", "15:00"],
      "Thu": [],
      "Fri": ["17:00", "18:00"],
      "Sat": ["10:00", "11:00", "14:00"],
      "Sun": []
    }
  }'
```

#### 요청 필드
- `timetableData` (JsonNode, 필수): 요일별 가능한 시간 데이터
  - 모든 요일(`Mon`, `Tue`, `Wed`, `Thu`, `Fri`, `Sat`, `Sun`) 포함 필요
  - 시간은 `"HH:mm"` 형식 (30분 단위만 사용)
  - 빈 배열 사용 (해당 요일 불가능)

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "팀 시간표 수정 성공",
  "data": {
    "userId": 1,
    "teamId": 1,
    "timetableData": {
      "Mon": ["14:00", "15:00", "16:00"],
      "Tue": ["18:00", "19:00"],
      "Wed": ["14:00", "15:00"],
      "Thu": [],
      "Fri": ["17:00", "18:00"],
      "Sat": ["10:00", "11:00", "14:00"],
      "Sun": []
    },
    "updatedTimetableAt": "2024-05-15T11:00:00"
  }
}
```

---

## 시간표 형식 규칙

### 요일 키
모든 요일 필수: `"Mon"`, `"Tue"`, `"Wed"`, `"Thu"`, `"Fri"`, `"Sat"`, `"Sun"`

### 시간 형식
- **형식**: `"HH:mm"` (24시간 형식)
- **범위**: `00:00` ~ `23:30`
- **단위**: 30분 단위만 사용 (분은 `00` 또는 `30`)
- **예시**: `"09:00"`, `"14:30"`, `"23:00"`

### 유효성 검사
- 같은 요일 내 중복 시간 불가
- 빈 배열 사용 (해당 요일 불가능)
- 시간표 입력/수정 시 `timetableUpdatedAt` 자동 업데이트

---

## 스케줄 조율 플로우

### 1단계: 스케줄 조율 시작
- 팀장/팀구성원이 `POST /teams/{teamId}/schedule-suggestion`로 조율 모드 시작
- `suggested_schedule_at`이 현재 시간으로 설정됨
- 팀원들에게 알림 발송 (프로덕티브 - 카카오톡 채널)

### 2단계: 시간표 입력
- 각 팀원이 `POST /teams/{teamId}/members/me/timetable`로 기존 개인 시간표 등록 (userTimetableId 사용)
- 또는 `PATCH /teams/{teamId}/members/me/timetable`로 시간표 직접 수정 (timetableData 직접 입력)
- `timetableUpdatedAt` 자동 업데이트로 제출 상황 추적
- `GET /teams/{teamId}`(팀 상세 조회)를 통해 전체 상황 확인

### 3단계: 결과 확인 및 일정 결정
- 프론트엔드에서 팀원들의 시간표를 오버레이한 기반으로 공통 가능 시간 계산
- 모든 팀원 입력 완료 시 알림 발송 (프로덕티브)
- 계산된 공통 가능 시간을 보고 팀 연습 일정 결정

---

## 에러 응답
```json
{
  "success": false,
  "message": "에러 메시지",
  "data": null
}
```

### 주요 에러 메시지
- `"시간표 ID는 필수입니다."` - userTimetableId가 null인 경우
- `"존재하지 않는 시간표입니다."` - 해당 ID의 시간표가 없거나 삭제된 경우
- `"시간표 소유자 정보를 찾을 수 없습니다."` - 시간표의 소유자 정보에 문제가 있는 경우
- `"권한이 없습니다: 본인의 시간표가 아닙니다"` - 다른 사용자의 시간표에 접근하려는 경우
- `"존재하지 않는 팀입니다."` - 해당 ID의 팀이 없거나 삭제된 경우
- `"본인의 시간표만 입력할 수 있습니다."` - 팀 멤버 권한 검증 실패

### HTTP 상태 코드
- `200 OK`: 성공
- `400 Bad Request`: 잘못된 요청 (시간표 형식 오류, 필수값 누락 등)
- `401 Unauthorized`: 인증 실패
- `403 Forbidden`: 동아리 부원 및 권한 없음
- `404 Not Found`: 팀 또는 시간표 없음

## 현재 구현 상태

### 완전 구현 완료
- `POST /teams/{teamId}/schedule-suggestion` - 스케줄 조율 제안
- `POST /teams/{teamId}/members/me/timetable` - 팀 시간표 등록 (개인 시간표 참조)
- `PATCH /teams/{teamId}/members/me/timetable` - 팀 시간표 수정 (직접 입력)

## 참고 항목
- **시간표 데이터**: 시간표 입력/수정 시 `timetableUpdatedAt` 자동 업데이트
- **공통 시간 계산**: 프론트엔드에서 팀원들의 시간표를 오버레이한 기반으로 계산
- **알림**: 중요한 계마다 프로덕티브(카카오톡 채널)를 통해 개인 알림 발송
- **시간표 등록 방식**: POST로 기존 개인 시간표 참조, PATCH로 시간표 직접 수정
- **조회 기능**: 팀원들의 시간표 목록을 조회하는 기능을 통해 팀 세부 조회 API (`GET /teams/{teamId}`)를 통해 전체 조회
