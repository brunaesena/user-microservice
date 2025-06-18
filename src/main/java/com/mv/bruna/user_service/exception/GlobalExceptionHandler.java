package com.mv.bruna.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomHttpException.class)
    public ResponseEntity<ErrorResponse> handleCustomHttpException(CustomHttpException ex) {
        return new ResponseEntity<>(ex.getErrorResponse(), HttpStatus.valueOf(ex.getErrorResponse().getStatusCode()));
    }
}