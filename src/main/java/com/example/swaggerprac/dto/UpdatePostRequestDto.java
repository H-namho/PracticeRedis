package com.example.swaggerprac.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePostRequestDto(@NotBlank String title,
                                   @NotBlank String content
                                   ) {
}
