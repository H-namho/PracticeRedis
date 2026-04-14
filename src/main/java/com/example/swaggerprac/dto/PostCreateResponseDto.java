package com.example.swaggerprac.dto;

import java.util.List;

public record PostCreateResponseDto(Long postId, List<PostAttachmentResponseDto> attachments) {
}
