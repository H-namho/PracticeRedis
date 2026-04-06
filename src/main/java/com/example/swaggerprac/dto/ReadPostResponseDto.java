package com.example.swaggerprac.dto;

import java.time.LocalDateTime;

public record ReadPostResponseDto(String title, String writer, String content, int viewCount, LocalDateTime createdAt) {
}
