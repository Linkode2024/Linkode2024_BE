package com.linkode.api_server.dto.studyroom;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateStudyroomRequest {

    private String studyroomName;

    private String studyroomProfile;

}
