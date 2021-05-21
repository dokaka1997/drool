package com.smartosc.fintech.risk.engine.dto.request;

import com.smartosc.fintech.risk.engine.common.contant.enumeration.DataType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertiesRequest {
    private String id;
    private String name;
    private DataType dataType;
}
