package com.smartosc.fintech.risk.engine.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartosc.fintech.risk.engine.dto.request.RuleDeletionRequest;
import com.smartosc.fintech.risk.engine.dto.response.RuleDeletionResponse;
import com.smartosc.fintech.risk.engine.dto.response.RuleGenerationResponse;
import com.smartosc.fintech.risk.engine.dto.response.SuccessResponse;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AllArgsConstructor
public class RuleExecutionHelper {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final MockMvc mvc;

    public SuccessResponse<List<RuleGenerationResponse>> callGenerateApi(String jsonFileName) throws Exception {
        String body = RuleFileHelper.readFile(jsonFileName);
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/rule-engine/v1/rules")
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        SuccessResponse<List<RuleGenerationResponse>> response =
                objectMapper.readValue(content, new TypeReference<SuccessResponse<List<RuleGenerationResponse>>>() {});
        Assertions.assertEquals("RSK-RE-200", response.getCode());

        return response;
    }

    public SuccessResponse<List<RuleDeletionResponse>> callDeleteApi(RuleDeletionRequest request) throws Exception {
        MvcResult result = mvc.perform(MockMvcRequestBuilders.delete("/rule-engine/v1/rules")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        SuccessResponse<List<RuleDeletionResponse>> response =
                objectMapper.readValue(content, new TypeReference<SuccessResponse<List<RuleDeletionResponse>>>() {});
        Assertions.assertEquals("RSK-RE-200", response.getCode());

        return response;
    }

    public List<Object> executeRule(RuleGenerationResponse response, String className, Map<String, Object> data) {
        DrlHelper helper = new DrlHelper().withDrl(this, response.getFileName(), response.getDrl()).build();
        Object factInstance = helper.getFactInstance("rules", className);
        Object fact = FactObjectConverter.convertObject(data, factInstance);

        RuleExecutionResult output = helper.execute(response.getAgendaGroup(), Collections.singletonList(fact));

        return output.getResults();
    }

    public List<Object> executeRule(RuleGenerationResponse response, Map<String, Map<String, Object>> data) {
        DrlHelper helper = new DrlHelper().withDrl(this, response.getFileName(), response.getDrl()).build();
        List<Object> facts = new ArrayList<>();

        data.keySet().forEach(className -> {
            Object factInstance = helper.getFactInstance("rules", className);
            Object fact = FactObjectConverter.convertObject(data.get(className), factInstance);
            facts.add(fact);
        });

        RuleExecutionResult output = helper.execute(response.getAgendaGroup(), facts);

        return output.getResults();
    }

    public List<Object> executeDeleteRule(RuleGenerationResponse response, String className, Map<String, Object> data) {
        DrlHelper helper = new DrlHelper().withDrlThenDelete(this, response.getFileName(), response.getDrl()).build();
        Object factInstance = helper.getFactInstance("rules", className);
        Object fact = FactObjectConverter.convertObject(data, factInstance);

        RuleExecutionResult output = helper.execute(response.getAgendaGroup(), Collections.singletonList(fact));

        return output.getResults();
    }
}
