package com.example.swaggerprac.controller;

import com.example.swaggerprac.dto.room.DirectRoomRequestDto;
import com.example.swaggerprac.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/direct")
    public ResponseEntity<?> directCreate(Authentication auth, DirectRoomRequestDto dto){

        String username = auth.getName();
        Long roomId = chatRoomService.directCreate(username,dto);

        return ResponseEntity.ok(roomId);
    }
}
