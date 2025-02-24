package com.example.demo.Menu;

import lombok.Data;

@Data
public class menuDTO {
    private String menu_url;
    private String sideYn;
    private Integer parent_id;
    private Integer depth;
    private Integer sort_no;
    private String menu_name;
}
