package com.example.demo.signin;

import com.example.demo.Menu.menuController;
import com.example.demo.common.searchDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import  com.example.demo.signin.JwtTokenProvider;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    userMapper userMapper;

    @Autowired
    JwtTokenProvider jwtTokenProvider;
    private static final Logger logger = LoggerFactory.getLogger(menuController.class);
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue (@RequestBody userDTO userDto){
        // 일단은 리이슈를 탔다는것이 토큰이 만료된것임
        // 해당아이디로 DB 조회후 리프레쉬 토큰가져옴, 유효하면 재발급, 아니면(유저 없음/만료) 401로 재로그인 유도
        userDTO returnUserDto = userMapper.selectRefreshToken(userDto.getUser_id());

        if (returnUserDto == null || !jwtTokenProvider.validateToken(returnUserDto.getUser_refresh())) {
            logger.info("reissue failed for user_id={} (not found or refresh token invalid)", userDto.getUser_id());
            Map<String, Object> loginInfo = new HashMap<>();
            loginInfo.put("code", "101");
            return new ResponseEntity<>(loginInfo, HttpStatus.UNAUTHORIZED);
        }

        String jwtToken = jwtTokenProvider.createToken(returnUserDto.getUser_email(), returnUserDto.getRole().toString());
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", returnUserDto.getUser_id());
        loginInfo.put("token", jwtToken);
        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
    }

}
