package com.example.swaggerprac.dto.post;

import jakarta.validation.constraints.NotBlank;

public record UpdatePostRequestDto(@NotBlank String title,
                                   @NotBlank String content
                                   ) {
}
