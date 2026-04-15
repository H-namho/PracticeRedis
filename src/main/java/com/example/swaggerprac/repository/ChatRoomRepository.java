package com.example.swaggerprac.repository;

import com.example.swaggerprac.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity,Long> {

    List<ChatRoomEntity> findByCreator_Username(String username);
}
