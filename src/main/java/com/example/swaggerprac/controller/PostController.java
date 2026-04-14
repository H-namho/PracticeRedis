package com.example.swaggerprac.controller;

import com.example.swaggerprac.dto.*;
import com.example.swaggerprac.entity.enumtype.PostSearchType;
import com.example.swaggerprac.service.PostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
@Validated
public class PostController {

    private final PostService postService;
    private final ObjectMapper objectMapper;

//    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<PostCreateResponseDto> writePost(@RequestPart("data") @Valid PostWriteRequestDto dto,
//                                                           @RequestPart(value = "files", required = false) List<MultipartFile> files,
//                                                           Authentication auth
//    ) {
//        String username = auth.getName();
//        return ResponseEntity.status(HttpStatus.CREATED).body(postService.writePost(username, dto, files));
//    }
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostCreateResponseDto> writePost(
            @RequestPart("data") String data,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            Authentication auth
    ) throws JsonProcessingException {
        PostWriteRequestDto dto = objectMapper.readValue(data, PostWriteRequestDto.class);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.writePost(auth.getName(), dto, files));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ReadPostResponseDto> readPost(@PathVariable @Positive  Long postId) {
        return ResponseEntity.ok(postService.readPost(postId));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<ReadPostResponseDto> updatePost(@PathVariable @Positive Long postId,@RequestBody @Valid UpdatePostRequestDto dto,
                           Authentication  auth) {
        String username = auth.getName();
        return ResponseEntity.ok(postService.updatePost(postId,username,dto));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> removePost(@PathVariable @Positive Long postId, Authentication auth){
        postService.removePost(postId, auth.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/searchPost")
    public ResponseEntity<PostSearchResponseDto> searchPost(@RequestParam PostSearchType type
                                    , @RequestParam String keyword,@RequestParam @Min(0) int page
                                    , @RequestParam @Max(30) @Min(1) int size) {
        PostSearchDto dto = new PostSearchDto(type,keyword,page,size);
        return ResponseEntity.ok(postService.searchPost(dto));
    }


}
