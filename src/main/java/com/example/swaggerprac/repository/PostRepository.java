package com.example.swaggerprac.repository;

import com.example.swaggerprac.entity.PostEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long>,PostRepositoryCustom {

    @Modifying
    @Query("""
            UPDATE PostEntity p SET p.viewCount = p.viewCount+:value
            WHERE p.postId=:postId
            """)
    void increaseViewCount(@Param(("postId")) Long postId, @Param("value") long value);
}
