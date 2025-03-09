package org.skyhigh.notessearchservice.validation.exception;

import org.shyhigh.grpc.notes.ResponseResultCode;

public class ValidationException extends GrpcResponseException {
    public ValidationException(ResponseResultCode responseResultCode) {
        super(responseResultCode);
    }

    public ValidationException(String message, ResponseResultCode responseResultCode) {
        super(message, responseResultCode);
    }

    public ValidationException(String message, Throwable cause, ResponseResultCode responseResultCode) {
        super(message, cause, responseResultCode);
    }

    public ValidationException(Throwable cause, ResponseResultCode responseResultCode) {
        super(cause, responseResultCode);
    }

    protected ValidationException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace,
            ResponseResultCode responseResultCode
    ) {
        super(message, cause, enableSuppression, writableStackTrace, responseResultCode);
    }
}
