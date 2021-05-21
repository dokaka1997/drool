package com.smartosc.fintech.risk.engine.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class RuleGenerationResponse {
    String fileName;
    String drl;
    String agendaGroup;
}
