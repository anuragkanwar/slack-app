package com.anuragkanwar.slackmessagebackend.exception.general;

import org.springframework.http.HttpStatus;

public class ValidationException extends ApiException {
    public ValidationException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
}
