package com.smartosc.fintech.risk.engine.common.contant.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConditionType {
    AND(" && "),
    OR(" || ");

    private final String value;
}
