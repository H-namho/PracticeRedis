package com.example.swaggerprac.service;

import com.example.swaggerprac.dto.room.DirectRoomRequestDto;
import com.example.swaggerprac.entity.ChatRoomEntity;
import com.example.swaggerprac.entity.ChatRoomMemberEntity;
import com.example.swaggerprac.entity.User;
import com.example.swaggerprac.entity.enumtype.ChatMemberRoleType;
import com.example.swaggerprac.entity.enumtype.RoomType;
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
}
/*        List<ChatRoomMemberEntity> members = chatRoomMemberRepository.findByMember(username);
        Set<Long> membersRoomId = new HashSet<>();
        for(ChatRoomMemberEntity c : members){
            membersRoomId.add(c.getChatRoom().getRoomId());
        }
        List<ChatRoomMemberEntity> targets = chatRoomMemberRepository.findByMember(target.getUsername());
        Set<Long> targetsRoomId = new HashSet<>();
        for(ChatRoomMemberEntity t : targets){
            targetsRoomId.add(t.getChatRoom().getRoomId());
        }
        membersRoomId.retainAll(targetsRoomId);
        for(Long m : membersRoomId){
            ChatRoomEntity room =chatRoomRepository.findById(m)
                    .orElseThrow(() -> new ResourceNotFoundException("존재하지 않은 방입니다."));
            if(room.getRoomType()== RoomType.DIRECT){
                return m;
            }
        }*/