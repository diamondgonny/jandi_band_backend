package com.jandi.band_backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("개발 서버"),
                        new Server().url("https://rhythmeet-be.yeonjae.kr").description("운영 서버")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    private Info apiInfo() {
        return new Info()
                .title("Jandi Band Backend API")
                .description("잔디밴드 백엔드 API 문서입니다. 동아리 및 팀 관리, 연습 일정, 투표 등의 기능을 제공합니다.")
                .version("v1.0.0")
                .contact(new Contact()
                        .name("Jandi Band Team")
                        .email("contact@jandiband.com")
                        .url("https://jandiband.com")
                )
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT")
                );
    }
} 