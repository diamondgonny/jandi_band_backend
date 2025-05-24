package com.jandi.band_backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshReqDTO {
    private String refreshToken;
}
