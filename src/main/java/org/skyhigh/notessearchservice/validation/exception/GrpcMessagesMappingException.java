package org.skyhigh.notessearchservice.validation.exception;

import lombok.Getter;
import org.shyhigh.grpc.notes.ResponseResultCode;

@Getter
public class GrpcMessagesMappingException extends GrpcResponseException {
    public GrpcMessagesMappingException(ResponseResultCode responseResultCode) {
        super(responseResultCode);
    }

    public GrpcMessagesMappingException(String message, ResponseResultCode responseResultCode) {
        super(message, responseResultCode);
    }

    public GrpcMessagesMappingException(String message, Throwable cause, ResponseResultCode responseResultCode) {
        super(message, cause, responseResultCode);
    }

    public GrpcMessagesMappingException(Throwable cause, ResponseResultCode responseResultCode) {
        super(cause, responseResultCode);
    }

    protected GrpcMessagesMappingException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace,
            ResponseResultCode responseResultCode
    ) {
        super(message, cause, enableSuppression, writableStackTrace, responseResultCode);
    }
}
