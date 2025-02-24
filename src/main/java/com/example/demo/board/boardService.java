package com.example.demo.board;

import org.springframework.web.multipart.MultipartFile;

public interface boardService {

    boardPagingDTO boardSelectList (Integer currentPage) throws Exception;
    boardDTO boardInsert (boardDTO boardDto , MultipartFile[] files) throws Exception;

    boardDTO boardUdate (boardDTO boardDto , MultipartFile[] files) throws Exception;
}
