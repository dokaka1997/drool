package com.smartosc.fintech.risk.engine.common.contant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    SUCCESS("RSK-RE-200"),
    SERVER_ERROR("RSK-RE-500"),
    CONNECTION_TIMEOUT("RSK-RE-504"),
    CONFLICT("RSK-RE-409"),
    BAD_REQUEST("RSK-RE-400"),
    UNSUPPORTED_MEDIA_TYPE("RSK-RE-415"),
    NOT_FOUND("RSK-RE-404"),
    METHOD_NOT_SUPPORT("RSK-RE-405"),
    RULE_GENERATION_ERROR("RSK-RE-1001"),
    RULE_EXECUTION_ERROR("RSK-RE-1002"),
    RULE_ENGINE_ERROR("RSK-RE-1003");

    private final String code;
}
