package com.example.demo.signin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class userDTO {
    private String user_name;
    private String user_id;
    private String user_email;
    private String user_refresh;
    private Role role = Role.USER;
}
