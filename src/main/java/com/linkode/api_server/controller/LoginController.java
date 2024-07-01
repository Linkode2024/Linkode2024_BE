package com.linkode.api_server.controller;

import com.linkode.api_server.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    /**
     * 소셜 로그인
     */
    @GetMapping("/oauth2/redirect")
    public ResponseEntity<String> githubLogin(@RequestParam String code) {
        String accessToken = loginService.getAccessToken(code);
        String userInfo = loginService.getUserInfo(accessToken); // 깃허브에서 받은 엑세스토큰으로 사용자 정보 요청하기

        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }

}