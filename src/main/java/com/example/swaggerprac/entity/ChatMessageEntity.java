package com.example.swaggerprac.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatMessageEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @JoinColumn(name = "room_id",nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoomEntity chatRoom;
    @JoinColumn(name = "sender_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;
    @Column(nullable = false)
    private String message;

    public ChatMessageEntity(ChatRoomEntity chatRoomEntity, User sender,String message){
        this.chatRoom=chatRoomEntity;
        this.sender=sender;
        this.message=message;
    }
}
