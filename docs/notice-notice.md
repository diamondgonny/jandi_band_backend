# Notice API

## 공지사항 관리
팝업 형태로 노출되는 공지사항 관리 시스템

---

## 1. 현재 활성 공지사항 조회 (팝업용)
```
GET /api/notices/active
```

> **응답 DTO**: `NoticeRespDTO`

### 요청 예시
```bash
curl "http://localhost:8080/api/notices/active"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "활성 공지사항 조회 성공",
  "data": [
    {
      "id": 1,
      "title": "시스템 점검 안내",
      "content": "오늘 밤 12시부터 새벽 2시까지 시스템 점검이 있습니다.",
      "startDatetime": "2024-12-10T00:00:00",
      "endDatetime": "2024-12-10T23:59:59",
      "isPaused": false
    }
  ]
}
```

### 활성 공지사항 조건
- `deletedAt` 값이 null (삭제되지 않음)
- `isPaused` 값이 false (일시정지되지 않음)
- `startDatetime` ≤ 현재시각 ≤ `endDatetime` (노출 기간 내)

---

## 2. 공지사항 목록 조회 (관리자 전용)
```
GET /api/notices?page=0&size=20&sort=createdAt,desc
Authorization: Bearer {JWT_TOKEN}
```

> **응답 DTO**: `NoticeRespDTO`

### 요청 예시
```bash
curl "http://localhost:8080/api/notices?page=0&size=20" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 쿼리 파라미터
- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 20)
- `sort`: 정렬 (기본값: createdAt,desc)

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "공지사항 목록 조회 성공",
  "data": {
    "content": [
      {
        "id": 1,
        "title": "시스템 점검 안내",
        "content": "오늘 밤 12시부터 새벽 2시까지 시스템 점검이 있습니다.",
        "startDatetime": "2024-12-10T00:00:00",
        "endDatetime": "2024-12-10T23:59:59",
        "isPaused": false
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

### 실패 응답
- **401**: 인증되지 않은 사용자
- **403**: 관리자 권한 없음

---

## 3. 공지사항 상세 조회 (관리자 전용)
```
GET /api/notices/{noticeId}
Authorization: Bearer {JWT_TOKEN}
```

> **응답 DTO**: `NoticeDetailRespDTO`

### 요청 예시
```bash
curl "http://localhost:8080/api/notices/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "공지사항 상세 조회 성공",
  "data": {
    "id": 1,
    "title": "시스템 점검 안내",
    "content": "오늘 밤 12시부터 새벽 2시까지 시스템 점검이 있습니다.",
    "startDatetime": "2024-12-10T00:00:00",
    "endDatetime": "2024-12-10T23:59:59",
    "isPaused": false,
    "creatorId": 1,
    "creatorName": "관리자",
    "createdAt": "2024-12-09T10:30:00",
    "updatedAt": "2024-12-09T15:45:00",
    "deletedAt": null
  }
}
```

### 실패 응답
- **404**: 존재하지 않는 공지사항 또는 삭제된 공지사항

---

## 4. 공지사항 생성 (관리자 전용)
```
POST /api/notices
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

> **응답 DTO**: `NoticeDetailRespDTO`

### 요청 예시
```bash
curl -X POST "http://localhost:8080/api/notices" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "시스템 점검 안내",
    "content": "오늘 밤 12시부터 새벽 2시까지 시스템 점검이 있습니다.",
    "startDatetime": "2024-12-10T00:00:00",
    "endDatetime": "2024-12-10T23:59:59"
  }'
```

### 요청 필드
- `title` (string, 필수): 공지사항 제목 (최대 255자)
- `content` (string, 필수): 공지사항 내용
- `startDatetime` (datetime, 필수): 팝업 노출 시작 시각
- `endDatetime` (datetime, 필수): 팝업 노출 종료 시각
- `isPaused` (boolean, 선택): 일시정지 여부 (생략 시 자동으로 false 설정)

### 성공 응답 (201)
```json
{
  "success": true,
  "message": "공지사항이 성공적으로 생성되었습니다",
  "data": {
    "id": 1,
    "title": "시스템 점검 안내",
    "content": "오늘 밤 12시부터 새벽 2시까지 시스템 점검이 있습니다.",
    "startDatetime": "2024-12-10T00:00:00",
    "endDatetime": "2024-12-10T23:59:59",
    "isPaused": false,
    "creatorId": 1,
    "creatorName": "관리자",
    "createdAt": "2024-12-09T10:30:00",
    "updatedAt": "2024-12-09T10:30:00",
    "deletedAt": null
  }
}
```

### 실패 응답
- **400**: 필수 필드 누락 또는 종료 시각이 시작 시각보다 이른 경우
- **403**: 관리자 권한 없음

### 참고사항
- `isPaused` 필드를 생략하면 자동으로 `false`로 설정됩니다
- 생성된 공지사항은 기본적으로 활성 상태(일시정지되지 않음)가 됩니다

---

## 5. 공지사항 수정 (관리자 전용)
```
PUT /api/notices/{noticeId}
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

> **응답 DTO**: `NoticeDetailRespDTO`

### 요청 예시
```bash
curl -X PUT "http://localhost:8080/api/notices/1" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "수정된 점검 안내",
    "content": "수정된 내용입니다.",
    "startDatetime": "2024-12-10T00:00:00",
    "endDatetime": "2024-12-10T23:59:59"
  }'
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "공지사항이 성공적으로 수정되었습니다",
  "data": {
    "id": 1,
    "title": "수정된 점검 안내",
    "content": "수정된 내용입니다.",
    "startDatetime": "2024-12-10T00:00:00",
    "endDatetime": "2024-12-10T23:59:59",
    "isPaused": false,
    "creatorId": 1,
    "creatorName": "관리자",
    "createdAt": "2024-12-09T10:30:00",
    "updatedAt": "2024-12-09T16:20:00",
    "deletedAt": null
  }
}
```

### 요청 필드
- `title` (string, 필수): 공지사항 제목 (최대 255자)
- `content` (string, 필수): 공지사항 내용
- `startDatetime` (datetime, 필수): 팝업 노출 시작 시각
- `endDatetime` (datetime, 필수): 팝업 노출 종료 시각

### 실패 응답
- **400**: 필수 필드 누락 또는 종료 시각이 시작 시각보다 이른 경우
- **403**: 관리자 권한 없음
- **404**: 존재하지 않는 공지사항

### 참고사항
- **일시정지 상태(`isPaused`)는 수정되지 않습니다**
- 일시정지 상태를 변경하려면 별도의 토글 API(`PATCH /api/notices/{noticeId}/toggle-pause`)를 사용하세요

---

## 6. 공지사항 삭제 (관리자 전용)
```
DELETE /api/notices/{noticeId}
Authorization: Bearer {JWT_TOKEN}
```

### 요청 예시
```bash
curl -X DELETE "http://localhost:8080/api/notices/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "공지사항이 성공적으로 삭제되었습니다",
  "data": null
}
```

### 삭제 처리 방식
- **소프트 삭제**: `deletedAt` 필드에 삭제 시각 설정
- 삭제된 공지사항은 모든 조회 API에서 제외됨
- 팝업 노출에서도 자동으로 제외됨

### 실패 응답
- **403**: 관리자 권한 없음
- **404**: 존재하지 않는 공지사항

---

## 7. 공지사항 일시정지/재개 토글 (관리자 전용)
```
PATCH /api/notices/{noticeId}/toggle-pause
Authorization: Bearer {JWT_TOKEN}
```

> **응답 DTO**: `NoticeRespDTO`

### 요청 예시
```bash
curl -X PATCH "http://localhost:8080/api/notices/1/toggle-pause" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

### 성공 응답 (200)
```json
{
  "success": true,
  "message": "공지사항 일시정지 상태가 성공적으로 변경되었습니다",
  "data": {
    "id": 1,
    "title": "시스템 점검 안내",
    "content": "오늘 밤 12시부터 새벽 2시까지 시스템 점검이 있습니다.",
    "startDatetime": "2024-12-10T00:00:00",
    "endDatetime": "2024-12-10T23:59:59",
    "isPaused": true
  }
}
```

### 기능 설명
- 현재 `isPaused` 값을 반전시킴 (true ↔ false)
- 일시정지된 공지사항은 팝업에 노출되지 않음
- 노출 기간 내에서도 일시정지 상태에 따라 제어 가능

### 실패 응답
- **403**: 관리자 권한 없음
- **404**: 존재하지 않는 공지사항

---

## 권한 관리

### 관리자 권한 검증
모든 관리자 전용 API는 다음 조건을 확인합니다:
- JWT 토큰의 유효성
- 사용자의 `adminRole`이 `ADMIN`인지 확인

### 권한 없음 시 응답
```json
{
  "success": false,
  "message": "관리자만 접근할 수 있습니다.",
  "data": null
}
```

---

## 비즈니스 로직

### 활성 공지사항 조건
1. **소프트 삭제 확인**: `deletedAt IS NULL`
2. **일시정지 확인**: `isPaused = false`
3. **노출 기간 확인**: `startDatetime ≤ 현재시각 ≤ endDatetime`

### 시간 검증
- 종료 시각은 시작 시각보다 늦어야 함
- 시각 정보는 `LocalDateTime` 형식으로 처리

### 로깅 정책
- 공지사항 생성, 수정, 삭제, 상태 변경 시 로그 기록
- 제목은 50자로 제한하여 로깅 (민감정보 보호)

---

## 에러 응답

### 표준 에러 형식
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
- `400 Bad Request`: 잘못된 요청 (필수 필드 누락, 시간 범위 오류)
- `401 Unauthorized`: 인증 실패
- `403 Forbidden`: 권한 부족 (관리자 아님)
- `404 Not Found`: 리소스 없음 (공지사항 없음, 삭제된 공지사항)
- `500 Internal Server Error`: 서버 오류

---

## 응답 DTO 구조

### NoticeRespDTO (간소화 버전)
팝업용, 목록 조회, 일시정지 토글에서 사용되는 기본 응답 DTO

**필드:**
- `id` (Integer): 공지사항 ID
- `title` (String): 공지사항 제목
- `content` (String): 공지사항 내용
- `startDatetime` (LocalDateTime): 팝업 노출 시작 시각
- `endDatetime` (LocalDateTime): 팝업 노출 종료 시각
- `isPaused` (Boolean): 일시정지 여부

**사용 API:**
- `GET /api/notices/active` (현재 활성 공지사항 조회)
- `GET /api/notices` (공지사항 목록 조회)
- `PATCH /api/notices/{noticeId}/toggle-pause` (일시정지/재개 토글)

### NoticeDetailRespDTO (상세 버전)
관리자용 상세 조회, 생성, 수정에서 사용되는 상세 응답 DTO

**추가 필드 (NoticeRespDTO 포함):**
- `creatorId` (Integer): 작성자 ID
- `creatorName` (String): 작성자명
- `createdAt` (LocalDateTime): 생성일시
- `updatedAt` (LocalDateTime): 수정일시
- `deletedAt` (LocalDateTime): 삭제일시

**사용 API:**
- `GET /api/notices/{noticeId}` (공지사항 상세 조회)
- `POST /api/notices` (공지사항 생성)
- `PUT /api/notices/{noticeId}` (공지사항 수정)

---

## 데이터베이스 스키마

### site_notice 테이블
```sql
CREATE TABLE site_notice (
    site_notice_id INT PRIMARY KEY AUTO_INCREMENT,
    creator_user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    start_datetime DATETIME NOT NULL,
    end_datetime DATETIME NOT NULL,
    is_paused BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    deleted_at DATETIME NULL,
    FOREIGN KEY (creator_user_id) REFERENCES users(user_id)
);
```
