package com.linkode.api_server.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateAvatarResponse {
    /**
     * 캐릭터 생성(회원가입)
     */
    private String accessToken;
    private String refreshToken;
}
