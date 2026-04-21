package com.example.swaggerprac.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class EmailRedisRepository {

    private static final String CODE_PREFIX = "email:code:";
    private static final String VERIFIED_PREFIX = "email:verified:";
    private final StringRedisTemplate redisTemplate;

    public void saveCode(String email, String code, long seconds){
        redisTemplate.opsForValue().set(CODE_PREFIX+email,
                code, Duration.ofSeconds(seconds));
    }

    public String getCode(String email){
       return redisTemplate.opsForValue().get(CODE_PREFIX+email);
    }

    public void delCode(String email){
        redisTemplate.delete(CODE_PREFIX+email);
    }

    public void markVerfied(String email, long seconds){
        redisTemplate.opsForValue().set(VERIFIED_PREFIX+email,"true"
                ,Duration.ofSeconds(seconds));
    }
    public boolean isVerified(String email){
        String value = redisTemplate.opsForValue().get(VERIFIED_PREFIX+email);
        return "true".equals(value);
    }
    public void delVerified(String email){
        redisTemplate.delete(VERIFIED_PREFIX+email);
    }



}
