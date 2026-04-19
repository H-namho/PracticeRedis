package com.example.swaggerprac.repository;

import com.example.swaggerprac.dto.room.GetMyRoomResponseDto;
import com.example.swaggerprac.entity.ChatRoomEntity;
import com.example.swaggerprac.entity.ChatRoomMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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

    @Query("""
    select new com.example.swaggerprac.dto.room.GetMyRoomResponseDto(
        rm.chatRoom.roomId,
        rm.chatRoom.roomName,
        rm.chatRoom.roomType,
        rm.chatRoom.lastMessage,
        rm.chatRoom.lastMessageAt,
        rm.unreadCount
    )
    from ChatRoomMemberEntity rm
    where rm.member.id = :id
    ORDER BY rm.chatRoom.lastMessageAt desc,rm.chatRoom.roomId desc
    """)
    List<GetMyRoomResponseDto> findMyRooms(Long id);

    @Query("""
            SELECT rm
            FROM ChatRoomMemberEntity rm
            WHERE rm.member.username=:username and rm.chatRoom.roomId=:roomId    
        """)
    Optional<ChatRoomMemberEntity> findMemberAndRoomId(String username, Long roomId);

    @Modifying
    @Query("""
            UPDATE ChatRoomMemberEntity rm SEt rm.unreadCount = rm.unreadCount+1
            WHERE rm.chatRoom.roomId=:roomId And rm.member.id <> :senderId
        """)
    int IncraseCount(Long roomId, Long senderId);


    boolean existsByMember_UsernameAndChatRoom_RoomId(String username, Long roomId);

    ChatRoomMemberEntity findByMember_UsernameAndChatRoom_RoomId(String username, Long roomId);

    @Modifying
    @Query("""
           UPDATE ChatRoomMemberEntity rm SET rm.unreadCount =0
           WHERE rm.member.id=:memberId
           AND rm.chatRoom.roomId=:roomId
            """)
    void UpdateUnraedCount(Long memberId, Long roomId);
}
