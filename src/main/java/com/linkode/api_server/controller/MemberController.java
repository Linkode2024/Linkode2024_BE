package com.linkode.api_server.controller;

import com.linkode.api_server.common.response.BaseResponse;
import com.linkode.api_server.common.response.status.BaseExceptionResponseStatus;
import com.linkode.api_server.dto.member.CreateAvatarRequest;
import com.linkode.api_server.service.MemberService;
import com.linkode.api_server.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class MemberController {

    @Autowired
    private final MemberService memberService;

    @Autowired
    private final JwtProvider jwtProvider;

    /**
     * 캐릭터 생성(회원가입)
     */
    @PostMapping("/avatar")
    public BaseResponse<Void> createAvatar(@RequestBody CreateAvatarRequest createAvatarRequest) {
        log.info("[MemberController.createAvatar]");
        memberService.createAvatar(createAvatarRequest);
        return new BaseResponse<>(null);
    }
}
