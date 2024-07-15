package com.linkode.api_server.controller;

import com.linkode.api_server.common.response.BaseResponse;
import com.linkode.api_server.common.response.status.BaseExceptionResponseStatus;
import com.linkode.api_server.dto.studyroom.*;
import com.linkode.api_server.service.MemberStudyroomService;
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
    MemberStudyroomService memberStudyroomService;
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

    /**
     * 스터디룸 수정
     */
    @PatchMapping("")
    public BaseResponse<Void> modifyStudyroom(@RequestHeader("Authorization") String authorization, @RequestBody PatchStudyroomRequest patchStudyroomRequest){
        log.info("[StudyroomController.modifyStudyroom]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        studyroomService.modifyStudyroom(memberId,patchStudyroomRequest);
        return new BaseResponse<>(null);
    }

    /**
     * 스터디룸 입장 (재입장)
     */
    @GetMapping("/detail")
    public DetailStudyroomResponse getStudyroomDetail(@RequestHeader("Authorization") String authorization, @RequestParam long studyroomId){
        log.info("[StudyroomController.getStudyroomDetail]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        return memberStudyroomService.getStudyroomDetail(studyroomId,memberId);
    }

    /**
     * 초대코드 생성
     */
    @PostMapping("/invite-code")
    public BaseResponse<PostInviteCodeResponse> createStudyroomCode(@RequestHeader("Authorization") String authorization,
                                                                    @RequestParam Long studyroomId){
        log.info("[StudyroomController.createStudyroomCode]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        return new BaseResponse<>(studyroomService.createStudyroomCode(memberId,studyroomId));
    }

}
