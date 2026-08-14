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

    // 대/중/소 카테고리의 모든 조합과 각 조합의 게시글 수 - 3단계 탭 UI를 프론트에서 파생시키는 데 쓰인다.
    public List<boardCategoryCountDTO> boardCategoryTree();
}
