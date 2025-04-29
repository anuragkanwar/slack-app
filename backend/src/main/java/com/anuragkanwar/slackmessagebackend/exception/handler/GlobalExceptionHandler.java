package com.anuragkanwar.slackmessagebackend.exception.handler;

import com.anuragkanwar.slackmessagebackend.exception.general.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApiException(
            ApiException exception,
            WebRequest webRequest
    ) {
        return ResponseEntity.status(exception.getHttpStatus().value())
                .body(ErrorResponse.builder()
                        .detail(exception.getMessage())
                        .title(exception.getHttpStatus().getReasonPhrase())
                        .status(exception.getHttpStatus().value())
                        .path(webRequest.getDescription(false))
                        .build());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnknownException(
            Exception exception,
            WebRequest webRequest
    ) {
        return ResponseEntity.internalServerError()
                .body(ErrorResponse.builder()
                        .detail(exception.getMessage())
                        .title(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .path(webRequest.getDescription(false))
                        .build());
    }
}
