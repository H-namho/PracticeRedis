package com.example.swaggerprac.dto.room;

import com.example.swaggerprac.entity.enumtype.RoomType;

import java.time.LocalDateTime;

public record GetMyRoomResponseDto(Long roomId,String roomName, RoomType roomType,
                                   String lastMessage, LocalDateTime lastMessageAt,
                                   int unreadCount) {
}
