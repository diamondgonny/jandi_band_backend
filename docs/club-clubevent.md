# Club Event API 명세서

## Base URL
`/api/clubs/{clubId}/events`

## 인증
JWT 인증 필요 (Authorization 헤더 직접 처리)

---

## 동아리 이벤트 생성
### POST `/api/clubs/{clubId}/events/add`

#### 요청
```bash
curl -X POST "http://localhost:8080/api/clubs/1/events/add" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "2024년 정기 공연",
    "startDatetime": "2024-03-15T19:00:00",
    "endDatetime": "2024-03-15T21:30:00",
    "location": "대학교 대강당",
    "address": "서울특별시 강남구 테헤란로 123",
    "description": "우리 밴드의 첫 번째 정기 공연입니다."
  }'
```

#### 요청 필드
- `name` (string, 필수): 이벤트 이름 (최대 255자)
- `startDatetime` (string, 필수): 시작 일시 (ISO 8601 형식)
- `endDatetime` (string, 필수): 종료 일시 (시작 일시 이후)
- `location` (string, 선택): 장소명 (최대 255자)
- `address` (string, 선택): 상세 주소 (최대 255자)
- `description` (string, 선택): 이벤트 설명

#### 응답 (200 OK)
```json
{
  "id": 15,
  "name": "2024년 정기 공연",
  "startDatetime": "2024-03-15T19:00:00",
  "endDatetime": "2024-03-15T21:30:00",
  "location": "대학교 대강당",
  "address": "서울특별시 강남구 테헤란로 123",
  "description": "우리 밴드의 첫 번째 정기 공연입니다."
}
```

---

## 에러 응답
- `400 Bad Request`: 필수 필드 누락, 잘못된 날짜 형식
- `401 Unauthorized`: 인증 실패
- `404 Not Found`: 동아리 없음

## 참고사항
- **JWT 처리**: Authorization 헤더에서 직접 kakaoOauthId 추출
- **응답 형식**: ApiResponse 래퍼 없이 직접 ClubEventRespDTO 반환
- **날짜 형식**: ISO 8601 (`YYYY-MM-DDTHH:mm:ss`)
