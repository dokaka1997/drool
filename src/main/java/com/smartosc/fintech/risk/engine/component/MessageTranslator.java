package com.smartosc.fintech.risk.engine.component;

import lombok.AllArgsConstructor;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@AllArgsConstructor
public class MessageTranslator {
    private final ResourceBundleMessageSource messageSource;

    public String getMessage(String message) {
        return messageSource.getMessage(message, null, Locale.ENGLISH);
    }
}
