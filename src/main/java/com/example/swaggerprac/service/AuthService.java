package com.example.swaggerprac.service;

import com.example.swaggerprac.dto.LoginRequestDto;
import com.example.swaggerprac.dto.LoginResponseDto;
import com.example.swaggerprac.dto.LogoutRequestDto;
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
            throw new ConflictException("이미 존재하는 회원입니다.");
        }
        if(userRepository.existsByEmail(dto.email())) {
            throw new ConflictException("이미 존재하는 이메일입니다.");
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
                .orElseThrow(() -> new UnauthorizedException("아이디 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new UnauthorizedException("아이디 또는 비밀번호가 일치하지 않습니다.");
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
    // 로그아웃
    public void logout(LogoutRequestDto dto, String username) {
        String tokenUsername = jwtUtil.extractUsername(dto.accessToken());
        Long userId = jwtUtil.extractUserId(dto.accessToken());
        // 파싱한 이름이랑 시큐리티에 있던 이름일치 검증
        if (!tokenUsername.equals(username)) {
            throw new UnauthorizedException("유효하지 않은 요청입니다.");
        }
        // 엑세스토큰 블랙리스트 등록 -> 필터단에서 메모리 검사 -> 메모리에 존재하면 필터통과X
        blackListAccessToken.blacklist(dto.accessToken());
        // 리프레시토큰 삭제
        refreshTokenRedisRepository.delete(userId);
    }
    // 리프레시 토큰 로테이트
    public LoginResponseDto refresh(String refreshToken) {
        Long userId = jwtUtil.extractUserId(refreshToken);
        String username = jwtUtil.extractUsername(refreshToken);

        // redis 메모리에 userId 키값으로 있는 리프레시토큰 불러오기
        String savedRefreshToken = refreshTokenRedisRepository.findByUserId(userId)
                .orElseThrow(() -> new UnauthorizedException("유효하지 않은 요청입니다."));
        // 요청으로 받은 리프레시토큰이랑 메모리에있던 리프레시 토큰 비교
        if (!savedRefreshToken.equals(refreshToken)) {
            throw new UnauthorizedException("유효하지 않은 요청입니다.");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        // 토큰 검증
        if (!jwtUtil.isValidToken(refreshToken, userDetails)) {
            throw new UnauthorizedException("유효하지 않은 요청입니다.");
        }

        String newAccessToken = jwtUtil.createAccessToken(userId, username);
        String newRefreshToken = jwtUtil.createRefreshToken(userId, username);

        // redis메모리에 있는 기존 리프레시토큰 제거
        refreshTokenRedisRepository.delete(userId);
        // 새로운 리프레시토큰 저장
        refreshTokenRedisRepository.save(
                userId,
                newRefreshToken,
                jwtUtil.getRefreshTokenExpirationSeconds()
        );
        return new LoginResponseDto("Bearer", newAccessToken, newRefreshToken);
    }

    // 내정보 조회
    @Transactional(readOnly = true)
    public MeResponseDto getMe(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다/"));

        return new MeResponseDto(user.getUsername(), user.getEmail(), user.getAge());
    }
}
