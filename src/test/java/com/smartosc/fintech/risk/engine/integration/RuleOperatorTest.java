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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
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
class RuleOperatorTest {
    @Autowired
    private MockMvc mvc;

    private RuleExecutionHelper ruleExecutionHelper;

    @BeforeEach
    public void setup() {
        ruleExecutionHelper = new RuleExecutionHelper(mvc);
    }

    @ParameterizedTest
    @CsvSource({"string1, 100", "string2, 200", "string3, 300"})
    void testOperatorInMatch(String stringField, int numberField) throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("operator-in.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("field_string", stringField);
        data.put("field_number", numberField);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_Data", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("ACTION", results.get(0));
    }

    @ParameterizedTest
    @CsvSource({"string1, 400", "string, 200", ",100"})
    void testOperatorInElse(String stringField, int numberField) throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("operator-in.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("field_string", stringField);
        data.put("field_number", numberField);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_Data", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("", results.get(0));
    }

    @ParameterizedTest
    @CsvSource({"string", "string_", "_string"})
    void testOperatorMatch(String value) throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("operator-match.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("field_string", value);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_Data", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("ACTION", results.get(0));
    }

    @ParameterizedTest
    @CsvSource({"strin", "tring", "striing"})
    void testOperatorMatchElse(String value) throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("operator-match.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("field_string", value);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_Data", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("", results.get(0));
    }

    @ParameterizedTest
    @CsvSource({"strin", "tring", "striing"})
    void testOperatorNotMatch(String value) throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("operator-not-match.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("field_string", value);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_Data", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("ACTION", results.get(0));
    }

    @ParameterizedTest
    @CsvSource({"string", "string_", "_string"})
    void testOperatorNotMatchElse(String value) throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("operator-not-match.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("field_string", value);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_Data", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("", results.get(0));
    }

    @ParameterizedTest
    @ValueSource(ints = {100,200,300})
    void testOperatorBetweenMatch(int value) throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("operator-between.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("field_number", value);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_Data", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("ACTION", results.get(0));
    }

    @ParameterizedTest
    @ValueSource(ints = {99,301})
    void testOperatorBetweenElse(int value) throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("operator-between.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("field_number", value);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_Data", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("", results.get(0));
    }
}
