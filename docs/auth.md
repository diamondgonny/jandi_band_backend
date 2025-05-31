# Auth API

## 인증 방식
JWT 토큰 기반 인증

---

## 1. 로그인
```
GET /api/auth/login
```

### 요청 예시
```bash
curl "http://localhost:8080/api/auth/login"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "로그인 성공",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "isRegistered": true,
    "user": {
      "id": 1,
      "email": "user@example.com",
      "nickname": "홍길동",
      "profilePhoto": "https://example.com/profile.jpg",
      "position": "GUITAR",
      "universityId": 1,
      "universityName": "서울대학교"
    }
  }
}
```

---

## 2. 로그아웃
```
POST /api/auth/logout
Authorization: Bearer {JWT_TOKEN}
```

### 요청 예시
```bash
curl -X POST "http://localhost:8080/api/auth/logout" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "로그아웃 성공",
  "data": null
}
```

---

## 3. 회원가입
```
POST /api/auth/signup
Content-Type: application/json
```

### 요청 예시
```bash
curl -X POST "http://localhost:8080/api/auth/signup" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "nickname": "홍길동",
    "position": "GUITAR",
    "universityId": 1
  }'
```

### 요청 필드
- `email`: 이메일 주소
- `nickname`: 닉네임
- `position`: 포지션 (VOCAL, GUITAR, KEYBOARD, BASS, DRUM)
- `universityId`: 대학교 ID

### 성공 응답 (201)
```json
{
  "success": true,
  "message": "회원가입 성공",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "nickname": "홍길동",
    "position": "GUITAR",
    "universityId": 1,
    "universityName": "서울대학교"
  }
}
```

---

## 4. 회원탈퇴
```
POST /api/auth/cancel
Authorization: Bearer {JWT_TOKEN}
```

### 요청 예시
```bash
curl -X POST "http://localhost:8080/api/auth/cancel" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "회원탈퇴 성공",
  "data": null
}
```

---

## 5. 토큰 갱신
```
POST /api/auth/refresh
Content-Type: application/json
```

### 요청 예시
```bash
curl -X POST "http://localhost:8080/api/auth/refresh" \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }'
```

### 요청 필드
- `refreshToken`: 리프레시 토큰

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "토큰 갱신 성공",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### 실패 응답
- **401**: 유효하지 않은 리프레시 토큰

## 포지션 값
- `VOCAL`: 보컬
- `GUITAR`: 기타
- `KEYBOARD`: 키보드
- `BASS`: 베이스
- `DRUM`: 드럼