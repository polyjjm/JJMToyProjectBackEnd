package com.example.demo.dbnote;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class dbNoteProjectDTO {
    private Integer project_id;
    private String name;
    private LocalDateTime created_at;
}
