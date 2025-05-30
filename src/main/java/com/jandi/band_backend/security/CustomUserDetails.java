package com.jandi.band_backend.security;

import com.jandi.band_backend.global.exception.UserNotFoundException;
import com.jandi.band_backend.user.entity.Users;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Slf4j
public class CustomUserDetails implements UserDetails {

    private final Users user;

    public CustomUserDetails(Users user) {
        this.user = user;
    }

    public Integer getUserId() {
        if(user.getDeletedAt() != null) {
            throw new UserNotFoundException("유저 정보가 존재하지 않습니다: 탈퇴한 회원입니다.");
        }
        return user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getAdminRole().name()));
    }

    @Override
    public String getPassword() {
        // 카카오 OAuth 인증이므로 암호가 없을 수 있음
        return "";
    }

    @Override
    public String getUsername() {
        return user.getKakaoOauthId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getDeletedAt() == null;
    }
}
