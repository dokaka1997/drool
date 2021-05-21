package com.smartosc.fintech.risk.engine.controller;

import com.smartosc.fintech.risk.engine.dto.request.DecisionRequest;
import com.smartosc.fintech.risk.engine.dto.response.DecisionResponse;
import com.smartosc.fintech.risk.engine.dto.response.SuccessResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/${application-short-name}/${version}/decision")
public interface DecisionController {
    @PostMapping
    SuccessResponse<DecisionResponse> executeRule(@RequestBody DecisionRequest request);
}
