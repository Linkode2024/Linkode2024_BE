package com.linkode.api_server.dto.studyroom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
@AllArgsConstructor
public class UploadDataRequest {

    private long studyroomId;
    private long memberId;
    private String datatype;
    private MultipartFile file;

}
