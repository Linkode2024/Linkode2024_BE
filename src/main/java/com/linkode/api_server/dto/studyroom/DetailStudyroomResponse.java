package com.linkode.api_server.dto.studyroom;

import com.linkode.api_server.domain.memberstudyroom.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetailStudyroomResponse {

    private Long studyroomId;
    private MemberRole role;
    private List<Member> members;

    @Getter @Setter
    @AllArgsConstructor
    public static class Member{
        Long memberId;
        String nickname;
        Long avatarId;
    }

}
