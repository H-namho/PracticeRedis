package com.example.swaggerprac.controller;

import com.example.swaggerprac.dto.attach.DownloadFileDto;
import com.example.swaggerprac.service.AttachService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/attach")
@RequiredArgsConstructor
public class AttachController {

    private final AttachService attachService;

    @GetMapping("/{attachId}")
    public ResponseEntity<Resource> readFile(@PathVariable Long attachId) throws MalformedURLException {
        DownloadFileDto dto = attachService.downloadFile(attachId);

        Resource resource = new UrlResource(Paths.get(dto.filePath()).toUri());
        // 클라이언트가 이 파일을 어떻게 다뤄야할지 알려줘야함
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(dto.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,  "attachment; filename=\"" + dto.originalFileName() + "\"")
                .body(resource);

    }

}
