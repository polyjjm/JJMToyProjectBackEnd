package com.example.demo.Menu;


import org.json.JSONObject;
import org.json.JSONArray;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/menu")
public class menuController {
    @Autowired
    menuServiceImpl menuServiceImpl;
    @Value("${oauth.kakao.redirect-uri}")
    private String kakaoRedirectUri;
    private static final Logger logger = LoggerFactory.getLogger(menuController.class);
    @PostMapping("/list")
    public Map<String,Object> menuService () throws Exception {

        Map<String,Object> returnList = new HashMap<>();

        logger.info("추가한값 확인중입니다123 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        logger.info(kakaoRedirectUri , ":::::::::::::::::주종민 uri 확인중");
        //추후 DB설정시 필요한처리
        List<menuDTO> menuList = new ArrayList<menuDTO>();
        menuList = menuServiceImpl.menuListService();
        logger.info(menuList.toString());
        returnList.put("data", menuList);

        return returnList;
    }
}