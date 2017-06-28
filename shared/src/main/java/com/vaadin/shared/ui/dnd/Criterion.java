package com.vaadin.shared.ui.dnd;

import java.io.Serializable;

public class Criterion implements Serializable {

    public static final String ITEM_PREFIX = "v-item";

    public static final String VALUE_TYPE_STRING = "string";
    public static final String VALUE_TYPE_INTEGER = "integer";
    public static final String VALUE_TYPE_DOUBLE = "double";

    private String key;
    private String value;
    private String valueType;
    private CriterionOperator operator;

    private Criterion() {

    }

    public Criterion(String key, String value, String valueType) {
        this(key, value, valueType, CriterionOperator.EQUALS);
    }

    public Criterion(String key, String value, String valueType,
            CriterionOperator operator) {
        this.key = key;
        this.value = value;
        this.valueType = valueType;
        this.operator = operator;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public CriterionOperator getOperator() {
        return operator;
    }

    public void setOperator(CriterionOperator operator) {
        this.operator = operator;
    }

    public String getTypeNamePrefix() {
        return ITEM_PREFIX + ":" + valueType + ":" + key;
    }

    public String getTypeName() {
        return getTypeNamePrefix() + ":" + value;
    }
}
