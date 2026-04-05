package com.example.swaggerprac.service;

import com.example.swaggerprac.dto.LoginRequestDto;
import com.example.swaggerprac.dto.LoginResponseDto;
import com.example.swaggerprac.dto.LogoutRequestDto;
import com.example.swaggerprac.dto.SignupRequestDto;
import com.example.swaggerprac.entity.User;
import com.example.swaggerprac.entity.enumtype.RoleType;
import com.example.swaggerprac.redis.BlackListAccessToken;
import com.example.swaggerprac.redis.RefreshTokenRedisRepository;
import com.example.swaggerprac.repository.UserRepository;
import com.example.swaggerprac.security.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final BlackListAccessToken blackListAccessToken;
    private final UserDetailsService userDetailsService;

    @Transactional
    public Long signup(SignupRequestDto dto) {
        if (userRepository.existsByUsername(dto.username())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        if (userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(dto.password());
        User user = new User(
                dto.username(),
                encodedPassword,
                dto.email(),
                dto.age(),
                RoleType.ROLE_USER
        );

        userRepository.save(user);
        return user.getId();
    }
    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto dto) {
        User user = userRepository.findByUsername(dto.username())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getUsername());
        String refreshToken = jwtUtil.createRefreshToken(user.getId(), user.getUsername());

        refreshTokenRedisRepository.save(
                user.getId(),
                refreshToken,
                jwtUtil.getRefreshTokenExpirationSeconds()
        );

        return new LoginResponseDto("Bearer", accessToken, refreshToken);
    }

    public void logout(LogoutRequestDto dto, String authenticatedUsername) {
        String tokenUsername = jwtUtil.extractUsername(dto.accessToken());
        Long userId = jwtUtil.extractUserId(dto.accessToken());

        if (!tokenUsername.equals(authenticatedUsername)) {
            throw new IllegalArgumentException("현재 로그인한 사용자와 토큰 정보가 일치하지 않습니다.");
        }

        blackListAccessToken.blacklist(dto.accessToken());
        refreshTokenRedisRepository.delete(userId);
    }

    public LoginResponseDto refresh(String refreshToken) {
        Long userId = jwtUtil.extractUserId(refreshToken);
        String username = jwtUtil.extractUsername(refreshToken);

        String savedRefreshToken = refreshTokenRedisRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다."));

        if (!savedRefreshToken.equals(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (!jwtUtil.isValidToken(refreshToken, userDetails)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        String newAccessToken = jwtUtil.createAccessToken(userId, username);
        String newRefreshToken = jwtUtil.createRefreshToken(userId, username);

        refreshTokenRedisRepository.delete(userId); // 기존 리프레시토큰 삭제
        refreshTokenRedisRepository.save( // 새로운 리프레시 토큰 추가
                userId,
                newRefreshToken,
                jwtUtil.getRefreshTokenExpirationSeconds()
        );

        return new LoginResponseDto("Bearer", newAccessToken, newRefreshToken);
    }
}
