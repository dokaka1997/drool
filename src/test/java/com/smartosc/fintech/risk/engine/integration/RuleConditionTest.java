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
import org.junit.jupiter.params.ParameterizedTest;
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
class RuleConditionTest {
    @Autowired
    private MockMvc mvc;

    private RuleExecutionHelper ruleExecutionHelper;

    @BeforeEach
    public void setup() {
        ruleExecutionHelper = new RuleExecutionHelper(mvc);
    }

    @Test
    void testSingleConditionMatchIf() throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("single_condition.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("age", "40");

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Customer", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("10", results.get(0));
    }

    @Test
    void testSingleConditionMatchElse() throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("single_condition.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("age", "10");

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Customer", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("1", results.get(0));
    }

    @ParameterizedTest
    @ValueSource(ints = {2000, 10000})
    void testSubConditionMatch(int value) throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("sub_condition.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("name_8", value);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_8b", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("100", results.get(0));
    }

    @Test
    void testSubConditionMatchElse() throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("sub_condition.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("name_8", 20000);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_8b", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("", results.get(0));
    }

    @ParameterizedTest
    @ValueSource(ints = {2000, 10000, 6})
    void testSubMultipleConditionMatch(int value) throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("sub_multiple_condition.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("name_8", value);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_8b", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("100", results.get(0));
    }

    @Test
    void testSubMultipleConditionMatchElse() throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("sub_multiple_condition.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("name_8", 5);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_8b", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("1", results.get(0));
    }

    @Test
    void testMultipleConditionMatchRange1() throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("multiple_condition.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("name_8", 5);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_8b", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("10", results.get(0));
    }

    @Test
    void testMultipleConditionMatchRange2() throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("multiple_condition.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("name_8", 95);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_8b", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("100", results.get(0));
    }

    @Test
    void testMultipleConditionMatchElse() throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("multiple_condition.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("name_8", 1000);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_8b", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("0", results.get(0));
    }

    @Test
    void testMultipleDataModelMatch() throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("multiple_data_model.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Map<String, Object>> data = new HashMap<>();

        Map<String, Object> data1 = new HashMap<>();
        data1.put("name_8", 15);
        data.put("Model_8b", data1);

        Map<String, Object> data2 = new HashMap<>();
        data2.put("name_9c", 25);
        data.put("Model_9a", data2);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("100", results.get(0));
    }

    @Test
    void testMultipleDataModelElse() throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("multiple_data_model.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Map<String, Object>> data = new HashMap<>();

        Map<String, Object> data1 = new HashMap<>();
        data1.put("name_8", 15);
        data.put("Model_8b", data1);

        Map<String, Object> data2 = new HashMap<>();
        data2.put("name_9c", 5);
        data.put("Model_9a", data2);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("0", results.get(0));
    }

    @Test
    void testFormulaConditionMatchIf() throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("formula_condition.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("age1", 20);
        data.put("age2", 30);
        data.put("age3", 50);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Customer2", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("ACTION", results.get(0));
    }

    @Test
    void testFormulaConditionMatchElse() throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("formula_condition.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("age1", 20);
        data.put("age2", 30);
        data.put("age3", 60);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Customer2", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("", results.get(0));
    }
}
