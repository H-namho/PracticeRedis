package com.example.swaggerprac.dto;

import java.time.LocalDateTime;

public record PostSummaryResponseDto(
        Long postId,
        String title,
        String writer,
        int viewCount,
        LocalDateTime createdAt
) {
}
