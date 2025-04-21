package com.example.demo.board;

import com.example.demo.common.CustomException;
import com.example.demo.common.ErrorCode;
import com.example.demo.common.commonServiceImpl;
import com.example.demo.common.searchDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/board")
public class boardController {


    private final boardServiceImpl boardServiceImpl;

    private final commonServiceImpl commonServiceImpl;

    public boardController(boardServiceImpl boardServiceImpl , commonServiceImpl commonServiceImpl){
        this.boardServiceImpl = boardServiceImpl;
        this.commonServiceImpl = commonServiceImpl;
    }

    private static final Logger logger = LoggerFactory.getLogger(boardController.class);
    @PostMapping("/select")
    public Map<String,Object> boardSelectList (@RequestBody searchDTO searchDto) throws Exception {
        //추후 DB설정시 필요한처리
        List<boardDTO> boardList = new ArrayList<>();
        Map returnMap = new HashMap();
        Map returnSeachMap = new HashMap();
        returnSeachMap = boardServiceImpl.boardSelectList(searchDto.getScrollIndex());
        //boardPagingDTO.getScrollIndex()
        returnMap.put("data" , returnSeachMap.get("searchData"));
        returnMap.put("totalCount" , returnSeachMap.get("totalCount"));
        returnMap.put("rowCount" , searchDto.getScrollIndex());
        returnMap.put("scrollIndex" ,searchDto.getScrollIndex()  + 6);

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


    @PostMapping("/boardSearch")
    public Map<String,Object> boardSearch (@RequestBody searchDTO searchDto)  throws Exception {
        //추후 DB설정시 필요한처리
        boardDTO boardDto = new boardDTO();
        if(searchDto.getSearchType().equals("hashTag")){
            String[] hashList;
            if(searchDto.getSearchWord().substring(searchDto.getSearchWord().length() -1).equals(",")){
                searchDto.setSearchWord(searchDto.getSearchWord().substring(searchDto.getSearchWord().length() , -1));
            }


            if(searchDto.getSearchWord().endsWith(",")){

                searchDto.setSearchWord(searchDto.getSearchWord().substring(searchDto.getSearchWord().length(), -1));
            }
            hashList = searchDto.getSearchWord().split(",");


            searchDto.setOptionCnt(hashList.length);



            for(int i = 0 ; i < hashList.length ; i++){
                if(i == 0 ){
                    searchDto.setOption(hashList[0]);
                }else if(i == 1){
                    searchDto.setOption1(hashList[1]);
                }else if(i == 2){
                    searchDto.setOption2(hashList[2]);
                }

            }





        };


        Map returnMap = new HashMap();

        Map returnSeachMap = new HashMap();

        returnSeachMap = commonServiceImpl.search(searchDto);
        returnMap.put("data" , returnSeachMap.get("searchData"));
        returnMap.put("totalCount" , returnSeachMap.get("totalCount"));
        returnMap.put("rowCount" , searchDto.getScrollIndex());
        returnMap.put("scrollIndex" , searchDto.getScrollIndex() + 6);

        return returnMap;
    }

    @PostMapping("/view")
    public void boardView (@RequestBody boardDTO boardDTO) throws Exception {

        boardServiceImpl.boardView(boardDTO.getBoard_no().toString());

    }
}
