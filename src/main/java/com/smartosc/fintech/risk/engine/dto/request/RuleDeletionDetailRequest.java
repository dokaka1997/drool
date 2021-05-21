package com.smartosc.fintech.risk.engine.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RuleDeletionDetailRequest {
    String dataModelGroup;
    String ruleSetId;
    String ruleSetName;
    String ruleId;
    String ruleName;
}
