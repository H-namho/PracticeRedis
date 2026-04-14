package com.example.swaggerprac.repository;

import com.example.swaggerprac.dto.post.PostSearchDto;
import com.example.swaggerprac.entity.PostEntity;

import java.util.List;

public interface PostRepositoryCustom {

    List<PostEntity> SearchPost(PostSearchDto dto);

    long countSearch(PostSearchDto dto);
}
