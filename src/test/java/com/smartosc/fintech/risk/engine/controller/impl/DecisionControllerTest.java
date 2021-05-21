package com.smartosc.fintech.risk.engine.controller.impl;

import com.smartosc.fintech.risk.engine.dto.request.DecisionRequest;
import com.smartosc.fintech.risk.engine.dto.response.DecisionResponse;
import com.smartosc.fintech.risk.engine.dto.response.SuccessResponse;
import com.smartosc.fintech.risk.engine.service.DecisionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DecisionControllerTest {
    @InjectMocks
    private DecisionControllerImpl decisionController;

    @Mock
    private DecisionService decisionService;

    @Test
    void testGenerateRule() {
        when(decisionService.executeRule(Mockito.any(DecisionRequest.class))).thenReturn(new DecisionResponse());
        DecisionRequest request = new DecisionRequest();
        SuccessResponse<DecisionResponse> response = decisionController.executeRule(request);
        Assertions.assertEquals("RSK-RE-200", response.getCode());
    }
}
