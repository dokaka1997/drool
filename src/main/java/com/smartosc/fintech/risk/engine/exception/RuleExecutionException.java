package com.smartosc.fintech.risk.engine.exception;

import lombok.Getter;

@Getter
public class RuleExecutionException extends RuntimeException {
    private final String fileName;

    public RuleExecutionException(String message, String fileName) {
        super(message);
        this.fileName = fileName;
    }
}
