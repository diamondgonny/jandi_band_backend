# Auth API

## 🔑 인증 방식
- **로그인**: 카카오 OAuth 코드 필요
- **회원가입**: 임시 JWT 토큰 필요
- **토큰 재발급**: Refresh Token 필요

---

## 1. 카카오 로그인
```
GET /api/auth/login?code={KAKAO_AUTH_CODE}
```

### 요청 예시
```bash
curl "http://localhost:8080/api/auth/login?code=abc123"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "로그인 성공",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "isRegistered": true
  }
}
```

### 주요 필드
- `isRegistered`: `false`면 회원가입 필요

### 실패 응답
- **400**: 잘못된 카카오 코드
- **500**: 카카오 서버 오류

---

## 2. 회원가입
```
POST /api/auth/signup
Authorization: Bearer {TEMP_JWT_TOKEN}
```

### 요청 예시
```bash
curl -X POST "http://localhost:8080/api/auth/signup" \
  -H "Authorization: Bearer {TEMP_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "position": "GUITAR",
    "university": "서울대학교"
  }'
```

### 요청 필드
- `position`: VOCAL, GUITAR, KEYBOARD, BASS, DRUM, OTHER
- `university`: 대학교 이름

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "회원가입 성공",
  "data": {
    "id": 1,
    "nickname": "홍길동",
    "profilePhoto": "",
    "position": "GUITAR",
    "university": "서울대학교"
  }
}
```

### 실패 응답
- **400**: 이미 회원가입 완료된 계정
- **401**: 유효하지 않은 임시 토큰

---

## 3. 토큰 재발급
```
POST /api/auth/refresh
```

### 요청 예시
```bash
curl -X POST "http://localhost:8080/api/auth/refresh" \
  -H "Content-Type: application/json" \
  -d '{"refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."}'
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "토큰 재발급 성공",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### 실패 응답
- **401**: 만료되거나 유효하지 않은 Refresh Token

---

## 4. 로그아웃
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
  "message": "로그아웃 완료",
  "data": null
}
```

---

## 5. 회원탈퇴
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

## 🎵 포지션 값
- `VOCAL`: 보컬
- `GUITAR`: 기타
- `KEYBOARD`: 키보드
- `BASS`: 베이스
- `DRUM`: 드럼
- `OTHER`: 기타