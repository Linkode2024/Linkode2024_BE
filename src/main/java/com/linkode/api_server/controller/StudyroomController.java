package com.linkode.api_server.controller;

import com.linkode.api_server.common.exception.DataException;
import com.linkode.api_server.common.response.BaseResponse;
import com.linkode.api_server.common.response.status.BaseExceptionResponseStatus;
import com.linkode.api_server.domain.data.DataType;
import com.linkode.api_server.dto.studyroom.*;
import com.linkode.api_server.service.DataService;
import com.linkode.api_server.service.MemberStudyroomService;
import com.linkode.api_server.service.StudyroomService;
import com.linkode.api_server.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.NOT_FOUND_DATA;
import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.SUCCESS;

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
    DataService dataService;
    @Autowired
    JwtProvider jwtProvider;

    /**
     * 스터디룸 삭제
     */
    @PatchMapping("/removal")
    public BaseResponse<BaseExceptionResponseStatus> deleteStudyroom(@RequestHeader("Authorization") String authorization, @RequestParam long studyroomId) {

        long memberId = jwtProvider.extractIdFromHeader(authorization);
        BaseExceptionResponseStatus responseStatus = studyroomService.deleteStudyroom(studyroomId, memberId);
        log.info("Run Delete Studyroom API ");
        if (responseStatus == SUCCESS) {
            return new BaseResponse<>(responseStatus);
        } else {
            return new BaseResponse<>(responseStatus, responseStatus);
        }
    }

    /**
     * 스터디룸 탈퇴
     */
    @PatchMapping("/leave")
    public BaseResponse<MemberStudyroomListResponse> leaveStudyroom(@RequestHeader("Authorization") String authorization, @RequestParam long studyroomId) {

        long memberId = jwtProvider.extractIdFromHeader(authorization);
        BaseExceptionResponseStatus responseStatus = memberStudyroomService.leaveStudyroom(studyroomId, memberId);
        MemberStudyroomListResponse latestStudyroomList = memberStudyroomService.getMemberStudyroomList(memberId);
        log.info("Run leaveStudyroom API ");
        return new BaseResponse<>(responseStatus, latestStudyroomList);
    }

    @PostMapping("/generation")
    public CreateStudyroomResponse createStudyroom(@RequestHeader("Authorization") String authorization, @RequestBody CreateStudyroomRequest request) {
        log.info("Success createStudyroom API");
        long memberId = jwtProvider.extractIdFromHeader(authorization);
        return studyroomService.createStudyroom(request, memberId);
    }

    /**
     * 스터디룸 수정
     */
    @PatchMapping("")
    public BaseResponse<Void> modifyStudyroom(@RequestHeader("Authorization") String authorization, @RequestBody PatchStudyroomRequest patchStudyroomRequest) {
        log.info("[StudyroomController.modifyStudyroom]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        studyroomService.modifyStudyroom(memberId, patchStudyroomRequest);
        return new BaseResponse<>(null);
    }

    /**
     * 스터디룸 입장 (재입장)
     */
    @GetMapping("/detail")
    public DetailStudyroomResponse getStudyroomDetail(@RequestHeader("Authorization") String authorization, @RequestParam long studyroomId) {
        log.info("[StudyroomController.getStudyroomDetail]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        return memberStudyroomService.getStudyroomDetail(studyroomId, memberId);
    }

    /**
     * 초대코드 생성
     */
    @PostMapping("/invite-code")
    public BaseResponse<PostInviteCodeResponse> createStudyroomCode(@RequestHeader("Authorization") String authorization,
                                                                    @RequestParam Long studyroomId) {
        log.info("[StudyroomController.createStudyroomCode]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        return new BaseResponse<>(studyroomService.createStudyroomCode(memberId, studyroomId));
    }

    /**
     * 스터디룸 리스트 조회
     */
    @GetMapping("")
    public BaseResponse<MemberStudyroomListResponse> getStudyroomList(@RequestHeader("Authorization") String authorization) {
        log.info("[StudyroomController.getStudyroomList]");
        Long memberId = jwtProvider.extractIdFromHeader(authorization);
        return new BaseResponse<>(memberStudyroomService.getStudyroomList(memberId));
    }

    /**
     * 자료실 조회
     */
    @GetMapping("/data/list")
    public BaseResponse<DataListResponse> getDataList(@RequestHeader("Authorization") String authorization,
                                                      @RequestParam long studyroomId, @RequestParam DataType type) {
        log.info("[StudyroomController.getDataList]");
        try {
            DataListResponse response = dataService.getDataList(studyroomId, type);
            return new BaseResponse<>(SUCCESS, response);
        } catch (DataException e) {
            return new BaseResponse<>(NOT_FOUND_DATA, null);
        }
    }
}
