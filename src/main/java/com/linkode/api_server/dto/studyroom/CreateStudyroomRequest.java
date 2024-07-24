package com.linkode.api_server.dto.studyroom;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateStudyroomRequest {
    private String studyroomName;
    private MultipartFile studyroomProfile;
}
