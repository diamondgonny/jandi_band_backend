
# Invite & Join API 명세서

## Base URL
`/api`

## 인증
모든 초대/가입 API는 JWT 인증 필요.

---

## 1. 동아리 초대 링크 생성
### POST `/api/invite/clubs/{clubId}`

#### 설명
특정 동아리의 초대 링크를 생성한다.  
프론트에서는 생성된 링크를 클립보드에 복사하거나, 카카오톡으로 공유할 수 있다.

#### 요청 예시 (React에서)
```js
const token = sessionStorage.getItem('userToken');
axios.post(
  `http://localhost:8080/api/invite/clubs/${clubId}`,
  {},
  {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  }
)
.then(response => {
  const inviteLink = response.data.data.link;
  console.log('생성된 초대 링크:', inviteLink);
});
```

#### 요청
```bash
curl -X POST "http://localhost:8080/api/invite/clubs/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (200 OK)
```json
{
  "success": true,
  "message": "동아리 초대 링크 생성 성공",
  "data": {
    "link": "https://rhythmeetdevelop.netlify.app/invite/accept?code=jaCprFeFtE"
  }
}
```

---

## 2. 동아리 초대 수락 (가입)
### POST `/api/join/clubs?code={code}`

#### 설명
초대 링크를 클릭하면 code 파라미터를 통해 동아리 가입이 처리된다.  
프론트에서는 성공 시 환영 페이지로 이동, 실패 시 에러 메시지를 표시한다.

#### 요청 예시 (React에서)
```js
const token = sessionStorage.getItem('userToken');
axios.post(
  'http://localhost:8080/api/join/clubs',
  null,
  {
    params: { code },
    headers: {
      'Authorization': `Bearer ${token}`
    }
  }
)
.then(response => {
  if (response.data.success) {
    navigate('/welcome', {
      state: {
        message: '동아리 가입을 축하합니다! 🎉',
        clubName: '리듬밋 동아리'
      }
    });
  }
});
```

#### 요청
```bash
curl -X POST "http://localhost:8080/api/join/clubs?code=jaCprFeFtE" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 응답 (성공 200 OK)
```json
{
  "success": true,
  "message": "동아리 가입 성공"
}
```

#### 응답 (실패 예시)
```json
{
  "success": false,
  "message": "이미 가입한 동아리입니다",
  "errorCode": "INVALID_ACCESS"
}
```

---

## 상태 코드
- `200 OK`: 성공
- `400 Bad Request`: 잘못된 요청
- `401 Unauthorized`: 인증 실패 (JWT 토큰 필요)
- `403 Forbidden`: 권한 없음
- `404 Not Found`: 리소스 없음

---

## 참고 사항
- 초대 링크는 기본적으로 7일간 유효합니다.
- 초대 링크를 통한 가입은 중복 가입이 불가능합니다.
- 링크 생성은 동아리 멤버인 사람만 가능합니다.
- 카카오톡 메시지 생성 및 환영 페이지 이동은 프론트에서 처리해주셔야 합니다.
- 팀 초대/가입은 동아리 초대/가입과 유사합니다. 요청 API 엔드포인트만 조금 다른게 전부라 DOCS에선 설명하지 않았습니다.

## 프론트 예시
<video controls src="./invite-join_exaple.mp4" title="Title"></video>