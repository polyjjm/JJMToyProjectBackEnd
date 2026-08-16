package com.example.demo.customtable;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class customTableRowDTO {
    private Integer row_id;
    private Integer table_id;
    // Raw JSON text: {"<column_id>": value, ...} - see the 2026-08-18 migration's comment for
    // why this is the one JSON-blob field in the whole feature. No MyBatis TypeHandler; the
    // frontend does JSON.stringify/parse.
    private String data;
    private Integer sort_order;
    private LocalDateTime created_at;
}
