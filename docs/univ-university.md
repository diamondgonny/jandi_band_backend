# University & Region API

## 대학교 및 지역 정보
인증 불필요 (공개 API)

---

## 1. 지역 목록 조회
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
    }
  ]
}
```

### 응답 필드
- `id`: 지역 ID
- `code`: 지역 코드 (SEOUL, BUSAN 등)
- `name`: 지역명

---

## 2. 대학교 목록 조회
```
GET /api/univ/all?filter=ALL&type=UNIVERSITY&region=SEOUL
```

### 요청 예시
```bash
# 전체 조회
curl "http://localhost:8080/api/univ/all"

# 종류별 조회
curl "http://localhost:8080/api/univ/all?filter=TYPE&type=UNIVERSITY"

# 지역별 조회
curl "http://localhost:8080/api/univ/all?filter=REGION&region=SEOUL"
```

### 쿼리 파라미터
- `filter`: 필터 타입 (기본값: ALL)
  - `ALL`: 전체 조회
  - `TYPE`: 종류별 조회 (type 파라미터 필요)
  - `REGION`: 지역별 조회 (region 파라미터 필요)
- `type`: 대학교 종류
  - `COLLEGE`: 전문대학
  - `UNIVERSITY`: 대학교  
  - `GRADUATE`: 일반대학원
  - `POLYTECH`: 한국폴리텍
- `region`: 지역 코드

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "대학 정보 조회 성공",
  "data": [
    {
      "id": 1,
      "name": "서울대학교"
    }
  ]
}
```

### 응답 필드
- `id`: 대학교 ID
- `name`: 대학교명

---

## 3. 대학교 상세 조회
```
GET /api/univ/{univId}
```

### 요청 예시
```bash
curl "http://localhost:8080/api/univ/1"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "대학 상세 정보 조회 성공",
  "data": {
    "universityCode": "1001",
    "name": "서울대학교",
    "region": "서울",
    "address": "서울특별시 관악구 관악로 1"
  }
}
```

### 응답 필드
- `universityCode`: 대학교 코드
- `name`: 대학교명
- `region`: 소재 지역명
- `address`: 주소

### 실패 응답
- **404**: 존재하지 않는 대학교 ID
