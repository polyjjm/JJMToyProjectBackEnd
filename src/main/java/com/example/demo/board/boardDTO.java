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
    private String board_hashTag;
    private String board_userName;

}
