package com.linkode.api_server.common.exception_handler;

import com.linkode.api_server.common.exception.DataException;
import com.linkode.api_server.common.response.BaseErrorResponse;
import jakarta.annotation.Priority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.linkode.api_server.common.response.status.BaseExceptionResponseStatus.FAILURE;

@Slf4j
@Priority(0)
@RestControllerAdvice
public class DataExceptionControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({DataException.class})
    public BaseErrorResponse handle_DataException(DataException e) {
        log.error("[handle_DataException]", e);
        return new BaseErrorResponse(e.getExceptionStatus(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("[Validation Exception]", ex);

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            String customMessage = String.format("'%s' 필드를 확인하세요.", field);
            errors.put(field, customMessage);
        });

        String errorMessages = errors.entrySet().stream()
                .map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(", "));

        return new BaseErrorResponse(FAILURE, errorMessages);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public BaseErrorResponse handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.error("[TypeMismatch Exception]", ex);
        String errorMessage = String.format("잘못된 요청 파라미터입니다. '%s'는 '%s' 타입이어야 합니다.",
                ex.getName(), ex.getRequiredType().getSimpleName());

        return new BaseErrorResponse(FAILURE, errorMessage);
    }
}
