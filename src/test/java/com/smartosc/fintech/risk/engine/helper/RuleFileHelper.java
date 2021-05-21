package com.smartosc.fintech.risk.engine.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RuleFileHelper {
    private static final String JSON_LOCATION = "json";
    private static final String RULE_LOCATION = "rules";

    public static String readFile(String fileName) {
        String filePath = JSON_LOCATION + File.separator + fileName;
        InputStream inputStream = RuleFileHelper.class.getClassLoader().getResourceAsStream(filePath);
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining());
    }

    public static void clearRules() {
        File ruleDir = new File(RULE_LOCATION);
        File[] files = ruleDir.listFiles((f, name) -> name.endsWith(".drl"));
        for (File file : files) {
            file.deleteOnExit();
        }
    }
}
