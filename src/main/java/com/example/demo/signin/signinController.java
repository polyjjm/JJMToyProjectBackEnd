package com.example.demo.signin;

import com.example.demo.Menu.menuController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/signin")
public class signinController {
    private static final Logger logger = LoggerFactory.getLogger(menuController.class);

    @PostMapping("/kakao")
    public String kakaoResult () throws Exception{

        return null;
    }
}
