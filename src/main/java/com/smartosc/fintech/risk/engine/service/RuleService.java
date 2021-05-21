package com.smartosc.fintech.risk.engine.service;

import com.smartosc.fintech.risk.engine.dto.request.RuleDeletionRequest;
import com.smartosc.fintech.risk.engine.dto.request.RuleGenerationRequest;
import com.smartosc.fintech.risk.engine.dto.response.RuleDeletionResponse;
import com.smartosc.fintech.risk.engine.dto.response.RuleGenerationResponse;

import java.util.List;

public interface RuleService {
    List<RuleGenerationResponse> generateRule(RuleGenerationRequest request);
    List<RuleDeletionResponse> deleteRule(RuleDeletionRequest request);
}
