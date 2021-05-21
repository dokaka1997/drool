package com.smartosc.fintech.risk.engine.controller;

import com.smartosc.fintech.risk.engine.dto.request.RuleDeletionRequest;
import com.smartosc.fintech.risk.engine.dto.request.RuleGenerationRequest;
import com.smartosc.fintech.risk.engine.dto.response.RuleDeletionResponse;
import com.smartosc.fintech.risk.engine.dto.response.RuleGenerationResponse;
import com.smartosc.fintech.risk.engine.dto.response.SuccessResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/${application-short-name}/${version}/rules")
public interface RuleController {
    @PostMapping
    SuccessResponse<List<RuleGenerationResponse>> generateRule(@RequestBody RuleGenerationRequest request);

    @DeleteMapping
    SuccessResponse<List<RuleDeletionResponse>> deleteRule(@RequestBody RuleDeletionRequest request);
}
