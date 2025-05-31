package com.jandi.band_backend.invite.controller;

import com.jandi.band_backend.global.dto.CommonRespDTO;
import com.jandi.band_backend.invite.dto.JoinRespDTO;
import com.jandi.band_backend.invite.service.JoinService;
import com.jandi.band_backend.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Join API")
@RestController
@RequestMapping("api/join")
@RequiredArgsConstructor
public class JoinController {
    private final JoinService joinService;

    @Operation(summary = "동아리 가입 요청")
    @PostMapping("/clubs")
    public CommonRespDTO<JoinRespDTO> joinClub(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String code
    ) {
        Integer userId = userDetails.getUserId();
        JoinRespDTO joinRespDTO = joinService.joinClub(userId, code);
        return CommonRespDTO.success("동아리 가입 성공", joinRespDTO);
    }

    @Operation(summary = "팀 가입 요청")
    @PostMapping("/teams")
    public CommonRespDTO<JoinRespDTO> joinTeam(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String code
    ) {
        Integer userId = userDetails.getUserId();
        JoinRespDTO joinRespDTO = joinService.joinTeam(userId, code);
        return CommonRespDTO.success("팀 가입 성공", joinRespDTO);
    }
}
