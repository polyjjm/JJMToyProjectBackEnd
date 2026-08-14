package com.example.demo.board;

import lombok.Data;

@Data
public class boardCommentDTO {
    private Integer comment_no;
    private Integer board_no;
    private String comment_userName;
    private String comment_content;
    private String comment_date;
}
