package com.example.swaggerprac.dto.room;

import java.util.List;

public record GroupRoomRequestDto(List<Long>targetIds,String roomName) {
}
