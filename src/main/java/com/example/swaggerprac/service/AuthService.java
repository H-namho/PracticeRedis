package com.example.swaggerprac.service;

import com.example.swaggerprac.dto.LoginRequestDto;
import com.example.swaggerprac.dto.LoginResponseDto;
import com.example.swaggerprac.dto.MeResponseDto;
import com.example.swaggerprac.dto.SignupRequestDto;
import com.example.swaggerprac.entity.User;
import com.example.swaggerprac.entity.enumtype.RoleType;
import com.example.swaggerprac.exception.ConflictException;
import com.example.swaggerprac.exception.ResourceNotFoundException;
import com.example.swaggerprac.exception.UnauthorizedException;
import com.example.swaggerprac.redis.BlackListAccessToken;
import com.example.swaggerprac.redis.RefreshTokenRedisRepository;
import com.example.swaggerprac.repository.UserRepository;
import com.example.swaggerprac.security.jwt.JwtUtil;
import io.jsonwebtoken.JwtException;
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
            throw new ConflictException("?대? 議댁옱?섎뒗 ?뚯썝?낅땲??");
        }
        if (userRepository.existsByEmail(dto.email())) {
            throw new ConflictException("?대? 議댁옱?섎뒗 ?대찓?쇱엯?덈떎.");
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
                .orElseThrow(() -> new UnauthorizedException("?꾩씠???먮뒗 鍮꾨?踰덊샇媛 ?쇱튂?섏? ?딆뒿?덈떎."));

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new UnauthorizedException("?꾩씠???먮뒗 鍮꾨?踰덊샇媛 ?쇱튂?섏? ?딆뒿?덈떎.");
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

    public void logout(String accessToken, String username) {
        try {
            String tokenUsername = jwtUtil.extractUsername(accessToken);
            Long userId = jwtUtil.extractUserId(accessToken);

            if (!tokenUsername.equals(username)) {
                throw new UnauthorizedException("유효하지 않은 요청입니다.");
            }


            blackListAccessToken.blacklist(accessToken);
            refreshTokenRedisRepository.delete(userId);
        } catch (JwtException | IllegalArgumentException exception) {
            throw new UnauthorizedException("유효하지 않은 토큰입니다.");
        }
    }

    public LoginResponseDto refresh(String refreshToken) {
        try {
            Long userId = jwtUtil.extractUserId(refreshToken);
            String username = jwtUtil.extractUsername(refreshToken);

            String savedRefreshToken = refreshTokenRedisRepository.findByUserId(userId)
                    .orElseThrow(() -> new UnauthorizedException("유효하지 않은 요청입니다."));

            if (!savedRefreshToken.equals(refreshToken)) {
                throw new UnauthorizedException("유효하지 않은 요청입니다.");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!jwtUtil.isValidToken(refreshToken, userDetails)) {
                throw new UnauthorizedException("유효하지 않은 요청입니다.");
            }

            String newAccessToken = jwtUtil.createAccessToken(userId, username);
            String newRefreshToken = jwtUtil.createRefreshToken(userId, username);

            refreshTokenRedisRepository.delete(userId);
            refreshTokenRedisRepository.save(
                    userId,
                    newRefreshToken,
                    jwtUtil.getRefreshTokenExpirationSeconds()
            );
            return new LoginResponseDto("Bearer", newAccessToken, newRefreshToken);
        } catch (JwtException | IllegalArgumentException exception) {
            throw new UnauthorizedException("유효하지 않은 토큰입니다.");
        }
    }

    @Transactional(readOnly = true)
    public MeResponseDto getMe(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않은 회원입니다."));

        return new MeResponseDto(user.getUsername(), user.getEmail(), user.getAge());
    }
}
