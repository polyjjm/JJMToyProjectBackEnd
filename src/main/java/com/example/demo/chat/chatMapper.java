package com.example.demo.chat;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface chatMapper {

    void insertChatRoom(@Param("isGroup") boolean isGroup);
    Long getLastInsertId();

    void insertChatRoomMember(@Param("roomId") Long roomId, @Param("userId") String userId);

    Long findExistingOneToOneRoom(@Param("user1") String user1, @Param("user2") String user2);
}
