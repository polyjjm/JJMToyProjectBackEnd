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
        // 처리해야할것
        /*
        * 일단은 리이슈를 탔다는것이 토큰이 만료된것임
        * 해당아이디로 DB 조회후 리프레쉬 토큰가져옴
        * 가져온 리프레쉬 토큰이 유효한 토큰인지 확인 (시간 남았는지 )
        * 토큰이 유효하다면 토큰 재발급 해서 다시 보내줌
        * 리프레쉬 토큰도 유효하지않다면 DB 삭제하고 로그아웃처리 재로그인
        *
        * */
        userDTO returnUserDto = new userDTO();
        returnUserDto = userMapper.selectRefreshToken(userDto.getUser_id());

        try {
            if (jwtTokenProvider.validateToken(returnUserDto.getUser_refresh())) {
                //여기서 jwt 토큰 재발급
                String jwtToken = jwtTokenProvider.createToken(returnUserDto.getUser_email(), returnUserDto.getRole().toString());

                Map<String, Object> loginInfo = new HashMap<>();
                loginInfo.put("id", returnUserDto.getUser_id());
                loginInfo.put("token", jwtToken);
                return new ResponseEntity<>(loginInfo, HttpStatus.OK);
            } else {
                //리프레쉬도 만료면 그냥 로그아웃
                throw new RuntimeException("Invalid refresh token");
            }
        }catch (Exception e){
            Map<String, Object> loginInfo = new HashMap<>();
            loginInfo.put("code", "101");
            logger.info(e.toString());
            return new ResponseEntity<>(loginInfo, HttpStatus.OK);

        }

    }

}
