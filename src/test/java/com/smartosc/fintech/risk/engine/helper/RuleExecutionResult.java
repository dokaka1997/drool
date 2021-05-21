package com.smartosc.fintech.risk.engine.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class RuleExecutionResult {
    private final List<Object> objects;
    private final Map<String, Object> facts;
    private final List<Object> results;
}
