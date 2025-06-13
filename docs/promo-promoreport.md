# Promo Report API

## Base URL
`/api/promos/reports`

## 인증
JWT 인증 필요 (Spring Security + @AuthenticationPrincipal CustomUserDetails)

## 권한 관리
- **신고 생성**: 로그인한 모든 사용자
- **신고 목록 조회**: 관리자(ADMIN) 권한 사용자만 가능

---

## 1. 공연 홍보 신고 생성
### POST `/api/promos/reports`

#### 요청
```bash
curl -X POST "http://localhost:8080/api/promos/reports" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "promoId": 1,
    "reportReasonId": 1,
    "description": "SPAM"
  }'
```

#### 요청 필드
- `promoId` (integer, 필수): 신고할 공연 홍보 ID
- `reportReasonId` (integer, 필수): 신고 사유 ID
- `description` (string, 선택): 신고 상세 설명

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "공연 홍보 신고 성공!",
  "data": null
}
```

---

## 2. 공연 홍보 신고 목록 조회 (관리자용)
### GET `/api/promos/reports`

#### 권한
**관리자(ADMIN) 권한 필요**

#### 요청
```bash
curl -X GET "http://localhost:8080/api/promos/reports?page=0&size=20&sort=createdAt,desc" \
  -H "Authorization: Bearer {ADMIN_JWT_TOKEN}"
```

#### 쿼리 파라미터
- `page` (integer): 페이지 번호 (기본값: 0)
- `size` (integer): 페이지 크기 (기본값: 20)
- `sort` (string): 정렬 기준 (기본값: "createdAt,desc")

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "공연 홍보 신고 목록 조회 성공",
  "data": {
    "content": [
      {
        "id": 1,
        "promoId": 1,
        "promoTitle": "록 밴드 공연 홍보",
        "promoCreatorId": 2,
        "promoCreatorName": "홍길동",
        "reporterUserId": 3,
        "reporterUserName": "김철수",
        "reportReasonId": 1,
        "reportReasonCode": "SPAM",
        "description": "스팸성 게시물입니다.",
        "createdAt": "2024-03-15T10:30:00"
      }
    ],
    "pageInfo": {
      "page": 0,
      "size": 20,
      "totalElements": 1,
      "totalPages": 1,
      "first": true,
      "last": true,
      "empty": false
    }
  }
}
```

#### 응답 필드
- `id` (integer): 신고 ID
- `promoId` (integer): 신고된 공연 홍보 ID
- `promoTitle` (string): 신고된 공연 홍보 제목
- `promoCreatorId` (integer): 공연 홍보 작성자 ID
- `promoCreatorName` (string): 공연 홍보 작성자 이름
- `reporterUserId` (integer): 신고한 사용자 ID
- `reporterUserName` (string): 신고한 사용자 이름
- `reportReasonId` (integer): 신고 이유 ID
- `reportReasonCode` (string): 신고 이유 코드
- `description` (string): 신고 상세 설명
- `createdAt` (string): 신고 생성일시

---

## 에러 응답

### 400 Bad Request - 잘못된 요청
```json
{
  "success": false,
  "message": "필수 필드가 누락되었습니다.",
  "data": null
}
```
**발생 케이스**: 필수 필드 누락, 잘못된 신고 사유

### 401 Unauthorized - 인증 실패
```json
{
  "success": false,
  "message": "유효하지 않은 토큰입니다.",
  "data": null
}
```
**발생 케이스**: JWT 토큰이 없거나 유효하지 않은 경우

### 403 Forbidden - 권한 없음
```json
{
  "success": false,
  "message": "관리자 권한이 필요합니다.",
  "data": null
}
```
**발생 케이스**: 관리자가 아닌 사용자가 신고 목록 조회 시도

### 404 Not Found - 리소스 없음
```json
{
  "success": false,
  "message": "해당 공연 홍보를 찾을 수 없습니다.",
  "data": null
}
```
**발생 케이스**: 존재하지 않는 공연 홍보 ID

### 400 Bad Request - 자기신고 시도
```json
{
  "success": false,
  "message": "본인이 작성한 게시물은 신고할 수 없습니다.",
  "data": null
}
```
**발생 케이스**: 사용자가 자신이 작성한 게시물을 신고하려고 할 때

### 400 Bad Request - 삭제된 게시물 신고 시도
```json
{
  "success": false,
  "message": "삭제된 게시물은 신고할 수 없습니다.",
  "data": null
}
```
**발생 케이스**: 이미 삭제된 게시물을 신고하려고 할 때

---

## 데이터 모델

### PromoReportReqDTO (요청)
```typescript
interface PromoReportReqDTO {
  promoId: number;       // 신고할 공연 홍보 ID
  reportReasonId: number; // 신고 사유 ID
  description?: string;   // 신고 상세 설명
}
```

### PromoReportRespDTO (응답)
```typescript
interface PromoReportRespDTO {
  id: number;                    // 신고 ID
  promoId: number;              // 신고된 공연 홍보 ID
  promoTitle: string;           // 신고된 공연 홍보 제목
  promoCreatorId: number;       // 공연 홍보 작성자 ID
  promoCreatorName: string;     // 공연 홍보 작성자 이름
  reporterUserId: number;       // 신고한 사용자 ID
  reporterUserName: string;     // 신고한 사용자 이름
  reportReasonId: number;       // 신고 이유 ID
  reportReasonCode: string;     // 신고 이유 코드
  description?: string;         // 신고 상세 설명
  createdAt: string;            // 신고 생성일시 (ISO 8601)
}
```

---

## 신고 사유 ID와 코드

실제 요청 시에는 `reportReasonId`를 사용하며, 응답에서는 `reason` 코드로 표시됩니다.

| ID | 코드 | 설명 |
|-----|------|------|
| 1 | `SPAM` | 스팸/도배 |
| 2 | `INAPPROPRIATE_CONTENT` | 부적절한 내용 |
| 3 | `HARASSMENT` | 괴롭힘/혐오 표현 |
| 4 | `FALSE_INFORMATION` | 허위 정보 |
| 5 | `COPYRIGHT_VIOLATION` | 저작권 위반 |
| 6 | `OTHER` | 기타 |

---

## 참고 사항
- **자기신고 방지**: 사용자가 본인이 작성한 게시물을 신고할 수 없음
- **삭제된 컨텐츠 보호**: 이미 삭제된 게시물은 신고할 수 없음
- **자동 처리**: 신고 누적 시 자동으로 게시물 검토 상태 변경
- **관리자 전용**: 신고 목록 조회는 관리자 권한이 있는 사용자만 가능
- **로그 기록**: 모든 신고 활동은 시스템 로그에 기록됨
