package com.example.swaggerprac.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRedisRepository {

    private final StringRedisTemplate stringRedisTemplate;

    // redis에 저장
    public void save(Long userId, String refreshToken, long ttlSeconds) {
        stringRedisTemplate.opsForValue().set("refresh:" + userId, refreshToken,
                Duration.ofSeconds(ttlSeconds));
    }

    // redis에서 리프레시토큰 삭제
    public void delete(Long userId) {
        stringRedisTemplate.delete("refresh:" + userId);
    }

    // redis에서 리프레시토큰 조회
    public Optional<String> findByUserId(Long userId) {
        String value = stringRedisTemplate.opsForValue().get("refresh:" + userId);
        return Optional.ofNullable(value);
    }
}
