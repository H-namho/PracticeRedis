package com.example.swaggerprac.dto;

public record LoginResponseDto(
        String grantType,
        String accessToken,
        String refreshToken
) {
}
