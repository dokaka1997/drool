package com.smartosc.fintech.risk.engine.service.impl;

import com.smartosc.fintech.risk.engine.common.contant.enumeration.DataType;
import com.smartosc.fintech.risk.engine.common.contant.enumeration.ReturnType;
import com.smartosc.fintech.risk.engine.component.RuleFileComponent;
import com.smartosc.fintech.risk.engine.dto.request.*;
import com.smartosc.fintech.risk.engine.dto.response.RuleGenerationResponse;
import com.smartosc.fintech.risk.engine.exception.RuleGenerationException;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.compiler.kie.builder.impl.KieFileSystemImpl;
import org.drools.compiler.kie.builder.impl.KieRepositoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.runtime.KieContainer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class RuleServiceTest {
    @InjectMocks
    private RuleServiceImpl ruleService;

    @Mock
    private KieServices kieServices;

    @Mock
    private KieContainer kieContainer;

    @Mock
    private RuleFileComponent ruleFileComponent;

    @Test
    void testGenerateRuleFileNotFound() {

        Mockito.when(kieServices.newKieBuilder(Mockito.any(KieFileSystem.class))).thenReturn(new KieBuilderImpl(new KieFileSystemImpl()));
        Mockito.when(kieServices.newKieFileSystem()).thenReturn(new KieFileSystemImpl());
        Mockito.when(ruleFileComponent.getRuleFile(Mockito.any(String.class))).thenReturn("new_folder/new_file.drl");

        List<RuleRequest> listRule = new ArrayList<>();
        RuleRequest ruleRequest = new RuleRequest();
        ActionRequest action = new ActionRequest();
        ruleRequest.setAction(action);

        listRule.add(ruleRequest);

        List<RuleDescRequest> rules = new ArrayList<>();
        RuleDescRequest ruleDescRequest = new RuleDescRequest();
        ruleDescRequest.setListRule(listRule);
        rules.add(ruleDescRequest);

        RuleGenerationRequest request = new RuleGenerationRequest();
        request.setRules(rules);

        Assertions.assertThrows(RuleGenerationException.class, () -> ruleService.generateRule(request));
    }

    @Test
    void testGenerateRuleActionEmpty() {
        Mockito.when(kieServices.newKieBuilder(Mockito.any(KieFileSystem.class))).thenReturn(new KieBuilderImpl(new KieFileSystemImpl()));
        Mockito.when(kieServices.newKieFileSystem()).thenReturn(new KieFileSystemImpl());
        Mockito.when(kieServices.getRepository()).thenReturn(new KieRepositoryImpl());
        Mockito.when(ruleFileComponent.getRuleFile(Mockito.any(String.class))).thenReturn("rules/test.drl");

        List<RuleRequest> listRule = new ArrayList<>();
        RuleRequest ruleRequest = new RuleRequest();
        ActionRequest action = new ActionRequest();
        ruleRequest.setAction(action);

        listRule.add(ruleRequest);

        List<RuleDescRequest> rules = new ArrayList<>();
        RuleDescRequest ruleDescRequest = new RuleDescRequest();
        ruleDescRequest.setListRule(listRule);
        rules.add(ruleDescRequest);

        RuleGenerationRequest request = new RuleGenerationRequest();
        request.setRules(rules);

        List<RuleGenerationResponse> responses = ruleService.generateRule(request);
        Assertions.assertEquals(1, responses.size());
    }

    @Test
    void testGenerateRuleActionSuccess() {
        Mockito.when(kieServices.newKieBuilder(Mockito.any(KieFileSystem.class))).thenReturn(new KieBuilderImpl(new KieFileSystemImpl()));
        Mockito.when(kieServices.newKieFileSystem()).thenReturn(new KieFileSystemImpl());
        Mockito.when(kieServices.getRepository()).thenReturn(new KieRepositoryImpl());
        Mockito.when(ruleFileComponent.getRuleFile(Mockito.any(String.class))).thenReturn("rules/test.drl");

        RuleGenerationRequest request = createRuleGenerationRequest();
        List<RuleGenerationResponse> responses = ruleService.generateRule(request);
        Assertions.assertEquals(1, responses.size());
    }

    private RuleGenerationRequest createRuleGenerationRequest() {
        RuleGenerationRequest request = new RuleGenerationRequest();

        RuleDescRequest ruleDescRequest = createRuleDescRequest();
        List<RuleDescRequest> rules = new ArrayList<>();
        rules.add(ruleDescRequest);
        request.setRules(rules);

        return request;
    }

    private RuleDescRequest createRuleDescRequest() {
        RuleDescRequest ruleDescRequest = new RuleDescRequest();
        ruleDescRequest.setRuleSetId("1");
        ruleDescRequest.setEffectiveDateStart(new Date().getTime());
        ruleDescRequest.setEffectiveDateEnd(new Date().getTime());
        ruleDescRequest.setStatus(true);

        AttributeRequest attribute = createAttributeRequest();
        List<AttributeRequest> attributes = new ArrayList<>();
        attributes.add(attribute);
        ruleDescRequest.setAttributes(attributes);

        RuleRequest ruleRequest = createRuleRequest();
        List<RuleRequest> listRule = new ArrayList<>();
        listRule.add(ruleRequest);
        ruleDescRequest.setListRule(listRule);

        return ruleDescRequest;
    }

    private AttributeRequest createAttributeRequest() {
        AttributeRequest attributeRequest = new AttributeRequest();
        attributeRequest.setKey("duration");
        attributeRequest.setValue("1d");

        return attributeRequest;
    }

    private RuleRequest createRuleRequest() {
        RuleRequest ruleRequest = new RuleRequest();

        ActionRequest action = createActionRequest();
        ruleRequest.setAction(action);

        DataModelRequest dataModelRequest = createDataModelRequest();
        List<DataModelRequest> dataModels = new ArrayList<>();
        dataModels.add(dataModelRequest);
        ruleRequest.setDataModels(dataModels);

        ConditionRequest conditionRequest = createConditionRequest();
        List<ConditionRequest> itemCondition = new ArrayList<>();
        itemCondition.add(conditionRequest);
        ruleRequest.setItemCondition(itemCondition);

        return ruleRequest;
    }

    private ConditionRequest createConditionRequest() {
        ConditionRequest conditionRequest = new ConditionRequest();
        conditionRequest.setLeft("$Data_Model.field_number");
        conditionRequest.setOperator("==");
        conditionRequest.setRight("10");

        return conditionRequest;
    }

    private ActionRequest createActionRequest() {
        ActionRequest action = new ActionRequest();
        action.setReturnType(ReturnType.RETURN.name());

        return action;
    }

    private DataModelRequest createDataModelRequest() {
        DataModelRequest dataModelRequest = new DataModelRequest();
        dataModelRequest.setTableName("Model_Name");

        PropertiesRequest propertiesRequest = createPropertiesRequest();
        List<PropertiesRequest> properties = new ArrayList<>();
        properties.add(propertiesRequest);
        dataModelRequest.setAttributes(properties);

        return dataModelRequest;
    }

    private PropertiesRequest createPropertiesRequest() {
        PropertiesRequest propertiesRequest = new PropertiesRequest();
        propertiesRequest.setDataType(DataType.NUMBER);
        propertiesRequest.setId("1");
        propertiesRequest.setName("field_number");

        return propertiesRequest;
    }
}
