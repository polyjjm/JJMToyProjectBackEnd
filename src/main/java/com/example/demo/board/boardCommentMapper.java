package com.example.demo.board;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface boardCommentMapper {
    List<boardCommentDTO> selectByBoardNo(@Param("board_no") Integer board_no);

    Integer insert(boardCommentDTO dto);

    // Author lookup used by the service to check ownership before allowing a delete.
    String selectAuthor(@Param("comment_no") Integer comment_no);

    void delete(@Param("comment_no") Integer comment_no);
}
