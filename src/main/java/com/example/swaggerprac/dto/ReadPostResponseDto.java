package com.example.swaggerprac.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ReadPostResponseDto(String title, String writer, String content, int viewCount,
                                  LocalDateTime createdAt, List<PostAttachmentResponseDto> attachments) {
}
