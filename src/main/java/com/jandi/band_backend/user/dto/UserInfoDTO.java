package com.jandi.band_backend.user.dto;

import com.jandi.band_backend.user.entity.UserPhoto;
import com.jandi.band_backend.user.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "사용자 정보")
public class UserInfoDTO {
    @Schema(description = "사용자 ID")
    private Integer id; // 자체 고유 아이디 (카카오 회원번호 아님)
    
    @Schema(description = "닉네임")
    private String nickname;
    
    @Schema(description = "프로필 사진 URL")
    private String profilePhoto;
    
    @Schema(description = "포지션")
    private String position;
    
    @Schema(description = "대학명")
    private String university;

    public UserInfoDTO(Users user, UserPhoto userPhoto) {
        id = user.getId();
        nickname = user.getNickname();
        profilePhoto = userPhoto == null ? "" : userPhoto.getImageUrl();
        position = user.getPosition().name();
        university = user.getUniversity().getName();
    }
}
