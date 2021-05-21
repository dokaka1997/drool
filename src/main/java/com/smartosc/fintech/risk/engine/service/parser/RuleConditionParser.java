package com.smartosc.fintech.risk.engine.service.parser;

import com.smartosc.fintech.risk.engine.common.util.DateUtils;
import com.smartosc.fintech.risk.engine.common.contant.enumeration.ConditionType;
import com.smartosc.fintech.risk.engine.common.contant.enumeration.DataType;
import com.smartosc.fintech.risk.engine.common.contant.enumeration.OperatorType;
import com.smartosc.fintech.risk.engine.dto.request.ConditionRequest;
import com.smartosc.fintech.risk.engine.dto.request.DataModelRequest;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class RuleConditionParser {
    private static final String MULTIPLE_VALUE_DELIMITER = ";";

    private final Map<String, DataType> attributes = new HashMap<>();
    private final EnumMap<DataType, List<String>> dataTypes = new EnumMap<>(DataType.class);
    private final List<ConditionRequest> listCondition;

    public RuleConditionParser(List<DataModelRequest> dataModels, List<ConditionRequest> listCondition) {
        this.listCondition = listCondition;

        dataModels.forEach(data -> data.getAttributes().forEach(attribute -> {
            String modelName = data.modelVariable() + "." + attribute.getName();
            attributes.put(modelName, attribute.getDataType());

            List<String> dataTypeValues = dataTypes.get(attribute.getDataType());
            dataTypeValues = CollectionUtils.isEmpty(dataTypeValues) ? new ArrayList<>() : dataTypeValues;
            dataTypeValues.add(modelName);
            dataTypes.put(attribute.getDataType(), dataTypeValues);
        }));
    }

    public String buildPattern() {
        ConditionRequest request = new ConditionRequest();
        request.setItems(listCondition);
        return buildPattern(request);
    }

    private String buildPattern(ConditionRequest request) {
        StringBuilder builder = new StringBuilder();
        if (CollectionUtils.isEmpty(request.getItems())) {
            builder.append(buildCondition(request.getCondition()));
            builder.append(renderConstraint(request));
            return builder.toString();
        }

        request.getItems().get(0).setCondition(null);
        builder.append(buildCondition(request.getCondition()));
        builder.append("(");
        for (ConditionRequest item : request.getItems()) {
            builder.append(buildPattern(item));
        }
        builder.append(")");
        return builder.toString();
    }

    private String buildCondition(String condition) {
        if (Objects.isNull(condition)) {
            return "";
        }

        return ConditionType.OR.name().equalsIgnoreCase(condition) ? ConditionType.OR.getValue() : ConditionType.AND.getValue();
    }

    private String renderConstraint(ConditionRequest request) {
        if (ObjectUtils.isEmpty(request.getLeft()) || ObjectUtils.isEmpty(request.getOperator())) {
            return "";
        }

        DataType dataType = Optional.ofNullable(attributes.get(request.getLeft())).orElse(DataType.OTHER);
        String left = parseLeftValue(dataType, request.getLeft());
        OperatorType operator = OperatorType.operatorOf(request.getOperator());
        switch (operator) {
            case IN:
                String rightValue = Arrays.stream(request.getRight().split(MULTIPLE_VALUE_DELIMITER))
                        .map(value -> parseRightValue(dataType, value))
                        .collect(Collectors.joining(","));
                return String.format("%s %s (%s)", left, request.getOperator(), rightValue);
            case MATCHES:
            case NOT_MATCHES:
                return String.format("%s %s \".*%s.*\"", left, request.getOperator(), request.getRight());
            case BETWEEN:
                List<String> rightValues = Arrays.stream(request.getRight().split(MULTIPLE_VALUE_DELIMITER))
                        .map(value -> parseRightValue(dataType, value))
                        .collect(Collectors.toList());
                return String.format("(%s >= %s && %s <= %s)",  left, rightValues.get(0), left, rightValues.get(1));
            default:
                return String.format("%s %s %s", left, request.getOperator(), parseRightValue(dataType, request.getRight()));
        }
    }

    private String parseLeftValue(DataType type, String value) {
        if (ObjectUtils.isEmpty(value)) {
            return "";
        }

        switch (type) {
            case DATE:
                return String.format("(%s).getTime()", value);
            case BIG_DECIMAL:
                return String.format("(%s).doubleValue()", value);
            default:
                return value;
        }
    }

    private String parseRightValue(DataType type, String value) {
        if (ObjectUtils.isEmpty(value)) {
            return "";
        }
        if (NumberUtils.isParsable(value) || value.contains("$")) {
            return value;
        }
        if (DataType.DATE.equals(type) && DateUtils.isParsable(value)) {
            return String.valueOf(DateUtils.parseToMillisecond(value));
        }

        return String.format("\"%s\"", value);
    }
}
