# Team API 명세서

## Base URL
`/api`

## 인증
JWT 인증 필요 (Spring Security + CustomUserDetails)

---

## 1. 팀 생성
### POST `/api/clubs/{clubId}/teams`

#### 요청
```bash
curl -X POST "http://localhost:8080/api/clubs/1/teams" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "락밴드 A팀"
  }'
```

#### 요청 필드
- `name` (string, 필수): 팀 이름 (최대 100자)

#### 응답 (201 Created)
```json
{
  "success": true,
  "message": "곡 팀이 성공적으로 생성되었습니다.",
  "data": {
    "id": 1,
    "name": "락밴드 A팀",
    "club": {
      "clubId": 1,
      "name": "락밴드 동아리"
    },
    "creator": {
      "userId": 1,
      "name": "홍길동"
    },
    "members": [
      {
        "userId": 1,
        "name": "홍길동",
        "position": "GUITAR"
      }
    ],
    "memberCount": 1,
    "createdAt": "2024-03-15T10:30:00",
    "updatedAt": "2024-03-15T10:30:00"
  }
}
```

---

## 2. 동아리 팀 목록 조회
### GET `/api/clubs/{clubId}/teams`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/clubs/1/teams?page=0&size=5" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 쿼리 파라미터
- `page` (integer): 페이지 번호 (기본값: 0)
- `size` (integer): 페이지 크기 (기본값: 5)

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "곡 팀 목록을 성공적으로 조회했습니다.",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "락밴드 A팀",
        "creatorId": 1,
        "creatorName": "홍길동",
        "memberCount": 4,
        "createdAt": "2024-03-15T10:30:00"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true
  }
}
```

---

## 3. 팀 상세 조회
### GET `/api/teams/{teamId}`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/teams/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (200 OK)
팀 생성 응답과 동일한 구조

---

## 4. 팀 정보 수정
### PATCH `/api/teams/{teamId}`

#### 요청
```bash
curl -X PATCH "http://localhost:8080/api/teams/1" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "수정된 락밴드 A팀"
  }'
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "곡 팀 정보가 성공적으로 수정되었습니다.",
  "data": {
    "id": 1,
    "name": "수정된 락밴드 A팀",
    "club": {
      "clubId": 1,
      "name": "락밴드 동아리"
    },
    "creator": {
      "userId": 1,
      "name": "홍길동"
    },
    "members": [
      {
        "userId": 1,
        "name": "홍길동",
        "position": "GUITAR"
      }
    ],
    "memberCount": 1,
    "createdAt": "2024-03-15T10:30:00",
    "updatedAt": "2024-03-15T11:00:00"
  }
}
```

---

## 5. 팀 삭제
### DELETE `/api/teams/{teamId}`

#### 요청
```bash
curl -X DELETE "http://localhost:8080/api/teams/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "곡 팀이 성공적으로 삭제되었습니다.",
  "data": null
}
```

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
- `400 Bad Request`: 잘못된 요청
- `401 Unauthorized`: 인증 실패
- `403 Forbidden`: 권한 없음
- `404 Not Found`: 리소스 없음

## 참고사항
- **권한**: 동아리 멤버만 팀 생성/조회 가능
- **수정/삭제 권한**: 팀 생성자 또는 동아리 대표자만 가능
- **자동 멤버 추가**: 팀 생성자는 자동으로 첫 번째 멤버 등록
- **페이지네이션**: 기본 크기 5개
- **소프트 삭제**: 실제 삭제가 아닌 deletedAt 설정
