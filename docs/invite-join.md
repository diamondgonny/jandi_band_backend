# Invite & Join API

## 🤝 초대 및 가입
JWT 인증 필요

---

## 1. 동아리 초대 코드 생성
```
POST /api/invites/clubs/{clubId}
Authorization: Bearer {JWT_TOKEN}
```

### 요청 예시
```bash
curl -X POST "http://localhost:8080/api/invites/clubs/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 성공 응답 (201)
```json
{
  "success": true,
  "message": "동아리 초대 코드가 생성되었습니다.",
  "data": {
    "inviteCode": "ABC123DEF",
    "clubId": 1,
    "clubName": "락밴드 동아리",
    "expiresAt": "2024-03-22T10:30:00"
  }
}
```

### 실패 응답
- **403**: 동아리 대표자가 아님

---

## 2. 팀 초대 코드 생성
```
POST /api/invites/teams/{teamId}
Authorization: Bearer {JWT_TOKEN}
```

### 요청 예시
```bash
curl -X POST "http://localhost:8080/api/invites/teams/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 성공 응답 (201)
```json
{
  "success": true,
  "message": "팀 초대 코드가 생성되었습니다.",
  "data": {
    "inviteCode": "XYZ789GHI",
    "teamId": 1,
    "teamName": "밴드 팀",
    "expiresAt": "2024-03-22T10:30:00"
  }
}
```

### 실패 응답
- **403**: 팀 생성자가 아님

---

## 3. 초대 코드로 가입
```
POST /api/joins/{inviteCode}
Authorization: Bearer {JWT_TOKEN}
```

### 요청 예시
```bash
curl -X POST "http://localhost:8080/api/joins/ABC123DEF" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "성공적으로 가입되었습니다.",
  "data": {
    "clubId": 1,
    "clubName": "락밴드 동아리",
    "teamId": null,
    "teamName": null,
    "joinedAt": "2024-03-15T10:30:00"
  }
}
```

### 응답 필드
- `clubId`/`teamId`: 둘 중 하나만 값이 있음
- `clubName`/`teamName`: 해당하는 이름만 값이 있음

### 실패 응답
- **400**: 만료된 초대 코드
- **404**: 존재하지 않는 초대 코드
- **409**: 이미 가입된 멤버

---

## 4. 초대 코드 조회
```
GET /api/invites/{inviteCode}
```

### 요청 예시
```bash
curl "http://localhost:8080/api/invites/ABC123DEF"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "초대 정보 조회 성공",
  "data": {
    "inviteCode": "ABC123DEF",
    "clubId": 1,
    "clubName": "락밴드 동아리",
    "teamId": null,
    "teamName": null,
    "expiresAt": "2024-03-22T10:30:00"
  }
}
```

### 실패 응답
- **404**: 존재하지 않는 초대 코드

---

## 📋 초대 코드 규칙
- **유효 기간**: 7일
- **형식**: 9자리 영숫자 (대문자)
- **일회성**: 사용 후에도 유효 (여러 명 가입 가능)
- **권한**: 동아리는 대표자만, 팀은 생성자만 생성 가능

## 프론트 예시
https://github.com/user-attachments/assets/9fe66dad-f867-4843-ab61-ec7f7e8fea76

