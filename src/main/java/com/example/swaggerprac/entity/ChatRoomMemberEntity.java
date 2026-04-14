package com.example.swaggerprac.entity;

import com.example.swaggerprac.entity.enumtype.ChatMemberRoleType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"room_id", "user_id"}))
public class ChatRoomMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatMemberId;

    @JoinColumn(name = "room_id",nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoomEntity chatRoom;

    @JoinColumn(name = "user_id",nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User member;
    private int unreadCount;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChatMemberRoleType chatMemberRoleType;

    public ChatRoomMemberEntity(ChatRoomEntity chatRoom, User member, ChatMemberRoleType chatMemberRoleType){
        this.chatRoom=chatRoom;
        this.member=member;
        this.chatMemberRoleType=chatMemberRoleType;
        this.unreadCount=0;
    }
}
