package com.example.demo.board;

import com.example.demo.common.searchDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface boardMapper {
    public List<boardDTO> boardSelectList(@Param("limitValue") Integer limitValue);

    public Integer boardInsert(boardDTO boardDto);

    public List<boardDTO> boardSearch(searchDTO searchDto);

    public Integer boardSearchCount();

    public void boardView(String board_no);

    public void boardDelete(Integer board_no);

    public Integer boardUpdate(boardDTO boardDto);
}
