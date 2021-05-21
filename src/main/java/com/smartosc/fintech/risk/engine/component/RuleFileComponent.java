package com.smartosc.fintech.risk.engine.component;

import com.smartosc.fintech.risk.engine.config.AppConfig;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@AllArgsConstructor
public class RuleFileComponent {
    private final AppConfig appConfig;

    public String getAllRuleFilePath() {
        return appConfig.getRuleLocation() + "/**/*.drl";
    }

    public List<File> getDrlFiles() {
        File ruleDir = new File(appConfig.getRuleLocation());
        File[] files = ruleDir.listFiles((f, name) -> name.endsWith(".drl"));

        return files ==  null ? new ArrayList<>() : Arrays.asList(files);
    }

    public String getRuleFile(String ruleName) {
        File ruleDir = new File(appConfig.getRuleLocation());
        if (!ruleDir.exists()) {
            ruleDir.mkdir();
        }

        return ruleDir.getAbsolutePath() + "/" + ruleName + ".drl";
    }
}
