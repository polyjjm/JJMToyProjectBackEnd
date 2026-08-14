package com.example.demo.board;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/board/comment")
public class boardCommentController {

    private final boardCommentServiceImpl boardCommentServiceImpl;

    public boardCommentController(boardCommentServiceImpl boardCommentServiceImpl) {
        this.boardCommentServiceImpl = boardCommentServiceImpl;
    }

    // GET is read through the frontend's get() helper, which returns the response body as-is
    // (no envelope needed) - see common.tsx.
    @GetMapping("/list")
    public List<boardCommentDTO> list(@RequestParam("board_no") Integer board_no) throws Exception {
        return boardCommentServiceImpl.list(board_no);
    }

    // POST/insert and POST/delete go through the frontend's post() helper, which unwraps a
    // top-level "data" key from every response (see common.tsx / boardController.boardDelete),
    // so both are wrapped in that same {data: ...} envelope for consistency with the rest of
    // the board feature's POST endpoints.
    @PostMapping("/insert")
    public Map<String, Object> insert(@RequestBody boardCommentDTO dto) throws Exception {
        Map<String, Object> result = new HashMap<>();
        result.put("data", boardCommentServiceImpl.insert(dto));
        return result;
    }

    @PostMapping("/delete")
    public Map<String, Object> delete(@RequestBody boardCommentDTO dto) throws Exception {
        boardCommentServiceImpl.delete(dto.getComment_no());
        Map<String, Object> result = new HashMap<>();
        result.put("data", true);
        return result;
    }
}
