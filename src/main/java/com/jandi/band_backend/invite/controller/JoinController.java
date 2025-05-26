package com.jandi.band_backend.invite.controller;

import com.jandi.band_backend.global.CommonResponse;
import com.jandi.band_backend.invite.service.JoinService;
import com.jandi.band_backend.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/join")
@RequiredArgsConstructor
public class JoinController {
    private final JoinService joinService;

    @PostMapping("/clubs")
    public CommonResponse<?> joinClub(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String code
    ) {
        Integer userId = userDetails.getUserId();
        joinService.joinClub(userId, code);
        return CommonResponse.success("동아리 가입 성공");
    }

    @PostMapping("/teams")
    public CommonResponse<?> joinTeam(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String code
    ) {
        Integer userId = userDetails.getUserId();
        joinService.joinTeam(userId, code);
        return CommonResponse.success("팀 가입 성공");
    }
}
