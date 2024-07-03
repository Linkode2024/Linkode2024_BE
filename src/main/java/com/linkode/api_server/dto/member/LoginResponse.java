package com.linkode.api_server.dto.member;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
}
