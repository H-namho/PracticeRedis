package com.example.swaggerprac.redis;

import com.example.swaggerprac.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class BlackListAccessToken {

    private final StringRedisTemplate template;
    private final JwtUtil jwtUtil;

    public void blacklist(String accessToken) {
        template.opsForValue().set(
                getBlacklistKey(accessToken),
                "logout",
                Duration.ofMillis(jwtUtil.getRemainingExpiration(accessToken))
        );
    }

    public boolean contains(String accessToken) {
        return template.hasKey(getBlacklistKey(accessToken));
    }

    private String getBlacklistKey(String accessToken) {
        return "blacklist:" + accessToken;
    }
}
