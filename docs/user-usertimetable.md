# User Timetable API

## Base URL
`/api/users`

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
      "name": "2024년 1학기 시간표"
    },
    {
      "id": 2,
      "name": "개인 연습 시간표"
    }
  ]
}
```

---

## 2. 내 특정 시간표 조회
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
    "name": "2024년 1학기 시간표",
    "timetableData": {
      "Mon": ["09:00", "10:00", "11:00"],
      "Tue": ["14:00", "15:00"],
      "Wed": ["09:00", "10:00"],
      "Thu": ["16:00", "17:00"],
      "Fri": ["13:00", "14:00"],
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
    "name": "새 시간표",
    "timetableData": {
      "Mon": ["09:00", "10:00", "11:00"],
      "Tue": ["14:00", "15:00"],
      "Wed": ["09:00", "10:00"],
      "Thu": ["16:00", "17:00"],
      "Fri": ["13:00", "14:00"],
      "Sat": [],
      "Sun": []
    }
  }'
```

#### 요청 필드
- `name` (string, 필수): 시간표 이름
- `timetableData` (object, 필수): 요일별 시간 데이터
  - 모든 요일(`Mon`, `Tue`, `Wed`, `Thu`, `Fri`, `Sat`, `Sun`) 포함 필요
  - 시간은 `"HH:mm"` 형식 (30분 단위만 사용)
  - 빈 배열 사용 (해당 요일 불가능)

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "시간표 생성 성공",
  "data": {
    "id": 3,
    "name": "새 시간표",
    "timetableData": {
      "Mon": ["09:00", "10:00", "11:00"],
      "Tue": ["14:00", "15:00"],
      "Wed": ["09:00", "10:00"],
      "Thu": ["16:00", "17:00"],
      "Fri": ["13:00", "14:00"],
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
      "Mon": ["10:00", "11:00"],
      "Tue": ["14:00", "15:00", "16:00"],
      "Wed": ["09:00", "10:00"],
      "Thu": [],
      "Fri": ["13:00", "14:00"],
      "Sat": ["10:00"],
      "Sun": []
    }
  }'
```

#### 요청 필드
- `name` (string, 선택): 시간표 이름
- `timetableData` (object, 선택): 요일별 시간 데이터

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "시간표 수정 성공",
  "data": {
    "id": 1,
    "name": "수정된 시간표",
    "timetableData": {
      "Mon": ["10:00", "11:00"],
      "Tue": ["14:00", "15:00", "16:00"],
      "Wed": ["09:00", "10:00"],
      "Thu": [],
      "Fri": ["13:00", "14:00"],
      "Sat": ["10:00"],
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
- **단위**: 30분 단위만 사용 (분은 `00` 또는 `30`)
- **예시**: `"09:00"`, `"14:30"`, `"23:00"`

### 유효성 검사
- 같은 요일 내 중복 시간 불가
- 빈 배열 사용 (해당 요일 불가능)

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
- `403 Forbidden`: 권한 없음
- `404 Not Found`: 시간표 없음

## 참고 항목
- **개인 시간표**: 사용자별로 독립적으로 관리
- **팀 시간표 연동**: 개인 시간표를 팀 시간표로 등록 가능 (team-teamtimetable.md 참조)
- **시간 형식**: 30분 단위로만 입력 가능
- **요일 형식**: 영문 3글자 약어 사용 (Mon, Tue, Wed, Thu, Fri, Sat, Sun)
