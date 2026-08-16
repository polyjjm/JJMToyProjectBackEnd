package com.example.demo.todo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class todoDTO {
    private Long id;
    private String text;
    private Boolean completed;
    private Boolean important;
    private String date; // YYYY-MM-DD 형식
    private String category;
    private String icon;
    // HIGH/MID/LOW - drives the dashboard todo widget's colored priority bar per item.
    private String priority;
    private String user_id;
}
