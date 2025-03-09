package org.skyhigh.notessearchservice.validation.exception;

import lombok.Getter;
import org.shyhigh.grpc.notes.ResponseResultCode;

@Getter
public class GrpcResponseException extends Exception {
    public ResponseResultCode responseResultCode;

    public GrpcResponseException(ResponseResultCode responseResultCode) {
        super();
        this.responseResultCode = responseResultCode;
    }

    public GrpcResponseException(String message, ResponseResultCode responseResultCode) {
        super(message + "\nResponse result code: " + responseResultCode);
        this.responseResultCode = responseResultCode;
    }

    public GrpcResponseException(String message, Throwable cause, ResponseResultCode responseResultCode) {
        super(message + "\nResponse result code: " + responseResultCode, cause);
        this.responseResultCode = responseResultCode;
    }

    public GrpcResponseException(Throwable cause, ResponseResultCode responseResultCode) {
        super(cause);
        this.responseResultCode = responseResultCode;
    }

    protected GrpcResponseException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace,
            ResponseResultCode responseResultCode
    ) {
        super(message + "\nResponse result code: " + responseResultCode, cause, enableSuppression, writableStackTrace);
        this.responseResultCode = responseResultCode;
    }
}
