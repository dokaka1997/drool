package com.smartosc.fintech.risk.engine.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableCaching
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Rule-Engine-Service")
                .apiInfo(apiInfo())
                .enable(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.smartosc.fintech.risk.engine.controller"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfo("RISK#rule-engine-service APIs",
                "SmartOSC fintech | Lending | RISK | All PoC functions Services", "1.0.0-dev",
                "https://developer.smartosc.com/policy", null, "Copyright of SmartOSC Fintech",
                "https://developer.smartosc.com/license", Collections.emptyList());
    }
}
