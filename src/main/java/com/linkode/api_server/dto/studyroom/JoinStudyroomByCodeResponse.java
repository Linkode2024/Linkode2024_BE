package com.linkode.api_server.dto.studyroom;

import com.linkode.api_server.domain.Studyroom;
import lombok.*;

@Getter
@NoArgsConstructor
public class JoinStudyroomByCodeResponse {

    private long studyroomId;
    private String studyroomName;
    private String studyroomProfile;

    @Builder
    public JoinStudyroomByCodeResponse(long studyroomId, String studyroomName, String studyroomProfile) {
        this.studyroomId = studyroomId;
        this.studyroomName = studyroomName;
        this.studyroomProfile = studyroomProfile;
    }

    public static JoinStudyroomByCodeResponse from(Studyroom studyroom){
        return JoinStudyroomByCodeResponse.builder()
                .studyroomId(studyroom.getStudyroomId())
                .studyroomName(studyroom.getStudyroomName())
                .studyroomProfile(studyroom.getStudyroomProfile())
                .build();
    }
}
