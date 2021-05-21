package com.smartosc.fintech.risk.engine.dto.response;

import com.smartosc.fintech.risk.engine.common.contant.ErrorCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuccessResponse<T> extends BaseResponse {
    private T data;

    public SuccessResponse() {
        super(ErrorCode.SUCCESS.getCode(), "OK");
    }

    public SuccessResponse<T> data(T data) {
        this.data = data;
        return this;
    }
}
