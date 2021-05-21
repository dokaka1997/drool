package com.smartosc.fintech.risk.engine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.zalando.logbook.BodyFilter;
import org.zalando.logbook.json.JacksonJsonFieldBodyFilter;

import java.util.Arrays;
import java.util.List;

@Component
@Getter
@Setter
public class LogBookConfig {
    List<String> filterResponseHeader;

    @Value("${logbook.filter.body:secondField}")
    private String filterBody;

    @Bean
    BodyFilter bodyFilter(){
        return new JacksonJsonFieldBodyFilter(Arrays.asList(filterBody.split(",")),"******");
    }
}
