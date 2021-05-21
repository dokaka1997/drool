package com.smartosc.fintech.risk.engine.controller.impl;

import com.smartosc.fintech.risk.engine.controller.DecisionController;
import com.smartosc.fintech.risk.engine.dto.request.DecisionRequest;
import com.smartosc.fintech.risk.engine.dto.response.DecisionResponse;
import com.smartosc.fintech.risk.engine.dto.response.SuccessResponse;
import com.smartosc.fintech.risk.engine.service.DecisionService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@CrossOrigin
public class DecisionControllerImpl implements DecisionController {
    private final DecisionService decisionService;

    @Override
    public SuccessResponse<DecisionResponse> executeRule(DecisionRequest request) {
        DecisionResponse response = decisionService.executeRule(request);
        return new SuccessResponse<DecisionResponse>().data(response);
    }
}
