package com.example.swaggerprac.controller;

import com.example.swaggerprac.dto.auth.EmailSendRequsetDto;
import com.example.swaggerprac.dto.auth.EmailVerifyRequestDto;
import com.example.swaggerprac.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailVerifyController {

    private final EmailService emailService;

    @PostMapping("/email/send")
    public ResponseEntity<?> sendEmailCode(@RequestBody @Valid EmailSendRequsetDto dto){
        emailService.sendCode(dto.email());
        return ResponseEntity.ok().build();
    }
    @PostMapping("/email/verify")
    public ResponseEntity<?> verifyEmailCode(@RequestBody @Valid EmailVerifyRequestDto dto){
        emailService.verifyCode(dto.email(), dto.code());
        return ResponseEntity.ok().build();
    }

}
