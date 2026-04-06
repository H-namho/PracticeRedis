package com.example.swaggerprac.service;

import com.example.swaggerprac.dto.PostCreateResponseDto;
import com.example.swaggerprac.dto.PostWriteRequestDto;
import com.example.swaggerprac.dto.ReadPostResponseDto;
import com.example.swaggerprac.entity.PostEntity;
import com.example.swaggerprac.entity.User;
import com.example.swaggerprac.exception.ResourceNotFoundException;
import com.example.swaggerprac.repository.PostRepository;
import com.example.swaggerprac.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostCreateResponseDto writePost(String username, PostWriteRequestDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 회원입니다."));

        PostEntity postEntity = PostEntity.create(dto.title(), user, dto.content());
        PostEntity savedPost = postRepository.save(postEntity);

        return new PostCreateResponseDto(savedPost.getPostId());
    }
    @Transactional
    public ReadPostResponseDto readPost(Long postId) {
        PostEntity post =postRepository.findById(postId)
                .orElseThrow(()-> new ResourceNotFoundException("존재하지 않는 게시물입니다."));
        post.increaseViewCount(); // 추후 원자적업데이트로 변경
        return new ReadPostResponseDto(post.getTitle(),post.getWriter().getUsername()
                ,post.getContent(),post.getViewCount(),post.getCreatedAt());
    }
}
