# User API 명세서

## Base URL
`/api/users`

## 인증
JWT 인증 필요

---

## 1. 내 정보 조회
### GET `/api/users/me/info`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/users/me/info" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (200 OK)
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

#### 응답 필드
- `id`: 사용자 ID
- `nickname`: 닉네임
- `profilePhoto`: 프로필 사진 URL (없으면 빈 문자열)
- `position`: 음악 포지션 (VOCAL/GUITAR/KEYBOARD/BASS/DRUM/OTHER)
- `university`: 소속 대학교 이름

---

## 2. 내 정보 수정
### PATCH `/api/users/me/info`

#### 요청
```bash
curl -X PATCH "http://localhost:8080/api/users/me/info" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -F "nickname=새로운닉네임" \
  -F "position=VOCAL" \
  -F "university=연세대학교" \
  -F "profilePhoto=@/path/to/profile.jpg"
```

#### 요청 필드 (Form Data)
- `nickname` (string, 선택): 닉네임 (최대 100자)
- `position` (string, 선택): 음악 포지션 (VOCAL/GUITAR/KEYBOARD/BASS/DRUM/OTHER)
- `university` (string, 선택): 대학교 이름 (실제 등록된 대학교)
- `profilePhoto` (file, 선택): 프로필 사진 파일 (이미지 파일)

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "내 정보 수정 성공",
  "data": null
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
- `404 Not Found`: 사용자 없음

## 참고사항
- **Content-Type**: 수정 시 `multipart/form-data` 사용
- **부분 수정**: 원하는 필드만 전송 가능
- **파일 업로드**: 프로필 사진은 선택사항
