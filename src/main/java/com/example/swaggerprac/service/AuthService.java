package com.example.swaggerprac.service;

import com.example.swaggerprac.dto.SignupRequestDto;
import com.example.swaggerprac.entity.User;
import com.example.swaggerprac.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long signup(SignupRequestDto dto) {

        boolean chk = userRepository.existsByUsername(dto.username());
        if(chk){
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        if (userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        String password = passwordEncoder.encode(dto.password());
        User user = new User(dto.username(), password, dto.email(), dto.age());
        userRepository.save(user);
        return user.getId();
    }
}
