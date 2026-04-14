package com.example.swaggerprac.dto.attach;

public record PostAttachmentResponseDto(
        String originalFileName,
        String storedFileName,
        String filePath,
        String contentType,
        long fileSize
) {
}
