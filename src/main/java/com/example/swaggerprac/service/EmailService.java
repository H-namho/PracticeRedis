package com.example.swaggerprac.service;

import com.example.swaggerprac.exception.ConflictException;
import com.example.swaggerprac.exception.UnauthorizedException;
import com.example.swaggerprac.redis.EmailRedisRepository;
import com.example.swaggerprac.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailRedisRepository emailRedisRepository;
    private final UserRepository userRepository;

    public void sendCode(String email){
        if(userRepository.existsByEmail(email)){
            throw new ConflictException("이미 사용중인 이메일입니다.");
        }
        Random random = new Random();
        int number = 10000 + random.nextInt(90000);
        String code = String.valueOf(number);
        emailRedisRepository.saveCode(email, code, 180);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("이메일 인증번호");
        message.setText("인증번호는"+code+" 입니다.");
        mailSender.send(message);
    }
    public void verifyCode(String email, String code) {
        String savedCode = emailRedisRepository.getCode(email);
        if(savedCode==null){
            throw new UnauthorizedException("인증번호가 만료되었거나 존재하지 않습니다.");
        }
        if(!savedCode.equals(code)){
            throw new UnauthorizedException("인증번호가 일치하지 않습니다.");
        }
        emailRedisRepository.markVerfied(email,600);
        emailRedisRepository.delCode(email);
    }

    public boolean validateVerifiedEmail(String email){
        return emailRedisRepository.isVerified(email);
    }
    public void consumeVerifiedEmail(String email){
        emailRedisRepository.delVerified(email);
    }

    @Async("mailExecutor")
    public void welcomeMail(String email,String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("회원가입을 환영합니다.");
        message.setText(username+"님 회원가입을 환영합니다.");
        mailSender.send(message);
    }
}
