package com.example.swaggerprac.controller;

import com.example.swaggerprac.dto.LoginRequestDto;
import com.example.swaggerprac.dto.LoginResponseDto;
import com.example.swaggerprac.dto.MeResponseDto;
import com.example.swaggerprac.dto.RefreshTokenRequestDto;
import com.example.swaggerprac.dto.SignupRequestDto;
import com.example.swaggerprac.exception.UnauthorizedException;
import com.example.swaggerprac.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Void> logout(Authentication auth,
                                       @RequestHeader("Authorization") String authHeader) {

        if (!authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("유효하지 않은 Authorization 헤더입니다.");
        }

        String accessToken = authHeader.substring("Bearer ".length());
        authService.logout(accessToken, auth.getName());
        return ResponseEntity.noContent().build();
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
