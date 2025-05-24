package com.jandi.band_backend.user.controller;

import com.jandi.band_backend.global.CommonResponse;
import com.jandi.band_backend.security.CustomUserDetails;
import com.jandi.band_backend.user.dto.MyClubRespDTO;
import com.jandi.band_backend.user.dto.MyTeamRespDTO;
import com.jandi.band_backend.user.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "MyPage API")
@RestController
@RequestMapping("/api/my")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @Operation(summary = "내가 참가한 동아리 목록 조회")
    @GetMapping("/clubs")
    public CommonResponse<List<MyClubRespDTO>> getMyClubs(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        List<MyClubRespDTO> myClubs = myPageService.getMyClubs(currentUser.getUserId());
        return CommonResponse.success("내가 참가한 동아리 목록 조회 성공", myClubs);
    }

    @Operation(summary = "내가 참가한 팀 목록 조회")
    @GetMapping("/teams")
    public CommonResponse<List<MyTeamRespDTO>> getMyTeams(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        List<MyTeamRespDTO> myTeams = myPageService.getMyTeams(currentUser.getUserId());
        return CommonResponse.success("내가 참가한 팀 목록 조회 성공", myTeams);
    }
} 