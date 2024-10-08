package com.linkode.api_server.dto.studyroom;

import com.linkode.api_server.domain.Studyroom;
import com.linkode.api_server.domain.memberstudyroom.MemberRole;
import lombok.*;

@Getter
@NoArgsConstructor
public class JoinStudyroomRequest {
    private Studyroom studyroom;
    private long memberId;
    private MemberRole memberRole;

    @Builder
    public JoinStudyroomRequest(Studyroom studyroom, long memberId, MemberRole memberRole) {
        this.studyroom = studyroom;
        this.memberId = memberId;
        this.memberRole = memberRole;
    }
}
