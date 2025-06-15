# Elasticsearch 검색 기능

이 폴더는 Spring Boot 프로젝트에 Elasticsearch 검색 기능을 통합하기 위한 예제 코드를 포함합니다.

## 개요

Elasticsearch는 분산형 검색 및 분석 엔진으로, 대용량 데이터에서 빠르고 정확한 검색을 제공합니다. 이 예제에서는 공연 홍보 데이터의 검색 기능을 Elasticsearch로 구현합니다.

## 주요 기능

### 1. 공연 홍보 검색 (Elasticsearch 기반)
- **엔드포인트**: `/api/promos/search-v2`
- **기능**: 키워드 기반 통합 검색 (제목, 팀명, 설명)
- **기존 JPA 검색**: `/api/promos/search` (기존 기능 유지)

### 2. 공연 홍보 필터링 (Elasticsearch 기반)
- **엔드포인트**: `/api/promos/filter-v2`
- **기능**: 날짜 범위, 팀명으로 필터링
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

## API 사용법

### 1. 키워드 검색
```bash
curl -X GET "http://localhost:8080/api/promos/search-v2?keyword=락밴드&page=0&size=10"
```

### 2. 필터링 (날짜 범위, 팀명)
```bash
# 날짜 범위와 팀명으로 필터링
curl -X GET "http://localhost:8080/api/promos/filter-v2?startDate=2024-03-01T00:00:00&endDate=2024-03-31T23:59:59&teamName=락밴드&page=0&size=10"

# 날짜 범위만으로 필터링
curl -X GET "http://localhost:8080/api/promos/filter-v2?startDate=2024-03-01T00:00:00&endDate=2024-03-31T23:59:59&page=0&size=10"

# 팀명만으로 필터링
curl -X GET "http://localhost:8080/api/promos/filter-v2?teamName=락밴드&page=0&size=10"
```

### 3. 지도 기반 검색 (위도/경도 범위)
```bash
curl -X GET "http://localhost:8080/api/promos/map-v2?startLatitude=37.5&startLongitude=126.9&endLatitude=37.6&endLongitude=127.0&page=0&size=10"
```

### 4. 제목 검색
```bash
curl -X GET "http://localhost:8080/api/promos/search-v2/title?title=정기공연&page=0&size=10"
```

### 5. 팀명 검색
```bash
curl -X GET "http://localhost:8080/api/promos/search-v2/team?teamName=락밴드&page=0&size=10"
```

### 6. 장소 검색
```bash
curl -X GET "http://localhost:8080/api/promos/search-v2/location?location=홍대&page=0&size=10"
```

### 7. 모든 공연 홍보 조회
```bash
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
        "description": "매주 토요일 밤 8시에 진행되는 락밴드 정기공연입니다.",
        "location": "홍대 클럽",
        "address": "서울시 마포구 홍익로 123",
        "latitude": 126.99597295767953,
        "longitude": 35.97664845766847,
        "admissionFee": 15000,
        "eventDatetime": "2024-03-15T19:00:00",
        "createdAt": "2024-03-01T10:00:00",
        "updatedAt": "2024-03-01T10:00:00",
        "likeCount": 25,
        "photoUrls": ["https://example.com/rock-band.jpg"]
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10,
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      },
      "offset": 0,
      "paged": true,
      "unpaged": false
    },
    "totalElements": 3,
    "totalPages": 1,
    "last": true,
    "size": 10,
    "number": 0,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "numberOfElements": 3,
    "first": true,
    "empty": false
  }
}
```

## 프론트엔드 통합 예제

### React 컴포넌트 예제
```jsx
import React, { useState, useEffect } from 'react';
import axios from 'axios';

const PromoSearch = () => {
  const [promos, setPromos] = useState([]);
  const [keyword, setKeyword] = useState('');
  const [loading, setLoading] = useState(false);

  const searchPromos = async (searchKeyword) => {
    setLoading(true);
    try {
      const response = await axios.get(`/api/promos/search-v2`, {
        params: {
          keyword: searchKeyword,
          page: 0,
          size: 20
        }
      });
      setPromos(response.data.data.content);
    } catch (error) {
      console.error('검색 실패:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <input
        type="text"
        value={keyword}
        onChange={(e) => setKeyword(e.target.value)}
        placeholder="검색어를 입력하세요"
      />
      <button onClick={() => searchPromos(keyword)} disabled={loading}>
        {loading ? '검색 중...' : '검색'}
      </button>
      
      <div>
        {promos.map(promo => (
          <div key={promo.id}>
            <h3>{promo.title}</h3>
            <p>팀: {promo.teamName}</p>
            <p>장소: {promo.location}</p>
            <p>입장료: {promo.admissionFee}원</p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default PromoSearch;
```

### Vue.js 컴포넌트 예제
```vue
<template>
  <div>
    <input v-model="keyword" placeholder="검색어를 입력하세요" />
    <button @click="searchPromos" :disabled="loading">
      {{ loading ? '검색 중...' : '검색' }}
    </button>
    
    <div v-for="promo in promos" :key="promo.id">
      <h3>{{ promo.title }}</h3>
      <p>팀: {{ promo.teamName }}</p>
      <p>장소: {{ promo.location }}</p>
      <p>입장료: {{ promo.admissionFee }}원</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      keyword: '',
      promos: [],
      loading: false
    };
  },
  methods: {
    async searchPromos() {
      this.loading = true;
      try {
        const response = await axios.get('/api/promos/search-v2', {
          params: {
            keyword: this.keyword,
            page: 0,
            size: 20
          }
        });
        this.promos = response.data.data.content;
      } catch (error) {
        console.error('검색 실패:', error);
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>
```

## 백엔드 테스트 가이드

### 1. Elasticsearch 시작
```bash
# Docker Compose로 Elasticsearch 시작
docker-compose -f docker-compose.elasticsearch.yml up -d

# 또는 스크립트 실행
./start-elasticsearch.sh
```

### 2. 애플리케이션 시작
```bash
./gradlew bootRun
```

### 3. 실제 데이터 동기화
```bash
# 데이터베이스의 모든 공연 홍보를 Elasticsearch에 동기화
curl -X POST http://localhost:8080/api/admin/promos/sync-all
```

### 4. 검색 테스트
```bash
# 키워드 검색
curl -X GET "http://localhost:8080/api/promos/search-v2?keyword=락밴드"

# 제목 검색
curl -X GET "http://localhost:8080/api/promos/search-v2/title?title=정기공연"

# 팀명 검색
curl -X GET "http://localhost:8080/api/promos/search-v2/team?teamName=락밴드"
```

## 문제 해결

### 1. Elasticsearch 연결 실패
- Elasticsearch가 실행 중인지 확인
- 포트 9200이 사용 가능한지 확인
- 방화벽 설정 확인

### 2. 검색 결과가 비어있음
- 샘플 데이터가 생성되었는지 확인
- Elasticsearch 인덱스가 올바르게 생성되었는지 확인
- 검색 키워드가 데이터와 일치하는지 확인

### 3. 403 Forbidden 오류
- Spring Security 설정에서 `/api/admin/**` 경로가 허용되었는지 확인
- 인증 토큰이 올바른지 확인

### 4. 인덱스 매핑 오류
- Elasticsearch 인덱스를 삭제하고 재생성
- 애플리케이션 재시작으로 자동 인덱스 생성

## 기존 JPA 검색과의 차이점

| 기능 | JPA 검색 | Elasticsearch 검색 |
|------|----------|-------------------|
| **키워드 검색** | `/api/promos/search` | `/api/promos/search-v2` |
| **필터링** | `/api/promos/filter` | `/api/promos/filter-v2` |
| **지도 검색** | `/api/promos/map` | `/api/promos/map-v2` |
| **검색 속도** | 일반적 | 매우 빠름 |
| **복잡한 검색** | 제한적 | 강력함 |
| **풀텍스트 검색** | 기본적 | 고급 |
| **페이징** | 지원 | 지원 |
| **정렬** | 지원 | 지원 |
| **사용자별 좋아요 상태** | 지원 | 지원 |
| **요청/응답 형식** | 동일 | 동일 |

## 다음 단계

1. **실제 데이터 동기화**: Promo 엔티티 변경 시 자동으로 Elasticsearch 동기화
2. **고급 검색**: 날짜 범위, 가격 범위, 위치 기반 검색 추가
3. **검색 결과 하이라이팅**: 검색어 강조 표시
4. **검색 제안**: 자동완성 기능
5. **검색 분석**: 인기 검색어, 검색 통계 