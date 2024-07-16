package com.linkode.api_server.dto.studyroom;

import com.linkode.api_server.domain.memberstudyroom.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class JoinStudyroomByCodeRequest {
    private String inviteCode;
}
