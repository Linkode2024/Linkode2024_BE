package com.linkode.api_server.controller;

import com.linkode.api_server.common.response.BaseResponse;
import com.linkode.api_server.common.response.status.BaseExceptionResponseStatus;
import com.linkode.api_server.dto.CreateStudyroomRequest;
import com.linkode.api_server.dto.CreateStudyroomResponse;
import com.linkode.api_server.service.StudyroomService;
import com.linkode.api_server.util.JwtProvider;
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

    @Autowired
    JwtProvider jwtProvider;

    @PatchMapping("/removal")
    public BaseResponse<BaseExceptionResponseStatus> deleteStudyroom(@RequestHeader("Authorization") String authorization, @RequestParam long studyroomId){

        long memberId = jwtProvider.extractIdFromHeader(authorization);
        BaseExceptionResponseStatus responseStatus = studyroomService.deleteStudyroom(studyroomId,memberId);
        log.info("Run Delete Studyroom API ");
        if (responseStatus == BaseExceptionResponseStatus.SUCCESS) {
            return new BaseResponse<>(responseStatus);
        } else {
            return new BaseResponse<>(responseStatus, responseStatus);
        }    }

    @PostMapping("/generation")
    public CreateStudyroomResponse createStudyroom(@RequestHeader("Authorization") String authorization,  @RequestBody CreateStudyroomRequest request){
        log.info("Success createStudyroom API");
        long memberId = jwtProvider.extractIdFromHeader(authorization);
        return studyroomService.createStudyroom(request, memberId);
    }
}
