package com.example.demo.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatRoomDto {
    private Long room_id;
    @JsonProperty("is_group")
    private boolean is_group;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private String userId;
}
