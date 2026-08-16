package com.example.demo.dbnote;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class dbNoteTableDTO {
    private Integer table_id;
    private Integer project_id;
    private String name;
    // Diff marker for the ALTER TABLE generator - see dbNoteColumnDTO.created_at and
    // dbNoteController.getGeneratedView.
    private LocalDateTime last_generated_at;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
