package com.linkode.api_server.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateAvatarRequest {

    /**
     * 캐릭터 생성 (회원 가입)
     */
    private String githubId;
    private String nickname;
    private Long avatarId;
    private String color;
}
