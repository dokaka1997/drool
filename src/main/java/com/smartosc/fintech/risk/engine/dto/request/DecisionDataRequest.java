package com.smartosc.fintech.risk.engine.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DecisionDataRequest {
    String dataModel;
    Map<String, Object> attributes = new HashMap<>();
}
