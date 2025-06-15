# Elasticsearch 검색 기능

이 문서는 Spring Boot 프로젝트에 Elasticsearch 검색 기능을 통합하기 위한 완전한 가이드입니다.

## 개요

Elasticsearch는 분산형 검색 및 분석 엔진으로, 대용량 데이터에서 빠르고 정확한 검색을 제공합니다. 이 프로젝트에서는 공연 홍보 데이터의 검색 기능을 Elasticsearch로 구현하여 기존 JPA 검색보다 향상된 성능과 정확도를 제공합니다.

## 주요 기능

### 1. 공연 홍보 검색 (Elasticsearch 기반)
- **엔드포인트**: `/api/promos/search-v2`
- **기능**: 키워드 기반 통합 검색 (제목, 팀명, 설명, 위치, 주소)
- **가중치**: 제목(2.0f), 팀명(1.5f)에 높은 가중치 부여
- **기존 JPA 검색**: `/api/promos/search` (기존 기능 유지)

### 2. 공연 홍보 필터링 (Elasticsearch 기반)
- **엔드포인트**: `/api/promos/filter-v2`
- **기능**: 날짜 범위, 팀명으로 필터링
- **날짜 형식**: ISO DATE 형식 (YYYY-MM-DD)
- **기존 JPA 필터링**: `/api/promos/filter` (기존 기능 유지)

### 3. 공연 홍보 지도 검색 (Elasticsearch 기반)
- **엔드포인트**: `/api/promos/map-v2`
- **기능**: 위도/경도 범위로 지도 기반 검색
- **기존 JPA 지도 검색**: `/api/promos/map` (기존 기능 유지)

### 4. 세부 검색 기능
- **제목 검색**: `/api/promos/search-v2/title`
- **팀명 검색**: `/api/promos/search-v2/team`
- **장소 검색**: `/api/promos/search-v2/location`
- **전체 조회**: `/api/promos/search-v2/all`

### 5. 관리 기능
- **전체 동기화**: `/api/admin/promos/sync-all` (실제 DB 데이터를 Elasticsearch에 동기화)

## 환경 설정

### 1. 필수 요구사항
- Docker 및 Docker Compose
- Java 21
- Spring Boot 3.4.5
- MySQL 데이터베이스 (기존 프로젝트 데이터)

### 2. Elasticsearch 시작

#### Docker Compose로 시작
```bash
# 프로젝트 루트 디렉토리에서
cd search
docker-compose -f docker-compose.elasticsearch.yml up -d
```

#### 또는 스크립트 실행
```bash
# 프로젝트 루트 디렉토리에서
cd search
chmod +x start-elasticsearch.sh
./start-elasticsearch.sh
```

### 3. Elasticsearch 상태 확인
```bash
# 클러스터 상태 확인
curl -X GET "http://localhost:9200/_cluster/health?pretty"

# 인덱스 목록 확인
curl -X GET "http://localhost:9200/_cat/indices?v"
```

### 4. Kibana 접속
- URL: http://localhost:5601
- Elasticsearch 연결: http://elasticsearch:9200

## 애플리케이션 설정

### 1. application.properties 설정
```properties
# Elasticsearch Configuration
spring.elasticsearch.uris=http://localhost:9200
spring.elasticsearch.connection-timeout=10s
spring.elasticsearch.socket-timeout=30s
```

### 2. Spring Boot 애플리케이션 시작
```bash
# 프로젝트 루트 디렉토리에서
./gradlew bootRun
```

### 3. 애플리케이션 상태 확인
```bash
# 헬스체크
curl -X GET "http://localhost:8080/health"

# Elasticsearch 연결 확인
curl -X GET "http://localhost:8080/actuator/health"
```

## 데이터 동기화

### 1. 전체 데이터 동기화
```bash
# 데이터베이스의 모든 공연 홍보를 Elasticsearch에 동기화
curl -X POST "http://localhost:8080/api/admin/promos/sync-all"
```

### 2. 동기화 상태 확인
```bash
# Elasticsearch 인덱스 확인
curl -X GET "http://localhost:9200/promos/_count"

# 샘플 데이터 확인
curl -X GET "http://localhost:9200/promos/_search?pretty&size=1"
```

## API 사용법

### 1. 키워드 검색
```bash
# 기본 검색
curl -X GET "http://localhost:8080/api/promos/search-v2?keyword=락밴드&page=0&size=10"

# 페이징과 정렬
curl -X GET "http://localhost:8080/api/promos/search-v2?keyword=락밴드&page=0&size=20&sort=createdAt,desc"
```

### 2. 필터링 (날짜 범위, 팀명)
```bash
# 날짜 범위와 팀명으로 필터링
curl -X GET "http://localhost:8080/api/promos/filter-v2?startDate=2024-03-01&endDate=2024-03-31&teamName=락밴드&page=0&size=10"

# 날짜 범위만으로 필터링
curl -X GET "http://localhost:8080/api/promos/filter-v2?startDate=2024-03-01&endDate=2024-03-31&page=0&size=10"

# 팀명만으로 필터링
curl -X GET "http://localhost:8080/api/promos/filter-v2?teamName=락밴드&page=0&size=10"
```

### 3. 지도 기반 검색 (위도/경도 범위)
```bash
# 서울 지역 검색
curl -X GET "http://localhost:8080/api/promos/map-v2?startLatitude=37.5&startLongitude=126.9&endLatitude=37.6&endLongitude=127.0&page=0&size=10"

# 홍대 지역 검색
curl -X GET "http://localhost:8080/api/promos/map-v2?startLatitude=37.55&startLongitude=126.92&endLatitude=37.56&endLongitude=126.93&page=0&size=10"
```

### 4. 세부 검색
```bash
# 제목 검색
curl -X GET "http://localhost:8080/api/promos/search-v2/title?title=정기공연&page=0&size=10"

# 팀명 검색
curl -X GET "http://localhost:8080/api/promos/search-v2/team?teamName=락밴드&page=0&size=10"

# 장소 검색
curl -X GET "http://localhost:8080/api/promos/search-v2/location?location=홍대&page=0&size=10"

# 모든 공연 홍보 조회
curl -X GET "http://localhost:8080/api/promos/search-v2/all?page=0&size=10"
```

## 응답 형식

모든 검색 API는 동일한 응답 형식을 사용합니다:

```json
{
  "success": true,
  "message": "공연 홍보 검색 성공 (Elasticsearch)",
  "data": {
    "content": [
      {
        "id": 1,
        "teamName": "락밴드 팀",
        "title": "락밴드 정기공연",
        "description": "락밴드 팀의 정기 공연입니다.",
        "location": "홍대 클럽",
        "address": "서울시 마포구 홍익로 123",
        "latitude": 37.5563,
        "longitude": 126.9236,
        "admissionFee": 10000,
        "eventDatetime": "2024-03-15T19:00:00",
        "createdAt": "2024-03-01T10:00:00",
        "updatedAt": "2024-03-01T10:00:00",
        "likeCount": 20,
        "isLikedByUser": true,
        "photoUrls": ["https://example.com/photo.jpg"]
      }
    ],
    "pageInfo": {
      "page": 0,
      "size": 10,
      "totalElements": 1,
      "totalPages": 1,
      "first": true,
      "last": true,
      "empty": false
    }
  }
}
```

## Kibana를 이용한 검색 테스트

### 1. Kibana Dev Tools 접속
- Kibana URL: http://localhost:5601
- 좌측 메뉴에서 "Dev Tools" 클릭

### 2. 인덱스 확인
```json
GET _cat/indices?v
```

### 3. 매핑 확인
```json
GET promos/_mapping
```

### 4. 샘플 데이터 확인
```json
GET promos/_search
{
  "size": 5
}
```

### 5. 키워드 검색 테스트
```json
GET promos/_search
{
  "query": {
    "multi_match": {
      "query": "락밴드",
      "fields": ["title^2.0", "teamName^1.5", "description", "location", "address"]
    }
  },
  "size": 10
}
```

### 6. 날짜 범위 검색 테스트
```json
GET promos/_search
{
  "query": {
    "range": {
      "eventDate": {
        "gte": "2024-03-01",
        "lte": "2024-03-31"
      }
    }
  },
  "size": 10
}
```

### 7. 위치 기반 검색 테스트
```json
GET promos/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "range": {
            "latitude": {
              "gte": 37.5,
              "lte": 37.6
            }
          }
        },
        {
          "range": {
            "longitude": {
              "gte": 126.9,
              "lte": 127.0
            }
          }
        }
      ]
    }
  },
  "size": 10
}
```

## 문제 해결

### 1. Elasticsearch 연결 실패
```bash
# Elasticsearch 상태 확인
curl -X GET "http://localhost:9200/_cluster/health?pretty"

# 포트 확인
netstat -an | grep 9200

# Docker 컨테이너 상태 확인
docker ps | grep elasticsearch
```

**해결 방법**:
- Docker Compose 재시작: `docker-compose -f docker-compose.elasticsearch.yml restart`
- 포트 충돌 확인: 다른 서비스가 9200 포트를 사용하고 있는지 확인
- 방화벽 설정 확인

### 2. 인덱스 생성 실패
```bash
# 기존 인덱스 삭제
curl -X DELETE "http://localhost:9200/promos"

# 애플리케이션 재시작
./gradlew bootRun
```

**해결 방법**:
- 기존 인덱스 삭제 후 애플리케이션 재시작
- 매핑 오류 확인: 로그에서 구체적인 오류 메시지 확인
- Elasticsearch 버전 호환성 확인

### 3. 검색 결과가 비어있음
```bash
# 인덱스 데이터 확인
curl -X GET "http://localhost:9200/promos/_count"

# 샘플 데이터 확인
curl -X GET "http://localhost:9200/promos/_search?size=1"

# 데이터 동기화 재실행
curl -X POST "http://localhost:8080/api/admin/promos/sync-all"
```

**해결 방법**:
- 데이터 동기화 재실행
- 검색 키워드가 데이터와 일치하는지 확인
- 인덱스 매핑 확인

### 4. 날짜 변환 오류
**증상**: `Conversion exception when converting document id`

**해결 방법**:
- 기존 인덱스 삭제: `curl -X DELETE "http://localhost:9200/promos"`
- 애플리케이션 재시작
- 데이터 재동기화

### 5. 403 Forbidden 오류
**해결 방법**:
- Spring Security 설정 확인
- `/api/admin/**` 경로가 허용되었는지 확인
- 인증 토큰 확인

### 6. 분석기 오류
**증상**: `analyzer [nori] has not been configured in mappings`

**해결 방법**:
- Standard 분석기 사용 (현재 설정)
- Nori 분석기 설치 필요 시:
  ```bash
  docker exec -it jandi-elasticsearch /bin/bash
  bin/elasticsearch-plugin install analysis-nori
  ```

## 성능 최적화

### 1. 인덱스 설정
```json
PUT promos/_settings
{
  "index": {
    "number_of_shards": 1,
    "number_of_replicas": 0,
    "refresh_interval": "1s"
  }
}
```

### 2. 쿼리 최적화
- 가중치 설정으로 관련성 높은 결과 우선 표시
- 페이징 사용으로 대용량 데이터 처리
- 필요한 필드만 선택적으로 검색

### 3. 로깅 설정
```properties
# application.properties
logging.level.org.springframework.data.elasticsearch=DEBUG
logging.level.org.elasticsearch=INFO
```

## 기존 JPA 검색과의 차이점

| 기능 | JPA 검색 | Elasticsearch 검색 |
|------|----------|-------------------|
| **키워드 검색** | `/api/promos/search` | `/api/promos/search-v2` |
| **필터링** | `/api/promos/filter` | `/api/promos/filter-v2` |
| **지도 검색** | `/api/promos/map` | `/api/promos/map-v2` |
| **검색 속도** | 일반적 | 매우 빠름 |
| **복잡한 검색** | 제한적 | 강력함 |
| **풀텍스트 검색** | 기본적 | 고급 |
| **가중치 검색** | 불가능 | 가능 |
| **페이징** | 지원 | 지원 |
| **정렬** | 지원 | 지원 |
| **사용자별 좋아요 상태** | 지원 | 지원 |
| **요청/응답 형식** | 동일 | 동일 |
| **날짜 형식** | ISO 8601 | ISO DATE |

## 개발 가이드

### 1. 새로운 검색 필드 추가
1. `PromoDocument`에 필드 추가
2. `PromoSearchService`에 검색 로직 추가
3. `PromoSearchController`에 엔드포인트 추가
4. 데이터 재동기화

### 2. 검색 가중치 조정
```java
Criteria criteria = new Criteria()
    .or("title").contains(keyword).boost(2.0f)      // 제목 가중치
    .or("teamName").contains(keyword).boost(1.5f)   // 팀명 가중치
    .or("description").contains(keyword)            // 설명 가중치 없음
```

### 3. 로깅 추가
```java
@Slf4j
@Service
public class PromoSearchService {
    public Page<PromoDocument> searchByKeyword(String keyword, Pageable pageable) {
        log.info("검색 시작 - 키워드: {}, 페이지: {}, 크기: {}", 
                keyword, pageable.getPageNumber(), pageable.getPageSize());
        // 검색 로직
    }
}
```

## 다음 단계

1. **실제 데이터 동기화**: Promo 엔티티 변경 시 자동으로 Elasticsearch 동기화
2. **고급 검색**: 날짜 범위, 가격 범위, 위치 기반 검색 추가
3. **검색 결과 하이라이팅**: 검색어 강조 표시
4. **검색 제안**: 자동완성 기능
5. **검색 분석**: 인기 검색어, 검색 통계
6. **성능 모니터링**: 검색 성능 지표 수집
7. **백업 및 복구**: Elasticsearch 데이터 백업 전략 