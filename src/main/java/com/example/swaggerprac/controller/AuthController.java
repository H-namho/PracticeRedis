package com.example.swaggerprac.controller;

import com.example.swaggerprac.dto.*;
import com.example.swaggerprac.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Long> signup(@RequestBody @Valid SignupRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(dto));
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody @Valid LoginRequestDto dto) {
        return authService.login(dto);
    }

    @PostMapping("/logout")
    public void logout(@RequestBody LogoutRequestDto dto, Authentication auth) {
        authService.logout(dto, auth.getName());
    }

    @PostMapping("/refresh")
    public LoginResponseDto refresh(@RequestBody @Valid RefreshTokenRequestDto dto) {
        return authService.refresh(dto.refreshToken());
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponseDto> getMe(Authentication auth) {
        return ResponseEntity.ok(authService.getMe(auth.getName()));
    }
}
