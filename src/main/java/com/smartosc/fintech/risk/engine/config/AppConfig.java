package com.smartosc.fintech.risk.engine.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AppConfig {
    @Value("${app.rule.location}")
    private String ruleLocation;
}
