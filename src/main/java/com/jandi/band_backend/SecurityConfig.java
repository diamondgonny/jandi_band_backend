package com.jandi.band_backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // SecurityFilterChain을 사용하여 보안 설정 정의
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests()  // authorizeRequests()는 deprecated 됨
                .requestMatchers("/")  // "/" URL을 예외로 처리
                .permitAll()  // "/" URL은 인증 없이 접근 가능
                .anyRequest().authenticated()  // 그 외의 요청은 인증을 요구
                .and()
                .formLogin()  // 기본 로그인 페이지 사용
                .loginPage("/login")  // 로그인 페이지 설정 (선택사항)
                .permitAll()
                .and()
                .logout()  // 로그아웃 처리
                .permitAll();
        return http.build();
    }

    // 비밀번호 인코더 설정
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}



