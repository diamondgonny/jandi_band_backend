# MyPage API 명세서

## Base URL
`/api/my`

## 인증
JWT 인증 필요

---

## 1. 내가 참가한 동아리 목록 조회
### GET `/api/my/clubs`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/my/clubs" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (200 OK)
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

#### 응답 필드
- `id`: 동아리 ID
- `name`: 동아리 이름  
- `description`: 동아리 설명
- `photoUrl`: 동아리 대표 사진 URL (없으면 null)
- `universityName`: 대학교 이름 (연합동아리면 null)
- `isUnionClub`: 연합동아리 여부 (true/false)
- `myRole`: 내 역할 (REPRESENTATIVE/MEMBER)
- `joinedAt`: 가입 일시
- `memberCount`: 동아리 총 멤버 수

---

## 2. 내가 참가한 팀 목록 조회
### GET `/api/my/teams`

#### 요청
```bash
curl -X GET "http://localhost:8080/api/my/teams" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "내가 참가한 팀 목록 조회 성공",
  "data": [
    {
      "id": 1,
      "name": "밴드 팀",
      "description": "락 음악을 연주하는 팀",
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

#### 응답 필드
- `id`: 팀 ID
- `name`: 팀 이름
- `description`: 팀 설명
- `clubId`: 소속 동아리 ID
- `clubName`: 소속 동아리 이름
- `creatorId`: 팀 생성자 ID
- `creatorName`: 팀 생성자 닉네임
- `joinedAt`: 팀 가입 일시
- `createdAt`: 팀 생성 일시
- `memberCount`: 팀 총 멤버 수

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

## 참고사항
- **빈 목록**: 참가한 동아리/팀이 없으면 빈 배열 반환
- **정렬**: 가입 순서대로 정렬
