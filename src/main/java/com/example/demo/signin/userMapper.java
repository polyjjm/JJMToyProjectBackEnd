package com.example.demo.signin;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface userMapper {
    public userDTO selectUser();

    public userDTO selectUserId(@Param("user_id") String user_id);

    public void insertKakao(String user_id , String user_pw ,String user_type ,String user_gender , String user_name , String user_email ,String user_role ,String user_refresh);

    public userDTO selectRefreshToken(String user_id);

    public void updateRefreshToken(String user_id , String user_refresch);

    public List<userDTO> userList();
}
