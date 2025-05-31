# User API

## 사용자 정보 관리
JWT 인증 필요

---

## 1. 내 정보 조회
```
GET /api/users/me/info
Authorization: Bearer {JWT_TOKEN}
```

### 요청 예시
```bash
curl "http://localhost:8080/api/users/me/info" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "내 정보 조회 성공",
  "data": {
    "id": 1,
    "nickname": "홍길동",
    "profilePhoto": "https://example.com/profile.jpg",
    "position": "GUITAR",
    "university": "서울대학교"
  }
}
```

### 응답 필드
- `id`: 사용자 ID
- `nickname`: 닉네임
- `profilePhoto`: 프로필 사진 URL (없으면 빈 문자열)
- `position`: 음악 포지션
- `university`: 소속 대학교 이름

### 실패 응답
- **401**: 인증 실패

---

## 2. 내 정보 수정
```
PATCH /api/users/me/info
Authorization: Bearer {JWT_TOKEN}
Content-Type: multipart/form-data
```

### 요청 예시
```bash
curl -X PATCH "http://localhost:8080/api/users/me/info" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -F "nickname=새로운닉네임" \
  -F "position=VOCAL" \
  -F "university=연세대학교" \
  -F "profilePhoto=@/path/to/profile.jpg"
```

### 요청 필드 (모두 선택)
- `nickname`: 닉네임 (최대 100자)
- `position`: 음악 포지션
- `university`: 대학교 이름
- `profilePhoto`: 프로필 사진 파일

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "내 정보 수정 성공",
  "data": null
}
```

### 실패 응답
- **400**: 잘못된 요청 (닉네임 중복, 존재하지 않는 대학교 등)
- **401**: 인증 실패

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

## 포지션 값
- `VOCAL`: 보컬
- `GUITAR`: 기타
- `KEYBOARD`: 키보드
- `BASS`: 베이스
- `DRUM`: 드럼
- `OTHER`: 기타

## 참고사항
- **파일 업로드**: @ModelAttribute와 @RequestPart 혼합 사용
- **부분 수정**: 전송된 필드만 수정, 나머지는 기존 값 유지
- **프로필 사진**: 새 파일 업로드 시 기존 사진 자동 교체
