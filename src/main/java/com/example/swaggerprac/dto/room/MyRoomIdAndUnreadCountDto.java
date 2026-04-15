package com.example.swaggerprac.dto.room;

import com.example.swaggerprac.entity.ChatRoomEntity;

public record MyRoomIdAndUnreadCountDto(ChatRoomEntity room, int unreadCount) {
}
