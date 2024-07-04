package com.linkode.api_server.controller;

import com.linkode.api_server.JwtProvider;
import com.linkode.api_server.dto.CreateStudyroomRequest;
import com.linkode.api_server.dto.CreateStudyroomResponse;
import com.linkode.api_server.service.StudyroomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/studyroom")
public class StudyroomController {

    @Autowired
    StudyroomService studyroomService;

    JwtProvider jwtProvider;

    @PostMapping("/generation")
    public CreateStudyroomResponse createStudyroom(@RequestHeader("Authorization") String authorization,  @RequestBody CreateStudyroomRequest request, @RequestParam long memberId){
        log.info("Success createStudyroom API");
//        long memberId = jwtProvider.getGithubId(authorization);
        return studyroomService.createStudyroom(request, memberId);
    }
}
