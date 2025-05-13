package com.example.demo.chat;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class chatMessage {
    private Long id;
    private Long roomId;
    private String sender_id;
    private String message;
    private String sender;
    private String content;
    private LocalDateTime timestamp;
    @JsonProperty("is_read")
    private Boolean is_read;

}
