# Health Check API

## 서버 상태 확인
인증 불필요 (공개 API)

---

## 1. 서버 상태 확인
```
GET /health
```

### 요청 예시
```bash
curl "http://localhost:8080/health"
```

### 성공 응답 (200)
```json
{
  "status": "UP",
  "uptime": "PT2H30M15S",
  "serverTime": "2024-03-15T10:30:00Z",
  "activeUsers": 25,
  "totalApiCalls": 1542,
  "systemInfo": {
    "availableProcessors": 8,
    "totalMemory": 2147483648,
    "freeMemory": 1073741824,
    "maxMemory": 4294967296,
    "usedMemory": 1073741824
  }
}
```

### 응답 필드
- `status`: 서버 상태 (UP/DOWN)
- `uptime`: 서버 가동 시간 (ISO 8601 Duration)
- `serverTime`: 현재 서버 시간
- `activeUsers`: 현재 활성 사용자 수
- `totalApiCalls`: 총 API 호출 수
- `systemInfo`: 시스템 정보 (메모리, CPU 등)

---

## 2. 상세 서버 메트릭 정보
```
GET /health/metrics
```

### 요청 예시
```bash
curl "http://localhost:8080/health/metrics"
```

### 성공 응답 (200)
```json
{
  "activeUsers": 25,
  "totalApiCalls": 1542,
  "systemInfo": {
    "availableProcessors": 8,
    "totalMemory": 2147483648,
    "freeMemory": 1073741824,
    "maxMemory": 4294967296,
    "usedMemory": 1073741824
  },
  "jvmInfo": {
    "javaVersion": "17.0.2",
    "javaVendor": "Eclipse Adoptium",
    "osName": "Linux",
    "osVersion": "5.4.0",
    "osArch": "amd64"
  }
}
```

---

## 3. 활성 사용자 수 증가 (테스트용)
```
GET /health/users/increment
```

### 요청 예시
```bash
curl "http://localhost:8080/health/users/increment"
```

### 성공 응답 (200)
```json
{
  "message": "Active users incremented",
  "activeUsers": 26
}
```

---

## 4. 활성 사용자 수 감소 (테스트용)
```
GET /health/users/decrement
```

### 요청 예시
```bash
curl "http://localhost:8080/health/users/decrement"
```

### 성공 응답 (200)
```json
{
  "message": "Active users decremented",
  "activeUsers": 24
}
```

---

## 에러 응답
```json
{
  "error": "에러 메시지",
  "timestamp": "2024-03-15T10:30:00Z"
}
```

### HTTP 상태 코드
- `200 OK`: 성공
- `500 Internal Server Error`: 서버 오류

## 메트릭 설명

### 메모리 정보
- `totalMemory`: JVM에 할당된 총 메모리 (바이트)
- `freeMemory`: 사용 가능한 메모리 (바이트)
- `maxMemory`: JVM이 사용할 수 있는 최대 메모리 (바이트)
- `usedMemory`: 현재 사용 중인 메모리 (바이트)

### 시스템 정보
- `availableProcessors`: 사용 가능한 프로세서 수
- `javaVersion`: Java 버전
- `osName`: 운영체제 이름
- `osVersion`: 운영체제 버전

## 참고사항
- **공개 API**: 인증 없이 누구나 접근 가능
- **모니터링**: Micrometer를 사용한 메트릭 수집
- **테스트용 API**: increment/decrement는 개발/테스트 환경에서만 사용 