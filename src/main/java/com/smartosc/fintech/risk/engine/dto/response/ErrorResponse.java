package com.smartosc.fintech.risk.engine.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse<T> extends BaseResponse {
    private T details;

    public ErrorResponse(String code, String message) {
        super(code, message);
    }

    public ErrorResponse<T> fail(T details) {
        this.details = details;
        return this;
    }
}
