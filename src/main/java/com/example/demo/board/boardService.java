package com.example.demo.board;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface boardService {

    Map<String,Object> boardSelectList (Integer currentPage) throws Exception;
    boardDTO boardInsert (boardDTO boardDto , MultipartFile[] files) throws Exception;

    boardDTO boardUdate (boardDTO boardDto , MultipartFile[] files) throws Exception;

    void boardView(String board_no) throws Exception;

    void boardDelete(Integer board_no) throws  Exception;
}
