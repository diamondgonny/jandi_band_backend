package com.jandi.band_backend.invite.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InviteLinkRespDTO {
    private final String link;
    private final Integer id;
}
