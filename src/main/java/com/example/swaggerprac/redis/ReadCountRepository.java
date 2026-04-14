package com.example.swaggerprac.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReadCountRepository {

    private final StringRedisTemplate template;

    private String key(Long postId){
        return "view:post:"+postId;
    }

    public void increase(Long postId){
        template.opsForValue().increment(key(postId));
    }

    public long getCount(Long postId){
        String value = template.opsForValue().get(key(postId));
        if(value==null){
            return 0L;
        }
        return Long.parseLong(value);
    }

    public void delete(Long postId){
        template.delete(key(postId));
    }
}
