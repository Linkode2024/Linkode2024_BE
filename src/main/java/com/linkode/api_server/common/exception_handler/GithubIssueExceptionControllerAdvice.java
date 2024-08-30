package com.linkode.api_server.common.exception_handler;

import com.linkode.api_server.common.exception.DataException;
import com.linkode.api_server.common.exception.GithubIssueException;
import com.linkode.api_server.common.response.BaseErrorResponse;
import jakarta.annotation.Priority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Priority(0)
@RestControllerAdvice
public class GithubIssueExceptionControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({GithubIssueException.class})
    public BaseErrorResponse handle_DataException(DataException e) {
        log.error("[handle_GithubIssueException]", e);
        return new BaseErrorResponse(e.getExceptionStatus(), e.getMessage());
    }
}
