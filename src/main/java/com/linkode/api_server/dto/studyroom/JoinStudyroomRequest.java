package com.linkode.api_server.dto.studyroom;

import com.linkode.api_server.domain.memberstudyroom.MemberRole;
import lombok.*;

@Getter
@NoArgsConstructor
public class JoinStudyroomRequest {
    private long studyroomId;
    private long memberId;
    private MemberRole memberRole;

    @Builder
    public JoinStudyroomRequest(long studyroomId, long memberId, MemberRole memberRole) {
        this.studyroomId = studyroomId;
        this.memberId = memberId;
        this.memberRole = memberRole;
    }
}
