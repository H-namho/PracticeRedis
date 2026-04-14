package com.example.swaggerprac.service;

import com.example.swaggerprac.dto.PostAttachmentResponseDto;
import com.example.swaggerprac.dto.PostCreateResponseDto;
import com.example.swaggerprac.dto.PostSearchDto;
import com.example.swaggerprac.dto.PostSearchResponseDto;
import com.example.swaggerprac.dto.PostSummaryResponseDto;
import com.example.swaggerprac.dto.PostWriteRequestDto;
import com.example.swaggerprac.dto.ReadPostResponseDto;
import com.example.swaggerprac.dto.UpdatePostRequestDto;
import com.example.swaggerprac.entity.PostAttachment;
import com.example.swaggerprac.entity.PostEntity;
import com.example.swaggerprac.entity.User;
import com.example.swaggerprac.exception.ForbiddenException;
import com.example.swaggerprac.exception.ResourceNotFoundException;
import com.example.swaggerprac.redis.ReadCountRepository;
import com.example.swaggerprac.repository.PostAttachmentRepository;
import com.example.swaggerprac.repository.PostRepository;
import com.example.swaggerprac.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostAttachmentRepository attachmentRepository;
    private final FileStorageService fileStorageService;
    private final ReadCountRepository countRepository;

    @Transactional
    public PostCreateResponseDto writePost(String username, PostWriteRequestDto dto, List<MultipartFile> files) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("회원정보를 찾을 수 없습니다."));

        PostEntity postEntity = PostEntity.create(dto.title(), user, dto.content());
        PostEntity post = postRepository.save(postEntity); // 제목, 작성자, 내용 저장
        List<PostAttachmentResponseDto> attachments = new ArrayList<>();

        if (files != null) {
            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) {
                    continue;
                }

                PostAttachmentResponseDto attachment = fileStorageService.store(file); // 실제 업로드
                PostAttachment postAttachment = new PostAttachment(
                        post,
                        attachment.originalFileName(),
                        attachment.storedFileName(),
                        attachment.filePath(),
                        attachment.contentType(),
                        attachment.fileSize()
                );
                attachmentRepository.save(postAttachment); // db에 경로, 파일타입 등 저장
                attachments.add(attachment);
            }
        }

        return new PostCreateResponseDto(post.getPostId(), attachments);
    }

    @Transactional
    public ReadPostResponseDto readPost(Long postId) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글정보를 찾을 수 없습니다"));
        countRepository.increase(post.getPostId());
        return toReadPostResponseDto(post);
    }

    @Transactional
    public ReadPostResponseDto updatePost(Long postId, String username, UpdatePostRequestDto dto) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글 정보를 찾을 수 없습니다."));
        String writer = post.getWriter().getUsername();

        if (!writer.equals(username)) {
            throw new ForbiddenException("본인의 게시글만 수정이 가능합니다.");
        }

        post.updatePost(dto.title(), dto.content());
        return toReadPostResponseDto(post);
    }

    @Transactional(readOnly = true)
    public PostSearchResponseDto searchPost(PostSearchDto dto) {
        List<PostEntity> posts = postRepository.SearchPost(dto);
        long totalCount = postRepository.countSearch(dto);
        long totalPage = (totalCount + dto.size() - 1) / dto.size();

        List<PostSummaryResponseDto> content = new ArrayList<>();
        for (PostEntity post : posts) {
            content.add(new PostSummaryResponseDto(
                    post.getPostId(),
                    post.getTitle(),
                    post.getWriter().getUsername(),
                    (int)countRepository.getCount(post.getPostId())+post.getViewCount(),
                    post.getCreatedAt()
            ));
        }

        return new PostSearchResponseDto(content, totalCount, totalPage, dto.page(), dto.size());
    }

    @Transactional
    public void removePost(Long postId, String username) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("게시글 정보를 찾을 수 없습니다."));
        String writer = post.getWriter().getUsername();

        if (!username.equals(writer)) {
            throw new ForbiddenException("본인의 게시글만 삭제가 가능합니다");
        }
        for (PostAttachment attachment : post.getAttachments()) {
            fileStorageService.delete(attachment.getFilePath());
        }
        postRepository.delete(post);
    }

    private ReadPostResponseDto toReadPostResponseDto(PostEntity post) {
        List<PostAttachmentResponseDto> attachments = new ArrayList<>();
        for (PostAttachment attachment : post.getAttachments()) {
            attachments.add(new PostAttachmentResponseDto(
                    attachment.getOriginalFileName(),
                    attachment.getStoredFileName(),
                    attachment.getFilePath(),
                    attachment.getContentType(),
                    attachment.getFileSize()
            ));
        }

        int viewCount = post.getViewCount() + (int)countRepository.getCount(post.getPostId());

        return new ReadPostResponseDto(
                post.getTitle(),
                post.getWriter().getUsername(),
                post.getContent(),
                viewCount,
                post.getCreatedAt(),
                attachments
        );
    }
}
