package com.linkode.api_server.dto.studyroom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberStudyroomListResponse {

    private List<MemberStudyroomListResponse.Studyroom> studyroomList;

    @Getter @Setter
    @AllArgsConstructor
    public static class Studyroom{
        Long studyroomId;
        String studyroomProfile;
    }
}
