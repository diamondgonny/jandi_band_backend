# User Timetable API

## 📅 개인 시간표 관리
JWT 인증 필요

---

## 1. 시간표 목록 조회
```
GET /api/users/me/timetables
Authorization: Bearer {JWT_TOKEN}
```

### 요청 예시
```bash
curl "http://localhost:8080/api/users/me/timetables" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 성공 응답 (200)
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

## 2. 시간표 상세 조회
```
GET /api/users/me/timetables/{timetableId}
Authorization: Bearer {JWT_TOKEN}
```

### 요청 예시
```bash
curl "http://localhost:8080/api/users/me/timetables/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 성공 응답 (200)
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

### 실패 응답
- **404**: 시간표 없음 또는 권한 없음

---

## 3. 시간표 생성
```
POST /api/users/me/timetables
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

### 요청 예시
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

### 요청 필드
- `name`: 시간표 이름 (공백 불가)
- `timetableData`: 요일별 시간 데이터
  - 모든 요일 포함 필요 (Mon, Tue, Wed, Thu, Fri, Sat, Sun)
  - 시간 형식: "HH:mm" (30분 단위만 허용)

### 성공 응답 (200)
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

### 실패 응답
- **400**: 잘못된 시간 형식, 중복 시간, 공백 이름

---

## 4. 시간표 수정
```
PATCH /api/users/me/timetables/{timetableId}
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

### 요청 예시
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

### 성공 응답 (200)
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
```
DELETE /api/users/me/timetables/{timetableId}
Authorization: Bearer {JWT_TOKEN}
```

### 요청 예시
```bash
curl -X DELETE "http://localhost:8080/api/users/me/timetables/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "시간표 삭제 성공",
  "data": null
}
```

---

## 📋 시간표 형식 규칙

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
