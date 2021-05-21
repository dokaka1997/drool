package com.smartosc.fintech.risk.engine.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.InvalidJsonException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FactObjectConverter {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Object convertObject(Map<String, Object> data, Object runtimeInstance) {
        try {
            return mapper.convertValue(data, runtimeInstance.getClass());
        } catch (Exception e) {
            throw new InvalidJsonException(e);
        }
    }
}
