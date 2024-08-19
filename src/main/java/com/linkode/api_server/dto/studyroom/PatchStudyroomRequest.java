package com.linkode.api_server.dto.studyroom;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatchStudyroomRequest {
    /**
     * 스터디룸 수정
     */
    private Long studyroomId;
    @Nullable
    private String studyroomName;
    @Nullable
    private MultipartFile studyroomImg;
}
