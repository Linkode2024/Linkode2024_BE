package com.linkode.api_server.dto.studyroom;

import com.linkode.api_server.domain.data.DataType;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class UploadDataRequest {

    private long studyroomId;
    private DataType dataType;
    @Nullable
    private MultipartFile file;
    @Nullable
    private String link;
}

