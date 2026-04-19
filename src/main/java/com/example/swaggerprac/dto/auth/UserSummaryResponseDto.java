package com.example.swaggerprac.dto.auth;

public record UserSummaryResponseDto(
        Long id,
        String username,
        String email
) {
}
