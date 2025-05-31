# Redis Health Check API

## Redis 연결 상태 확인
인증 불필요 (공개 API)

---

## 1. Redis 연결 상태 확인
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

### 응답 필드
- `status`: Redis 연결 상태 (UP/DOWN)
- `message`: 상태 메시지
- `timestamp`: 확인 시간
- `basicTest`: 기본 Redis 연결 테스트 결과
- `inviteServiceTest`: InviteCodeService 테스트 결과
- `retrievedValue`: 테스트로 저장/조회한 값

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

## 2. Redis 기본 연결 테스트
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

### 응답 필드
- `status`: 테스트 상태 (UP/DOWN)
- `message`: 테스트 결과 메시지
- `timestamp`: 테스트 시간
- `testResult`: ping 테스트 결과

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

## 3. Redis 설정 정보 확인
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

### 응답 필드
- `connectionFactory`: Redis 연결 팩토리 클래스명
- `timestamp`: 정보 확인 시간
- `status`: 정보 조회 상태

### 실패 응답 (500)
```json
{
  "status": "ERROR",
  "message": "Failed to retrieve Redis info: NullPointerException",
  "timestamp": "2024-03-15T10:30:00Z"
}
```

---

## 에러 응답
```json
{
  "status": "DOWN/ERROR",
  "message": "에러 메시지",
  "timestamp": "2024-03-15T10:30:00Z",
  "error": "예외 클래스명"
}
```

### HTTP 상태 코드
- `200 OK`: 성공
- `500 Internal Server Error`: 서버 오류
- `503 Service Unavailable`: Redis 연결 실패

## 참고사항
- **공개 API**: 인증 없이 누구나 접근 가능
- **연결 테스트**: StringRedisTemplate과 InviteCodeService 모두 확인
- **자동 정리**: 테스트 데이터는 테스트 후 자동 삭제
- **로깅**: 모든 테스트 과정이 로그로 기록
- **타임스탬프**: ISO 8601 형식으로 시간 정보 제공
