package com.jandi.band_backend.auth.dto;

import com.jandi.band_backend.user.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private int id;
    private String nickname;
    private String profilePhoto;
    private String position;
    private String university;

    public UserInfoDTO(Users user) {
        id = user.getId();
        nickname = user.getNickname();
        profilePhoto = user.getProfilePhoto() == null ?
                "이미지 없음" : user.getProfilePhoto().getImageUrl();
        position = user.getPosition().name();
        university = user.getUniversity().getName();
    }
}
