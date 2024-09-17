package com.linkode.api_server.controller;

import com.linkode.api_server.common.exception.MemberException;
import com.linkode.api_server.common.response.BaseResponse;
import com.linkode.api_server.domain.Member;
import com.linkode.api_server.dto.member.LoginResponse;
import com.linkode.api_server.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.NOT_FOUND_MEMBER;

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
//        try {
//            return new BaseResponse<>(loginService.githubLogin(code));
//        }catch (MemberEx e){
//            return new BaseResponse<>(NOT_FOUND_MEMBER,null);
//        }
        return new BaseResponse<>(loginService.githubLogin(code));

    }

}