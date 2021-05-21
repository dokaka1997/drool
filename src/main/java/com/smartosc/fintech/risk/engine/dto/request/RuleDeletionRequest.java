package com.smartosc.fintech.risk.engine.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RuleDeletionRequest {
    List<RuleDeletionDetailRequest> rules = new ArrayList<>();
}
