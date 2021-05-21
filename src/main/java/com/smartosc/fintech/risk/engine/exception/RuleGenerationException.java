package com.smartosc.fintech.risk.engine.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RuleGenerationException extends RuntimeException {
    private final String fileName;
}
