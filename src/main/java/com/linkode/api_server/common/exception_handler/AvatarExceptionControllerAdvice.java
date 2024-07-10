package com.linkode.api_server.common.exception_handler;

import com.linkode.api_server.common.exception.AvatarException;
import com.linkode.api_server.common.exception.ColorException;
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
public class AvatarExceptionControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AvatarException.class)
    public BaseErrorResponse handle_AvatarException(AvatarException e) {
        log.error("[handle_AvatarException]", e);
        return new BaseErrorResponse(e.getExceptionStatus(), e.getMessage());
    }
}
