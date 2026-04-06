package com.example.swaggerprac.controller;

import com.example.swaggerprac.dto.PostCreateResponseDto;
import com.example.swaggerprac.dto.PostWriteRequestDto;
import com.example.swaggerprac.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostCreateResponseDto> writePost(@RequestBody @Valid PostWriteRequestDto dto,
                                                           Authentication auth
    ) {
        String username = auth.getName();
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.writePost(username, dto));
    }
}
