package com.smartosc.fintech.risk.engine.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartosc.fintech.risk.engine.common.util.RuleUtils;
import com.smartosc.fintech.risk.engine.dto.request.DecisionRequest;
import com.smartosc.fintech.risk.engine.dto.response.DecisionResponse;
import com.smartosc.fintech.risk.engine.exception.RuleEngineException;
import com.smartosc.fintech.risk.engine.service.DecisionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class DecisionServiceImpl implements DecisionService {
    private static final ObjectMapper mapper = new ObjectMapper();

    private final KieContainer kieContainer;

    @Override
    public DecisionResponse executeRule(DecisionRequest request) {
        KieSession kieSession = kieContainer.newKieSession();

        String agendaGroup = RuleUtils.getAgendaGroup(request.getDataModelGroup(), request.getRuleSetId());
        kieSession.getAgenda().getAgendaGroup(agendaGroup).setFocus();

        List<Object> objects = new ArrayList<>();
        request.getData().forEach(data -> {
            Object factInstance = getFactInstance(kieSession, data.getDataModel());
            Object fact = convertObject(data.getAttributes(), factInstance);
            objects.add(fact);
        });

        List<Object> results = new ArrayList<>();
        kieSession.setGlobal(RuleServiceImpl.RESULT, results);
        objects.forEach(kieSession::insert);

        kieSession.fireAllRules();
        kieSession.dispose();

        DecisionResponse response = new DecisionResponse();
        response.setValues(results);
        return response;
    }

    private Object getFactInstance(KieSession kieSession, String className) {
        FactType factType = kieSession.getKieBase().getFactType(RuleServiceImpl.PACKAGE_NAME, className);
        try {
            return factType.newInstance();
        } catch (Exception e) {
            throw new RuleEngineException();
        }
    }

    private Object convertObject(Map<String, Object> data, Object runtimeInstance) {
        try {
            return mapper.convertValue(data, runtimeInstance.getClass());
        } catch (Exception e) {
            throw new RuleEngineException();
        }
    }
}
