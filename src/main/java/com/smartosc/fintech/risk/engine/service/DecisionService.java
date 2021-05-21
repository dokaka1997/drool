package com.smartosc.fintech.risk.engine.service;

import com.smartosc.fintech.risk.engine.dto.request.DecisionRequest;
import com.smartosc.fintech.risk.engine.dto.response.DecisionResponse;

public interface DecisionService {
    DecisionResponse executeRule(DecisionRequest request);
}
