package com.example.swaggerprac.service;

import com.example.swaggerprac.dto.room.DirectRoomRequestDto;
import com.example.swaggerprac.dto.room.GroupRoomRequestDto;
import com.example.swaggerprac.entity.ChatRoomEntity;
import com.example.swaggerprac.entity.ChatRoomMemberEntity;
import com.example.swaggerprac.entity.User;
import com.example.swaggerprac.entity.enumtype.ChatMemberRoleType;
import com.example.swaggerprac.entity.enumtype.RoomType;
import com.example.swaggerprac.exception.ForbiddenException;
import com.example.swaggerprac.exception.ResourceNotFoundException;
import com.example.swaggerprac.repository.ChatRoomMemberRepository;
import com.example.swaggerprac.repository.ChatRoomRepository;
import com.example.swaggerprac.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    // 추후 반복문없이 쿼리문으로 한방에 DIRECT 같은방 찾기
    @Transactional
    public Long directCreate(String username, DirectRoomRequestDto dto) {

        log.info("들어옴");
        User user=userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("회원정보를 찾을 수 없습니다."));
        User target = userRepository.findById(dto.targetId())
                .orElseThrow(()-> new ResourceNotFoundException("상대방을 찾을 수 없습니다"));
        if(user.getId().equals(dto.targetId())){
            throw new IllegalArgumentException("자기자신과 대화할 수 없습니다.");
        }

        Optional<ChatRoomEntity> originalRoom = chatRoomMemberRepository.findDirectRoom(user.getId(),target.getId());
        if(originalRoom.isPresent()){
            return originalRoom.get().getRoomId();
        }

        ChatRoomEntity newRoom = new ChatRoomEntity(user, dto.roomName(), RoomType.DIRECT, dto.isPrivate());
        ChatRoomEntity room =chatRoomRepository.save(newRoom);
        ChatRoomMemberEntity roomMeber =new ChatRoomMemberEntity(room,user, ChatMemberRoleType.ADMIN);
        chatRoomMemberRepository.save(roomMeber);
        ChatRoomMemberEntity targetMember = new ChatRoomMemberEntity(room,target,ChatMemberRoleType.MEMBER);
        chatRoomMemberRepository.save(targetMember);

        return room.getRoomId();
    }
    @Transactional
    public Long groupCreate(String username, GroupRoomRequestDto dto) {

        User user =userRepository.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("존재하지 않는 회원입니다."));
        List<User> targetUsers =userRepository.findAllById(dto.targetIds());
        if(targetUsers.isEmpty() || targetUsers.size()!=dto.targetIds().size()){
            throw new ResourceNotFoundException("상대방을 찾을 수 없습니다.");
        }
        HashSet<Long> targetIds = new HashSet<>();
        for(User targetUser : targetUsers){
            targetIds.add(targetUser.getId());
        }
        if(targetIds.contains(user.getId())){
            throw new IllegalArgumentException("자기자신과 대화할 수 없습니다.");
        }
        ChatRoomEntity groupRoom = new ChatRoomEntity(user, dto.roomName(), RoomType.GROUP, dto.isPrivate());
        chatRoomRepository.save(groupRoom);
        ChatRoomMemberEntity roomMember = new ChatRoomMemberEntity(groupRoom,user, ChatMemberRoleType.ADMIN);
        chatRoomMemberRepository.save(roomMember);
        for(User targetUser : targetUsers){
            ChatRoomMemberEntity roomTargets = new ChatRoomMemberEntity(groupRoom,targetUser,ChatMemberRoleType.MEMBER);
            chatRoomMemberRepository.save(roomTargets);
        }
        return groupRoom.getRoomId();
    }
    @Transactional
    public void deleteRoom(Long roomId, String username) {
        ChatRoomEntity room =chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new ResourceNotFoundException("해당채팅방을 찾을 수 없습니다."));
        if(!room.getCreator().getUsername().equals(username)){
            throw new ForbiddenException("관리자만 채팅방을 삭제할 수 있습니다.");
        }
        List<ChatRoomMemberEntity> roomMember = chatRoomMemberRepository.findByChatRoom_RoomId(roomId);
        chatRoomMemberRepository.deleteAll(roomMember);
        chatRoomRepository.delete(room);
    }
}
