package com.example.swaggerprac.service;

import com.example.swaggerprac.dto.*;
import com.example.swaggerprac.entity.PostEntity;
import com.example.swaggerprac.entity.User;
import com.example.swaggerprac.exception.ForbiddenException;
import com.example.swaggerprac.exception.ResourceNotFoundException;
import com.example.swaggerprac.repository.PostRepository;
import com.example.swaggerprac.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    @Transactional
    public ReadPostResponseDto updatePost(  Long postId, String username
            , UpdatePostRequestDto dto) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(()-> new ResourceNotFoundException("존재하지 않는 게시물입니다."));
        String writer = post.getWriter().getUsername();
        if(!writer.equals(username)) {
            throw new ForbiddenException("본인이 작성한 게시글만 수정이 가능합니다.");
        }
        post.updatePost(dto.title(), dto.content());
        return new ReadPostResponseDto(post.getTitle(),username,post.getContent(),
                post.getViewCount(),post.getCreatedAt());
    }
    @Transactional(readOnly = true)
    public PostSearchResponseDto searchPost(PostSearchDto dto) {

        List<PostEntity> post = postRepository.SearchPost(dto);
        long totalCount = postRepository.countSearch(dto);

        // 전체 페이지 수는 요청한 size 기준으로 올림 계산합니다.
        long totalPage = (totalCount + dto.size() - 1) / dto.size();

        // 게시글 데이터와 페이지 메타데이터를 분리해서 응답합니다.
        List<ReadPostResponseDto> content = new ArrayList<>();

        for (PostEntity p : post) {
            ReadPostResponseDto d= new ReadPostResponseDto(
                    p.getTitle(),
                    p.getWriter().getUsername(),
                    p.getContent(),
                    p.getViewCount(),
                    p.getCreatedAt()
            );
            content.add(d);
        }

        return new PostSearchResponseDto(content, totalCount, totalPage, dto.page(), dto.size());
    }
}
