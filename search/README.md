# 잔디밴드 검색 시스템 (Elasticsearch)

**엘라스틱서치를 사용한 팀 검색 기능**을 제공합니다. 팀 이름, 설명, 카테고리 등으로 빠른 검색이 가능합니다.

> **엘라스틱서치란?** 실시간 검색 및 분석 엔진으로, 빠른 전문 검색과 복잡한 쿼리를 지원합니다.

## 구조

```
search/
├── docker-compose.elasticsearch.yml   # 엘라스틱서치/키바나 Docker 설정
├── start-elasticsearch.sh             # 엘라스틱서치 환경 시작 스크립트
└── README.md                          # 이 파일
```

## 빠른 시작 (5분 완료)

### 1. 엘라스틱서치 환경 시작
```bash
# search 폴더로 이동
cd search

# 엘라스틱서치 환경 시작 (약 30초 소요)
./start-elasticsearch.sh
```

### 2. 애플리케이션 설정
```bash
# 프로젝트 루트로 돌아가기
cd ..

# application.properties 파일 생성 (없는 경우에만)
cp src/main/resources/application.properties.example src/main/resources/application.properties

# Spring Boot 애플리케이션 실행
./gradlew bootRun
```

### 3. 테스트 데이터 생성
```bash
# 샘플 데이터 생성 (5개 팀 데이터)
curl -X POST "http://localhost:8080/api/admin/search/teams/sample-data"

# 성공 응답: "샘플 데이터가 생성되었습니다."
```

### 4. 동작 확인
```bash
# 검색 테스트
curl "http://localhost:8080/api/search/teams?query=스터디"

# 응답이 오면 설정 완료!
```

## 접속 정보

| 서비스 | URL | 설명 |
|--------|-----|------|
| **엘라스틱서치** | http://localhost:9200 | 검색 엔진 직접 접속 |
| **키바나** | http://localhost:5601 | 검색 데이터 시각화 대시보드 |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | API 문서 및 테스트 |

## API 명세서

### 검색 API (프론트엔드용)

#### 1. 통합 검색 (가장 많이 사용)
```http
GET /api/search/teams?query={검색어}
```

**curl 예제:**
```bash
# 스터디 관련 팀 검색
curl "http://localhost:8080/api/search/teams?query=스터디"

# 개발 관련 팀 검색
curl "http://localhost:8080/api/search/teams?query=개발"

# 한글 검색 (URL 인코딩 자동 처리)
curl "http://localhost:8080/api/search/teams?query=프로젝트"
```

**JavaScript 예제:**
```javascript
// 통합 검색
async function searchTeams(query) {
    const response = await fetch(`http://localhost:8080/api/search/teams?query=${encodeURIComponent(query)}`);
    const teams = await response.json();
    return teams;
}

// 사용 예시
searchTeams('스터디').then(teams => {
    console.log('검색 결과:', teams);
});
```

**응답 예제:**
```json
[
    {
        "id": "1a2b3c4d-5e6f-7g8h-9i0j-k1l2m3n4o5p6",
        "name": "스터디 모임",
        "description": "함께 공부하는 개발자 모임입니다. 매주 모여서 알고리즘과 CS 공부를 합니다.",
        "category": "스터디",
        "status": "RECRUITING",
        "memberCount": 5,
        "maxMembers": 10,
        "createdAt": "2024-01-08T10:00:00",
        "updatedAt": "2024-01-15T14:30:00"
    }
]
```

#### 2. 팀 이름으로 검색
```http
GET /api/search/teams/name?name={팀이름}
```

**curl 예제:**
```bash
curl "http://localhost:8080/api/search/teams/name?name=스터디"
```

**JavaScript 예제:**
```javascript
async function searchTeamsByName(name) {
    const response = await fetch(`http://localhost:8080/api/search/teams/name?name=${encodeURIComponent(name)}`);
    return response.json();
}
```

#### 3. 팀 설명으로 검색
```http
GET /api/search/teams/description?description={설명}
```

**curl 예제:**
```bash
curl "http://localhost:8080/api/search/teams/description?description=개발자"
```

#### 4. 카테고리별 검색
```http
GET /api/search/teams/category?category={카테고리}
```

**curl 예제:**
```bash
# 사용 가능한 카테고리: 스터디, 스포츠, 문화, 취미, 프로젝트
curl "http://localhost:8080/api/search/teams/category?category=스터디"
curl "http://localhost:8080/api/search/teams/category?category=스포츠"
```

**JavaScript 예제:**
```javascript
async function searchTeamsByCategory(category) {
    const response = await fetch(`http://localhost:8080/api/search/teams/category?category=${encodeURIComponent(category)}`);
    return response.json();
}
```

#### 5. 상태별 검색
```http
GET /api/search/teams/status?status={상태}
```

**curl 예제:**
```bash
# 사용 가능한 상태: RECRUITING (모집중), ACTIVE (활동중)
curl "http://localhost:8080/api/search/teams/status?status=RECRUITING"
curl "http://localhost:8080/api/search/teams/status?status=ACTIVE"
```

#### 6. 멤버 수 범위 검색
```http
GET /api/search/teams/members?minCount={최소}&maxCount={최대}
```

**curl 예제:**
```bash
# 5명~10명 사이 팀 검색
curl "http://localhost:8080/api/search/teams/members?minCount=5&maxCount=10"

# 3명 이상 팀 검색 (maxCount 생략 가능)
curl "http://localhost:8080/api/search/teams/members?minCount=3&maxCount=999"
```

#### 7. 모든 팀 조회
```http
GET /api/search/teams/all
```

**curl 예제:**
```bash
curl "http://localhost:8080/api/search/teams/all"
```

### 관리 API (개발/테스트용)

#### 8. 팀 문서 저장 (직접 저장)
```http
POST /api/search/teams
```

**curl 예제:**
```bash
curl -X POST "http://localhost:8080/api/search/teams" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "custom-team-1",
    "name": "새로운 팀",
    "description": "새로 생성된 팀입니다",
    "category": "스터디",
    "status": "RECRUITING",
    "memberCount": 1,
    "maxMembers": 5,
    "createdAt": "2024-01-15T10:00:00",
    "updatedAt": "2024-01-15T10:00:00"
  }'
```

#### 9. 팀 문서 삭제
```http
DELETE /api/search/teams/{id}
```

**curl 예제:**
```bash
curl -X DELETE "http://localhost:8080/api/search/teams/custom-team-1"
```

---

### 관리 API (개발/테스트용)

> **주의**: 이 API들은 개발 및 테스트 용도로만 사용하세요.

#### 1. 샘플 데이터 생성
```http
POST /api/admin/search/teams/sample-data
```

**curl 예제:**
```bash
curl -X POST "http://localhost:8080/api/admin/search/teams/sample-data"

# 성공 응답: "샘플 데이터가 생성되었습니다."
```

**JavaScript 예제:**
```javascript
async function createSampleData() {
    const response = await fetch('http://localhost:8080/api/admin/search/teams/sample-data', {
        method: 'POST'
    });
    const message = await response.text();
    console.log(message);
}
```

#### 2. 모든 검색 데이터 삭제
```http
DELETE /api/admin/search/teams/all
```

**curl 예제:**
```bash
curl -X DELETE "http://localhost:8080/api/admin/search/teams/all"

# 성공 응답: "모든 검색 데이터가 삭제되었습니다."
```

#### 3. 특정 팀 동기화
```http
POST /api/admin/search/teams/sync?teamId={팀ID}&name={이름}&description={설명}&category={카테고리}&status={상태}&memberCount={현재멤버}&maxMembers={최대멤버}
```

**curl 예제:**
```bash
curl -X POST "http://localhost:8080/api/admin/search/teams/sync" \
  -d "teamId=123" \
  -d "name=새로운 팀" \
  -d "description=새로 생성된 팀입니다" \
  -d "category=스터디" \
  -d "status=RECRUITING" \
  -d "memberCount=1" \
  -d "maxMembers=10"

# 성공 응답: "팀 데이터가 동기화되었습니다."
```

---

## 백엔드 개발자 테스트 가이드

### 1단계: 기본 동작 확인
```bash
# 1. 샘플 데이터 생성
curl -X POST "http://localhost:8080/api/admin/search/teams/sample-data"

# 2. 모든 팀 조회로 데이터 확인
curl "http://localhost:8080/api/search/teams/all"

# 3. 검색 테스트
curl "http://localhost:8080/api/search/teams?query=스터디"
```

### 2단계: 디버그 엔드포인트로 문제 진단
```bash
# Repository를 통한 조회 테스트
curl "http://localhost:8080/api/search/teams/debug/repo"

# Native Query를 통한 조회 테스트
curl "http://localhost:8080/api/search/teams/debug/native"

# 두 결과를 비교하여 어디서 문제가 발생하는지 확인
# - 둘 다 빈 배열: Elasticsearch 연결 문제
# - repo만 빈 배열: Repository 설정 문제
# - native만 빈 배열: ElasticsearchOperations 설정 문제
# - 둘 다 정상: 검색 쿼리 문제
```

### 3단계: 다양한 검색 테스트
```bash
# 카테고리별 검색
curl "http://localhost:8080/api/search/teams/category?category=스터디"
curl "http://localhost:8080/api/search/teams/category?category=스포츠"

# 상태별 검색
curl "http://localhost:8080/api/search/teams/status?status=RECRUITING"
curl "http://localhost:8080/api/search/teams/status?status=ACTIVE"

# 멤버 수 범위 검색
curl "http://localhost:8080/api/search/teams/members?minCount=5&maxCount=15"
```

### 4단계: 데이터 조작 테스트
```bash
# 새 팀 추가
curl -X POST "http://localhost:8080/api/search/teams" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "test-team-1",
    "name": "테스트 팀",
    "description": "테스트용 팀입니다",
    "category": "테스트",
    "status": "RECRUITING",
    "memberCount": 1,
    "maxMembers": 5
  }'

# 추가된 팀 검색 확인
curl "http://localhost:8080/api/search/teams?query=테스트"

# 팀 삭제
curl -X DELETE "http://localhost:8080/api/search/teams/test-team-1"
```

### 5단계: 문제 해결 테스트
```bash
# Elasticsearch 직접 확인
curl "http://localhost:9200/teams/_search?pretty"

# 인덱스 상태 확인
curl "http://localhost:9200/_cat/indices?v"

# 클러스터 상태 확인
curl "http://localhost:9200/_cluster/health?pretty"
```

---

## 프론트엔드 연동 가이드

### React 예제
```javascript
// 팀 검색 컴포넌트
import React, { useState, useEffect } from 'react';

const TeamSearch = () => {
    const [searchQuery, setSearchQuery] = useState('');
    const [teams, setTeams] = useState([]);
    const [loading, setLoading] = useState(false);

    const searchTeams = async (query) => {
        setLoading(true);
        try {
            const response = await fetch(
                `http://localhost:8080/api/search/teams?query=${encodeURIComponent(query)}`
            );
            const data = await response.json();
            setTeams(data);
        } catch (error) {
            console.error('검색 오류:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = (e) => {
        e.preventDefault();
        if (searchQuery.trim()) {
            searchTeams(searchQuery);
        }
    };

    return (
        <div>
            <form onSubmit={handleSearch}>
                <input
                    type="text"
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    placeholder="팀 검색..."
                />
                <button type="submit">검색</button>
            </form>

            {loading && <p>검색 중...</p>}

            <div>
                {teams.map(team => (
                    <div key={team.id}>
                        <h3>{team.name}</h3>
                        <p>{team.description}</p>
                        <p>카테고리: {team.category}</p>
                        <p>상태: {team.status}</p>
                        <p>멤버: {team.memberCount}/{team.maxMembers}</p>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default TeamSearch;
```

### Vue.js 예제
```javascript
// TeamSearch.vue
<template>
  <div>
    <form @submit.prevent="searchTeams">
      <input 
        v-model="searchQuery" 
        placeholder="팀 검색..."
        type="text"
      />
      <button type="submit">검색</button>
    </form>

    <div v-if="loading">검색 중...</div>

    <div v-for="team in teams" :key="team.id">
      <h3>{{ team.name }}</h3>
      <p>{{ team.description }}</p>
      <p>카테고리: {{ team.category }}</p>
      <p>상태: {{ team.status }}</p>
      <p>멤버: {{ team.memberCount }}/{{ team.maxMembers }}</p>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      searchQuery: '',
      teams: [],
      loading: false
    }
  },
  methods: {
    async searchTeams() {
      if (!this.searchQuery.trim()) return;
      
      this.loading = true;
      try {
        const response = await fetch(
          `http://localhost:8080/api/search/teams?query=${encodeURIComponent(this.searchQuery)}`
        );
        this.teams = await response.json();
      } catch (error) {
        console.error('검색 오류:', error);
      } finally {
        this.loading = false;
      }
    }
  }
}
</script>
```

---

## 실제 팀 데이터 연동

기존 팀 서비스에서 엘라스틱서치 동기화를 위해 `TeamSyncService`를 사용하세요:

```java
@Service
public class TeamService {
    
    @Autowired
    private TeamSyncService teamSyncService;
    
    public Team createTeam(TeamCreateRequest request) {
        // 기존 팀 생성 로직
        Team team = teamRepository.save(new Team(request));
        
        // 엘라스틱서치 동기화
        teamSyncService.syncTeamCreate(
            team.getId(), 
            team.getName(), 
            team.getDescription(),
            team.getCategory(),
            team.getStatus().toString(),
            team.getMemberCount(),
            team.getMaxMembers()
        );
        
        return team;
    }
    
    public Team updateTeam(Long teamId, TeamUpdateRequest request) {
        // 기존 팀 수정 로직
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new TeamNotFoundException());
        team.update(request);
        teamRepository.save(team);
        
        // 엘라스틱서치 동기화
        teamSyncService.syncTeamUpdate(
            team.getId(),
            team.getName(),
            team.getDescription(),
            team.getCategory(),
            team.getStatus().toString(),
            team.getMemberCount(),
            team.getMaxMembers()
        );
        
        return team;
    }
    
    public void deleteTeam(Long teamId) {
        // 기존 팀 삭제 로직
        teamRepository.deleteById(teamId);
        
        // 엘라스틱서치 동기화
        teamSyncService.syncTeamDelete(teamId);
    }
}
```

## 설정 정보

### Docker Compose 설정 (docker-compose.elasticsearch.yml)
- **엘라스틱서치**: 8.18.0
- **키바나**: 8.18.0
- **메모리**: 512MB (개발용)
- **보안**: 비활성화 (개발용)

### Spring Boot 설정
```properties
# application.properties에 추가
spring.elasticsearch.uris=http://localhost:9200
spring.elasticsearch.connection-timeout=10s
spring.elasticsearch.socket-timeout=30s
```

## 주요 기능

### 구현된 기능
- 팀 이름/설명 통합 검색
- 카테고리별 필터링
- 상태별 필터링  
- 멤버 수 범위 검색
- 실시간 데이터 동기화
- 샘플 데이터 생성/삭제

### 데이터 동기화
- 팀 생성 시 자동 인덱싱
- 팀 수정 시 자동 업데이트
- 팀 삭제 시 자동 제거

### 향후 개선사항
- 한국어 형태소 분석기 (Nori) 추가
- 자동완성 기능
- 검색 결과 하이라이팅
- 검색 성능 모니터링
- 클러스터 구성

---

## 데이터 구조 (TeamDocument)

```json
{
  "id": "문자열 (팀 고유 ID)",
  "name": "문자열 (팀 이름)",
  "description": "문자열 (팀 설명)",
  "category": "문자열 (카테고리: 스터디, 스포츠, 문화, 취미, 프로젝트)",
  "status": "문자열 (상태: RECRUITING, ACTIVE)",
  "memberCount": "숫자 (현재 멤버 수)",
  "maxMembers": "숫자 (최대 멤버 수)",
  "createdAt": "날짜 (생성일시: ISO 8601 형식)",
  "updatedAt": "날짜 (수정일시: ISO 8601 형식)"
}
```

---

## 문제 해결 가이드

### 엘라스틱서치 연결 실패
**증상**: `Connection refused` 또는 `ConnectException` 오류

**해결 방법:**
```bash
# 1. 엘라스틱서치 상태 확인
curl http://localhost:9200/_cluster/health

# 정상 응답 예시:
# {
#   "cluster_name" : "jandi-es-cluster",
#   "status" : "yellow",
#   "timed_out" : false,
#   ...
# }

# 2. 컨테이너 상태 확인
docker ps | grep elasticsearch

# 3. 컨테이너가 없다면 시작
cd search
./start-elasticsearch.sh

# 4. 컨테이너는 있지만 응답이 없다면 재시작
docker-compose -f docker-compose.elasticsearch.yml restart
```

### 메모리 부족 오류
**증상**: `OutOfMemoryError` 또는 컨테이너 종료

**해결 방법:**
```bash
# 1. Docker 메모리 사용량 확인
docker stats

# 2. 시스템 메모리 확인 (Linux/Mac)
free -h  # Linux
vm_stat  # Mac

# 3. 메모리 설정 조정 (docker-compose.elasticsearch.yml)
# ES_JAVA_OPTS를 -Xms256m -Xmx256m으로 변경
```

### 한글 검색이 안 되는 경우
**증상**: 한글로 검색했는데 결과가 나오지 않음

**해결 방법:**
1. URL 인코딩 확인:
```bash
# 잘못된 예시
curl "http://localhost:8080/api/search/teams?query=스터디"

# 올바른 예시 (URL 인코딩)
curl "http://localhost:8080/api/search/teams?query=%EC%8A%A4%ED%84%B0%EB%94%94"

# 또는 따옴표 사용
curl 'http://localhost:8080/api/search/teams?query=스터디'
```

2. JavaScript에서는 `encodeURIComponent()` 사용:
```javascript
const query = encodeURIComponent('스터디');
fetch(`http://localhost:8080/api/search/teams?query=${query}`)
```

### Spring Boot 애플리케이션 시작 실패
**증상**: `NoSuchBeanDefinitionException` 또는 `ClassNotFoundException`

**해결 방법:**
```bash
# 1. Gradle 의존성 새로고침
./gradlew clean build --refresh-dependencies

# 2. IDE 캐시 삭제 (IntelliJ의 경우)
# File -> Invalidate Caches and Restart

# 3. application.properties 확인
# 엘라스틱서치 설정이 올바른지 확인:
# spring.elasticsearch.uris=http://localhost:9200
```

### 검색 결과가 비어있는 경우
**해결 방법:**
```bash
# 1. 샘플 데이터 확인
curl "http://localhost:8080/api/search/teams/all"

# 2. 데이터가 없다면 샘플 데이터 생성
curl -X POST "http://localhost:8080/api/admin/search/teams/sample-data"

# 3. 키바나에서 인덱스 확인
# http://localhost:5601 접속 후 Management > Index Management에서 'teams' 인덱스 확인
```

### CORS 오류 (프론트엔드에서)
**증상**: `Access-Control-Allow-Origin` 오류

**해결 방법:**
Spring Boot에 CORS 설정 추가 (이미 설정되어 있을 수 있음):
```java
@CrossOrigin(origins = "http://localhost:3000") // React 개발 서버
@RestController
public class TeamSearchController {
    // ...
}
```

---

## 지원 및 문의

**엘라스틱서치 관련 문제**:
- [Elasticsearch 공식 문서](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)
- [Spring Data Elasticsearch 공식 문서](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/)

**버그 리포트**:
- 팀 Slack 채널 또는 GitHub Issues에 문의해주세요.

**개선 제안**:
- 새로운 검색 기능이나 성능 개선 아이디어가 있다면 언제든 제안해주세요! 