package com.smartosc.fintech.risk.engine.service.impl;

import com.smartosc.fintech.risk.engine.dto.request.DecisionDataRequest;
import com.smartosc.fintech.risk.engine.dto.request.DecisionRequest;
import com.smartosc.fintech.risk.engine.dto.response.DecisionResponse;
import com.smartosc.fintech.risk.engine.exception.RuleEngineException;
import org.drools.core.common.DefaultAgenda;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.Agenda;
import org.kie.api.runtime.rule.AgendaGroup;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class DecisionServiceTest {
    @InjectMocks
    private DecisionServiceImpl decisionService;

    @Mock
    private KieContainer kieContainer;

    @Test
    void testExecuteRuleFactTypeError() {
        KieSession kieSession = Mockito.mock(StatefulKnowledgeSessionImpl.class);
        KnowledgeBaseImpl kieBase = Mockito.mock(KnowledgeBaseImpl.class);
        Agenda agenda = Mockito.mock(DefaultAgenda.class);
        AgendaGroup agendaGroup = Mockito.mock(InternalAgendaGroup.class);

        Mockito.when(kieContainer.newKieSession()).thenReturn(kieSession);
        Mockito.when(kieSession.getAgenda()).thenReturn(agenda);
        Mockito.when(agenda.getAgendaGroup(Mockito.anyString())).thenReturn(agendaGroup);
        Mockito.when(kieSession.getKieBase()).thenReturn(kieBase);
        Mockito.when(kieBase.getFactType(Mockito.anyString(), Mockito.anyString())).thenReturn(new ClassDefinition());

        DecisionDataRequest data = new DecisionDataRequest();
        data.setDataModel("Data_Model");

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("field", 10);
        data.setAttributes(attributes);

        DecisionRequest request = new DecisionRequest();
        request.setDataModelGroup("LMS");
        request.setRuleSetId("1");
        request.setData(Collections.singletonList(data));

        Assertions.assertThrows(RuleEngineException.class, () -> decisionService.executeRule(request));
    }

    @Test
    void testExecuteRuleParseDataError() throws Exception {
        KieSession kieSession = Mockito.mock(StatefulKnowledgeSessionImpl.class);
        KnowledgeBaseImpl kieBase = Mockito.mock(KnowledgeBaseImpl.class);
        ClassDefinition factType = Mockito.mock(ClassDefinition.class);
        Agenda agenda = Mockito.mock(DefaultAgenda.class);
        AgendaGroup agendaGroup = Mockito.mock(InternalAgendaGroup.class);

        Mockito.when(kieContainer.newKieSession()).thenReturn(kieSession);
        Mockito.when(kieSession.getAgenda()).thenReturn(agenda);
        Mockito.when(agenda.getAgendaGroup(Mockito.anyString())).thenReturn(agendaGroup);
        Mockito.when(kieSession.getKieBase()).thenReturn(kieBase);
        Mockito.when(kieBase.getFactType(Mockito.anyString(), Mockito.anyString())).thenReturn(factType);
        Mockito.when(factType.newInstance()).thenReturn(null);

        DecisionDataRequest data = new DecisionDataRequest();
        data.setDataModel("Data_Model");

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("field", 10);
        data.setAttributes(attributes);

        DecisionRequest request = new DecisionRequest();
        request.setDataModelGroup("LMS");
        request.setRuleSetId("1");
        request.setData(Collections.singletonList(data));

        Assertions.assertThrows(RuleEngineException.class, () -> decisionService.executeRule(request));
    }

    @Test
    void testExecuteRuleSuccess() throws Exception {
        KieSession kieSession = Mockito.mock(StatefulKnowledgeSessionImpl.class);
        KnowledgeBaseImpl kieBase = Mockito.mock(KnowledgeBaseImpl.class);
        ClassDefinition factType = Mockito.mock(ClassDefinition.class);
        Agenda agenda = Mockito.mock(DefaultAgenda.class);
        AgendaGroup agendaGroup = Mockito.mock(InternalAgendaGroup.class);

        Mockito.when(kieContainer.newKieSession()).thenReturn(kieSession);
        Mockito.when(kieSession.getAgenda()).thenReturn(agenda);
        Mockito.when(agenda.getAgendaGroup(Mockito.anyString())).thenReturn(agendaGroup);
        Mockito.doNothing().when(kieSession).setGlobal(Mockito.anyString(), Mockito.any());
        Mockito.when(kieSession.getKieBase()).thenReturn(kieBase);
        Mockito.when(kieBase.getFactType(Mockito.anyString(), Mockito.anyString())).thenReturn(factType);
        Mockito.when(factType.newInstance()).thenReturn(new Object());

        DecisionDataRequest data = new DecisionDataRequest();
        data.setDataModel("Data_Model");

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("field", 10);
        data.setAttributes(attributes);

        DecisionRequest request = new DecisionRequest();
        request.setDataModelGroup("LMS");
        request.setRuleSetId("1");
        request.setData(Collections.singletonList(data));

        DecisionResponse response = decisionService.executeRule(request);
        Assertions.assertTrue(response.getValues().isEmpty());
    }
}
