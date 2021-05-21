package com.smartosc.fintech.risk.engine.common.contant.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum OperatorType {
    IN("in"),
    MATCHES("matches"),
    NOT_MATCHES("not matches"),
    BETWEEN("between"),
    OTHER("other");

    private final String operator;

    public static OperatorType operatorOf(String operator) {
        return Arrays.stream(values())
                .filter(status -> status.getOperator().equalsIgnoreCase(operator)).findFirst()
                .orElse(OTHER);
    }
}
