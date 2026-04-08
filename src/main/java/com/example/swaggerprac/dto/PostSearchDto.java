package com.example.swaggerprac.dto;

import com.example.swaggerprac.entity.enumtype.PostSearchType;

public record PostSearchDto(PostSearchType type,String keyword,int page, int size) {
}
