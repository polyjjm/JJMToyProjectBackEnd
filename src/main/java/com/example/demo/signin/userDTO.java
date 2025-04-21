package com.example.demo.signin;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
}
