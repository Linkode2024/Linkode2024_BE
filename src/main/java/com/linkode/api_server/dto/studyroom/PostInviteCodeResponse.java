package com.linkode.api_server.dto.studyroom;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostInviteCodeResponse {

    /**
     * 초대코드 생성
     */
    private String inviteCode;
}
