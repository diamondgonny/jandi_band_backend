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

# Elasticsearch 디버그 로그
logging.level.org.springframework.data.elasticsearch=DEBUG
logging.level.org.elasticsearch.client=DEBUG
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
        "imageUrl": "https://example.com/photo.jpg"
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

## 데이터 구조

### PromoDocument 필드 정보
- **id**: String (Keyword 타입)
- **title**: String (Text 타입, standard 분석기)
- **teamName**: String (Text 타입, standard 분석기)
- **description**: String (Text 타입, standard 분석기)
- **location**: String (Keyword 타입)
- **address**: String (Keyword 타입)
- **latitude**: BigDecimal (Double 타입)
- **longitude**: BigDecimal (Double 타입)
- **admissionFee**: BigDecimal (Double 타입)
- **eventDate**: LocalDate (Date 타입)
- **createdAt**: LocalDate (Date 타입)
- **updatedAt**: LocalDate (Date 타입)
- **likeCount**: Integer (Integer 타입)
- **imageUrl**: String (Keyword 타입)

## 문제 해결 가이드

### 1. Docker 네트워크 연결 문제

#### 문제 상황
- Spring Boot 애플리케이션 시작 시 `java.net.UnknownHostException: elasticsearch: Temporary failure in name resolution` 오류 발생
- Elasticsearch와 Spring Boot가 서로 다른 Docker 네트워크에 있어서 호스트명 해석 불가

#### 해결 방법

**1단계: 현재 네트워크 상황 확인**
```bash
# 실행 중인 컨테이너 확인
docker ps

# Docker 네트워크 목록 확인
docker network ls

# Spring Boot 컨테이너의 네트워크 설정 확인
docker inspect <spring-boot-container-name> | grep -A 10 "Networks"

# Elasticsearch 컨테이너의 네트워크 설정 확인
docker inspect <elasticsearch-container-name> | grep -A 10 "Networks"
```

**2단계: 네트워크 연결**
```bash
# 방법 1: Spring Boot를 Elasticsearch 네트워크에 연결
docker network connect <elasticsearch-network-name> <spring-boot-container-name>

# 방법 2: Elasticsearch를 Spring Boot 네트워크에 연결
docker network connect <spring-boot-network-name> <elasticsearch-container-name>
```

**3단계: 연결 확인**
```bash
# Spring Boot 컨테이너에서 elasticsearch 호스트 확인
docker exec <spring-boot-container-name> nslookup elasticsearch

# Elasticsearch 연결 테스트
docker exec <spring-boot-container-name> curl -X GET "http://elasticsearch:9200/_cluster/health?pretty"
```

**4단계: 애플리케이션 재시작**
```bash
# Spring Boot 컨테이너 재시작
docker restart <spring-boot-container-name>

# 로그 확인
docker logs -f <spring-boot-container-name>
```

### 2. 날짜 형식 변환 오류

#### 문제 상황
- `Conversion exception when converting document id` 오류 발생
- Elasticsearch에 저장된 날짜 형식과 Java 코드의 날짜 타입 불일치

#### 해결 방법

**1단계: PromoDocument 클래스 수정**
```java
// LocalDateTime에서 LocalDate로 변경
@Field(type = FieldType.Date)
private LocalDate eventDate;

@Field(type = FieldType.Date)
private LocalDate createdAt;

@Field(type = FieldType.Date)
private LocalDate updatedAt;
```

**2단계: 관련 서비스 클래스 수정**
```java
// PromoSyncService에서 날짜 변환
public void syncPromoCreate(Promo promo) {
    PromoDocument promoDocument = new PromoDocument(
        // ... 다른 필드들
        promo.getEventDatetime().toLocalDate(),  // LocalDateTime -> LocalDate
        promo.getCreatedAt().toLocalDate(),      // LocalDateTime -> LocalDate
        promo.getUpdatedAt().toLocalDate()       // LocalDateTime -> LocalDate
    );
}
```

**3단계: 인덱스 재생성**
```bash
# 기존 인덱스 삭제
curl -X DELETE "http://localhost:9200/promos"

# 애플리케이션 재시작 (새 인덱스 자동 생성)
docker restart <spring-boot-container-name>

# 데이터 재동기화
curl -X POST "http://localhost:8080/api/admin/promos/sync-all"
```

### 3. 한국어 검색 문제

#### 문제 상황
- 한국어 키워드로 검색 시 결과가 나오지 않음
- Elasticsearch가 한국어 형태소 분석을 제대로 수행하지 못함

#### 해결 방법

**1단계: Nori 분석기 설치 (선택사항)**
```bash
# Elasticsearch 컨테이너에 접속
docker exec -it <elasticsearch-container-name> bash

# Nori 분석기 설치
bin/elasticsearch-plugin install analysis-nori
```

**2단계: 표준 분석기 사용 (권장)**
```java
// PromoDocument 클래스에서 표준 분석기 사용
@Document(indexName = "promos")
public class PromoDocument {
    // 기본 분석기 사용 (별도 설정 불필요)
}
```

**3단계: 인덱스 재생성 및 데이터 재동기화**
```bash
# 인덱스 삭제
curl -X DELETE "http://localhost:9200/promos"

# 애플리케이션 재시작
docker restart <spring-boot-container-name>

# 데이터 재동기화
curl -X POST "http://localhost:8080/api/admin/promos/sync-all"
```

### 4. 배포 환경 설정

#### 로컬 환경
```properties
# application.properties
spring.elasticsearch.uris=http://localhost:9200
```

#### 배포 환경 (Docker)
```properties
# application.properties
spring.elasticsearch.uris=http://elasticsearch:9200
```

#### 디버그 로그 활성화
```properties
# application.properties
logging.level.org.springframework.data.elasticsearch=DEBUG
logging.level.org.elasticsearch.client=DEBUG
logging.level.org.elasticsearch=DEBUG
```

### 5. 컨테이너 관리 명령어

#### 컨테이너 재시작
```bash
# 단일 컨테이너 재시작
docker restart <container-name>

# 컨테이너 중지 후 시작
docker stop <container-name>
docker start <container-name>
```

#### 로그 확인
```bash
# 실시간 로그
docker logs -f <container-name>

# 최근 로그
docker logs --tail 100 <container-name>
```

#### 네트워크 관리
```bash
# 네트워크 목록
docker network ls

# 네트워크 상세 정보
docker network inspect <network-name>

# 컨테이너를 네트워크에 연결
docker network connect <network-name> <container-name>

# 컨테이너를 네트워크에서 분리
docker network disconnect <network-name> <container-name>
```

### 6. 일반적인 문제 해결 순서

1. **Elasticsearch 상태 확인**
   ```bash
   curl -X GET "http://localhost:9200/_cluster/health?pretty"
   ```

2. **네트워크 연결 확인**
   ```bash
   docker network ls
   docker inspect <container-name> | grep -A 10 "Networks"
   ```

3. **애플리케이션 로그 확인**
   ```bash
   docker logs -f <spring-boot-container-name>
   ```

4. **인덱스 상태 확인**
   ```bash
   curl -X GET "http://localhost:9200/_cat/indices?v"
   curl -X GET "http://localhost:9200/promos/_search?pretty&size=1"
   ```

5. **필요시 인덱스 재생성 및 데이터 재동기화**
   ```bash
   curl -X DELETE "http://localhost:9200/promos"
   docker restart <spring-boot-container-name>
   curl -X POST "http://localhost:8080/api/admin/promos/sync-all"
   ```

이 가이드를 참고하여 문제가 발생했을 때 체계적으로 해결할 수 있습니다. 