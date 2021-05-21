package com.smartosc.fintech.risk.engine.controller.impl;

import com.smartosc.fintech.risk.engine.controller.RuleController;
import com.smartosc.fintech.risk.engine.dto.request.RuleDeletionRequest;
import com.smartosc.fintech.risk.engine.dto.request.RuleGenerationRequest;
import com.smartosc.fintech.risk.engine.dto.response.RuleDeletionResponse;
import com.smartosc.fintech.risk.engine.dto.response.RuleGenerationResponse;
import com.smartosc.fintech.risk.engine.dto.response.SuccessResponse;
import com.smartosc.fintech.risk.engine.service.RuleService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin
public class RuleControllerImpl implements RuleController {
    private final RuleService ruleService;

    @Override
    public SuccessResponse<List<RuleGenerationResponse>> generateRule(RuleGenerationRequest request) {
        List<RuleGenerationResponse> response = ruleService.generateRule(request);
        return new SuccessResponse<List<RuleGenerationResponse>>().data(response);
    }

    @Override
    public SuccessResponse<List<RuleDeletionResponse>> deleteRule(RuleDeletionRequest request) {
        List<RuleDeletionResponse> response = ruleService.deleteRule(request);
        return new SuccessResponse<List<RuleDeletionResponse>>().data(response);
    }
}
