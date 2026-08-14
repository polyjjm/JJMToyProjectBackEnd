package com.example.demo.board;
import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@Data
@ToString
public class boardDTO {
    private Integer board_no;
    private String board_title;
    private String board_writer;
    private String board_content;
    private String board_changeThumbnail;
    private Integer board_hit;
    private Integer board_like;
    private Integer board_hate;
    private String board_date;
    private Integer board_datgleNo;
    private String board_imgList;
    private Integer totalCount;
    private List<MultipartFile> uploadFile;
    private Integer updateNum;
    private List<fileSrcNameDTO> boardImgList;
    private List<fileSrcNameDTO> boardImgLegacyList;
    private String boardImgListText;
    // 3-tier freeform category: 대분류(main) > 중분류(mid) > 소분류(sub).
    // All three are user-entered free text, not picked from a fixed preset list -
    // only the 3-level shape is fixed. Replaces the old comma-joined board_hashTag column.
    private String board_categoryMain;
    private String board_categoryMid;
    private String board_categorySub;
    private String board_userName;
    // Not a real column - populated by a correlated subquery in boardMapper.xml
    // (COUNT of board_comment rows for this post) so the board list/search results
    // can show a comment count without a separate round trip per post.
    private Integer commentCount;

}
