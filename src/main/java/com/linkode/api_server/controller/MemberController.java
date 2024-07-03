package com.linkode.api_server.controller;

import com.linkode.api_server.common.exception.MemberException;
import com.linkode.api_server.util.JwtProvider;
import com.linkode.api_server.common.response.BaseResponse;
import com.linkode.api_server.dto.member.CreateAvatarRequest;
import com.linkode.api_server.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.INVALID_TOKEN;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
public class MemberController {

    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    /**
     * 캐릭터 생성(회원가입)
     */
    @PostMapping("/avatar")
    public BaseResponse<Void> createAvatar(@RequestHeader("Authorization") String authorization,
                                           @RequestBody CreateAvatarRequest createAvatarRequest){
        log.info("[MemberController.createAvatar]");
        if(jwtProvider.validateToken(authorization)) {
            memberService.createAvatar(createAvatarRequest);
        }else{
            throw new MemberException(INVALID_TOKEN);
        }
        return new BaseResponse<>(null);
    }
}
