package com.example.swaggerprac.service;

import com.example.swaggerprac.dto.attach.PostAttachmentResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;



import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public PostAttachmentResponseDto store(MultipartFile file){
        if(file ==null || file.isEmpty()){
            throw new IllegalArgumentException("비어있는 파일은 업로드 할 수 없습니다.");
        }
        try{
            // 업로드 경로 path객체로만듬
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

            // 폴더가 없다면 폴더를 만들어라
            Files.createDirectories(uploadPath);
            // 원본이름
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            // uuid + 원본이름
            String storedFileName = UUID.randomUUID() + "_" + originalFileName;
            // 최종 경로 uploadPath = C:/project/uploads + storedFileName = abc123_cat.png
            Path targetPath = uploadPath.resolve(storedFileName);

            Files.copy(file.getInputStream(),targetPath, StandardCopyOption.REPLACE_EXISTING);
            return new PostAttachmentResponseDto(originalFileName,storedFileName,targetPath.toString(),file.getContentType(),file.getSize());
        }catch (IOException e){
            throw new IllegalArgumentException("파일 저장에 실패했습니다.");
        }
    }

    // 디스크에서 원본삭제
    public void delete(String filePath) {
        try {
                Path path = Paths.get(filePath).toAbsolutePath().normalize();
                Files.deleteIfExists(path);
            } catch (IOException e) {
                throw new IllegalArgumentException("파일 삭제에 실패했습니다.");
            }
    }
}
