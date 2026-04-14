package com.example.swaggerprac.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatRoomEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @JoinColumn(name = "user_id",nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User creator;
    @Column(nullable = false)
    private String roomName;
    @Column(nullable = false)
    private boolean isPrivate;

    private String lastMessage;
    private LocalDateTime lastMessageAt;

    public ChatRoomEntity(User creator, String roomName, boolean isPrivate){
        this.creator = creator;
        this.roomName = roomName;
        this.isPrivate = isPrivate;
    }
    public void updateLastMessage(String lastMessage,LocalDateTime lastMessageAt){
        this.lastMessage=lastMessage;
        this.lastMessageAt=lastMessageAt;
    }
}

