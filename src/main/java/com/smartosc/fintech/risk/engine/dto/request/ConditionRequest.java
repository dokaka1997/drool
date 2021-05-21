package com.smartosc.fintech.risk.engine.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ConditionRequest {
    private String type;
    private String condition;
    private String left;
    private String right;
    private String operator;
    private List<ConditionRequest> items;
}
