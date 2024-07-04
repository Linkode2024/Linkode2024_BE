package com.linkode.api_server.controller;

import com.linkode.api_server.JwtProvider;
import com.linkode.api_server.common.response.status.BaseExceptionResponseStatus;
import com.linkode.api_server.service.StudyroomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/studyroom")
public class StudyroomController {

    @Autowired
    StudyroomService studyroomService;

    JwtProvider jwtProvider;

    @PatchMapping("/removal")
    public ResponseEntity<BaseExceptionResponseStatus> deleteStudyroom(@RequestHeader("Authorization") String authorization,@RequestHeader @RequestParam long studyroomId, @RequestParam  long memberId){

//        long memberId = jwtProvider.getMemberId(authorization);
        BaseExceptionResponseStatus responseStatus = studyroomService.deleteStudyroom(studyroomId,memberId);
        log.info("Success Delete Studyroom API ");
        HttpStatus httpStatus = HttpStatus.OK;
        return new ResponseEntity<>(responseStatus,httpStatus);
    }

}
