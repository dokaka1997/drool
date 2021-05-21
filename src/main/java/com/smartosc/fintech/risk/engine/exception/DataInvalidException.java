package com.smartosc.fintech.risk.engine.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DataInvalidException extends RuntimeException {
    private static final long serialVersionUID = -8880008994186415872L;

    private final String field;
    private final String value;
    private final String type;
}
