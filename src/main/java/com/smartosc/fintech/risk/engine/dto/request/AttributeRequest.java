package com.smartosc.fintech.risk.engine.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.ObjectUtils;

@Getter
@Setter
public class AttributeRequest {
    private String key;
    private String value;

    public String generateValue() {
       return ObjectUtils.isEmpty(value) ? "" : "\"" + value + "\"";
    }
}
