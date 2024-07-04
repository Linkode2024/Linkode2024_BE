package com.linkode.api_server.dto;

import com.linkode.api_server.domain.memberstudyroom.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class JoinStudyroomRequest {
    private long studyroomId;
    private long memberId;
    private MemberRole memberRole;
}
