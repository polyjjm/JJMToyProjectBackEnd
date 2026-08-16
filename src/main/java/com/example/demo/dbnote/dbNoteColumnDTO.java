package com.example.demo.dbnote;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class dbNoteColumnDTO {
    private Integer column_id;
    private Integer table_id;
    private String name;
    private String sql_type;      // e.g. VARCHAR, INT, DATETIME, DECIMAL, BOOLEAN
    private String length;        // e.g. "100" or "10,2" for DECIMAL precision,scale
    private Boolean nullable;
    private Boolean is_primary_key;
    private String default_value;
    private String note;
    private Integer sort_order;
    // Compared against the owning table's last_generated_at to find "columns added since last
    // generated" for the ALTER TABLE generator - see dbNoteController.getGeneratedView.
    private LocalDateTime created_at;
}
