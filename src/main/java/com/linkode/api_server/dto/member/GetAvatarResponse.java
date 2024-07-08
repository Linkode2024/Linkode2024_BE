package com.linkode.api_server.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetAvatarResponse {

    /**
     * 캐릭터 조회
     */
    private String nickname;
    private Avatar avatar;

    @Getter
    @AllArgsConstructor
    public static class Avatar{
        private Long avatarId;
        private String avatarColor;
    }
}
