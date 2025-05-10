package com.example.demo.chat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface chatMessageMapper {
    void insertMessage(chatMessage message);
    List<chatMessage> selectMessagesByRoomId(@Param("roomId") Long roomId);
}
