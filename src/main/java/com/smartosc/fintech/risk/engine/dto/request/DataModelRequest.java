package com.smartosc.fintech.risk.engine.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DataModelRequest {
    private String tableName;
    private List<PropertiesRequest> attributes = new ArrayList<>();

    public String modelVariable() {
        return ObjectUtils.isEmpty(tableName) ? null : "$" + tableName;
    }
}
