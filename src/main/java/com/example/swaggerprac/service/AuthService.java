package com.example.swaggerprac.service;

import com.example.swaggerprac.dto.auth.LoginRequestDto;
import com.example.swaggerprac.dto.auth.LoginResponseDto;
import com.example.swaggerprac.dto.auth.MeResponseDto;
import com.example.swaggerprac.dto.auth.SignupRequestDto;
import com.example.swaggerprac.dto.auth.UserSummaryResponseDto;
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

import java.util.List;

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
            throw new ConflictException("이미 사용중인 아이디입니다.");
        }
        if (userRepository.existsByEmail(dto.email())) {
            throw new ConflictException("이미 사용중인 이메일입니다.");
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
                .orElseThrow(() -> new UnauthorizedException("존재하지 않은 회원입니다."));

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
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
        Long userId = jwtUtil.extractUserId(refreshToken);
        String value = refreshTokenRedisRepository.trylock(userId, 3_000);

        try {
            String username = jwtUtil.extractUsername(refreshToken);
            if (value == null) {
                throw new UnauthorizedException("이미 처리중인 요청입니다.");
            }
            String savedRefreshToken = refreshTokenRedisRepository.findByUserId(userId)
                    .orElseThrow(() -> new UnauthorizedException("토큰이 존재하지 않습니다."));

            if (!savedRefreshToken.equals(refreshToken)) {
                throw new UnauthorizedException("토큰이 일치하지 않습니다.");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!jwtUtil.isValidToken(refreshToken, userDetails)) {
                throw new UnauthorizedException("유효하지 않은 요청입니다.");
            }

            String newAccessToken = jwtUtil.createAccessToken(userId, username);
            String newRefreshToken = jwtUtil.createRefreshToken(userId, username);

//            덮어쓰니까 삭제로직 필요하지않음
//            refreshTokenRedisRepository.delete(userId);
            refreshTokenRedisRepository.save(
                    userId,
                    newRefreshToken,
                    jwtUtil.getRefreshTokenExpirationSeconds()
            );
            return new LoginResponseDto("Bearer", newAccessToken, newRefreshToken);
        } catch (JwtException | IllegalArgumentException exception) {
            throw new UnauthorizedException("유효하지 않은 토큰입니다.");
        } finally {
            refreshTokenRedisRepository.unlock(userId,value);
        }
    }

    @Transactional(readOnly = true)
    public MeResponseDto getMe(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않은 회원입니다."));

        return new MeResponseDto(user.getUsername(), user.getEmail(), user.getAge());
    }
    // jpql로 수정예정
    @Transactional(readOnly = true)
    public List<UserSummaryResponseDto> getMembers(String username) {
        List<User> users = userRepository.findAll();
        List<UserSummaryResponseDto> members = new java.util.ArrayList<>();

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                continue;
            }

            members.add(new UserSummaryResponseDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail()
            ));
        }

        return members;
    }
}
