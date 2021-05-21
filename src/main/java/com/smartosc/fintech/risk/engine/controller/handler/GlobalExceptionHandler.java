package com.smartosc.fintech.risk.engine.controller.handler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.smartosc.fintech.risk.engine.component.MessageTranslator;
import com.smartosc.fintech.risk.engine.common.contant.ErrorCode;
import com.smartosc.fintech.risk.engine.common.contant.MessageKey;
import com.smartosc.fintech.risk.engine.dto.response.ErrorResponse;
import com.smartosc.fintech.risk.engine.exception.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
@AllArgsConstructor
public class GlobalExceptionHandler {
    private static final String KEY = "error";

    private final MessageTranslator messageTranslator;

    /**
     * Handle MissingServletRequestParameterException. Triggered when a 'required' request parameter is missing.
     *
     * @param ex MissingServletRequestParameterException
     * @return the ApiError object
     */

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
        log.info("Error MissingServletRequestParameterException", ex);
        String message = String.format(messageTranslator.getMessage(MessageKey.HANDLE_MISSING_SERVLET_REQUEST_PARAMETER), ex.getParameterName());
        ErrorResponse<Map<String, String>> error =
                new ErrorResponse<>(ErrorCode.BAD_REQUEST.getCode(), messageTranslator.getMessage(MessageKey.BAD_REQUEST));
        error.fail(Collections.singletonMap(KEY, message));

        return buildResponseEntity(HttpStatus.BAD_REQUEST, error);
    }

    /**
     * Handle HttpMediaTypeNotSupportedException. This one triggers when JSON is invalid as well.
     *
     * @param ex HttpMediaTypeNotSupportedException
     * @return the ApiError object
     */

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        log.info("Error HttpMediaTypeNotSupportedException", ex);
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(messageTranslator.getMessage(MessageKey.HANDLE_HTTP_MEDIA_TYPE_NOT_SUPPORTED));
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
        ErrorResponse<Map<String, String>> error =
                new ErrorResponse<>(ErrorCode.UNSUPPORTED_MEDIA_TYPE.getCode(),
                        messageTranslator.getMessage(MessageKey.HANDLE_HTTP_MEDIA_TYPE_NOT_SUPPORTED));
        error.fail(Collections.singletonMap(KEY, builder.substring(0, builder.length() - 2)));

        return buildResponseEntity(HttpStatus.UNSUPPORTED_MEDIA_TYPE, error);
    }

    /**
     * Handle MethodArgumentNotValidException. Triggered when an object fails @Valid validation.
     *
     * @param ex the MethodArgumentNotValidException that is thrown when @Valid validation fails
     * @return the ApiError object
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.info("Error MethodArgumentNotValidException", ex);

        return validationError(ex.getBindingResult());
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex) {
        ErrorResponse<Map<String, String>> error =
                new ErrorResponse<>(ErrorCode.BAD_REQUEST.getCode(), messageTranslator.getMessage(MessageKey.BAD_REQUEST));
        error.fail(Collections.singletonMap("error TypeMismatchException", ex.getMessage()));

        return buildResponseEntity(HttpStatus.BAD_REQUEST, error);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Object> handleBindException(BindException ex) {
        log.info("Error BindException", ex);

        return validationError(ex.getBindingResult());
    }

    /**
     * Handles javax.validation.ConstraintViolationException. Thrown when @Validated fails.
     *
     * @param ex the ConstraintViolationException
     * @return the ApiError object
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex) {
        log.info("Error ConstraintViolationException", ex);
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(constraintViolation -> {
            String field = null;
            for (Path.Node node : constraintViolation.getPropertyPath()) {
                field = node.getName();
            }
            String errorMessage = constraintViolation.getMessage();
            try {
                errorMessage = messageTranslator.getMessage(errorMessage);
            } catch (Exception e) {
                log.error("cant get message in message data source");
            }
            field = field == null ? KEY : field;
            errors.put(field, errorMessage);
        });
        ErrorResponse<Map<String, String>> error = new ErrorResponse<>(ErrorCode.BAD_REQUEST.getCode(),
                                                        messageTranslator.getMessage(MessageKey.HANDLE_CONSTRAINT_VIOLATION));
        error.fail(errors);

        return buildResponseEntity(HttpStatus.BAD_REQUEST, error);
    }

    /**
     * Handle HttpMessageNotReadableException. Happens when request JSON is malformed.
     *
     * @param ex HttpMessageNotReadableException
     * @return the ApiError object
     */

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        log.info("error HttpMessageNotReadableException", ex);

        if (ex.contains(InvalidFormatException.class) && ex.getRootCause() != null) {
            return handleInvalidFormatException((InvalidFormatException) ex.getRootCause());
        }

        if (ex.contains(EnumInvalidException.class) && ex.getRootCause() != null) {
            return handleEnumInvalidException((EnumInvalidException) ex.getRootCause());
        }

        if (ex.contains(DataInvalidException.class) && ex.getRootCause() != null) {
            return handleDataInvalidException((DataInvalidException) ex.getRootCause());
        }

        if (ex.contains(JsonParseException.class) && ex.getRootCause() != null) {
            return handleJsonParseException((JsonParseException) ex.getRootCause());
        }

        ServletWebRequest servletWebRequest = (ServletWebRequest) request;
        log.info("{} to {}", servletWebRequest.getHttpMethod(), servletWebRequest.getRequest().getServletPath());
        
        ErrorResponse<Map<String, String>> error = new ErrorResponse<>(ErrorCode.BAD_REQUEST.getCode(),
                                                        messageTranslator.getMessage(MessageKey.HANDLE_CONSTRAINT_VIOLATION));
        error.fail(Collections.singletonMap(KEY, messageTranslator.getMessage(MessageKey.HANDLE_HTTP_MESSAGE_NOT_READABLE)));
        return buildResponseEntity(HttpStatus.BAD_REQUEST, error);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<Object> handleInvalidFormatException(InvalidFormatException ex) {
        log.info("InvalidFormatException");
        String value = ex.getValue().toString();
        String field = ex.getPath().get(ex.getPath().size() - 1).getFieldName();
        String type = ex.getTargetType().getSimpleName();
        DataInvalidException e = new DataInvalidException(field, value, type);
        ErrorResponse<Map<String, String>> error = 
                new ErrorResponse<>(ErrorCode.BAD_REQUEST.getCode(), messageTranslator.getMessage(MessageKey.HANDLE_CONSTRAINT_VIOLATION));
        error.fail(Collections.singletonMap(e.getField(), e.getMessage()));

        return buildResponseEntity(HttpStatus.BAD_REQUEST, error);
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<Object> handleJsonParseException(JsonParseException ex) {
        log.info("JsonParseException");
        ErrorResponse<Map<String, String>> error = 
                new ErrorResponse<>(ErrorCode.BAD_REQUEST.getCode(), messageTranslator.getMessage(MessageKey.HANDLE_CONSTRAINT_VIOLATION));
        JsonStreamContext jsc = ex.getProcessor().getParsingContext();
        String name = ObjectUtils.isEmpty(jsc.getCurrentName()) ? jsc.getParent().getCurrentName() : jsc.getCurrentName();
        error.fail(Collections.singletonMap(ObjectUtils.isEmpty(name) ? KEY : name,
                messageTranslator.getMessage(MessageKey.HANDLE_HTTP_MESSAGE_NOT_READABLE)));

        return buildResponseEntity(HttpStatus.BAD_REQUEST, error);
    }

    @ExceptionHandler(DataInvalidException.class)
    public ResponseEntity<Object> handleDataInvalidException(DataInvalidException ex) {
        log.info("DataInValidException");
        ErrorResponse<Map<String, String>> error = 
                new ErrorResponse<>(ErrorCode.BAD_REQUEST.getCode(), messageTranslator.getMessage(MessageKey.HANDLE_CONSTRAINT_VIOLATION));
        if (Objects.isNull(ex)) {
            return buildResponseEntity(HttpStatus.BAD_REQUEST, error);
        }

        String message = String.format(messageTranslator.getMessage(MessageKey.DATA_NOT_VALID_FORMAT), ex.getValue(), ex.getType());
        error.fail(Collections.singletonMap(ex.getField(), message));
        return buildResponseEntity(HttpStatus.BAD_REQUEST, error);
    }

    @ExceptionHandler(EnumInvalidException.class)
    public ResponseEntity<Object> handleEnumInvalidException(EnumInvalidException ex) {
        log.info("EnumNotFoundException");
        ErrorResponse<Map<String, String>> error = 
                new ErrorResponse<>(ErrorCode.BAD_REQUEST.getCode(), messageTranslator.getMessage(MessageKey.HANDLE_CONSTRAINT_VIOLATION));
        if (Objects.isNull(ex)) {
            return buildResponseEntity(HttpStatus.BAD_REQUEST, error);
        }

        String message = String.format(messageTranslator.getMessage(MessageKey.ENUM_NOT_FOUND), ex.getValue(), ex.getData());
        error.fail(Collections.singletonMap(ex.getField(), message));
        return buildResponseEntity(HttpStatus.BAD_REQUEST, error);
    }

    @ExceptionHandler(HttpMessageNotWritableException.class)
    public ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex) {
        log.info("Error HttpMessageNotWritableException", ex);
        ErrorResponse<Map<String, String>> error = new ErrorResponse<>(ErrorCode.SERVER_ERROR.getCode(),
                messageTranslator.getMessage(MessageKey.HANDLE_HTTP_MESSAGE_NOT_WRITABLE));

        return buildResponseEntity(HttpStatus.BAD_REQUEST, error);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        log.info("Error NoHandlerFoundException", ex);
        String message = String.format(messageTranslator.getMessage(MessageKey.HANDLE_NOT_FOUND_EXCEPTION), ex.getHttpMethod(), ex.getRequestURL());
        ErrorResponse<Map<String, String>> error = 
                new ErrorResponse<>(ErrorCode.NOT_FOUND.getCode(), messageTranslator.getMessage(MessageKey.URL_NOT_FOUND));
        error.fail(Collections.singletonMap(KEY, message));

        return buildResponseEntity(HttpStatus.BAD_REQUEST, error);
    }

    /**
     * Handle DataIntegrityViolationException, inspects the cause for different DB causes.
     *
     * @param ex the DataIntegrityViolationException
     * @return the ApiError object
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.info("Error DataIntegrityViolationException", ex);
        ErrorResponse<Map<String, String>> error;
        if (ex.getCause() instanceof ConstraintViolationException) {
            error = new ErrorResponse<>(ErrorCode.CONFLICT.getCode(), messageTranslator.getMessage(MessageKey.HANDLE_DATA_INTEGRITY_VIOLATION));
            return buildResponseEntity(HttpStatus.CONFLICT, error);
        }
        error = new ErrorResponse<>(ErrorCode.SERVER_ERROR.getCode(), messageTranslator.getMessage(MessageKey.INTERNAL_SERVER_ERROR));
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.info("Error MethodArgumentTypeMismatchException", ex);
        String message = String.format(messageTranslator.getMessage(MessageKey.HANDLE_METHOD_ARGUMENT_TYPE_MISMATCH),
                ex.getName(), ex.getValue(), Objects.requireNonNull(ex.getRequiredType()).getSimpleName());
        ErrorResponse<Map<String, String>> error = 
                new ErrorResponse<>(ErrorCode.BAD_REQUEST.getCode(), messageTranslator.getMessage(MessageKey.BAD_REQUEST));
        error.fail(Collections.singletonMap(KEY, message));

        return buildResponseEntity(HttpStatus.BAD_REQUEST, error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleInternalException(HttpRequestMethodNotSupportedException ex) {
        log.info("Error Exception", ex);
        ErrorResponse<Map<String, String>> error =
                new ErrorResponse<>(ErrorCode.METHOD_NOT_SUPPORT.getCode(), messageTranslator.getMessage(MessageKey.METHOD_NOT_SUPPORT));
        error.fail(Collections.singletonMap(KEY, ex.getMessage()));

        return buildResponseEntity(HttpStatus.METHOD_NOT_ALLOWED, error);
    }

    @ExceptionHandler(BusinessServiceException.class)
    public ResponseEntity<Object> handleBusinessService(BusinessServiceException ex) {
        log.info("Error BusinessServiceException", ex);
        ErrorResponse<Map<String, String>> error = 
                new ErrorResponse<>(ex.getError().getCode(), messageTranslator.getMessage(MessageKey.INTERNAL_SERVER_ERROR));
        error.fail(Collections.singletonMap(KEY, ex.getMessage()));
        return buildResponseEntity(HttpStatus.CONFLICT, error);
    }

    @ExceptionHandler(RuleGenerationException.class)
    public ResponseEntity<Object> handleRuleGeneration(RuleGenerationException ex) {
        log.info("Error RuleGenerationException", ex);
        ErrorResponse<Map<String, String>> error =
                new ErrorResponse<>(ErrorCode.RULE_GENERATION_ERROR.getCode(), messageTranslator.getMessage(MessageKey.RULE_GENERATION_ERROR));
        error.fail(Collections.singletonMap(KEY, ex.getMessage()));
        return buildResponseEntity(HttpStatus.CONFLICT, error);
    }

    @ExceptionHandler(RuleExecutionException.class)
    public ResponseEntity<Object> handleRuleExecution(RuleExecutionException ex) {
        log.info("Error RuleExecutionException", ex);
        ErrorResponse<Map<String, String>> error =
                new ErrorResponse<>(ErrorCode.RULE_EXECUTION_ERROR.getCode(), messageTranslator.getMessage(MessageKey.RULE_EXECUTION_ERROR));
        error.fail(Collections.singletonMap(KEY, ex.getMessage()));

        return buildResponseEntity(HttpStatus.CONFLICT, error);
    }

    @ExceptionHandler(RuleEngineException.class)
    public ResponseEntity<Object> handleRuleEngine(RuleEngineException ex) {
        log.info("Error RuleEngineException", ex);
        ErrorResponse<Map<String, String>> error =
                new ErrorResponse<>(ErrorCode.RULE_ENGINE_ERROR.getCode(), messageTranslator.getMessage(MessageKey.RULE_ENGINE_ERROR));
        error.fail(Collections.singletonMap(KEY, ex.getMessage()));
        return buildResponseEntity(HttpStatus.CONFLICT, error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleInternalException(Exception ex) {
        log.info("Error Exception", ex);
        ErrorResponse<Map<String, String>> error = 
                new ErrorResponse<>(ErrorCode.SERVER_ERROR.getCode(), messageTranslator.getMessage(MessageKey.INTERNAL_SERVER_ERROR));
        error.fail(Collections.singletonMap(KEY, ex.getMessage()));
        return buildResponseEntity(HttpStatus.CONFLICT, error);
    }

    private ResponseEntity<Object> buildResponseEntity(HttpStatus httpStatus, ErrorResponse<Map<String, String>> error) {
        return new ResponseEntity<>(error, httpStatus);
    }

    private ResponseEntity<Object> validationError(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        bindingResult.getAllErrors().forEach(error -> {
            String fieldName;
            try {
                fieldName = ((FieldError) error).getField();
            } catch (Exception e) {
                fieldName = KEY;
            }
            log.info("InvalidFormatException in field: " + fieldName);
            String errorMessage = error.getDefaultMessage();
            try {
                errorMessage = messageTranslator.getMessage(errorMessage);
            } catch (Exception e) {
                log.error("cant get message in message data source");
            }
            errors.put(fieldName, errorMessage);
        });
        ErrorResponse<Map<String, String>> error =
                new ErrorResponse<>(ErrorCode.BAD_REQUEST.getCode(),
                        messageTranslator.getMessage(MessageKey.HANDLE_METHOD_ARGUMENT_NOT_VALID));
        error.fail(errors);

        return buildResponseEntity(HttpStatus.BAD_REQUEST, error);
    }
}
