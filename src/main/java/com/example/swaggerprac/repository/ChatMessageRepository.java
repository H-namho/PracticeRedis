package com.example.swaggerprac.repository;

import com.example.swaggerprac.dto.message.ReadMessageResponseDto;
import com.example.swaggerprac.entity.ChatMessageEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity,Long> {

    @Query("""
            SELECT new com.example.swaggerprac.dto.message.ReadMessageResponseDto(
                        me.chatRoom.roomId,me.messageId,me.sender.id,me.message,me.createdAt
                        )
            FROM ChatMessageEntity me
            WHERE me.chatRoom.roomId=:roomId ORDER BY me.messageId desc
            """)
    List<ReadMessageResponseDto> findMessageTop20(Long roomId, Pageable limit20);

    @Query("""
            SELECT new com.example.swaggerprac.dto.message.ReadMessageResponseDto(
                        me.chatRoom.roomId,me.messageId,me.sender.username,me.message,me.createdAt
                        )
            FROM ChatMessageEntity me
            WHERE me.chatRoom.roomId=:roomId And me.messageId<:lastMessageId
            ORDER BY me.messageId desc
            """)
    List<ReadMessageResponseDto> findLastMessageTop20(Long roomId, Long lastMessageId, Pageable limit20);
}
