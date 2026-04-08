package com.example.swaggerprac.repository;

import com.example.swaggerprac.dto.PostSearchDto;
import com.example.swaggerprac.entity.PostEntity;
import com.example.swaggerprac.entity.enumtype.PostSearchType;

import java.util.List;

public interface PostRepositoryCustom {

    List<PostEntity> SearchPost(PostSearchDto dto);

    long countSearch(PostSearchDto dto);
}
