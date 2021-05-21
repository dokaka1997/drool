package com.smartosc.fintech.risk.engine.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EnumInvalidException extends RuntimeException {
    private static final long serialVersionUID = -4292066769296408376L;

    private final String field;
    private final String value;
    private final String data;
}
