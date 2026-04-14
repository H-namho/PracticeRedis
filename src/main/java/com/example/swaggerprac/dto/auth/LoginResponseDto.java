package com.example.swaggerprac.dto.auth;

public record LoginResponseDto(
        String grantType,
        String accessToken,
        String refreshToken
) {
}
