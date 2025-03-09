package org.skyhigh.notessearchservice.validation.exception;

import org.shyhigh.grpc.notes.ResponseResultCode;

public class DataInteractionException extends GrpcResponseException {

    public DataInteractionException(ResponseResultCode responseResultCode) {
        super(responseResultCode);
    }

    public DataInteractionException(String message, ResponseResultCode responseResultCode) {
        super(message, responseResultCode);
    }

    public DataInteractionException(String message, Throwable cause, ResponseResultCode responseResultCode) {
        super(message, cause, responseResultCode);
    }

    public DataInteractionException(Throwable cause, ResponseResultCode responseResultCode) {
        super(cause, responseResultCode);
    }

    protected DataInteractionException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace,
            ResponseResultCode responseResultCode
    ) {
        super(message, cause, enableSuppression, writableStackTrace, responseResultCode);
    }
}
