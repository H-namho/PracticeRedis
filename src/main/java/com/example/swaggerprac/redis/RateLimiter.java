package com.example.swaggerprac.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RateLimiter {

    private final StringRedisTemplate redisTemplate;
    private static final long RATE_LIMIT_TIME_MS = 10_000;
    private static final long MAX_REQUEST = 30;

    public boolean allow(Long userId) {

        long now = System.currentTimeMillis();
        String key = "rate_limit:"+ userId;
        String requestId = UUID.randomUUID().toString();
        // 0 ~ 현재시간 - RATE_LIMIT_TIME_MS 만큼의데이터는 삭제됌
        // 0 ~ 현재시간 - 10초 데이터 삭제 -> 10초가넘어간 요청 삭제
        redisTemplate.opsForZSet().removeRangeByScore(key,0,now-RATE_LIMIT_TIME_MS);
        // 키값 : key, 밸류 requestId, score -> now
        redisTemplate.opsForZSet().add(key,requestId,now);

        // 요청 갯수를 가져옴
        Long count = redisTemplate.opsForZSet().size(key);
        // 최적화를 위해 ttl설정
        redisTemplate.expire(key,RATE_LIMIT_TIME_MS, TimeUnit.MILLISECONDS);

        return count <= MAX_REQUEST;
    }
}
