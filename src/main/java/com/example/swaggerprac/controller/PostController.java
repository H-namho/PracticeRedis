package com.example.swaggerprac.controller;

import com.example.swaggerprac.dto.*;
import com.example.swaggerprac.entity.enumtype.PostSearchType;
import com.example.swaggerprac.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
@Validated
public class PostController {

    private final PostService postService;

    @PostMapping("")
    public ResponseEntity<PostCreateResponseDto> writePost(@RequestBody @Valid PostWriteRequestDto dto,
                                                           Authentication auth
    ) {
        String username = auth.getName();
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.writePost(username, dto));
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
    @GetMapping("/searchPost")
    public ResponseEntity<PostSearchResponseDto> searchPost(@RequestParam PostSearchType type
                                    , @RequestParam String keyword,@RequestParam @Min(0) int page
                                    // 페이지 크기는 1 이상이어야 정상적인 페이징 계산이 가능합니다.
                                    , @RequestParam @Max(30) @Min(1) int size) {
        PostSearchDto dto = new PostSearchDto(type,keyword,page,size);
        return ResponseEntity.ok(postService.searchPost(dto));
    }


}
