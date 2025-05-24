# Club API 명세서

## Base URL
`/api/clubs`

## 인증
생성, 수정, 삭제는 JWT 인증 필요 (Spring Security + CustomUserDetails). 조회는 인증 불필요.

---

## 1. 동아리 생성
### POST `/api/clubs`

#### 요청
```bash
curl -X POST "http://localhost:8080/api/clubs" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "락밴드 동아리",
    "universityId": 1,
    "chatroomUrl": "https://open.kakao.com/o/example",
    "description": "음악을 사랑하는 사람들이 모인 락밴드 동아리입니다.",
    "instagramId": "rockband_club"
  }'
```

#### 요청 필드
- `name` (string, 필수): 동아리 이름 (최대 100자)
- `universityId` (integer, 선택): 대학교 ID (null이면 연합동아리)
- `chatroomUrl` (string, 선택): 카카오톡 채팅방 링크 (최대 255자)
- `description` (string, 선택): 동아리 설명
- `instagramId` (string, 선택): 인스타그램 아이디 (최대 50자)

#### 응답 (201 Created)
```json
{
  "success": true,
  "message": "동아리가 성공적으로 생성되었습니다",
  "data": {
    "id": 1,
    "name": "락밴드 동아리",
    "university": {
      "id": 1,
      "name": "서울대학교"
    },
    "isUnionClub": false,
    "chatroomUrl": "https://open.kakao.com/o/example",
    "description": "음악을 사랑하는 사람들이 모인 락밴드 동아리입니다.",
    "instagramId": "rockband_club",
    "photoUrl": null,
    "memberCount": 1,
    "createdAt": "2024-03-15T10:30:00",
    "updatedAt": "2024-03-15T10:30:00"
  }
}
```

---

## 2. 동아리 목록 조회
### GET `/api/clubs`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/clubs?page=0&size=5"
```

#### 쿼리 파라미터
- `page` (integer): 페이지 번호 (기본값: 0)
- `size` (integer): 페이지 크기 (기본값: 5)

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "동아리 목록 조회 성공",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "락밴드 동아리",
        "universityName": "서울대학교",
        "isUnionClub": false,
        "photoUrl": null,
        "memberCount": 5
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

## 3. 동아리 상세 조회
### GET `/api/clubs/{clubId}`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/clubs/1"
```

#### 응답 (200 OK)
동아리 생성 응답과 동일한 구조

---

## 4. 동아리 정보 수정
### PATCH `/api/clubs/{clubId}`

#### 요청
```bash
curl -X PATCH "http://localhost:8080/api/clubs/1" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "description": "수정된 동아리 설명입니다.",
    "instagramId": "new_instagram_id"
  }'
```

#### 요청 필드 (모두 선택적)
- `name`: 동아리 이름
- `chatroomUrl`: 채팅방 URL
- `description`: 동아리 설명
- `instagramId`: 인스타그램 ID

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "동아리 정보가 성공적으로 수정되었습니다",
  "data": {
    "id": 1,
    "name": "락밴드 동아리",
    "university": {
      "id": 1,
      "name": "서울대학교"
    },
    "isUnionClub": false,
    "chatroomUrl": "https://open.kakao.com/o/example",
    "description": "수정된 동아리 설명입니다.",
    "instagramId": "new_instagram_id",
    "photoUrl": null,
    "memberCount": 5,
    "createdAt": "2024-03-15T10:30:00",
    "updatedAt": "2024-03-15T11:00:00"
  }
}
```

---

## 5. 동아리 삭제
### DELETE `/api/clubs/{clubId}`

#### 요청
```bash
curl -X DELETE "http://localhost:8080/api/clubs/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "동아리가 성공적으로 삭제되었습니다",
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
- **연합동아리**: `universityId`가 null이면 연합동아리 (`isUnionClub: true`)
- **권한**: 생성은 모든 인증된 사용자, 수정/삭제는 동아리 대표자만
- **자동 멤버 추가**: 동아리 생성자는 자동으로 대표자(REPRESENTATIVE)로 등록
- **소프트 삭제**: 실제 삭제가 아닌 deletedAt 설정
- **페이지네이션**: 기본 크기 5개
