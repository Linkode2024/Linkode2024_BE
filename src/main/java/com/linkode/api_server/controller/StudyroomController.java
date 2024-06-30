package com.linkode.api_server.controller;

import com.linkode.api_server.dto.CreateStudyroomRequest;
import com.linkode.api_server.dto.CreateStudyroomResponse;
import com.linkode.api_server.service.StudyroomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StudyroomController {

    @Autowired
    StudyroomService studyroomService;

    @PostMapping("/create")
    public CreateStudyroomResponse createStudyroom(@RequestBody CreateStudyroomRequest request){
        return studyroomService.createStudyroom(request);
    }

}
