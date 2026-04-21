package com.example.swaggerprac.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class ReadCountRepository {

    private static final String KEY = "post:view:delta";
    private static final String newKey = "post:view:delta:newKey";
    private final StringRedisTemplate template;

    public Long increase(Long postId) {
        // HASH KEY에서 field(postId)의 값을 1 증가시킨다.
        // KEY나 field가 없으면 새로 생성되고, 첫 값은 1이 된다.
        Long value = template.opsForHash().increment(KEY, String.valueOf(postId), 1);
        return value;
    }

    public long getCount(Long postId) {
        // 키 필드에 대한 델타값 가져옴
        Object value = template.opsForHash().get(KEY, String.valueOf(postId));
        if (value == null) {
            return 0L;
        }
        return Long.parseLong(value.toString());
    }

    public Map<Long, Long> getCounts(List<Long> postIds) {

        if (postIds == null || postIds.isEmpty()) {
            return Collections.emptyMap();
        }
        // fields 에는 필드값들이
        List<Object> fields = new ArrayList<>();
        for (Long l : postIds) {
            fields.add(String.valueOf(l));
        }
        // 해당 키값과 필드값들이랑 일치하는 value를 다가져옴
        // values -> value값들이 List로 들어감
        List<Object> values = template.opsForHash().multiGet(KEY, fields);
        Map<Long, Long> result = new HashMap<>();
        for (int i = 0; i < fields.size(); i++) {
            Object value = values.get(i);
            if (value == null) {
                result.put(postIds.get(i), 0L);
                continue;
            }
            result.put(postIds.get(i), Long.parseLong(value.toString()));
        }
        return result;
    }

    public Map<Object, Object> renameCount() {

        // key있나 체크
        boolean chk = template.hasKey(KEY);
        if (!chk) {
            return Collections.emptyMap();
        }
        // KEY를 newKey로 교체
        template.rename(KEY, newKey);
        // 키값이 newKey인 field,value 다가져옴
        return template.opsForHash().entries(newKey);
    }
    // KEY Hash에 들어있는 모든 field-value를 조회한다.
    public Map<Object, Object> getAllCounts() {
        return template.opsForHash().entries(KEY);
    }
    
    // HASH KEY에서 해당 field(postId) 하나를 삭제한다.
    public void delete(Long postId) {
        template.opsForHash().delete(KEY, String.valueOf(postId));
    }

    // newKey에 해당하는 Hash 전체를 삭제한다.
    public void clear() {
        template.delete(newKey);
    }
}
//public class ReadCountRepository {
//
//    private final StringRedisTemplate template;
//
//    private String key(Long postId){
//        return "view:post:"+postId;
//    }
//
//    public void increase(Long postId){
//        template.opsForValue().increment(key(postId));
//    }
//
//    public long getCount(Long postId){
//        String value = template.opsForValue().get(key(postId));
//        if(value==null){
//            return 0L;
//        }
//        return Long.parseLong(value);
//    }
//
//    public void delete(Long postId){
//        template.delete(key(postId));
//    }
//}
