package com.linkode.api_server.dto.studyroom;

import com.linkode.api_server.domain.memberstudyroom.MemberRole;
import lombok.*;
import org.kohsuke.github.GHEventPayload;

import java.util.List;

@Getter
@NoArgsConstructor
public class DetailStudyroomResponse {

    private Long studyroomId;
    private String studyroomName;
    private String studyroomProfile;
    private MemberRole role;
    private List<Member> members;

    @Builder
    public DetailStudyroomResponse(Long studyroomId,String studyroomName,String studyroomProfile,MemberRole role,List<Member> members){
        this.studyroomId=studyroomId;
        this.studyroomName=studyroomName;
        this.studyroomProfile=studyroomProfile;
        this.role=role;
        this.members=members;
    }

    @Getter
    public static class Member{
        Long memberId;
        String nickname;
        Long avatarId;
        Long colorId;
        @Builder
        public Member(Long memberId, String nickname, Long avatarId, Long colorId) {
            this.memberId = memberId;
            this.nickname = nickname;
            this.avatarId = avatarId;
            this.colorId= colorId;
        }
    }

}
