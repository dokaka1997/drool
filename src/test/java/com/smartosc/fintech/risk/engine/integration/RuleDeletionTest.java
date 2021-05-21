package com.smartosc.fintech.risk.engine.integration;

import com.smartosc.fintech.risk.engine.RuleEngineService;
import com.smartosc.fintech.risk.engine.component.MessageTranslator;
import com.smartosc.fintech.risk.engine.component.RuleFileComponent;
import com.smartosc.fintech.risk.engine.config.AppConfig;
import com.smartosc.fintech.risk.engine.config.DroolsConfig;
import com.smartosc.fintech.risk.engine.config.LogBookConfig;
import com.smartosc.fintech.risk.engine.config.ResourceConfig;
import com.smartosc.fintech.risk.engine.controller.handler.GlobalExceptionHandler;
import com.smartosc.fintech.risk.engine.dto.request.RuleDeletionDetailRequest;
import com.smartosc.fintech.risk.engine.dto.request.RuleDeletionRequest;
import com.smartosc.fintech.risk.engine.dto.response.RuleDeletionResponse;
import com.smartosc.fintech.risk.engine.dto.response.RuleGenerationResponse;
import com.smartosc.fintech.risk.engine.dto.response.SuccessResponse;
import com.smartosc.fintech.risk.engine.helper.RuleExecutionHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = RuleEngineService.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = {AppConfig.class, DroolsConfig.class, LogBookConfig.class, ResourceConfig.class,
        MessageTranslator.class, RuleFileComponent.class, GlobalExceptionHandler.class})
class RuleDeletionTest {
    @Autowired
    private MockMvc mvc;

    private RuleExecutionHelper ruleExecutionHelper;

    @BeforeEach
    public void setup() {
        ruleExecutionHelper = new RuleExecutionHelper(mvc);
    }

    @Test
    void testDeletion() throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("field", 10);

        SuccessResponse<List<RuleGenerationResponse>> generation = ruleExecutionHelper.callGenerateApi("rule-deletion.json");
        Assertions.assertEquals(1, generation.getData().size());

        List<Object> results = ruleExecutionHelper.executeRule(generation.getData().get(0), "Deletion_Model", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("ACTION", results.get(0));

        RuleDeletionDetailRequest detail = new RuleDeletionDetailRequest();
        detail.setDataModelGroup("1");
        detail.setRuleSetId("1");
        detail.setRuleId("23");
        detail.setRuleName("deletion-rule");
        RuleDeletionRequest request = new RuleDeletionRequest();
        request.setRules(Collections.singletonList(detail));

        SuccessResponse<List<RuleDeletionResponse>> deletion = ruleExecutionHelper.callDeleteApi(request);
        Assertions.assertEquals(1, deletion.getData().size());

        Assertions.assertThrows(Exception.class,
                () -> ruleExecutionHelper.executeDeleteRule(generation.getData().get(0), "Deletion_Model", data));
    }
}
