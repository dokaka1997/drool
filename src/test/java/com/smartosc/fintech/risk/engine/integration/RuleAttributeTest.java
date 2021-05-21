package com.smartosc.fintech.risk.engine.integration;

import com.smartosc.fintech.risk.engine.RuleEngineService;
import com.smartosc.fintech.risk.engine.component.MessageTranslator;
import com.smartosc.fintech.risk.engine.component.RuleFileComponent;
import com.smartosc.fintech.risk.engine.config.AppConfig;
import com.smartosc.fintech.risk.engine.config.DroolsConfig;
import com.smartosc.fintech.risk.engine.config.LogBookConfig;
import com.smartosc.fintech.risk.engine.config.ResourceConfig;
import com.smartosc.fintech.risk.engine.controller.handler.GlobalExceptionHandler;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = RuleEngineService.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = {AppConfig.class, DroolsConfig.class, LogBookConfig.class, ResourceConfig.class,
        MessageTranslator.class, RuleFileComponent.class, GlobalExceptionHandler.class})
class RuleAttributeTest {
    @Autowired
    private MockMvc mvc;

    private RuleExecutionHelper ruleExecutionHelper;

    @BeforeEach
    public void setup() {
        ruleExecutionHelper = new RuleExecutionHelper(mvc);
    }

    @Test
    void testEffectiveDate() throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("rule-attribute-effective-date.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("field_int", 10);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_Data", data);
        Assertions.assertEquals(0, results.size());
    }

    @Test
    void testExpireDate() throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("rule-attribute-expire-date.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("field_int", 10);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_Data", data);
        Assertions.assertEquals(0, results.size());
    }

    @Test
    void testEnable() throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("rule-attribute-enable.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("field_int", 10);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_Data", data);
        Assertions.assertEquals(0, results.size());
    }
}
