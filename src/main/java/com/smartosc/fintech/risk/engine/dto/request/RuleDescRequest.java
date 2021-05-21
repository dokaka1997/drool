package com.smartosc.fintech.risk.engine.dto.request;

import com.smartosc.fintech.risk.engine.common.contant.enumeration.RuleType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RuleDescRequest {
    private RuleType ruleType = RuleType.UI;
    private String dataModelGroup;
    private String ruleSetId;
    private String ruleSetName;
    private String ruleId;
    private String ruleName;
    private Long effectiveDateStart;
    private Long effectiveDateEnd;
    private Boolean status;
    private List<AttributeRequest> attributes = new ArrayList<>();
    private List<RuleRequest> listRule = new ArrayList<>();
}
