package com.example.demo.chat;

import lombok.Data;

import java.util.List;

@Data
public class ChatRoomRequest {
    private List<String> memberIds;
    private boolean isGroup;
}