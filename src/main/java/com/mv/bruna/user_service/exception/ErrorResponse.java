package com.mv.bruna.user_service.exception;

public class ErrorResponse {
    private int statusCode;
    private String errorMessage;
    private String cause;

    public ErrorResponse(int statusCode, String errorMessage, String cause) {
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
        this.cause = cause;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }
}