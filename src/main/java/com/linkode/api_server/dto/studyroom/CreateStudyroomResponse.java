package com.linkode.api_server.dto.studyroom;

import lombok.*;

@Getter
@NoArgsConstructor
public class CreateStudyroomResponse {

    private long studyroomId;
    private String studyroomName;
    private String studyroomProfile;

    @Builder
    public CreateStudyroomResponse(long studyroomId, String studyroomName, String studyroomProfile) {
        this.studyroomId = studyroomId;
        this.studyroomName = studyroomName;
        this.studyroomProfile = studyroomProfile;
    }
}
