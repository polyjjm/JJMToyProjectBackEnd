package com.example.demo.board;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface boardMapper {
    public List<boardDTO> boardSelectList(@Param("limitValue") Integer limitValue);

    public Integer boardInsert(boardDTO boardDTo);
}
