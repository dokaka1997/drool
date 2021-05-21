package com.smartosc.fintech.risk.engine.config;

import com.smartosc.fintech.risk.engine.component.RuleFileComponent;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.File;


/**
 * This class for configuration Drools, but it's not used at current, keep for future if need
 */
@Configuration
@Slf4j
public class DroolsConfig {
    private final KieServices kieServices = KieServices.Factory.get();
    private final RuleFileComponent ruleFileComponent;

    public DroolsConfig(RuleFileComponent ruleFileComponent) {
        this.ruleFileComponent = ruleFileComponent;
    }

    @Bean
    @Primary
    public KieContainer getKieContainer() {
        long startTime = System.currentTimeMillis();
        log.info("Container created...");
        getKieRepository();
        KieBuilder kb = kieServices.newKieBuilder(kieFileSystem());
        kb.buildAll();
        KieModule kieModule = kb.getKieModule();
        KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());
        long endTime = System.currentTimeMillis();
        log.info("Time to build rules : " + (endTime - startTime)  + " ms" );
        return kieContainer;
    }

    public KieFileSystem kieFileSystem() {
        KieFileSystem kieFileSystem = getKieServices().newKieFileSystem();
        try {
            for (File file: ruleFileComponent.getDrlFiles()) {
                kieFileSystem.write(ResourceFactory.newFileResource(file));
            }
        } catch (Exception e) {
            log.error("error kieFileSystem: ", e);
        }
        return kieFileSystem;
    }

    private void getKieRepository() {
        final KieRepository kieRepository = kieServices.getRepository();
        kieRepository.addKieModule(kieRepository::getDefaultReleaseId);
    }

    @Bean
    public KieServices getKieServices() {
        return KieServices.Factory.get();
    }

    @Bean
    public KieFileSystem getKieFileSystem(KieServices kieServices) {
        ReleaseId defaultReleaseId = kieServices.getRepository().getDefaultReleaseId();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        if (kieServices.getRepository().getKieModule(defaultReleaseId) == null) {
            kieServices.newKieBuilder(kieFileSystem).buildAll();
        }
        return kieFileSystem;
    }

}
