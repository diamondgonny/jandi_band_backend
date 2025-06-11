# Region API

## 지역 정보
인증 불필요

---

## 1. 전체 지역 목록 조회
```
GET /api/region/all
```

### 요청 예시
```bash
curl "http://localhost:8080/api/region/all"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "지역 리스트 조회 성공",
  "data": [
    {
      "id": 1,
      "code": "SEOUL",
      "name": "서울"
    },
    {
      "id": 2,
      "code": "GYEONGGI",
      "name": "경기"
    },
    {
      "id": 3,
      "code": "INCHEON",
      "name": "인천"
    }
  ]
}
```

### 응답 필드
- `id`: 지역 ID
- `code`: 지역 코드 (SEOUL, BUSAN 등)
- `name`: 지역명

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
- `500 Internal Server Error`: 서버 오류
