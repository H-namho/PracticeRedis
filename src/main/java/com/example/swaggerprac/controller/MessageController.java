package com.example.swaggerprac.controller;

import com.example.swaggerprac.dto.message.SendMessageRequestDto;
import com.example.swaggerprac.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @MessageMapping("/chat.send")
    public void sendMessage(Principal principal, SendMessageRequestDto dto){
        String sender = principal.getName();
        messageService.sendMessage(sender,dto);
    }

    @GetMapping("/readMessage")
    public ResponseEntity<?> readMessage(Principal principal, @RequestParam Long roomId,
                                         @RequestParam(required = false) Long lastMessageId){
       return ResponseEntity.ok(messageService.readMessage(principal.getName(),roomId,lastMessageId));
    }
}
