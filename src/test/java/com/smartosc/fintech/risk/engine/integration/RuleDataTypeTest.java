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

import java.math.BigDecimal;
import java.util.*;

@SpringBootTest(classes = RuleEngineService.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = {AppConfig.class, DroolsConfig.class, LogBookConfig.class, ResourceConfig.class,
        MessageTranslator.class, RuleFileComponent.class, GlobalExceptionHandler.class})
class RuleDataTypeTest {
    @Autowired
    private MockMvc mvc;

    private RuleExecutionHelper ruleExecutionHelper;

    @BeforeEach
    public void setup() {
        ruleExecutionHelper = new RuleExecutionHelper(mvc);
    }

    @Test
    void testDataTypeIntMatch() throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("data-type-int.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("field_int", 10);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_Data", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("ACTION", results.get(0));
    }

    @Test
    void testDataTypeStringMatch() throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("data-type-string.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("field_string", "string");

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_Data", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("ACTION", results.get(0));
    }

    @Test
    void testDataTypeByteMatch() throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("data-type-byte.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("field_byte", 1);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_Data", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("ACTION", results.get(0));
    }

    @Test
    void testDataTypeLongMatch() throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("data-type-long.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("field_long", 1000000L);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_Data", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("ACTION", results.get(0));
    }

    @Test
    void testDataTypeFloatMatch() throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("data-type-float.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("field_float", 10.1f);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_Data", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("ACTION", results.get(0));
    }

    @Test
    void testDataTypeDateMatch() throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("data-type-date.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2021, Calendar.APRIL, 23, 0, 0, 0);
        data.put("field_date", calendar.getTime());

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_Data", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("ACTION", results.get(0));
    }

    @Test
    void testDataTypeBigDecimalMatch() throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("data-type-big-decimal.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("field_big_decimal", BigDecimal.valueOf(10.2));

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_Data", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("ACTION", results.get(0));
    }

    @Test
    void testDataTypeDoubleMatch() throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("data-type-double.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("field_double", 10.3d);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_Data", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("ACTION", results.get(0));
    }

    @Test
    void testDataTypeShortMatch() throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("data-type-short.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("field_short", 2);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_Data", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("ACTION", results.get(0));
    }

    @Test
    void testDataTypeBooleanMatch() throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("data-type-boolean.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("field_boolean", true);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_Data", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("ACTION", results.get(0));
    }

    @Test
    void testDataTypeNumberMatch() throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("data-type-number.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("field_number", 100);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_Data", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("ACTION", results.get(0));
    }

    @Test
    void testDataTypeAllMatch() throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("data-type-all.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("field_int", 10);
        data.put("field_string", "string");
        data.put("field_byte", 1);
        data.put("field_long", 1000000L);
        data.put("field_float", 10.1f);

        Calendar calendar = Calendar.getInstance();
        calendar.set(2021, Calendar.APRIL, 23, 0, 0, 0);
        data.put("field_date", calendar.getTime());

        data.put("field_big_decimal", BigDecimal.valueOf(10.2));
        data.put("field_double", 10.3d);
        data.put("field_short", 2);
        data.put("field_boolean", true);
        data.put("field_number", 100);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_Data", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("ACTION", results.get(0));
    }

    @Test
    void testDataTypeAllElse() throws Exception {
        SuccessResponse<List<RuleGenerationResponse>> response = ruleExecutionHelper.callGenerateApi("data-type-all.json");
        Assertions.assertEquals(1, response.getData().size());

        Map<String, Object> data = new HashMap<>();
        data.put("field_int", 10);

        List<Object> results = ruleExecutionHelper.executeRule(response.getData().get(0), "Model_Data", data);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals("", results.get(0));
    }
}
