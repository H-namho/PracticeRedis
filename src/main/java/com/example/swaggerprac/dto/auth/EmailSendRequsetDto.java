package com.example.swaggerprac.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailSendRequsetDto(@NotBlank @Email String email ) {
}
