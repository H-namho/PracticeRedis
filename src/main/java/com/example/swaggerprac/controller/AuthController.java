package com.example.swaggerprac.controller;

import com.example.swaggerprac.dto.LoginRequestDto;
import com.example.swaggerprac.dto.LoginResponseDto;
import com.example.swaggerprac.dto.SignupRequestDto;
import com.example.swaggerprac.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public Long  signup(@RequestBody @Valid SignupRequestDto dto){
        return authService.signup(dto);
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody @Valid LoginRequestDto dto){
        return authService.login(dto);
    }
}
