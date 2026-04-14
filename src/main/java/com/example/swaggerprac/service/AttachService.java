package com.example.swaggerprac.service;

import com.example.swaggerprac.dto.attach.DownloadFileDto;
import com.example.swaggerprac.entity.PostAttachment;
import com.example.swaggerprac.exception.ResourceNotFoundException;
import com.example.swaggerprac.repository.PostAttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttachService {

    private final PostAttachmentRepository attachmentRepository;

    @Transactional(readOnly = true)
    public DownloadFileDto downloadFile(Long attachId) {

        PostAttachment attachment = attachmentRepository.findById(attachId)
                .orElseThrow(()->new ResourceNotFoundException("첨부파일을 찾지 못했습니다."));
        return new DownloadFileDto(attachment.getOriginalFileName()
        ,attachment.getContentType(),attachment.getFilePath());
    }
}
