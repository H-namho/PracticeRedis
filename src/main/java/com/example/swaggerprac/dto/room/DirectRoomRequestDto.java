package com.example.swaggerprac.dto.room;

public record DirectRoomRequestDto(Long targetId,String roomName,boolean isPrivate) {
}
