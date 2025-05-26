# Team Timetable API 명세서

## Base URL
`/api/teams/{teamId}`

## 인증
JWT 인증 필요 + GET은 동아리 부원 권한 필요 (단, POST PATCH DELETE는 팀원 권한 필요)

---

## 1. 팀내 스케줄 조율 제안 API ('시간 언제 돼? 모드' 시작)
### POST `/api/teams/{teamId}/schedule-suggestion`

#### 설명
팀 내에서 스케줄 조율을 시작하는 API입니다. `suggested_schedule_at`을 현재 시간으로 설정하여 팀원들이 시간표를 입력할 수 있는 모드를 활성화합니다. (TODO: 팀원들에게 시간 잡자는 카톡 알림을 보내는 로직 추가, 단 팀원 수가 1이면 카카오 알림을 보내지 않습니다.)

#### 요청
```bash
curl -X POST "http://localhost:8080/api/teams/1/schedule-suggestion" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "이번 주 연습 시간 조율해요!"
  }'
```

#### 요청 필드
- `message` (string, 선택): 스케줄 조율 관련 메시지

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "스케줄 조율 모드가 시작되었습니다",
  "data": {
    "teamId": 1,
    "suggestedScheduleAt": "2024-05-15T10:30:00"
  }
}
```

---

## 2. 팀내 팀원들 시간표 목록 조회 API
### GET `/api/teams/{teamId}/timetables`

#### 설명
팀 내 모든 팀원들의 시간표 현황을 조회합니다. 각 팀원의 시간표 입력 상태를 확인할 수 있으며, 프론트엔드에서 공통 가능 시간 계산에 필요한 데이터를 제공합니다. isSubmitted 판정은 suggestedScheduleAt 이후에 팀원이 시간표를 업데이트 했는지 여부에 따라 결정됩니다. timetableUpdatedAt이 null인 경우도 false입니다. 단, suggestedScheduleAt이 null이면 예외를 날립니다.

#### 요청
```bash
curl -X GET "http://localhost:8080/api/teams/1/timetables" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "팀 시간표 목록 조회 성공",
  "data": {
    "teamInfo": {
      "id": 1,
      "name": "록밴드팀",
      "suggestedScheduleAt": "2024-05-15T10:30:00",
      "isScheduleActive": true
    },
    "members": [
      {
        "userId": 1,
        "username": "김철수",
        "position": "보컬",
        "timetableUpdatedAt": "2024-05-15T11:00:00",
        "isSubmitted": true,
        "timetableData": {
          "Mon": ["14:00", "15:00", "16:00"],
          "Tue": ["18:00", "19:00"],
          "Wed": ["14:00", "15:00"],
          "Thu": [],
          "Fri": ["17:00", "18:00"],
          "Sat": ["10:00", "11:00"],
          "Sun": []
        }
      },
      {
        "userId": 2,
        "username": "이영희",
        "position": "기타",
        "timetableUpdatedAt": null,
        "isSubmitted": false,
        "timetableData": null
      }
    ],
    "submissionProgress": {
      "submitted": 3,
      "total": 5
    }
  }
}
```

---

## 3. 팀내 내 시간표 입력 API
### POST `/api/teams/{teamId}/members/me/timetable`

#### 설명
팀원이 자신의 가능한 시간표를 입력합니다. 시간표 입력시 `updatedTimetableAt`이 현재 시간으로 자동 업데이트됩니다. (TODO: 만약 이 입력으로 팀원 전원이 시간표를 제출했다면 팀원들에게 카톡 알림을 보내는 로직 추가)

#### 요청
```bash
curl -X POST "http://localhost:8080/api/teams/1/members/me/timetable" \
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
- `timetableData` (object, 필수): 요일별 가능한 시간 데이터
  - 모든 요일(`Mon`, `Tue`, `Wed`, `Thu`, `Fri`, `Sat`, `Sun`) 포함 필요
  - 시간은 `"HH:mm"` 형식 (30분 단위만 허용)
  - 빈 배열 허용 (해당 요일 불가능)

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "팀 시간표 입력 성공",
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
    "updatedAt": "2024-05-15T11:00:00"
  }
}
```

---

## 4. 팀내 내 시간표 조회 API
### GET `/api/teams/{teamId}/members/me/timetable`

#### 설명
팀에서 내가 입력한 시간표를 조회합니다.

#### 요청
```bash
curl -X GET "http://localhost:8080/api/teams/1/members/me/timetable" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "내 팀 시간표 조회 성공",
  "data": {
    "userId": 1,
    "teamId": 1,
    "timetableData": {
      "Mon": ["14:00", "15:00"],
      "Tue": ["18:00", "19:00", "20:00"],
      "Wed": ["14:00"],
      "Thu": ["16:00"],
      "Fri": ["17:00", "18:00"],
      "Sat": ["10:00", "11:00"],
      "Sun": []
    },
    "updatedAt": "2024-05-15T11:30:00"
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
- **단위**: 30분 단위만 허용 (분은 `00` 또는 `30`)
- **예시**: `"09:00"`, `"14:30"`, `"23:00"`

### 유효성 검사
- 같은 요일 내 중복 시간 불가
- 빈 배열 허용 (해당 요일 불가능)
- 시간표 입력/수정시 `updatedTimetableAt` 자동 업데이트

---

## 스케줄 조율 플로우

### 1단계: 스케줄 조율 시작
- 팀원 중 누구나 `POST /teams/{teamId}/schedule-suggestion`로 조율 모드 시작
- `suggested_schedule_at`이 현재 시간으로 설정됨
- 팀원들에게 알림 발송 (써드파티 - 카카오톡 채널)

### 2단계: 시간표 입력
- 각 팀원이 `POST` 또는 `PATCH /teams/{teamId}/members/me/timetable`로 시간표 입력/수정
- `updatedTimetableAt` 자동 업데이트로 제출 현황 추적
- `GET /teams/{teamId}/timetables`를 통해 전체 현황 확인

### 3단계: 결과 확인 및 일정 확정
- 프론트엔드에서 팀원들의 시간표 데이터를 기반으로 공통 가능 시간 계산
- 모든 팀원 입력 완료시 알림 발송 (써드파티)
- 계산된 공통 가능 시간을 보고 팀 연습 일정 확정

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
- `400 Bad Request`: 잘못된 요청 (시간표 형식 오류 등)
- `401 Unauthorized`: 인증 실패
- `403 Forbidden`: 동아리 부원 권한 없음
- `404 Not Found`: 팀 없음

## 참고사항
- **권한**: 해당 팀이 속한 동아리의 부원만 접근 가능하나, 쓰기/수정/삭제는 팀원 권한 필요
- **시간표 업데이트**: 시간표 입력/수정시 `updatedTimetableAt` 자동 업데이트
- **공통 시간 계산**: 프론트엔드에서 팀원들의 시간표 데이터를 기반으로 계산
- **알림**: 중요한 단계마다 써드파티(카카오톡 채널)를 통한 개인 알림 발송
