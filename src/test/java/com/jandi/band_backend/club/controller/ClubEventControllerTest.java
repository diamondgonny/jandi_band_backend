package com.jandi.band_backend.club.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jandi.band_backend.club.dto.ClubEventReqDTO;
import com.jandi.band_backend.club.dto.ClubEventRespDTO;
import com.jandi.band_backend.club.service.ClubEventService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClubEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    //  @MockBean  // mockbean 테스트 용
    @Autowired  // 실제 데이터를 넣는 테스트
    private ClubEventService clubEventService;

    @Autowired
    private ObjectMapper objectMapper;

    // 동아리 일정 추가 api 테스트 코드 추가
    @Test
    @WithMockUser(username = "testuser", roles = "USER") // 인증된 사용자 시뮬레이션
    void createClubEvent_success() throws Exception {
        // given
        Integer clubId = 1;
        ClubEventReqDTO request = ClubEventReqDTO.builder()
                .name("정기 일정 예시250610")
                .startDatetime(LocalDateTime.of(2025, 6, 20, 18, 0))
                .endDatetime(LocalDateTime.of(2025, 6, 20, 20, 0))
                .build();

        ClubEventRespDTO response = ClubEventRespDTO.builder()
                .id(1L)
                .name(request.getName())
                .startDatetime(request.getStartDatetime())
                .endDatetime(request.getEndDatetime())
                .build();

//        mockbean 테스트 용
//        when(clubEventService.createClubEvent(eq(clubId), anyInt(), any(ClubEventReqDTO.class)))
//                .thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/clubs/{clubId}/events", clubId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()) // Spring Security에서 POST 요청은 CSRF 토큰 필요
                        .header("Authorization",
                                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0MjcxMDU1NjQ1Iiwicm9sZSI6IlJPTEVfQURNSU4iLCJpYXQiOjE3NDk4MTI5NzgsImV4cCI6MTc0OTgxMzg3OH0.jh66rHadxQvZn0eobxA5j674a6CAopz-gmDQAS-9XWg")) // 인증 헤더 추가
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("정기 일정 예시250610"));
    }
}