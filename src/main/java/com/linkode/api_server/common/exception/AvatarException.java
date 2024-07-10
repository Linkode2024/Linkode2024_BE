package com.linkode.api_server.common.exception;

import com.linkode.api_server.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class AvatarException extends RuntimeException{
    private final ResponseStatus exceptionStatus;

    public AvatarException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }

    public AvatarException(ResponseStatus exceptionStatus, String message) {
        super(message);
        this.exceptionStatus = exceptionStatus;
    }
}
