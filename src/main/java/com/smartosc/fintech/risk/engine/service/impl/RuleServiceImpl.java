package com.smartosc.fintech.risk.engine.service.impl;

import com.smartosc.fintech.risk.engine.common.util.DateUtils;
import com.smartosc.fintech.risk.engine.common.util.RuleUtils;
import com.smartosc.fintech.risk.engine.component.RuleFileComponent;
import com.smartosc.fintech.risk.engine.dto.request.*;
import com.smartosc.fintech.risk.engine.dto.response.RuleDeletionResponse;
import com.smartosc.fintech.risk.engine.dto.response.RuleGenerationResponse;
import com.smartosc.fintech.risk.engine.exception.RuleEngineException;
import com.smartosc.fintech.risk.engine.exception.RuleExecutionException;
import com.smartosc.fintech.risk.engine.exception.RuleGenerationException;
import com.smartosc.fintech.risk.engine.service.RuleService;
import com.smartosc.fintech.risk.engine.service.parser.RuleConditionParser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.drools.compiler.lang.api.*;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.mvel.DrlDumper;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@AllArgsConstructor
public class RuleServiceImpl implements RuleService {
    public static final String PACKAGE_NAME = "rules";
    public static final String RESULT = "results";

    private static final String STRING_VALUE_FORMAT = "\"%s\"";

    private final KieServices kieServices;
    private final KieContainer kieContainer;

    private final RuleFileComponent ruleFileComponent;

    @Override
    public List<RuleGenerationResponse> generateRule(RuleGenerationRequest request) {
        List<RuleGenerationResponse> responses = new ArrayList<>();
        request.getRules().forEach(ruleDescRequest -> {
            PackageDescrBuilder packageDescrBuilder = DescrFactory.newPackage();
            packageDescrBuilder.name(PACKAGE_NAME);
            packageDescrBuilder.attribute("dialect", "mvel");

            createImportInRule(packageDescrBuilder);
            createGlobalVariableInRule(packageDescrBuilder);

            Map<String, List<PropertiesRequest>> dataModels = buildDataModelMap(ruleDescRequest);
            int count = 0;
            for (RuleRequest rule : ruleDescRequest.getListRule()) {
                RuleDescrBuilder ruleDescrBuilder = packageDescrBuilder.newRule();
                String ruleName = String.format("%s - %s - %s", ruleDescRequest.getRuleSetName(), ruleDescRequest.getRuleName(), count);
                ruleDescrBuilder.name(ruleName);

                int salience = ruleDescRequest.getListRule().size() - count;
                createAttributeInRule(ruleDescRequest, salience, ruleDescrBuilder);

                CEDescrBuilder<RuleDescrBuilder, AndDescr> lhs = ruleDescrBuilder.lhs();
                createConditionInRule(rule.getItemCondition(), lhs, rule.getDataModels());

                createActionInRule(ruleDescrBuilder, rule.getAction());

                count++;
            }
            createDataModelInRule(dataModels, packageDescrBuilder);

            String drl = new DrlDumper().dump(packageDescrBuilder.getDescr());
            log.info(drl);

            String fileName = RuleUtils.getDrlFileName(ruleDescRequest.getDataModelGroup(),
                    ruleDescRequest.getRuleSetId(), ruleDescRequest.getRuleId());
            validateBuildRule(drl, fileName);
            generateRuleToDrlFile(drl, fileName);

            String agendaGroup = RuleUtils.getAgendaGroup(ruleDescRequest.getDataModelGroup(), ruleDescRequest.getRuleSetId());
            RuleGenerationResponse responseItem = new RuleGenerationResponse(fileName, drl, agendaGroup);
            responses.add(responseItem);
        });
        reloadRule();

        return responses;
    }

    @Override
    public List<RuleDeletionResponse> deleteRule(RuleDeletionRequest request) {
        List<String> deletedFiles = request.getRules().stream()
                .map(rule -> RuleUtils.getDrlFileName(rule.getDataModelGroup(), rule.getRuleSetId(), rule.getRuleId()))
                .collect(Collectors.toList());
        deleteFiles(deletedFiles);
        reloadRule();

        return deletedFiles.stream().map(RuleDeletionResponse::new).collect(Collectors.toList());
    }

    private void createGlobalVariableInRule(PackageDescrBuilder packageDescrBuilder) {
        packageDescrBuilder.newGlobal().type("List").identifier(RESULT);
    }

    private void createImportInRule(PackageDescrBuilder packageDescrBuilder) {
        packageDescrBuilder.newImport().target("java.util.*").end();
        packageDescrBuilder.newImport().target("java.math.*").end();
    }

    private void createActionInRule(RuleDescrBuilder ruleDescrBuilder, ActionRequest action) {
        if (action.isEmpty()) {
            return;
        }

        String drlAction = String.format("%s.add(\"%s\")", RESULT, action.getValue());
        ruleDescrBuilder.rhs(drlAction).end();
    }

    private void createConditionInRule(List<ConditionRequest> listCondition,
                                       CEDescrBuilder<RuleDescrBuilder, AndDescr> lhs,
                                       List<DataModelRequest> dataModels) {
        for (DataModelRequest dataModel : dataModels) {
            lhs.pattern().id(dataModel.modelVariable(),false).type(dataModel.getTableName()).end();
        }
        lhs.end();

        RuleConditionParser parser = new RuleConditionParser(dataModels, listCondition);
        String condition = parser.buildPattern();
        lhs.pattern("Object").constraint(condition).end();
    }

    private void createAttributeInRule(RuleDescRequest ruleDescRequest, int salience, RuleDescrBuilder ruleDescrBuilder) {
        String agendaGroup = RuleUtils.getAgendaGroup(ruleDescRequest.getDataModelGroup(), ruleDescRequest.getRuleSetId());
        ruleDescrBuilder.attribute("agenda-group").value(String.format(STRING_VALUE_FORMAT, agendaGroup)).end();

        String activationGroup = RuleUtils.getActivationGroup(ruleDescRequest.getDataModelGroup(),
                ruleDescRequest.getRuleSetId(), ruleDescRequest.getRuleId());
        ruleDescrBuilder.attribute("activation-group").value(String.format(STRING_VALUE_FORMAT, activationGroup)).end();

        ruleDescrBuilder.attribute("salience").value(String.valueOf(salience)).end();

        if (Objects.nonNull(ruleDescRequest.getEffectiveDateStart())) {
            String dateEffective = DateUtils.formatDateTime(ruleDescRequest.getEffectiveDateStart());
            dateEffective = String.format(STRING_VALUE_FORMAT, dateEffective);
            ruleDescrBuilder.attribute("date-effective").value(dateEffective).end();
        }

        if (Objects.nonNull(ruleDescRequest.getEffectiveDateEnd())) {
            String dateExpires = DateUtils.formatDateTime(ruleDescRequest.getEffectiveDateEnd());
            dateExpires = String.format(STRING_VALUE_FORMAT, dateExpires);
            ruleDescrBuilder.attribute("date-expires").value(dateExpires).end();
        }

        if (Objects.nonNull(ruleDescRequest.getStatus())) {
            ruleDescrBuilder.attribute("enabled").value(String.valueOf(ruleDescRequest.getStatus())).end();
        }

        ruleDescRequest.getAttributes().forEach(attributeRequest -> ruleDescrBuilder
                .attribute(attributeRequest.getKey())
                .value(attributeRequest.generateValue())
                .end());
    }

    private void createDataModelInRule(Map<String, List<PropertiesRequest>> dataModels, PackageDescrBuilder packageDescrBuilder) {
        dataModels.forEach((key, value) -> {
            TypeDeclarationDescrBuilder descBuilder = packageDescrBuilder
                    .newDeclare()
                    .type()
                    .name(key);
            value.forEach(attribute -> descBuilder.newField(attribute.getName()).type(attribute.getDataType().getTypeName()).end());
            descBuilder.end();
        });
    }

    private Map<String, List<PropertiesRequest>> buildDataModelMap(RuleDescRequest ruleDescRequest) {
        return ruleDescRequest.getListRule().stream()
                .map(RuleRequest::getDataModels).flatMap(Collection::stream)
                .collect(Collectors.toMap(DataModelRequest::getTableName,
                        DataModelRequest::getAttributes,
                        (v1, v2) -> Stream.of(v1, v2).flatMap(Collection::stream).collect(Collectors.toList())));
    }

    private void generateRuleToDrlFile(String stringRule, String fileName) {
        File file = new File(ruleFileComponent.getRuleFile(fileName));
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(stringRule);
        } catch (Exception e) {
            log.error("generateRuleToDrlFile ", e);
            throw new RuleGenerationException(fileName);
        }
    }

    private void validateBuildRule(String stringRule, String fileName) {
        KieFileSystem newKieFileSystem = kieServices.newKieFileSystem();
        newKieFileSystem.write( "/src/main/resources/rules" + File.separator + fileName +".drl", stringRule);
        KieBuilder kieBuilder = kieServices.newKieBuilder(newKieFileSystem);
        kieBuilder.buildAll();

        if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
            String message = "Build Errors:\n" + kieBuilder.getResults().toString();
            throw new RuleExecutionException(message, fileName);
        }
    }

    private void reloadRule() {
        KieBuilder kb = kieServices.newKieBuilder(getKieFileSystem());
        kb.buildAll();
        KieModule kieModule = kb.getKieModule();
        kieServices.getRepository().addKieModule(kieModule);
        kieContainer.updateToVersion(kieContainer.getReleaseId());
    }

    private KieFileSystem getKieFileSystem() {
        KieFileSystem newKieFileSystem = kieServices.newKieFileSystem();
        try {
            ruleFileComponent.getDrlFiles()
                    .forEach(file -> newKieFileSystem.write(ResourceFactory.newFileResource(file)));
        } catch (Exception e) {
            log.error("error kieFileSystem: ", e);
            throw new RuleEngineException();
        }
        return newKieFileSystem;
    }

    private void deleteFiles(List<String> fileNames) {
        fileNames.forEach(fileName -> {
            try {
                Files.delete(new File(ruleFileComponent.getRuleFile(fileName)).toPath());
            } catch (IOException e) {
                throw new RuleExecutionException(e.getMessage(), fileName);
            }
        });

        KieFileSystem newKieFileSystem = kieServices.newKieFileSystem();
        String[] files = fileNames.stream()
                .map(fileName -> "/src/main/resources/rules" + File.separator + fileName + ".drl")
                .toArray(String[]::new);
        newKieFileSystem.delete(files);
        KieBuilder kieBuilder = kieServices.newKieBuilder(newKieFileSystem);
        kieBuilder.buildAll();
        if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
            String message = "Build Errors:\n" + kieBuilder.getResults().toString();
            String fileError = String.join(",", fileNames);
            throw new RuleExecutionException(message, fileError);
        }
    }
}
