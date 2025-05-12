package com.example.demo.signin;

import com.example.demo.DemoApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/member")
public class MemberController {
    //private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    //private final GoogleService googleService;
    private final KakaoService kakaoService;

    private final userMapper userMapper;



    private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

    public MemberController( JwtTokenProvider jwtTokenProvider, KakaoService kakaoService ,userMapper userMapper) {
        //this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
        //this.googleService = googleService;
        this.kakaoService = kakaoService;
        this.userMapper = userMapper;
    }

//    @PostMapping("/create")
//    public ResponseEntity<?> memberCreate(@RequestBody MemberCreateDto memberCreateDto){
//        Member member = memberService.create(memberCreateDto);
//        return new ResponseEntity<>(member.getId(), HttpStatus.CREATED);
//    }

//    @PostMapping("/doLogin")
//    public ResponseEntity<?> doLogin(@RequestBody MemberLoginDto memberLoginDto){
////        email, password 일치한지 검증
//        Member member = memberService.login(memberLoginDto);
//
////        일치할 경우 jwt accesstoken 생성
//        String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().toString());
//
//        Map<String, Object> loginInfo = new HashMap<>();
//        loginInfo.put("id", member.getId());
//        loginInfo.put("token", jwtToken);
//        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
//    }

//    @PostMapping("/google/doLogin")
//    public ResponseEntity<?> googleLogin(@RequestBody RedirectDto redirectDto){
////        accesstoken 발급
//        AccessTokenDto accessTokenDto = googleService.getAccessToken(redirectDto.getCode());
////        사용자정보 얻기
//        GoogleProfileDto googleProfileDto = googleService.getGoogleProfile(accessTokenDto.getAccess_token());
////        회원가입이 되어 있지 않다면 회원가입
//        Member originalMember = memberService.getMemberBySocialId(googleProfileDto.getSub());
//        if(originalMember == null){
//            originalMember = memberService.createOauth(googleProfileDto.getSub(), googleProfileDto.getEmail(), SocialType.GOOGLE);
//        }
////        회원가입돼 있는 회원이라면 토큰발급
//        String jwtToken = jwtTokenProvider.createToken(originalMember.getEmail(), originalMember.getRole().toString());
//
//        Map<String, Object> loginInfo = new HashMap<>();
//        loginInfo.put("id", originalMember.getId());
//        loginInfo.put("token", jwtToken);
//        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
//    }


    @PostMapping("/kakao/doLogin")
    public ResponseEntity<?> kakaoLogin(@RequestBody RedirectDTO redirectDto){
        AccessTokenDTO accessTokenDto = kakaoService.getAccessToken(redirectDto.getCode());
        KakaoProfileDTO kakaoProfileDto  = kakaoService.getKakaoProfile(accessTokenDto.getAccess_token());
        //Member originalMember = memberService.getMemberBySocialId(kakaoProfileDto.getId());
        userDTO originalMember = new userDTO();
        originalMember =  userMapper.selectRefreshToken(kakaoProfileDto.getId());


        if(originalMember == null){
            String  refreschToken = jwtTokenProvider.refreshToken(kakaoProfileDto.getId(), "USER");
            userMapper.insertKakao(kakaoProfileDto.getId(),"1" ,"kakao" ,kakaoProfileDto.getKakao_account().getGender() ,kakaoProfileDto.getKakao_account().getProfile().getNickname() , kakaoProfileDto.getKakao_account().getEmail(),"USER" ,refreschToken);
        }else {
            String refreschToken = jwtTokenProvider.refreshToken(originalMember.getUser_email(), originalMember.getRole().toString());
            //update
            //재로그인시 토큰 시간 만료면 다시발급
            // 그전에 리프레쉬 유효기간 확인해야함
            if (!jwtTokenProvider.validateToken(originalMember.getUser_refresh())) {
                userMapper.updateRefreshToken(originalMember.getUser_id(), refreschToken);
            }
        }
        String jwtToken = jwtTokenProvider.createToken(originalMember.getUser_email(), originalMember.getRole().toString());



        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", originalMember.getUser_id());
        loginInfo.put("token", jwtToken);
        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
    }

    @PostMapping("/userList")
    public Map<String,Object> memboerList(){
        Map returnMap = new HashMap();
        //userMapper.userList();
        returnMap.put("data" , userMapper.userList());
        return returnMap;
    }


//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(String accessToken ,String email){
//        Long expiration = jwtTokenProvider.getExpiration(accessToken);
////        redisDao.setBlackList(accessToken, "logout", expiration);
////        if (redisDao.hasKey(email)) {
////            redisDao.deleteRefreshToken(email);
////        } else {
////            throw new IllegalArgumentException("이미 로그아웃한 유저입니다.");
////        }
//        return ResponseEntity.ok("로그아웃 완료");
//
//    }

}
