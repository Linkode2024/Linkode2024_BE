package com.linkode.api_server.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

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
    private List<Studyroom> studyroomList;

    @Getter
    @AllArgsConstructor
    public static class Profile{
        private String nickname;
        private Long avatarId;
        private Long colorId;
    }

    @Getter
    @AllArgsConstructor
    public static class Studyroom{
        private Long studyroomId;
        private String studyroomProfile;
    }
}
