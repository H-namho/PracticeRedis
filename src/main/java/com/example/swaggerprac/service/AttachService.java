package com.example.swaggerprac.service;

import com.example.swaggerprac.dto.DownloadFileDto;
import com.example.swaggerprac.entity.PostAttachment;
import com.example.swaggerprac.exception.ResourceNotFoundException;
import com.example.swaggerprac.repository.PostAttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
