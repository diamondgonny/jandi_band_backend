# Elasticsearch 트러블슈팅 가이드

이 문서는 Elasticsearch 통합 과정에서 발생할 수 있는 문제들과 해결책을 정리한 것입니다.

## 주요 문제 유형

### 1. Docker 네트워크 연결 문제

#### 문제 상황
```
java.net.UnknownHostException: elasticsearch: Temporary failure in name resolution
```

**원인**: Spring Boot 애플리케이션과 Elasticsearch가 서로 다른 Docker 네트워크에 있어서 호스트명 해석 불가

**해결 방법**:

1. **현재 네트워크 상황 확인**
   ```bash
   # 실행 중인 컨테이너 확인
   docker ps
   
   # Docker 네트워크 목록 확인
   docker network ls
   
   # Spring Boot 컨테이너의 네트워크 설정 확인
   docker inspect rhythmeet-be | grep -A 10 "Networks"
   
   # Elasticsearch 컨테이너의 네트워크 설정 확인
   docker inspect jandi-elasticsearch | grep -A 10 "Networks"
   ```

2. **네트워크 연결**
   ```bash
   # 방법 1: Spring Boot를 Elasticsearch 네트워크에 연결
   docker network connect search_elastic rhythmeet-be
   
   # 방법 2: Elasticsearch를 Spring Boot 네트워크에 연결
   docker network connect spring-app_spring-network jandi-elasticsearch
   ```

3. **연결 확인**
   ```bash
   # Spring Boot 컨테이너에서 elasticsearch 호스트 확인
   docker exec rhythmeet-be nslookup elasticsearch
   
   # Elasticsearch 연결 테스트
   docker exec rhythmeet-be curl -X GET "http://elasticsearch:9200/_cluster/health?pretty"
   ```

4. **애플리케이션 재시작**
   ```bash
   # Spring Boot 컨테이너 재시작
   docker restart rhythmeet-be
   
   # 로그 확인
   docker logs -f rhythmeet-be
   ```

### 2. 날짜 형식 변환 오류

#### 문제 상황
```
Conversion exception when converting document id 23
```

**원인**: Elasticsearch에 저장된 날짜 형식과 Java 코드의 날짜 타입 불일치

**해결 방법**:

1. **PromoDocument 클래스 수정**
   ```java
   // LocalDateTime에서 LocalDate로 변경
   @Field(type = FieldType.Date)
   private LocalDate eventDate;
   
   @Field(type = FieldType.Date)
   private LocalDate createdAt;
   
   @Field(type = FieldType.Date)
   private LocalDate updatedAt;
   ```

2. **PromoSyncService 수정**
   ```java
   public void syncPromoCreate(Promo promo) {
       PromoDocument promoDocument = new PromoDocument(
           // ... 다른 필드들
           promo.getEventDatetime().toLocalDate(),  // LocalDateTime -> LocalDate
           promo.getCreatedAt().toLocalDate(),      // LocalDateTime -> LocalDate
           promo.getUpdatedAt().toLocalDate()       // LocalDateTime -> LocalDate
       );
   }
   ```

3. **인덱스 재생성**
   ```bash
   # 기존 인덱스 삭제
   curl -X DELETE "http://localhost:9200/promos"
   
   # 애플리케이션 재시작 (새 인덱스 자동 생성)
   docker restart rhythmeet-be
   
   # 데이터 재동기화
   curl -X POST "http://localhost:8080/api/admin/promos/sync-all"
   ```

### 3. 한국어 검색 문제

#### 문제 상황
- 한국어 키워드로 검색 시 결과가 나오지 않음
- Kibana에서는 데이터가 정상적으로 보이지만 API 검색 결과가 비어있음

**원인**: Elasticsearch가 한국어 형태소 분석을 제대로 수행하지 못함

**해결 방법**:

1. **표준 분석기 사용 (권장)**
   ```java
   @Document(indexName = "promos")
   public class PromoDocument {
       @Field(type = FieldType.Text, analyzer = "standard")
       private String title;
       
       @Field(type = FieldType.Text, analyzer = "standard")
       private String teamName;
       
       @Field(type = FieldType.Text, analyzer = "standard")
       private String description;
   }
   ```

2. **Nori 분석기 설치 (선택사항)**
   ```bash
   # Elasticsearch 컨테이너에 접속
   docker exec -it jandi-elasticsearch bash
   
   # Nori 분석기 설치
   bin/elasticsearch-plugin install analysis-nori
   ```

3. **인덱스 재생성 및 데이터 재동기화**
   ```bash
   # 인덱스 삭제
   curl -X DELETE "http://localhost:9200/promos"
   
   # 애플리케이션 재시작
   docker restart rhythmeet-be
   
   # 데이터 재동기화
   curl -X POST "http://localhost:8080/api/admin/promos/sync-all"
   ```

### 4. Spring Boot Bean 생성 실패

#### 문제 상황
```
Failed to instantiate [org.springframework.data.elasticsearch.repository.support.SimpleElasticsearchRepository]: Constructor threw exception
```

**원인**: Elasticsearch 연결 실패로 인한 Repository Bean 생성 실패

**해결 방법**:

1. **Elasticsearch 상태 확인**
   ```bash
   # Elasticsearch 클러스터 상태 확인
   curl -X GET "http://localhost:9200/_cluster/health?pretty"
   
   # Elasticsearch 컨테이너 상태 확인
   docker ps | grep elasticsearch
   ```

2. **네트워크 연결 확인**
   ```bash
   # 네트워크 연결 상태 확인
   docker network ls
   docker inspect rhythmeet-be | grep -A 10 "Networks"
   ```

3. **디버그 로그 활성화**
   ```properties
   # application.properties
   logging.level.org.springframework.data.elasticsearch=DEBUG
   logging.level.org.elasticsearch.client=DEBUG
   logging.level.org.elasticsearch=DEBUG
   ```

4. **애플리케이션 재시작**
   ```bash
   docker restart rhythmeet-be
   docker logs -f rhythmeet-be
   ```

### 5. 배포 환경 설정 문제

#### 문제 상황
- 로컬에서는 정상 작동하지만 배포 서버에서 연결 실패

**원인**: 로컬과 배포 환경의 Elasticsearch URI 차이

**해결 방법**:

1. **환경별 설정 확인**
   ```properties
   # 로컬 환경
   spring.elasticsearch.uris=http://localhost:9200
   
   # 배포 환경 (Docker)
   spring.elasticsearch.uris=http://elasticsearch:9200
   ```

2. **Docker 네트워크 연결**
   ```bash
   # Spring Boot를 Elasticsearch 네트워크에 연결
   docker network connect search_elastic rhythmeet-be
   ```

3. **연결 테스트**
   ```bash
   # Spring Boot 컨테이너에서 Elasticsearch 연결 테스트
   docker exec rhythmeet-be curl -X GET "http://elasticsearch:9200/_cluster/health?pretty"
   ```

### 6. 엘라스틱서치 검색에서 이미지 URL 누락 문제

#### 문제 상황
- 엘라스틱서치 검색 결과에서 최근에 생성된 게시물의 이미지가 표시되지 않음
- 기존 게시물은 이미지가 정상적으로 표시되지만 새로 생성된 게시물은 이미지가 null로 나옴

**원인**: JPA Lazy Loading으로 인해 `PromoSyncService`에서 `promo.getPhotos()` 호출 시 이미지 데이터가 로드되지 않음

**해결 방법**:

1. **PromoSyncService 수정**
   ```java
   @Service
   public class PromoSyncService {
       private final PromoPhotoRepository promoPhotoRepository;
       
       private String getImageUrl(Promo promo) {
           try {
               // PromoPhotoRepository를 사용하여 직접 조회하여 Lazy Loading 문제 해결
               List<PromoPhoto> photos = 
                   promoPhotoRepository.findByPromoIdAndNotDeleted(promo.getId());
               
               return photos.stream()
                       .filter(photo -> photo.getDeletedAt() == null)
                       .findFirst()
                       .map(photo -> photo.getImageUrl())
                       .orElse(null);
           } catch (Exception e) {
               log.error("이미지 URL 가져오기 실패 - Promo ID: {}, 오류: {}", promo.getId(), e.getMessage());
               return null;
           }
       }
   }
   ```

2. **PromoRepository 수정 (선택사항)**
   ```java
   @Query("SELECT p FROM Promo p LEFT JOIN FETCH p.photos WHERE p.deletedAt IS NULL")
   Page<Promo> findAllNotDeleted(Pageable pageable);
   ```

3. **데이터 재동기화**
   ```bash
   # 기존 인덱스 삭제
   curl -X DELETE "http://localhost:9200/promos"
   
   # 애플리케이션 재시작 (새 인덱스 자동 생성)
   docker restart rhythmeet-be
   
   # 데이터 재동기화
   curl -X POST "http://localhost:8080/api/admin/promos/sync-all"
   ```

4. **검증**
   ```bash
   # 특정 게시물 검색하여 이미지 URL 확인
   curl -X GET "http://localhost:8080/api/promos/search-v2?keyword=테스트&page=0&size=10"
   
   # Elasticsearch에서 직접 확인
   curl -X GET "http://localhost:9200/promos/_search?pretty&q=title:테스트"
   ```

**예방 방법**:
- 새로운 게시물 생성/수정 시 항상 `promoSyncService.syncPromoCreate()` 또는 `promoSyncService.syncPromoUpdate()` 호출
- 이미지 업로드 후 반드시 동기화 실행
- 정기적으로 전체 데이터 동기화 실행 (`/api/admin/promos/sync-all`)

## 일반적인 문제 해결 순서

### 1단계: 환경 확인
```bash
# Elasticsearch 상태 확인
curl -X GET "http://localhost:9200/_cluster/health?pretty"

# 컨테이너 상태 확인
docker ps

# 네트워크 상태 확인
docker network ls
```

### 2단계: 네트워크 연결 확인
```bash
# 컨테이너 네트워크 설정 확인
docker inspect rhythmeet-be | grep -A 10 "Networks"
docker inspect jandi-elasticsearch | grep -A 10 "Networks"

# 호스트명 해석 확인
docker exec rhythmeet-be nslookup elasticsearch
```

### 3단계: 로그 확인
```bash
# Spring Boot 로그 확인
docker logs -f rhythmeet-be

# Elasticsearch 로그 확인
docker logs -f jandi-elasticsearch
```

### 4단계: 인덱스 상태 확인
```bash
# 인덱스 목록 확인
curl -X GET "http://localhost:9200/_cat/indices?v"

# 인덱스 데이터 확인
curl -X GET "http://localhost:9200/promos/_count"
curl -X GET "http://localhost:9200/promos/_search?pretty&size=1"
```

### 5단계: 문제 해결
```bash
# 네트워크 연결 (필요시)
docker network connect search_elastic rhythmeet-be

# 인덱스 재생성 (필요시)
curl -X DELETE "http://localhost:9200/promos"
docker restart rhythmeet-be
curl -X POST "http://localhost:8080/api/admin/promos/sync-all"
```

## 유용한 명령어 모음

### 컨테이너 관리
```bash
# 컨테이너 재시작
docker restart rhythmeet-be
docker restart jandi-elasticsearch

# 컨테이너 로그 확인
docker logs -f rhythmeet-be
docker logs -f jandi-elasticsearch

# 컨테이너 상태 확인
docker ps
docker ps -a
```

### 네트워크 관리
```bash
# 네트워크 목록
docker network ls

# 네트워크 상세 정보
docker network inspect search_elastic
docker network inspect spring-app_spring-network

# 컨테이너를 네트워크에 연결
docker network connect search_elastic rhythmeet-be

# 컨테이너를 네트워크에서 분리
docker network disconnect search_elastic rhythmeet-be
```

### Elasticsearch 관리
```bash
# 클러스터 상태
curl -X GET "http://localhost:9200/_cluster/health?pretty"

# 인덱스 관리
curl -X GET "http://localhost:9200/_cat/indices?v"
curl -X DELETE "http://localhost:9200/promos"

# 데이터 검색
curl -X GET "http://localhost:9200/promos/_search?pretty&size=1"
curl -X GET "http://localhost:9200/promos/_count"
```

### 애플리케이션 테스트
```bash
# API 테스트
curl -X GET "http://localhost:8080/api/promos/search-v2?keyword=락밴드&page=0&size=10"

# 동기화 테스트
curl -X POST "http://localhost:8080/api/admin/promos/sync-all"

# 헬스체크
curl -X GET "http://localhost:8080/actuator/health"
```

## 주의사항

1. **네트워크 연결**: Spring Boot와 Elasticsearch가 같은 네트워크에 있어야 함
2. **날짜 형식**: Elasticsearch에서는 LocalDate 사용, DTO에서는 LocalDateTime 사용
3. **인덱스 재생성**: 매핑 변경 시 기존 인덱스 삭제 후 재생성 필요
4. **환경 설정**: 로컬과 배포 환경의 Elasticsearch URI가 다름
5. **로그 확인**: 문제 발생 시 항상 로그를 먼저 확인

이 가이드를 참고하여 문제가 발생했을 때 체계적으로 해결할 수 있습니다. 