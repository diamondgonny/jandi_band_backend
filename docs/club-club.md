# Club API

## 🏛️ 동아리 관리
모든 API는 JWT 인증 필요

---

## 1. 동아리 생성
```
POST /api/clubs
Authorization: Bearer {JWT_TOKEN}
Content-Type: multipart/form-data
```

### 요청 예시
```bash
curl -X POST "http://localhost:8080/api/clubs" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -F "name=락밴드 동아리" \
  -F "description=음악을 사랑하는 사람들의 모임" \
  -F "universityId=1" \
  -F "photo=@/path/to/photo.jpg"
```

### 요청 필드
- `name`: 동아리 이름
- `description`: 동아리 설명
- `universityId`: 대학교 ID (연합동아리는 null)
- `photo`: 동아리 사진 (선택)

### 성공 응답 (201)
```json
{
  "success": true,
  "message": "동아리가 성공적으로 생성되었습니다.",
  "data": {
    "id": 1,
    "name": "락밴드 동아리",
    "description": "음악을 사랑하는 사람들의 모임",
    "photoUrl": "https://example.com/photo.jpg",
    "universityName": "서울대학교",
    "isUnionClub": false,
    "memberCount": 1,
    "createdAt": "2024-03-15T10:30:00"
  }
}
```

### 실패 응답
- **400**: 필수 필드 누락 또는 중복된 동아리명
- **404**: 존재하지 않는 대학교 ID

---

## 2. 동아리 목록 조회
```
GET /api/clubs?page=0&size=10&sort=createdAt,desc
```

### 요청 예시
```bash
curl "http://localhost:8080/api/clubs?page=0&size=10"
```

### 쿼리 파라미터
- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 10)
- `sort`: 정렬 (기본값: createdAt,desc)

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "동아리 목록 조회 성공",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "락밴드 동아리",
        "description": "음악을 사랑하는 사람들의 모임",
        "photoUrl": "https://example.com/photo.jpg",
        "universityName": "서울대학교",
        "isUnionClub": false,
        "memberCount": 15,
        "createdAt": "2024-03-15T10:30:00"
      }
    ],
    "pageInfo": {
      "page": 0,
      "size": 10,
      "totalElements": 1,
      "totalPages": 1,
      "first": true,
      "last": true
    }
  }
}
```

---

## 3. 동아리 상세 조회
```
GET /api/clubs/{clubId}
Authorization: Bearer {JWT_TOKEN}
```

### 요청 예시
```bash
curl "http://localhost:8080/api/clubs/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "동아리 상세 조회 성공",
  "data": {
    "id": 1,
    "name": "락밴드 동아리",
    "description": "음악을 사랑하는 사람들의 모임",
    "photoUrl": "https://example.com/photo.jpg",
    "universityName": "서울대학교",
    "isUnionClub": false,
    "memberCount": 15,
    "createdAt": "2024-03-15T10:30:00",
    "members": [
      {
        "userId": 1,
        "nickname": "홍길동",
        "profilePhoto": "https://example.com/profile.jpg",
        "position": "GUITAR",
        "role": "REPRESENTATIVE",
        "joinedAt": "2024-03-15T10:30:00"
      }
    ]
  }
}
```

### 실패 응답
- **403**: 동아리 멤버가 아님
- **404**: 존재하지 않는 동아리

---

## 4. 동아리 수정
```
PATCH /api/clubs/{clubId}
Authorization: Bearer {JWT_TOKEN}
Content-Type: multipart/form-data
```

### 요청 예시
```bash
curl -X PATCH "http://localhost:8080/api/clubs/1" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -F "name=수정된 동아리명" \
  -F "description=수정된 설명"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "동아리 정보가 수정되었습니다.",
  "data": null
}
```

### 실패 응답
- **403**: 동아리 대표자가 아님

---

## 5. 동아리 삭제
```
DELETE /api/clubs/{clubId}
Authorization: Bearer {JWT_TOKEN}
```

### 요청 예시
```bash
curl -X DELETE "http://localhost:8080/api/clubs/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "동아리가 삭제되었습니다.",
  "data": null
}
```

### 실패 응답
- **403**: 동아리 대표자가 아님
- **400**: 멤버가 있는 동아리는 삭제 불가

---

## 6. 동아리 검색
```
GET /api/clubs/search?keyword={검색어}&page=0&size=10
```

### 요청 예시
```bash
curl "http://localhost:8080/api/clubs/search?keyword=락밴드&page=0&size=10"
```

### 성공 응답 (200)
동아리 목록 조회와 동일한 형식

---

## 7. 동아리 필터링
```
GET /api/clubs/filter?universityName={대학명}&page=0&size=10
```

### 요청 예시
```bash
curl "http://localhost:8080/api/clubs/filter?universityName=서울대학교"
```

### 쿼리 파라미터
- `universityName`: 대학교명 (선택)
- `isUnionClub`: 연합동아리 여부 (선택)
