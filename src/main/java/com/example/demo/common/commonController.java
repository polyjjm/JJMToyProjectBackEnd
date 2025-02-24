package com.example.demo.common;

import com.example.demo.board.boardDTO;
import com.example.demo.board.boardPagingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/common")
public class commonController {

    @Autowired
    commonServiceImpl commonServiceImpl;
    @PostMapping("/imageUpload")
    public Map<String,Object> postImage(MultipartFile[] upload, HttpServletResponse res, HttpServletRequest req) throws Exception {

        String newFileName = null;
        List<String> urlList = new ArrayList<String>();
        urlList = commonServiceImpl.ckEditorUpload(upload);


        Map returnMap = new HashMap();
        returnMap.put("data" , urlList);

        return returnMap;


    }


}
