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

## 5. Redis 연결 상태 확인
```
GET /health/redis
```

### 요청 예시
```bash
curl "http://localhost:8080/health/redis"
```

### 성공 응답 (200)
```json
{
  "status": "UP",
  "message": "Redis connection successful",
  "timestamp": "2024-03-15T10:30:00Z",
  "basicTest": "PASS",
  "inviteServiceTest": "PASS",
  "retrievedValue": "999"
}
```

### 실패 응답 (503)
```json
{
  "status": "DOWN",
  "message": "Redis connection failed: Connection refused",
  "timestamp": "2024-03-15T10:30:00Z",
  "error": "RedisConnectionFailureException"
}
```

---

## 6. Redis 기본 연결 테스트
```
GET /health/redis/basic
```

### 요청 예시
```bash
curl "http://localhost:8080/health/redis/basic"
```

### 성공 응답 (200)
```json
{
  "status": "UP",
  "message": "Basic Redis test successful",
  "timestamp": "2024-03-15T10:30:00Z",
  "testResult": "ping"
}
```

### 실패 응답 (503)
```json
{
  "status": "DOWN",
  "message": "Basic Redis test failed: Connection timeout",
  "timestamp": "2024-03-15T10:30:00Z",
  "error": "RedisTimeoutException"
}
```

---

## 7. Redis 설정 정보 확인
```
GET /health/redis/info
```

### 요청 예시
```bash
curl "http://localhost:8080/health/redis/info"
```

### 성공 응답 (200)
```json
{
  "connectionFactory": "org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory",
  "timestamp": "2024-03-15T10:30:00Z",
  "status": "INFO_RETRIEVED"
}
```

### 실패 응답 (500)
```json
{
  "status": "ERROR",
  "message": "Failed to retrieve Redis info: NullPointerException",
  "timestamp": "2024-03-15T10:30:00Z"
}
```

---

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