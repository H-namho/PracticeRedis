package com.example.swaggerprac.config.scheduler;

import com.example.swaggerprac.entity.PostEntity;
import com.example.swaggerprac.redis.ReadCountRepository;
import com.example.swaggerprac.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class Sechduer {

    private final ReadCountRepository countRepository;
    private final PostRepository postRepository;

    // 현재 구조의 문제:
    // clear를 트랜잭션 내부에서 먼저 해버리면, 이후 DB commit 실패 시 Redis 값은 이미 사라져 데이터가 유실됨
    // 대안:
    // afterCommit에서 clear하면 롤백으로 인한 유실은 줄일 수 있다.
    // 다만 DB commit 후 clear 전에 서버가 중단되면, 다음 스케줄에서 같은 데이터가 다시 반영되어 중복 반영
    @Transactional
    @Scheduled(fixedDelay = 3000)
    public void readCount(){

        Map<Object,Object> map = countRepository.renameCount();
        if(map.isEmpty()){
            return;
        }
        for(Map.Entry<Object,Object> m : map.entrySet()){
            // 필드값
            Long postId = Long.parseLong( m.getKey().toString());
            // value
            long value = Long.parseLong(m.getValue().toString());
            postRepository.increaseViewCount(postId,value);
        }
        countRepository.clear();
    }
}
