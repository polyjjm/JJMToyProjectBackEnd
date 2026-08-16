package com.example.demo.customtable;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class customTableDTO {
    private Integer table_id;
    private String name;
    private String user_id;
    private LocalDateTime created_at;
}
