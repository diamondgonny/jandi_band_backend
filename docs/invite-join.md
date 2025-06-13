# Invite & Join API

## 초대 및 가입
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

## 3. 동아리 가입
```
POST /api/join/clubs?code={INVITE_CODE}
Authorization: Bearer {JWT_TOKEN}
```

### 요청 예시
```bash
curl -X POST "http://localhost:8080/api/join/clubs?code=ABC123DEF" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "동아리 가입 성공",
  "data": {
    "clubId": 1,
    "teamId": null
  }
}
```

### 재가입 처리
- **탈퇴한 사용자**: 이전 회원 정보가 자동으로 재활성화됩니다
- **강퇴된 사용자**: 재가입이 차단됩니다

### 실패 응답
- **400**: 만료된 초대 코드 또는 강퇴된 사용자의 재가입 시도
- **404**: 존재하지 않는 초대 코드
- **409**: 이미 가입된 멤버

#### 강퇴된 사용자 재가입 시도 시
```json
{
  "success": false,
  "message": "강퇴된 사용자는 해당 동아리 및 관련 팀에 가입할 수 없습니다.",
  "data": null
}
```

---

## 4. 팀 가입
```
POST /api/join/teams?code={INVITE_CODE}
Authorization: Bearer {JWT_TOKEN}
```

### 요청 예시
```bash
curl -X POST "http://localhost:8080/api/join/teams?code=XYZ789GHI" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "팀 가입 성공",
  "data": {
    "clubId": 1,
    "teamId": 1
  }
}
```

### 응답 필드
- `clubId`: 동아리 ID
- `teamId`: 팀 ID (동아리 가입 시에는 null)

### 실패 응답
- **400**: 만료된 초대 코드
- **404**: 존재하지 않는 초대 코드
- **409**: 이미 가입된 멤버

#### 강퇴된 사용자의 팀 가입 시도 시
```json
{
  "success": false,
  "message": "강퇴된 사용자는 해당 동아리 및 관련 팀에 가입할 수 없습니다.",
  "data": null
}
```

---

## 가입 로직 상세

### 동아리 가입 시
1. **신규 사용자**: 새로운 회원 정보 생성
2. **탈퇴한 사용자**: 기존 회원 정보 재활성화 (역할: MEMBER)
3. **강퇴된 사용자**: 가입 차단 (BannedMemberJoinAttemptException 발생)
4. **이미 활성화된 회원**: "이미 가입한 동아리입니다" 오류

### 팀 가입 시
1. **신규 사용자**: 새로운 팀 멤버 정보 생성
2. **탈퇴한 사용자**: 기존 팀 멤버 정보 재활성화
3. **이미 활성화된 멤버**: "이미 가입한 팀입니다" 오류
4. **동아리 미가입자**: 동아리 가입도 자동으로 처리
5. **동아리에서 강퇴된 사용자**: 팀 가입 차단 (BannedMemberJoinAttemptException 발생)

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
- `201 Created`: 생성 성공
- `400 Bad Request`: 잘못된 요청 (강퇴된 사용자 재가입 포함)
- `401 Unauthorized`: 인증 실패
- `403 Forbidden`: 권한 없음
- `404 Not Found`: 리소스 없음
- `409 Conflict`: 이미 가입된 멤버

## 초대 코드 규칙
- **유효 기간**: 7일
- **형식**: 9자리 영숫자 (대문자)
- **일회성**: 사용 후에도 유효 (여러 명 가입 가능)
- **권한**: 동아리는 대표자만, 팀은 생성자만 생성 가능

## 주의사항
- 동아리에서 강퇴된 사용자는 해당 동아리에 재가입할 수 없습니다
- 동아리에서 강퇴된 사용자는 해당 동아리의 팀에도 가입할 수 없습니다
- 재가입 시 기존 회원/멤버 정보가 재활성화되어 유니크 제약조건 문제가 해결됩니다
