package com.linkode.api_server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateStudyroomResponse {

    private long studyroomId;
    private String studyroomName;

    private String studyroomProfile;

}
