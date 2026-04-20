package com.example.swaggerprac.dto.message;

import java.time.LocalDateTime;

public record ReadMessageResponseDto(Long roomId, Long messageId, String sender, String content,LocalDateTime createdAt) {
}
