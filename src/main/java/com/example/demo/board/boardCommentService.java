package com.example.demo.board;

import java.util.List;

public interface boardCommentService {
    List<boardCommentDTO> list(Integer board_no) throws Exception;

    boardCommentDTO insert(boardCommentDTO dto) throws Exception;

    void delete(Integer comment_no) throws Exception;
}
