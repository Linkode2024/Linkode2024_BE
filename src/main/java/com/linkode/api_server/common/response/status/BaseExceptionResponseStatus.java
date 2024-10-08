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
    NOT_FOUND_MEMBER(4002, HttpStatus.OK.value(), "회원을 찾을 수 없습니다."),
    /**
     * 스터디룸 관련 code : 5000 대
     */
    INVALID_ROLE(5000,HttpStatus.OK.value(), "해당 스터디룸에 접근할 권한이 없습니다."),
    NOT_FOUND_STUDYROOM(5001,HttpStatus.OK.value(), "스터디룸을 찾을 수 없습니다."),
    /**
     * 멤버_스터디룸 관련 code : 6000 대
     */
    NOT_FOUND_MEMBER_STUDYROOM(6000, HttpStatus.OK.value(), "조건에 맞는 멤버_스터디룸을 찾을 수 없습니다."),
    NOT_FOUND_CREW(6001, HttpStatus.OK.value(), "해당 스터디룸의 CREW 를 찾을 수 없습니다."),
    CANNOT_LEAVE_STUDYROOM(6002, HttpStatus.OK.value(), "방장은 탈퇴할 수 없습니다."),
    INVALID_INVITE_CODE(6003,HttpStatus.BAD_REQUEST.value(), "초대코드가 유효하지 않습니다."),
    JOINED_STUDYROOM(6004,HttpStatus.CONFLICT.value(), "이미 가입된 스터디룸입니다."),



    /**
     * color 관련 code : 7000대
     */
    NOT_FOUND_COLOR(7000, HttpStatus.OK.value(), "컬러값을 찾을 수 없습니다."),

    /**
     * color 관련 code : 8000대
     */
    NOT_FOUND_AVATAR(8000, HttpStatus.OK.value(), "캐릭터를 찾을 수 없습니다."),
  
    /**
     * Data 관련 code : 9000대
     * */
    FAILED_UPLOAD_FILE(9000,HttpStatus.BAD_REQUEST.value(), "파일 업로드 실패"),
    NONE_FILE(9001,HttpStatus.BAD_REQUEST.value(), "자료를 업로드하지 않았습니다"),
    NOT_FOUND_DATA(9002,HttpStatus.BAD_REQUEST.value(), "조건에 맞는 자료실을 불러올 수 없습니다."),
    INVALID_EXTENSION(9003,HttpStatus.BAD_REQUEST.value(), "파일 확장자를 확인해주세요."),
    INVALID_URL(9004,HttpStatus.BAD_REQUEST.value(), "URL 형식을 확인해주세요."),
    INVALID_TYPE(9005,HttpStatus.BAD_REQUEST.value(), "데이터 타입을 확인해주세요"),

    /**
     * Issue 관련 : 10000대
     * */
    ISSUE_PARSING_ERROR(10000,HttpStatus.BAD_REQUEST.value(), "이슈를 파싱하는데 문제가 생겼습니다."),
    NOT_FOUND_ISSUE(10001,HttpStatus.BAD_REQUEST.value(), "이슈를 찾을 수 없습니다.");

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
