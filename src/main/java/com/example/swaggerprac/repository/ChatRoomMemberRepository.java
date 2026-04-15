package com.example.swaggerprac.repository;

import com.example.swaggerprac.entity.ChatRoomEntity;
import com.example.swaggerprac.entity.ChatRoomMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMemberEntity,Long> {

    @Query("""
           SELECT distinct rm1.chatRoom
           FROM ChatRoomMemberEntity rm1
           join ChatRoomMemberEntity rm2 on rm1.chatRoom = rm2.chatRoom
           WHERE rm1.member.id = :userId and rm2.member.id = :targetId 
                    and rm1.chatRoom.roomType = com.example.swaggerprac.entity.enumtype.RoomType.DIRECT  
           """)
    Optional<ChatRoomEntity> findDirectRoom(Long userId, Long targetId);
    List<ChatRoomMemberEntity> findByChatRoom_RoomId(Long roomId);
}
