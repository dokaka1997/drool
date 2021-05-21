package com.smartosc.fintech.risk.engine.exception;

import com.smartosc.fintech.risk.engine.common.contant.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessServiceException extends RuntimeException {
    private final ErrorCode error;

    public BusinessServiceException(String message, ErrorCode error) {
        super(message);
        this.error = error;
    }
}
