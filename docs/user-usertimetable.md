# User Timetable API 명세서

## Base URL
`/api/users/me/timetables`

## 인증
JWT 인증 필요 (Spring Security + @AuthenticationPrincipal CustomUserDetails)

---

## 1. 내 시간표 목록 조회
### GET `/api/users/me/timetables`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/users/me/timetables" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "내 시간표 목록 조회 성공",
  "data": [
    {
      "id": 1,
      "name": "2024학년도 1학기"
    }
  ]
}
```

---

## 2. 특정 시간표 조회
### GET `/api/users/me/timetables/{timetableId}`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/users/me/timetables/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "내 시간표 조회 성공",
  "data": {
    "id": 1,
    "name": "2024학년도 1학기",
    "timetableData": {
      "Mon": ["09:00", "10:30", "14:00"],
      "Tue": ["11:00", "13:30"],
      "Wed": ["09:00", "15:30"],
      "Thu": ["10:00", "14:30"],
      "Fri": ["09:30", "11:00"],
      "Sat": [],
      "Sun": []
    }
  }
}
```

---

## 3. 시간표 생성
### POST `/api/users/me/timetables`

#### 요청
```bash
curl -X POST "http://localhost:8080/api/users/me/timetables" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "2024학년도 1학기",
    "timetableData": {
      "Mon": ["09:00", "10:30"],
      "Tue": ["11:00", "13:30"],
      "Wed": ["09:00"],
      "Thu": ["10:00"],
      "Fri": ["09:30"],
      "Sat": [],
      "Sun": []
    }
  }'
```

#### 요청 필드
- `name` (string, 필수): 시간표 이름 (공백 불가)
- `timetableData` (object, 필수): 요일별 시간 데이터
  - 모든 요일(`Mon`, `Tue`, `Wed`, `Thu`, `Fri`, `Sat`, `Sun`) 포함 필요
  - 시간은 `"HH:mm"` 형식 (30분 단위만 허용)
  - 같은 요일 내 중복 시간 불가

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "시간표 생성 성공",
  "data": {
    "id": 1,
    "name": "2024학년도 1학기",
    "timetableData": {
      "Mon": ["09:00", "10:30"],
      "Tue": ["11:00", "13:30"],
      "Wed": ["09:00"],
      "Thu": ["10:00"],
      "Fri": ["09:30"],
      "Sat": [],
      "Sun": []
    }
  }
}
```

---

## 4. 시간표 수정
### PATCH `/api/users/me/timetables/{timetableId}`

#### 요청
```bash
curl -X PATCH "http://localhost:8080/api/users/me/timetables/1" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "수정된 시간표",
    "timetableData": {
      "Mon": ["09:00", "14:00"],
      "Tue": ["11:00"],
      "Wed": [],
      "Thu": ["10:00"],
      "Fri": ["09:30"],
      "Sat": [],
      "Sun": []
    }
  }'
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "시간표 수정 성공",
  "data": {
    "id": 1,
    "name": "수정된 시간표",
    "timetableData": {
      "Mon": ["09:00", "14:00"],
      "Tue": ["11:00"],
      "Wed": [],
      "Thu": ["10:00"],
      "Fri": ["09:30"],
      "Sat": [],
      "Sun": []
    }
  }
}
```

---

## 5. 시간표 삭제
### DELETE `/api/users/me/timetables/{timetableId}`

#### 요청
```bash
curl -X DELETE "http://localhost:8080/api/users/me/timetables/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "시간표 삭제 성공",
  "data": null
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
- 시간표 이름 공백 불가
- 같은 요일 내 중복 시간 불가
- 빈 배열 허용 (해당 요일 일정 없음)

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
- `404 Not Found`: 시간표 없음 또는 권한 없음

## 참고사항
- **소프트 삭제**: 실제 데이터는 유지되고 `deletedAt` 설정
- **권한**: 본인의 시간표만 조회/수정/삭제 가능
