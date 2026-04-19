package com.example.swaggerprac.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

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

    public String trylock(Long userId,long ttl) {
        String key = "lock:refresh:"+ userId;
        String value = UUID.randomUUID().toString();
        // 키값이 없으면 저장 하면서 true리턴 , 있으면 저장 못해서 false
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(key,value,Duration.ofMillis(ttl));
        return Boolean.TRUE.equals(success) ? value : null;
    }

    public void unlock(Long userId,String lockValue) {
        String key = "lock:refresh:"+ userId;
        String current = stringRedisTemplate.opsForValue().get(key);
        if(lockValue != null&&lockValue.equals(current) ) {
            stringRedisTemplate.delete(key);
        }

    }
}
