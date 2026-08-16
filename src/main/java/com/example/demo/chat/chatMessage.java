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
    // Unused going forward - a single boolean can't represent "read by 2 of 3 group members".
    // Superseded by chat_room_member.last_read_message_id (see ChatRoomController/chatMessageMapper).
    // Left in place rather than dropped, same call as board_changeThumbnail.
    @JsonProperty("is_read")
    private Boolean is_read;

    // TEXT (default) or IMAGE - see the 2026-08-16 chat migration. null/blank from the client
    // means TEXT (handled in chatMessageMapper.xml's insertMessage).
    //
    // Named snake_case (not messageType/attachmentUrl) to match sender_id/is_read above and the
    // DB column name exactly - this project has mybatis.configuration.map-underscore-to-camel-case
    // OFF, and selectMessagesByRoomId relies on SELECT * implicit column->property mapping, so a
    // camelCase field here would silently read back as null.
    private String message_type;
    // Populated only for IMAGE messages - the uploaded image's public URL (see
    // ChatRoomController.uploadImage, which reuses commonServiceImpl's existing MinIO upload +
    // thumbnail pipeline).
    private String attachment_url;

}
