package com.smartosc.fintech.risk.engine.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class RuleGenerationRequest {
    List<RuleDescRequest> rules = new ArrayList<>();
}
