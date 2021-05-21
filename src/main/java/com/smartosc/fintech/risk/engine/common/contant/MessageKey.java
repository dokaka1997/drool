package com.smartosc.fintech.risk.engine.common.contant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageKey {
    public static final String HANDLE_MISSING_SERVLET_REQUEST_PARAMETER = "handle.missing.servlet.request.parameter";
    public static final String HANDLE_HTTP_MEDIA_TYPE_NOT_SUPPORTED = "handle.http.media.type.not.supported";
    public static final String HANDLE_METHOD_ARGUMENT_NOT_VALID = "handle.method.argument.not.valid";
    public static final String HANDLE_CONSTRAINT_VIOLATION = "handle.constraint.violation";
    public static final String HANDLE_HTTP_MESSAGE_NOT_READABLE = "handle.http.message.not.readable";
    public static final String HANDLE_HTTP_MESSAGE_NOT_WRITABLE = "handle.http.message.not.writable";
    public static final String HANDLE_NOT_FOUND_EXCEPTION = "handle.not.found.exception";
    public static final String HANDLE_DATA_INTEGRITY_VIOLATION = "handle.data.integrity.violation";
    public static final String INTERNAL_SERVER_ERROR = "internal.server.error";
    public static final String HANDLE_METHOD_ARGUMENT_TYPE_MISMATCH = "handle.method.argument.type.mismatch";
    public static final String BAD_REQUEST = "bad.request";
    public static final String METHOD_NOT_SUPPORT = "method.not.support";
    public static final String URL_NOT_FOUND = "url.not.found";
    public static final String DATA_NOT_VALID_FORMAT = "data.invalid.format";
    public static final String ENUM_NOT_FOUND = "enum.not.found";
    public static final String RULE_GENERATION_ERROR = "rule.generation.error";
    public static final String RULE_EXECUTION_ERROR = "rule.execution.error";
    public static final String RULE_ENGINE_ERROR = "rule.engine.error";
}
