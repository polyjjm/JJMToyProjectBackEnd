package com.example.demo.board;

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
        returnMap.put("scrollIndex" ,searchDto.getScrollIndex()  + 8);

        logger.info(boardList.toString());
        return returnMap;
    }


    @PostMapping("/subMit")
    public Map<String,Object> boardSubMIt (@RequestPart(value="data", required=false) boardDTO boardDto, @RequestPart(value="files", required=false) MultipartFile[] files) throws Exception {
        //추후 DB설정시 필요한처리
        List<boardDTO> boardList = new ArrayList<>();

        //이후 DB 저장
        boardServiceImpl.boardInsert(boardDto , files);

        // post() (frontend) unwraps a top-level "data" key from every response (see common.tsx).
        // This used to return an empty {} here (the line populating "data" was commented out),
        // so the insert always succeeded in the DB but the frontend read the empty response as
        // a failure and showed "게시글 등록에 실패했습니다" even though the row was saved.
        Map returnMap = new HashMap();
        returnMap.put("data", true);

        logger.info(boardList.toString());
        return returnMap;
    }


    @PostMapping("/update")
    public Map<String,Object> boardUpdate (@RequestPart(value="data", required=false) boardDTO boardDto, @RequestPart(value="files", required=false) MultipartFile[] files) throws Exception {
        //추후 DB설정시 필요한처리
        List<boardDTO> boardList = new ArrayList<>();

        //이후 DB 저장
        boardServiceImpl.boardUdate(boardDto , files);

        // 같은 이유로 (subMit 참고): data 키를 채워야 프론트가 성공으로 인식한다.
        Map returnMap = new HashMap();
        returnMap.put("data", true);

        logger.info(boardList.toString());
        return returnMap;
    }
    @PostMapping("/delete")
    public Map<String,Object> boardDelete (@RequestBody boardDTO boardDto) throws Exception {

        boardServiceImpl.boardDelete(boardDto.getBoard_no());

        // post() (frontend) unwraps a top-level "data" key from every response - see common.tsx
        Map<String,Object> returnMap = new HashMap<>();
        returnMap.put("data", true);
        return returnMap;
    }

    // Board search/filter: combines free-text search (searchWord + searchScope) with the
    // 3-tier category filter (categoryMain/Mid/Sub). Unlike the old hashtag scheme, category
    // values arrive on searchDto already shaped for the mapper's exact-match <if> blocks, so
    // there's no string-splitting to do here anymore - the controller is a thin pass-through.
    @PostMapping("/boardSearch")
    public Map<String,Object> boardSearch (@RequestBody searchDTO searchDto)  throws Exception {
        Map returnMap = new HashMap();

        Map returnSeachMap = commonServiceImpl.search(searchDto);
        returnMap.put("data" , returnSeachMap.get("searchData"));
        returnMap.put("totalCount" , returnSeachMap.get("totalCount"));
        returnMap.put("rowCount" , searchDto.getScrollIndex());
        returnMap.put("scrollIndex" , searchDto.getScrollIndex() + 8);

        return returnMap;
    }

    // Distinct (대분류, 중분류, 소분류) combinations + post counts, used by the frontend to
    // derive all 3 category pill rows without hardcoding any category values.
    @GetMapping("/categoryTree")
    public List<boardCategoryCountDTO> boardCategoryTree() throws Exception {
        return boardServiceImpl.boardCategoryTree();
    }

    @PostMapping("/view")
    public void boardView (@RequestBody boardDTO boardDTO) throws Exception {

        boardServiceImpl.boardView(boardDTO.getBoard_no().toString());

    }
}