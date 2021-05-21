package com.smartosc.fintech.risk.engine.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RuleUtils {
    public static String getAgendaGroup(String dataModelGroup, String ruleSet) {
        return String.format("%s.%s", dataModelGroup, ruleSet);
    }

    public static String getActivationGroup(String dataModelGroup, String ruleSet, String rule) {
        return String.format("%s.%s.%s", dataModelGroup, ruleSet, rule);
    }

    public static String getDrlFileName(String dataModelGroup, String ruleSet, String rule) {
        return String.format("%s.%s.%s", dataModelGroup, ruleSet, rule);
    }
}
