package com.jandi.band_backend.user.dto;

import com.jandi.band_backend.user.entity.UserPhoto;
import com.jandi.band_backend.user.entity.Users;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "사용자 정보 DTO")
public class UserInfoDTO {
    @Schema(description = "사용자 고유 ID", example = "1")
    private Integer id; // 자체 고유 아이디 (카카오 회원번호 아님)
    
    @Schema(description = "사용자 닉네임", example = "록스타김철수")
    private String nickname;
    
    @Schema(description = "프로필 사진 URL", example = "https://example.com/profile.jpg")
    private String profilePhoto;
    
    @Schema(description = "포지션 (악기/보컬)", example = "GUITAR")
    private String position;
    
    @Schema(description = "소속 대학명", example = "서울대학교")
    private String university;

    public UserInfoDTO(Users user, UserPhoto userPhoto) {
        id = user.getId();
        nickname = user.getNickname();
        profilePhoto = userPhoto == null ? "" : userPhoto.getImageUrl();
        position = user.getPosition().name();
        university = user.getUniversity().getName();
    }
}
