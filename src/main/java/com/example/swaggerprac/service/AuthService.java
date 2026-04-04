package com.example.swaggerprac.service;

import com.example.swaggerprac.dto.LoginRequestDto;
import com.example.swaggerprac.dto.LoginResponseDto;
import com.example.swaggerprac.dto.SignupRequestDto;
import com.example.swaggerprac.entity.User;
import com.example.swaggerprac.entity.enumtype.RoleType;
import com.example.swaggerprac.redis.RefreshTokenRedisRepository;
import com.example.swaggerprac.repository.UserRepository;
import com.example.swaggerprac.security.jwt.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRedisRepository refreshToeknRedisRepository;

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
        User user = new User(dto.username(), password, dto.email(), dto.age(), RoleType.ROLE_USER);
        userRepository.save(user);
        return user.getId();
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto dto) {
        User user = userRepository.findByUsername(dto.username())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        // 액세스토큰 발급
        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getUsername());
        // 리프레시 토큰 발급
        String refreshToken = jwtUtil.createRefreshToken(user.getId(), user.getUsername());

        // redis메모리에 저장
        refreshToeknRedisRepository.save(
                user.getId(),
                refreshToken,
                jwtUtil.getRefreshTokenExpirationSeconds()
        );

        return new LoginResponseDto("Bearer", accessToken, refreshToken);
    }
}
