# Elasticsearch 검색 기능 가이드

이 문서는 Spring Boot 프로젝트에 Elasticsearch 검색 기능을 통합하기 위한 완전한 가이드입니다.

## 목차

- [개요](#개요)
- [주요 기능](#주요-기능)
- [환경 설정](#환경-설정)
- [사용법](#사용법)
- [API 문서](#api-문서)
- [문제 해결](#문제-해결)
- [참고 자료](#참고-자료)

## 개요

Elasticsearch는 분산형 검색 및 분석 엔진으로, 대용량 데이터에서 빠르고 정확한 검색을 제공합니다. 이 프로젝트에서는 공연 홍보 데이터의 검색 기능을 Elasticsearch로 구현하여 기존 JPA 검색보다 향상된 성능과 정확도를 제공합니다.

### 주요 장점

- **빠른 검색**: 인덱스 기반 검색으로 대용량 데이터에서도 빠른 응답
- **정확한 검색**: 가중치 기반 검색으로 관련성 높은 결과 우선 표시
- **통합 검색**: 제목, 팀명, 설명, 위치, 주소에서 동시 검색
- **확장성**: 대용량 데이터 처리에 최적화된 구조

## 주요 기능

### 검색 기능

| 기능 | 엔드포인트 | 설명 |
|------|------------|------|
| **통합 검색** | `/api/promos/search-v2` | 키워드 기반 통합 검색 (제목, 팀명, 설명, 위치, 주소) |
| **제목 검색** | `/api/promos/search-v2/title` | 제목만으로 검색 |
| **팀명 검색** | `/api/promos/search-v2/team` | 팀명만으로 검색 |
| **장소 검색** | `/api/promos/search-v2/location` | 장소만으로 검색 |
| **전체 조회** | `/api/promos/search-v2/all` | 모든 공연 홍보 조회 |

### 필터링 기능

| 기능 | 엔드포인트 | 설명 |
|------|------------|------|
| **날짜/팀명 필터링** | `/api/promos/filter-v2` | 날짜 범위, 팀명으로 필터링 |
| **지도 기반 검색** | `/api/promos/map-v2` | 위도/경도 범위로 지도 기반 검색 |
| **공연 상태별 필터링** | `/api/promos/status-v2` | 진행 중/예정/종료 공연 구분 |

### 관리 기능

| 기능 | 엔드포인트 | 설명 |
|------|------------|------|
| **전체 동기화** | `/api/admin/promos/sync-all` | DB 데이터를 Elasticsearch에 동기화 |

## 환경 설정

### 사전 요구사항

- **Docker Desktop** (Windows/macOS) 또는 **Docker Engine** (Linux)
- **Java 21**
- **Spring Boot 3.4.5**
- **MySQL 데이터베이스** (기존 프로젝트 데이터)

### Elasticsearch 시작

#### 방법 1: 통합 스크립트 사용 (권장)

```bash
# 프로젝트 루트 디렉토리에서
cd search
chmod +x start-elasticsearch.sh
./start-elasticsearch.sh
```

**환경별 자동 감지:**
- **Linux/macOS**: `docker-compose.elasticsearch.yml` 사용
- **Windows**: `docker-compose.elasticsearch.windows.yml` 사용 (Git Bash가 있는 경우)
- **EC2 Ubuntu**: `docker-compose.elasticsearch.ec2.yml` 선택 가능

#### 방법 2: Docker Compose 직접 사용

```bash
# 프로젝트 루트 디렉토리에서
cd search

# Linux/macOS
docker-compose -f docker-compose.elasticsearch.yml up -d

# Windows
docker-compose -f docker-compose.elasticsearch.windows.yml up -d

# EC2 Ubuntu
docker-compose -f docker-compose.elasticsearch.ec2.yml up -d
```

#### 방법 3: Windows 전용 스크립트 (백업)

```cmd
# CMD에서
cd search
start-elasticsearch-windows.bat
```

```powershell
# PowerShell에서
cd search
.\start-elasticsearch-windows.ps1

# 강제 재시작
.\start-elasticsearch-windows.ps1 -Force

# 시스템 리소스 확인 건너뛰기
.\start-elasticsearch-windows.ps1 -SkipChecks
```

### 환경별 설정

#### Windows 환경

**사전 요구사항:**
- [Docker Desktop for Windows](https://www.docker.com/products/docker-desktop/) 설치
- [Git for Windows](https://git-scm.com/download/win) 설치 (권장)
- WSL 2 백엔드 사용 권장 (Windows 10/11)
- 최소 4GB RAM 할당 권장

**PowerShell 실행 정책 설정 (선택사항):**
```powershell
# 관리자 권한으로 PowerShell 실행 후
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

#### EC2 Ubuntu 환경

**사전 요구사항:**
- 최소 사양: t3.medium (2 vCPU, 4GB RAM)
- 권장 사양: t3.large (2 vCPU, 8GB RAM) 또는 t3.xlarge (4 vCPU, 16GB RAM)
- 최소 20GB 스토리지

**시스템 설정:**
```bash
# vm.max_map_count 설정 (Elasticsearch 필수)
sudo sysctl -w vm.max_map_count=262144
echo "vm.max_map_count=262144" | sudo tee -a /etc/sysctl.conf

# 파일 디스크립터 제한 설정
echo "* soft nofile 65536" | sudo tee -a /etc/security/limits.conf
echo "* hard nofile 65536" | sudo tee -a /etc/security/limits.conf
```

### 상태 확인

```bash
# Elasticsearch 클러스터 상태 확인
curl -X GET "http://localhost:9200/_cluster/health?pretty"

# 인덱스 목록 확인
curl -X GET "http://localhost:9200/_cat/indices?v"

# Kibana 접속
# URL: http://localhost:5601
```

## 사용법

### 애플리케이션 설정

#### application.properties 설정

```properties
# Elasticsearch Configuration
spring.elasticsearch.uris=http://localhost:9200
spring.elasticsearch.connection-timeout=10s
spring.elasticsearch.socket-timeout=30s

# Elasticsearch 디버그 로그 (문제 해결 시)
logging.level.org.springframework.data.elasticsearch=DEBUG
logging.level.org.elasticsearch.client=DEBUG
```

#### Spring Boot 애플리케이션 시작

```bash
# 프로젝트 루트 디렉토리에서
./gradlew bootRun

# Windows
.\gradlew.bat bootRun
```

#### 애플리케이션 상태 확인

```bash
# 헬스체크
curl -X GET "http://localhost:8080/health"

# Elasticsearch 연결 확인
curl -X GET "http://localhost:8080/actuator/health"
```

### 데이터 동기화

#### 전체 데이터 동기화

```bash
# 데이터베이스의 모든 공연 홍보를 Elasticsearch에 동기화
curl -X POST "http://localhost:8080/api/admin/promos/sync-all"
```

#### 동기화 상태 확인

```bash
# Elasticsearch 인덱스 확인
curl -X GET "http://localhost:9200/promos/_count"

# 샘플 데이터 확인
curl -X GET "http://localhost:9200/promos/_search?pretty&size=1"
```

## API 문서

### 검색 API

#### 기본 검색

```bash
# 키워드 검색
curl -X GET "http://localhost:8080/api/promos/search-v2?keyword=락밴드&page=0&size=10"

# 페이징과 정렬
curl -X GET "http://localhost:8080/api/promos/search-v2?keyword=락밴드&page=0&size=20&sort=createdAt,desc"
```

#### 세부 검색

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

### 필터링 API

#### 날짜/팀명 필터링

```bash
# 날짜 범위와 팀명으로 필터링
curl -X GET "http://localhost:8080/api/promos/filter-v2?startDate=2024-03-01&endDate=2024-03-31&teamName=락밴드&page=0&size=10"

# 날짜 범위만으로 필터링
curl -X GET "http://localhost:8080/api/promos/filter-v2?startDate=2024-03-01&endDate=2024-03-31&page=0&size=10"

# 팀명만으로 필터링
curl -X GET "http://localhost:8080/api/promos/filter-v2?teamName=락밴드&page=0&size=10"
```

#### 지도 기반 검색

```bash
# 서울 지역 검색
curl -X GET "http://localhost:8080/api/promos/map-v2?startLatitude=37.5&startLongitude=126.9&endLatitude=37.6&endLongitude=127.0&page=0&size=10"

# 홍대 지역 검색
curl -X GET "http://localhost:8080/api/promos/map-v2?startLatitude=37.55&startLongitude=126.92&endLatitude=37.56&endLongitude=126.93&page=0&size=10"
```

#### 공연 상태별 필터링

```bash
# 진행 중인 공연만 조회
curl -X GET "http://localhost:8080/api/promos/status-v2?status=ongoing&page=0&size=20"

# 예정된 공연 중에서 키워드 검색
curl -X GET "http://localhost:8080/api/promos/status-v2?status=upcoming&keyword=락밴드&page=0&size=20"

# 특정 팀의 종료된 공연 조회
curl -X GET "http://localhost:8080/api/promos/status-v2?status=ended&teamName=밴드A&page=0&size=20"
```

### 응답 형식

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

### 데이터 구조

#### PromoDocument 필드 정보

| 필드명 | 타입 | 설명 |
|--------|------|------|
| `id` | String (Keyword) | 공연 홍보 ID |
| `title` | String (Text) | 제목 (standard 분석기) |
| `teamName` | String (Text) | 팀명 (standard 분석기) |
| `description` | String (Text) | 설명 (standard 분석기) |
| `location` | String (Keyword) | 장소 |
| `address` | String (Keyword) | 주소 |
| `latitude` | BigDecimal (Double) | 위도 |
| `longitude` | BigDecimal (Double) | 경도 |
| `admissionFee` | BigDecimal (Double) | 입장료 |
| `eventDate` | LocalDate (Date) | 공연 날짜 |
| `createdAt` | LocalDate (Date) | 생성일 |
| `updatedAt` | LocalDate (Date) | 수정일 |
| `likeCount` | Integer | 좋아요 수 |
| `imageUrl` | String (Keyword) | 이미지 URL |

## 문제 해결

### 일반적인 문제들

#### 1. Docker 네트워크 연결 문제

**증상**: `java.net.UnknownHostException: elasticsearch: Temporary failure in name resolution`

**해결 방법**:
```bash
# 네트워크 연결 확인
docker network ls
docker inspect <container-name> | grep -A 10 "Networks"

# 네트워크 연결
docker network connect <elasticsearch-network> <spring-boot-container>

# 연결 확인
docker exec <spring-boot-container> nslookup elasticsearch
```

#### 2. 날짜 형식 변환 오류

**증상**: `Conversion exception when converting document id`

**해결 방법**:
```bash
# 인덱스 재생성
curl -X DELETE "http://localhost:9200/promos"
docker restart <spring-boot-container>
curl -X POST "http://localhost:8080/api/admin/promos/sync-all"
```

#### 3. 한국어 검색 문제

**증상**: 한국어 키워드로 검색 시 결과가 나오지 않음

**해결 방법**:
```bash
# 표준 분석기 사용 (권장)
# PromoDocument에서 별도 설정 불필요

# 인덱스 재생성
curl -X DELETE "http://localhost:9200/promos"
docker restart <spring-boot-container>
curl -X POST "http://localhost:8080/api/admin/promos/sync-all"
```

### 유용한 명령어

#### 컨테이너 관리
```bash
# 컨테이너 상태 확인
docker ps

# 로그 확인
docker logs -f jandi-elasticsearch
docker logs -f jandi-kibana

# 컨테이너 재시작
docker restart jandi-elasticsearch
docker restart jandi-kibana

# 서비스 중지
docker-compose -f docker-compose.elasticsearch.yml down
```

#### Elasticsearch 관리
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

#### 애플리케이션 테스트
```bash
# API 테스트
curl -X GET "http://localhost:8080/api/promos/search-v2?keyword=락밴드&page=0&size=10"

# 동기화 테스트
curl -X POST "http://localhost:8080/api/admin/promos/sync-all"

# 헬스체크
curl -X GET "http://localhost:8080/actuator/health"
```

### 상세 문제 해결

더 자세한 문제 해결 방법은 [TROUBLESHOOTING.md](./TROUBLESHOOTING.md) 문서를 참조하세요.

## 참고 자료

### 관련 문서

- [Elasticsearch 공식 문서](https://www.elastic.co/guide/index.html)
- [Spring Data Elasticsearch 문서](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/)
- [Docker Compose 문서](https://docs.docker.com/compose/)

### 유용한 링크

- **Elasticsearch**: http://localhost:9200
- **Kibana**: http://localhost:5601
- **Spring Boot 애플리케이션**: http://localhost:8080

### 파일 구조

```
search/
├── README.md                           # 메인 문서 (이 파일)
├── TROUBLESHOOTING.md                  # 문제 해결 가이드
├── start-elasticsearch.sh              # 통합 실행 스크립트
├── start-elasticsearch-windows.bat     # Windows 배치 파일
├── start-elasticsearch-windows.ps1     # Windows PowerShell 스크립트
├── start-elasticsearch-ec2.sh          # EC2 전용 스크립트
├── docker-compose.elasticsearch.yml    # 기본 Docker Compose
├── docker-compose.elasticsearch.windows.yml  # Windows용 Docker Compose
└── docker-compose.elasticsearch.ec2.yml      # EC2용 Docker Compose
```

### 다음 단계

1. **Elasticsearch 시작**: `./start-elasticsearch.sh`
2. **Spring Boot 애플리케이션 시작**: `./gradlew bootRun`
3. **데이터 동기화**: `curl -X POST http://localhost:8080/api/admin/promos/sync-all`
4. **검색 테스트**: `curl -X GET "http://localhost:8080/api/promos/search-v2?keyword=락밴드"`

---

**문의사항이나 문제가 발생하면 [TROUBLESHOOTING.md](./TROUBLESHOOTING.md) 문서를 먼저 확인해주세요.** 