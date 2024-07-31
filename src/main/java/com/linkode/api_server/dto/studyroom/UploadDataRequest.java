package com.linkode.api_server.dto.studyroom;

import com.linkode.api_server.domain.data.DataType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
@AllArgsConstructor
public class UploadDataRequest {

    private long studyroomId;
    private DataType datatype;
    private MultipartFile file;

}
