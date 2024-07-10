package com.linkode.api_server.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.swing.*;

@Getter
@AllArgsConstructor
public class LoginResponse {
    /**
     * 깃허브 소셜로그인
     */
    private boolean memberStatus;
    private String githubId;
    private String accessToken;
    private String refreshToken;
    private Profile profile;

    @Getter
    @AllArgsConstructor
    public static class Profile{
        private String nickname;
        private Long avatarId;
        private Long colorId;
    }
}
