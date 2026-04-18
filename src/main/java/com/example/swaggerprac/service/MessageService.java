package com.example.swaggerprac.service;

import com.example.swaggerprac.dto.message.ReadMessageResponseDto;
import com.example.swaggerprac.dto.message.SendMessageRequestDto;
import com.example.swaggerprac.dto.message.SendMessageResponseDto;
import com.example.swaggerprac.entity.ChatMessageEntity;
import com.example.swaggerprac.entity.ChatRoomEntity;
import com.example.swaggerprac.entity.ChatRoomMemberEntity;
import com.example.swaggerprac.exception.ForbiddenException;
import com.example.swaggerprac.exception.ResourceNotFoundException;
import com.example.swaggerprac.repository.ChatMessageRepository;
import com.example.swaggerprac.repository.ChatRoomMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final ChatRoomMemberRepository memberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Transactional
    public void sendMessage(String sender, SendMessageRequestDto dto) {

        // 유저 또는 채팅방 있는지 검증
        ChatRoomMemberEntity roomMember = memberRepository.findMemberAndRoomId(sender,dto.roomId())
                                .orElseThrow(()-> new ResourceNotFoundException("유저또는 채팅방을 찾을 수 없습니다."));
        // 메시지 엔터티에 저장
        ChatMessageEntity Message= new ChatMessageEntity(roomMember.getChatRoom(),roomMember.getMember(),dto.content());
        chatMessageRepository.save(Message);
        // 마지막메시지, 마지막메시지 시간 저장
        ChatRoomEntity room =roomMember.getChatRoom();
        room.updateLastMessage(Message.getMessage(),Message.getCreatedAt());
        memberRepository.IncraseCount(roomMember.getChatRoom().getRoomId(),roomMember.getMember().getId());

        SendMessageResponseDto response = new SendMessageResponseDto(room.getRoomId(),Message.getMessageId()
                ,sender,Message.getMessage(),Message.getCreatedAt());
        simpMessagingTemplate.convertAndSend("/sub/chat/room/"+room.getRoomId(),response);
    }

    @Transactional(readOnly = true)
    public List<ReadMessageResponseDto> readMessage(String username,Long roomId,Long lastMessageId){
        boolean chk = memberRepository.existsByMember_UsernameAndChatRoom_RoomId(username,roomId);
        if(!chk){
            throw new ForbiddenException("해당 채팅방 멤버만 메시지 조회가 가능합니다.");
        }
        Pageable limit20 =  PageRequest.of(0, 20);
        if(lastMessageId==null){
           return chatMessageRepository.findMessageTop20(roomId,limit20);
        }
        return chatMessageRepository.findLastMessageTop20(roomId,lastMessageId,limit20);


    }
}
