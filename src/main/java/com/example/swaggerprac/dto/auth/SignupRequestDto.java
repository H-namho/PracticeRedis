package com.example.swaggerprac.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SignupRequestDto(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank @Email String email,
        @NotNull @Min(1) Integer age) {
}
