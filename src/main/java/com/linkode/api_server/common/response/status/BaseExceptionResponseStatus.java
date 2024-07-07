package com.linkode.api_server.common.response.status;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum BaseExceptionResponseStatus implements ResponseStatus{

    /**
     * 1000: 요청 성공 (OK)
     */
    SUCCESS(1000,HttpStatus.OK.value(), "요청에 성공하였습니다."),
    FAILURE(2000, HttpStatus.BAD_REQUEST.value(), "요청에 실패하였습니다."),
    /**
     * Token 관련 code : 3000 대
     */
    INVALID_TOKEN(3000, HttpStatus.OK.value(), "유효하지 않은 토큰입니다."),
    /**
     * 멤버 관련 code : 4000 대
     */
    ALREADY_EXIST_MEMBER(4000, HttpStatus.OK.value(), "이미 존재하는 회원입니다."),
    NOT_FOUND_MEMBERROLE(4001, HttpStatus.OK.value(), "해당 회원의 역할을 찾을 수 없습니다." ),
    /**
     * 스터디룸 관련 code : 5000 대
     */
    INVALID_ROLE(5000,HttpStatus.OK.value(), "해당 스터디룸에 접근할 권한이 없습니다."),
    NOT_FOUND_STUDYROOM(5001,HttpStatus.OK.value(), "스터디룸을 찾을 수 없습니다.");

    private final int code;
    private final int status;
    private final String message;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
