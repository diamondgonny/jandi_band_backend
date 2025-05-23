# 곡 팀 관리 API 명세서

## 개요
곡 팀 생성, 조회, 수정, 삭제 기능을 제공하는 API들입니다.

### 사용 DTO
- **TeamReqDTO**: 곡 팀 생성/수정 요청용 DTO
- **TeamRespDTO**: 곡 팀 목록 조회 응답용 DTO
- **TeamDetailRespDTO**: 곡 팀 상세 조회 응답용 DTO
- **Page<TeamRespDTO>**: Spring Page 제네릭을 사용한 페이지네이션 응답

---

## 1. 곡 팀 추가 API

### 기본 정보
- **Method**: POST
- **Endpoint**: `/api/clubs/{clubId}/teams`
- **Priority**: RED (매우 높음)
- **Role**: 부원
- **Description**: 동아리 내에서 새로운 곡 팀을 생성합니다.
- **Request DTO**: TeamReqDTO
- **Response DTO**: TeamDetailRespDTO

### 경로 매개변수
| 매개변수 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| clubId | Integer | Y | 동아리 ID |

### 요청 바디
```json
{
  "name": "string"
}
```

### 응답
#### 성공 (201 Created)
```json
{
  "success": true,
  "message": "곡 팀이 성공적으로 생성되었습니다.",
  "data": {
    "id": 1,
    "name": "락밴드팀",
    "club": {
      "clubId": 1,
      "name": "한양대학교 밴드부"
    },
    "creator": {
      "userId": 123,
      "name": "김철수"
    },
    "members": [
      {
        "userId": 123,
        "name": "김철수",
        "position": "기타"
      }
    ],
    "memberCount": 1,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
}
```

#### 실패 (400 Bad Request)
```json
{
  "success": false,
  "message": "팀 이름은 필수입니다.",
  "data": null
}
```

#### 실패 (403 Forbidden)
```json
{
  "success": false,
  "message": "동아리 부원만 팀을 생성할 수 있습니다.",
  "data": null
}
```

### 비즈니스 로직
- 요청자가 해당 동아리의 부원인지 확인
- 팀 생성 시 생성자를 자동으로 팀원으로 추가
- 생성 시간과 수정 시간을 현재 시간으로 설정

---

## 2. 곡 팀 목록 조회 API

### 기본 정보
- **Method**: GET
- **Endpoint**: `/api/clubs/{clubId}/teams`
- **Priority**: YELLOW (높음)
- **Role**: 부원
- **Description**: 동아리 내 곡 팀 목록을 페이지네이션으로 조회합니다.
- **Response DTO**: Page<TeamRespDTO>

### 경로 매개변수
| 매개변수 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| clubId | Integer | Y | 동아리 ID |

### 쿼리 매개변수
| 매개변수 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|-------|------|
| page | Integer | N | 0 | 페이지 번호 (0부터 시작) |
| size | Integer | N | 5 | 페이지 크기 |
| sort | String | N | createdAt,desc | 정렬 기준 |

### 응답
#### 성공 (200 OK)
```json
{
  "success": true,
  "message": "곡 팀 목록을 성공적으로 조회했습니다.",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "락밴드팀",
        "creatorId": 123,
        "creatorName": "김철수",
        "memberCount": 4
      }
    ],
    "number": 0,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "first": true,
    "numberOfElements": 1,
    "empty": false
  }
}
```

#### 실패 (403 Forbidden)
```json
{
  "success": false,
  "message": "동아리 부원만 팀 목록을 조회할 수 있습니다.",
  "data": null
}
```

### 비즈니스 로직
- 요청자가 해당 동아리의 부원인지 확인
- 삭제되지 않은 팀만 조회 (deletedAt이 null인 경우)
- 팀별 멤버 수를 포함하여 반환

---

## 3. 곡 팀 상세 조회 API

### 기본 정보
- **Method**: GET
- **Endpoint**: `/api/teams/{teamId}`
- **Priority**: YELLOW (높음)
- **Role**: 부원
- **Description**: 특정 곡 팀의 상세 정보를 조회합니다.
- **Response DTO**: TeamDetailRespDTO

### 경로 매개변수
| 매개변수 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| teamId | Integer | Y | 팀 ID |

### 응답
#### 성공 (200 OK)
```json
{
  "success": true,
  "message": "곡 팀 정보를 성공적으로 조회했습니다.",
  "data": {
    "id": 1,
    "name": "락밴드팀",
    "club": {
      "clubId": 1,
      "name": "한양대학교 밴드부"
    },
    "creator": {
      "userId": 123,
      "name": "김철수"
    },
    "members": [
      {
        "userId": 123,
        "name": "김철수",
        "position": "기타"
      },
      {
        "userId": 124,
        "name": "이영희",
        "position": "보컬"
      }
    ],
    "memberCount": 2,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
}
```

#### 실패 (404 Not Found)
```json
{
  "success": false,
  "message": "존재하지 않는 팀입니다.",
  "data": null
}
```

#### 실패 (403 Forbidden)
```json
{
  "success": false,
  "message": "해당 동아리 부원만 팀 정보를 조회할 수 있습니다.",
  "data": null
}
```

### 비즈니스 로직
- 요청자가 해당 팀이 속한 동아리의 부원인지 확인
- 삭제되지 않은 팀만 조회
- 팀 멤버 목록과 상세 정보 포함

---

## 4. 곡 팀 수정 API

### 기본 정보
- **Method**: PATCH
- **Endpoint**: `/api/teams/{teamId}`
- **Priority**: GREEN (보통)
- **Role**: 대표자, 생성자
- **Description**: 특정 곡 팀의 정보를 수정합니다.
- **Request DTO**: TeamReqDTO
- **Response DTO**: TeamDetailRespDTO

### 경로 매개변수
| 매개변수 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| teamId | Integer | Y | 팀 ID |

### 요청 바디
```json
{
  "name": "string"
}
```

### 응답
#### 성공 (200 OK)
```json
{
  "success": true,
  "message": "곡 팀 정보가 성공적으로 수정되었습니다.",
  "data": {
    "id": 1,
    "name": "수정된 팀명",
    "club": {
      "clubId": 1,
      "name": "한양대학교 밴드부"
    },
    "creator": {
      "userId": 123,
      "name": "김철수"
    },
    "members": [
      {
        "userId": 123,
        "name": "김철수",
        "position": "기타"
      }
    ],
    "memberCount": 1,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-16T15:45:00"
  }
}
```

#### 실패 (403 Forbidden)
```json
{
  "success": false,
  "message": "팀 생성자 또는 동아리 대표자만 팀 정보를 수정할 수 있습니다.",
  "data": null
}
```

#### 실패 (404 Not Found)
```json
{
  "success": false,
  "message": "존재하지 않는 팀입니다.",
  "data": null
}
```

### 비즈니스 로직
- 요청자가 팀 생성자이거나 동아리 대표자인지 확인
- 제공된 필드만 부분 업데이트
- 수정 시간을 현재 시간으로 업데이트

---

## 5. 곡 팀 삭제 API

### 기본 정보
- **Method**: DELETE
- **Endpoint**: `/api/teams/{teamId}`
- **Priority**: GREEN (보통)
- **Role**: 대표자, 생성자
- **Description**: 특정 곡 팀을 삭제합니다.

### 경로 매개변수
| 매개변수 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| teamId | Integer | Y | 팀 ID |

### 응답
#### 성공 (200 OK)
```json
{
  "success": true,
  "message": "곡 팀이 성공적으로 삭제되었습니다.",
  "data": null
}
```

#### 실패 (403 Forbidden)
```json
{
  "success": false,
  "message": "팀 생성자 또는 동아리 대표자만 팀을 삭제할 수 있습니다.",
  "data": null
}
```

#### 실패 (404 Not Found)
```json
{
  "success": false,
  "message": "존재하지 않는 팀입니다.",
  "data": null
}
```

### 비즈니스 로직
- 요청자가 팀 생성자이거나 동아리 대표자인지 확인
- 소프트 삭제 방식 사용 (deletedAt 필드에 현재 시간 설정)
- 관련된 팀 멤버, 이벤트, 투표 등도 함께 소프트 삭제 처리

---

## 공통 에러 코드

| HTTP 상태 코드 | 에러 코드 | 설명 |
|---------------|-----------|------|
| 400 | INVALID_REQUEST | 잘못된 요청 데이터 |
| 401 | UNAUTHORIZED | 인증되지 않은 사용자 |
| 403 | FORBIDDEN | 권한 없음 |
| 404 | NOT_FOUND | 리소스를 찾을 수 없음 |
| 500 | INTERNAL_SERVER_ERROR | 서버 내부 오류 |

## 참고사항
- 모든 API는 JWT 토큰을 통한 인증이 필요합니다.
- 요청 시간은 모두 ISO 8601 형식을 사용합니다.
- 페이지네이션은 0부터 시작합니다.
- 모든 문자열 데이터는 XSS 방지를 위해 이스케이프 처리됩니다.

---

## DTO 구조

### TeamReqDTO
```java
public class TeamReqDTO {
    @NotBlank(message = "팀 이름은 필수입니다.")
    @Size(max = 100, message = "팀 이름은 최대 100자까지 입력 가능합니다.")
    private String name;

    // getter, setter
}
```

### TeamRespDTO (목록 조회용)
```java
public class TeamRespDTO {
    private Integer id;
    private String name;
    private Integer creatorId;
    private String creatorName;
    private Integer memberCount;
    private LocalDateTime createdAt;

    // getter, setter
}
```

### TeamDetailRespDTO (상세 조회용)
```java
public class TeamDetailRespDTO {
    private Integer id;
    private String name;
    private ClubInfoDTO club;
    private CreatorInfoDTO creator;
    private List<MemberInfoDTO> members;
    private Integer memberCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // inner classes
    public static class ClubInfoDTO {
        private Integer clubId;
        private String name;
    }

    public static class CreatorInfoDTO {
        private Integer userId;
        private String name;
    }

    public static class MemberInfoDTO {
        private Integer userId;
        private String name;
        private String position;
    }

    // getter, setter
}
```
