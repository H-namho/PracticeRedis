package com.example.swaggerprac.controller;

import com.example.swaggerprac.dto.room.DirectRoomRequestDto;
import com.example.swaggerprac.dto.room.GroupRoomRequestDto;
import com.example.swaggerprac.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/direct")
    public ResponseEntity<?> directCreate(Authentication auth, @RequestBody DirectRoomRequestDto dto){

        String username = auth.getName();
        Long roomId = chatRoomService.directCreate(username,dto);

        return ResponseEntity.ok(roomId);
    }

    @PostMapping("/group")
    public ResponseEntity<?> groupCraete(Authentication auth,@RequestBody GroupRoomRequestDto dto){
        String username = auth.getName();
        Long roomId = chatRoomService.groupCreate(username,dto);
        return ResponseEntity.ok(roomId);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<?> deleteRoom(@PathVariable Long roomId,Authentication auth){
        String username= auth.getName();
        chatRoomService.deleteRoom(roomId,username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/myroom")
    public ResponseEntity getMyRoom(Authentication auth){
        String username= auth.getName();
        return ResponseEntity.ok(chatRoomService.myRooms(username));

    }
}
