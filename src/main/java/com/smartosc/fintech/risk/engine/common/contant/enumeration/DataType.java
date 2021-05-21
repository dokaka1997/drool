package com.smartosc.fintech.risk.engine.common.contant.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DataType {
    INTEGER("Integer", "Integer.class"),
    STRING("String", "String.class"),
    BYTE("Byte", "Byte.class"),
    LONG("Long", "Long.class"),
    FLOAT("Float", "Float.class"),
    DATE("Date", "java.sql.Timestamp.class"),
    BIG_DECIMAL("BigDecimal", "BigDecimal.class"),
    DOUBLE("Double", "Double.class"),
    SHORT("Short", "Short.class"),
    BOOLEAN("Boolean", "Boolean.class"),
    NUMBER("Number", "Number.class"),
    OTHER("", "");

    private final String typeName;
    private final String className;
}
