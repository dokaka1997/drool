package com.smartosc.fintech.risk.engine.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RuleRequest {
    private List<ConditionRequest> itemCondition = new ArrayList<>();
    private ActionRequest action;
    private List<DataModelRequest> dataModels = new ArrayList<>();

    private String ifStatement;
    private String thenStatement;
}
