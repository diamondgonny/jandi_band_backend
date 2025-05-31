# Invite API

## 초대 링크 생성
JWT 인증 필요

---

## 1. 동아리 초대 링크 생성
```
POST /api/invite/clubs/{clubId}
Authorization: Bearer {JWT_TOKEN}
```

### 요청 예시
```bash
curl -X POST "http://localhost:8080/api/invite/clubs/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "동아리 초대 링크 생성 성공",
  "data": {
    "inviteLink": "http://localhost:8080/api/join/clubs?code=abc123def456",
    "expireTime": "2024-03-16T10:30:00"
  }
}
```

### 응답 필드
- `inviteLink`: 생성된 초대 링크
- `expireTime`: 링크 만료 시간

### 실패 응답
- **403**: 동아리 멤버가 아님
- **404**: 존재하지 않는 동아리

---

## 2. 팀 초대 링크 생성
```
POST /api/invite/teams/{teamId}
Authorization: Bearer {JWT_TOKEN}
```

### 요청 예시
```bash
curl -X POST "http://localhost:8080/api/invite/teams/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "팀 초대 링크 생성 성공",
  "data": {
    "inviteLink": "http://localhost:8080/api/join/teams?code=def456ghi789",
    "expireTime": "2024-03-16T10:30:00"
  }
}
```

### 응답 필드
- `inviteLink`: 생성된 초대 링크
- `expireTime`: 링크 만료 시간

### 실패 응답
- **403**: 팀 멤버가 아님
- **404**: 존재하지 않는 팀

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
- `401 Unauthorized`: 인증 실패
- `403 Forbidden`: 권한 없음
- `404 Not Found`: 리소스 없음
