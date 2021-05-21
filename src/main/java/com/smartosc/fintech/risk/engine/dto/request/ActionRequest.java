package com.smartosc.fintech.risk.engine.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.ObjectUtils;

@Getter
@Setter
public class ActionRequest {
    private String returnType;
    private String value;

    public boolean isEmpty() {
        return ObjectUtils.isEmpty(returnType) && ObjectUtils.isEmpty(value);
    }
}
