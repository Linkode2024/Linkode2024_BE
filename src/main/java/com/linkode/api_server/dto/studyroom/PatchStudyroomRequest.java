package com.linkode.api_server.dto.studyroom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatchStudyroomRequest {
    /**
     * 스터디룸 수정
     */
    private Long studyroomId;
    private String studyroomName;
    private String studyroomImg;
}
