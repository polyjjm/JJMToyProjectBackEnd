package com.example.demo.customtable;

import lombok.Data;

@Data
public class customTableColumnDTO {
    private Integer column_id;
    private Integer table_id;
    private String name;
    private String type; // TEXT | NUMBER | DATE | CHECKBOX | SELECT
    // Raw JSON text (e.g. ["옵션1","옵션2"]) for SELECT columns, null otherwise - no MyBatis
    // TypeHandler, the frontend stringifies/parses this itself (see customTableDTO.java's
    // sibling row DTO, same reasoning).
    private String options;
    private Integer sort_order;
}
