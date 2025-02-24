package com.example.demo.board;

import com.example.demo.Menu.menuController;
import com.example.demo.common.commonServiceImpl;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/board")
public class boardController {
    @Autowired
    boardServiceImpl boardServiceImpl;


    @Autowired
    commonServiceImpl commonServiceImpl;

    private static final Logger logger = LoggerFactory.getLogger(boardController.class);
    @PostMapping("/select")
    public Map<String,Object> boardSelectList (@RequestBody boardPagingDTO boardPagingDTO) throws Exception {
        //추후 DB설정시 필요한처리
        List<boardDTO> boardList = new ArrayList<>();
        Map returnMap = new HashMap();
        boardPagingDTO returnboardPagingDTO;
        returnboardPagingDTO = boardServiceImpl.boardSelectList(boardPagingDTO.getCurrentPage());
        returnMap.put("data" , returnboardPagingDTO);

        logger.info(boardList.toString());
        return returnMap;
    }


    @PostMapping("/subMit")
    public Map<String,Object> boardSubMIt (@RequestPart(value="data", required=false) boardDTO boardDto, @RequestPart(value="files", required=false) MultipartFile[] files) throws Exception {
        //추후 DB설정시 필요한처리
        List<boardDTO> boardList = new ArrayList<>();

        //이후 DB 저장
        boardServiceImpl.boardInsert(boardDto , files);

        // 파일 업로드 호출
        Map returnMap = new HashMap();
        //boardPagingDTO returnboardPagingDTO;
        //returnboardPagingDTO = boardServiceImpl.boardSelectList(boardPagingDTO.getCurrentPage());
        //returnMap.put("data" , returnboardPagingDTO);

        logger.info(boardList.toString());
        return returnMap;
    }


    @PostMapping("/update")
    public Map<String,Object> boardUpdate (@RequestPart(value="data", required=false) boardDTO boardDto, @RequestPart(value="files", required=true) MultipartFile[] files) throws Exception {
        //추후 DB설정시 필요한처리
        List<boardDTO> boardList = new ArrayList<>();

        //이후 DB 저장
        boardServiceImpl.boardInsert(boardDto , files);

        // 파일 업로드 호출
        Map returnMap = new HashMap();
        //boardPagingDTO returnboardPagingDTO;
        //returnboardPagingDTO = boardServiceImpl.boardUpdateList(boardPagingDTO.getCurrentPage());
        //returnMap.put("data" , returnboardPagingDTO);

        logger.info(boardList.toString());
        return returnMap;
    }
}
