package com.smartosc.fintech.risk.engine.controller.impl;

import com.smartosc.fintech.risk.engine.dto.request.RuleGenerationRequest;
import com.smartosc.fintech.risk.engine.dto.response.RuleGenerationResponse;
import com.smartosc.fintech.risk.engine.dto.response.SuccessResponse;
import com.smartosc.fintech.risk.engine.service.RuleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RuleControllerTest {
    @InjectMocks
    private RuleControllerImpl ruleController;

    @Mock
    private RuleService ruleService;

    @Test
    void testGenerateRule() {
        when(ruleService.generateRule(Mockito.any(RuleGenerationRequest.class))).thenReturn(new ArrayList<>());
        RuleGenerationRequest request = new RuleGenerationRequest();
        SuccessResponse<List<RuleGenerationResponse>> response = ruleController.generateRule(request);
        Assertions.assertEquals("RSK-RE-200", response.getCode());
    }
}
