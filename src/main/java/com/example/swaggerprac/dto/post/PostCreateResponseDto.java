package com.example.swaggerprac.dto.post;

import com.example.swaggerprac.dto.attach.PostAttachmentResponseDto;

import java.util.List;

public record PostCreateResponseDto(Long postId, List<PostAttachmentResponseDto> attachments) {
}
