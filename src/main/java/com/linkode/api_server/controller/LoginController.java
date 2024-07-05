package com.linkode.api_server.controller;

import com.linkode.api_server.common.response.BaseResponse;
import com.linkode.api_server.dto.member.LoginResponse;
import com.linkode.api_server.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    /**
     * 소셜 로그인
     */
    @GetMapping("/oauth2/redirect")
    public BaseResponse<LoginResponse> githubLogin(@RequestParam String code) {
        log.info("[MemberController.githubLogin]");
        return new BaseResponse<>(loginService.githubLogin(code));
    }

    @GetMapping("/test")
    public String test(@RequestHeader("authorization") String authorization){

        return "success!";
    }

}