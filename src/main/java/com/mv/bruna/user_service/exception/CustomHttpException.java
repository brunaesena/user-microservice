package com.mv.bruna.user_service.exception;

public class CustomHttpException extends RuntimeException {
    private final ErrorResponse errorResponse;

    public CustomHttpException(ErrorResponse errorResponse) {
        super(errorResponse.getErrorMessage());
        this.errorResponse = errorResponse;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }
}
