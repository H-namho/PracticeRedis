package com.example.swaggerprac.dto;

import java.util.List;

// 검색 목록과 페이지 메타데이터를 분리해서 내려주는 응답입니다.
public record PostSearchResponseDto(
        List<PostSummaryResponseDto> content,
        long totalCount,
        long totalPage,
        int page,
        int size
) {
}
