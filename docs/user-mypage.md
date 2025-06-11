# MyPage API

## 마이페이지
JWT 인증 필요

---

## 1. 내가 참가한 동아리 목록
```
GET /api/my/clubs
Authorization: Bearer {JWT_TOKEN}
```

### 요청 예시
```bash
curl "http://localhost:8080/api/my/clubs" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "내가 참가한 동아리 목록 조회 성공",
  "data": [
    {
      "id": 1,
      "name": "락밴드 동아리",
      "description": "음악을 사랑하는 사람들의 모임",
      "photoUrl": "https://example.com/club-photo.jpg",
      "universityName": "서울대학교",
      "isUnionClub": false,
      "myRole": "REPRESENTATIVE",
      "joinedAt": "2024-03-15T10:30:00",
      "memberCount": 15
    }
  ]
}
```

### 응답 필드
- `myRole`: 내 역할 (`REPRESENTATIVE` 또는 `MEMBER`)
- `isUnionClub`: 연합동아리 여부
- `joinedAt`: 가입 일시

---

## 2. 내가 참가한 팀 목록
```
GET /api/my/teams
Authorization: Bearer {JWT_TOKEN}
```

### 요청 예시
```bash
curl "http://localhost:8080/api/my/teams" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "내가 참가한 팀 목록 조회 성공",
  "data": [
    {
      "id": 1,
      "name": "밴드 팀",
      "description": null,
      "clubId": 1,
      "clubName": "락밴드 동아리",
      "creatorId": 2,
      "creatorName": "홍길동",
      "joinedAt": "2024-03-20T14:00:00",
      "createdAt": "2024-03-15T10:30:00",
      "memberCount": 4
    }
  ]
}
```

### 응답 필드
- `description`: 현재 사용하지 않음 (항상 null)
- `creatorId`: 팀 생성자 ID
- `creatorName`: 팀 생성자 닉네임
