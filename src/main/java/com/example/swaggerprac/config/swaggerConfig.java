package com.example.swaggerprac.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class swaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Open Board API 문서")
                        .description("커뮤니티 게시판 서비스 API")
                        .version("1.0"))
                // 전체 API 문서에 bearerAuth 보안 요구사항 적용
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        // bearerAuth 라는 이름으로 JWT 인증 방식을 등록
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP) // HTTP 인증 방식
                                .scheme("bearer")               // Bearer 토큰 방식
                                .bearerFormat("JWT")));        // 토큰 형식은 JWT
    }

}

