package com.example.demo.chat;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatRoomDto {
    private Long room_id;
    private boolean is_group;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private String userId;
}
