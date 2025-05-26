package com.jandi.band_backend.invite.controller;

import com.jandi.band_backend.global.CommonResponse;
import com.jandi.band_backend.invite.dto.InviteLinkRespDTO;
import com.jandi.band_backend.invite.service.InviteService;
import com.jandi.band_backend.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/invite")
@RequiredArgsConstructor
public class InviteController {
    private final InviteService inviteService;

    @PostMapping("/clubs/{clubId}")
    public CommonResponse<InviteLinkRespDTO> inviteClub(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("clubId") Integer clubId)
    {
        Integer userId = userDetails.getUserId();
        InviteLinkRespDTO inviteLinkRespDTO = inviteService.generateInviteClubLink(userId, clubId);
        return CommonResponse.success("동아리 초대 링크 생성 성공", inviteLinkRespDTO);
    }
}
