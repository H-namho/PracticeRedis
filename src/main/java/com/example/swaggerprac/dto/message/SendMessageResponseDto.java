package com.example.swaggerprac.dto.message;

import java.time.LocalDateTime;

public record SendMessageResponseDto(Long roomid, Long messageId, String sender, String content,
                                     LocalDateTime createdAt) {
}
