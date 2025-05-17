package com.jandi.band_backend.auth.dto;

import com.jandi.band_backend.user.entity.UserPhoto;
import com.jandi.band_backend.user.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class UserInfoDTO {
    private int id;
    private String nickname;
    private String profilePhoto;
    private String position;
    private String university;

    public UserInfoDTO(Users user, UserPhoto userPhoto) {
        id = user.getId();
        nickname = user.getNickname();
        profilePhoto = userPhoto == null ? "" : userPhoto.getImageUrl();
        position = user.getPosition().name();
        university = user.getUniversity().getName();
    }
}
