# Auth API 명세서

## Base URL
`/api/auth`

## 인증
- **로그인**: 인증 불필요 (카카오 OAuth 코드 필요)
- **회원가입**: 임시 JWT 토큰 필요 (카카오 로그인 후 발급받은 토큰)
- **토큰 재발급**: 인증 불필요 (Refresh Token 필요)

---

## 1. 카카오 로그인
### GET `/api/auth/login`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/auth/login?code={KAKAO_AUTH_CODE}"
```

#### 쿼리 파라미터
- `code` (string, 필수): 카카오 OAuth 인증 코드

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "로그인 성공",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "accessTokenExpiresIn": 3600000,
    "refreshTokenExpiresIn": 604800000
  }
}
```

#### 응답 필드
- `accessToken`: JWT 액세스 토큰
- `refreshToken`: JWT 리프레시 토큰
- `accessTokenExpiresIn`: 액세스 토큰 만료 시간 (밀리초)
- `refreshTokenExpiresIn`: 리프레시 토큰 만료 시간 (밀리초)

---

## 2. 회원가입
### POST `/api/auth/signup`

#### 요청
```bash
curl -X POST "http://localhost:8080/api/auth/signup" \
  -H "Authorization: Bearer {TEMP_JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "nickname": "홍길동",
    "position": "GUITAR",
    "universityId": 1
  }'
```

#### 요청 헤더
- `Authorization`: 카카오 로그인 후 발급받은 임시 JWT 토큰

#### 요청 필드
- `nickname` (string, 필수): 닉네임 (최대 100자)
- `position` (string, 필수): 음악 포지션 (VOCAL/GUITAR/KEYBOARD/BASS/DRUM/OTHER)
- `universityId` (integer, 필수): 대학교 ID

#### 응답 (200 OK)
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

---

## 3. 토큰 재발급
### POST `/api/auth/refresh`

#### 요청
```bash
curl -X POST "http://localhost:8080/api/auth/refresh" \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }'
```

#### 요청 필드
- `refreshToken` (string, 필수): 리프레시 토큰

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "토큰 재발급 성공",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "accessTokenExpiresIn": 3600000,
    "refreshTokenExpiresIn": 604800000
  }
}
```

---

## 포지션 값
- `VOCAL`: 보컬
- `GUITAR`: 기타
- `KEYBOARD`: 키보드
- `BASS`: 베이스
- `DRUM`: 드럼
- `OTHER`: 기타

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
- `404 Not Found`: 리소스 없음

### 주요 에러 케이스
- **잘못된 카카오 코드**: `InvalidKakaoCodeException` - 유효하지 않은 카카오 인증 코드입니다
- **만료된 토큰**: `ExpiredJwtException` - 토큰이 만료되었습니다
- **잘못된 토큰**: `InvalidTokenException` - 유효하지 않은 토큰입니다
- **중복 닉네임**: `DuplicateNicknameException` - 이미 사용 중인 닉네임입니다

## 참고사항
- **카카오 OAuth**: 카카오 개발자 콘솔에서 발급받은 인증 코드 사용
- **임시 토큰**: 회원가입 시 사용하는 토큰은 카카오 로그인 후 발급받은 임시 토큰
- **토큰 만료**: 액세스 토큰은 1시간, 리프레시 토큰은 7일 후 만료
- **자동 로그인**: 기존 사용자는 카카오 로그인 시 자동으로 JWT 토큰 발급 