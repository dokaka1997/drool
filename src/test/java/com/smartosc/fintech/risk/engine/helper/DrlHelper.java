package com.smartosc.fintech.risk.engine.helper;

import org.drools.core.command.runtime.rule.AgendaGroupSetFocusCommand;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.command.Command;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;
import org.springframework.util.StringUtils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DrlHelper {
    private static final String GET_OBJECTS_KEY = "_getObjects";

    private StatelessKieSession kSession;
    private final KieFileSystem kieFileSystem;
    private final KieServices kieServices;

    public DrlHelper() {
        kieServices = KieServices.Factory.get();
        kieFileSystem = kieServices.newKieFileSystem();
    }

    public RuleExecutionResult execute(String agendaGroup, List<Object> objects) {
        if (kSession == null){
            throw new IllegalStateException("No Kie Session defined. Did you call 'build()'?");
        }

        List<Command> commands = new ArrayList<>();

        AgendaGroupSetFocusCommand agendaGroupCommand = new AgendaGroupSetFocusCommand(agendaGroup);
        commands.add(agendaGroupCommand);

        List<Object> results = new ArrayList<>();
        Command globalVariable = CommandFactory.newSetGlobal("results", results);
        commands.add(globalVariable);

        objects.forEach(object -> {
            Command insertFactCommand = CommandFactory.newInsert(object);
            commands.add(insertFactCommand);
        });

        commands.add(CommandFactory.newFireAllRules());
        commands.add(CommandFactory.newGetObjects(GET_OBJECTS_KEY));

        ExecutionResults executionResults = kSession.execute(CommandFactory.newBatchExecution(commands));
        Map<String, Object> facts = new HashMap<>(executionResults.getIdentifiers().size());

        for (String identifier : executionResults.getIdentifiers()) {
            facts.put(identifier, executionResults.getValue(identifier));
        }

        return new RuleExecutionResult( (List<Object>)(executionResults.getValue(GET_OBJECTS_KEY)), facts, results);
    }

    public DrlHelper withDrl(Object context, String fileName, String drl){
        if (StringUtils.isEmpty(drl)){
            throw new IllegalArgumentException("rule is empty");
        }

        String filePath = "src/main/resources/" + fileName + ".drl";
        kieFileSystem.write(filePath, kieServices.getResources().newReaderResource(new StringReader(drl)).setResourceType(ResourceType.DRL));
        return this;
    }

    public DrlHelper withDrlThenDelete(Object context, String fileName, String drl){
        if (StringUtils.isEmpty(drl)){
            throw new IllegalArgumentException("rule is empty");
        }

        String filePath = "src/main/resources/" + fileName + ".drl";
        kieFileSystem.write(filePath, kieServices.getResources().newReaderResource(new StringReader(drl)).setResourceType(ResourceType.DRL));
        kieFileSystem.delete(filePath);
        return this;
    }

    public DrlHelper build() {
        if (kSession != null){
            throw new IllegalStateException("Kie Session has already been built.");
        }

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();

        if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + kieBuilder.getResults().toString());
        }

        KieRepository kieRepository = kieServices.getRepository();
        KieContainer kContainer = kieServices.newKieContainer(kieRepository.getDefaultReleaseId());

        kSession = kContainer.newStatelessKieSession();

        return this;
    }

    public Object getFactInstance(String pkg, String className) {
        FactType factType = kSession.getKieBase().getFactType(pkg, className);
        try {
            return factType.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Convert data Errors:\n" + e.getMessage());
        }
    }
}
