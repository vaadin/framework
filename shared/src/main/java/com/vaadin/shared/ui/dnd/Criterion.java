/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.shared.ui.dnd;

import java.io.Serializable;

/**
 * Stores parameters for the drag and drop acceptance criterion defined using
 * the criteria API.
 * <p>
 * When data is dragged over a drop target, the value here is compared to the
 * payload added in DropTargetExtension with same key and value type.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
public class Criterion implements Serializable {

    /**
     * Prefix for payload stored in data transfer object.
     */
    public static final String ITEM_PREFIX = "v-item";

    /**
     * Type name for payload value of type String.
     */
    public static final String VALUE_TYPE_STRING = "string";

    /**
     * Type name for payload value of type Integer.
     */
    public static final String VALUE_TYPE_INTEGER = "integer";

    /**
     * Type name for payload value of type Double.
     */
    public static final String VALUE_TYPE_DOUBLE = "double";

    private String key;
    private String value;
    private String valueType;
    private ComparisonOperator operator;

    /**
     * Mandatory zero param constructor.
     */
    private Criterion() {

    }

    /**
     * Creates a criterion object.
     *
     * @param key
     *         key of the payload to be compared
     * @param value
     *         value of the payload to be compared
     * @param valueType
     *         type of the payload to be compared
     */
    public Criterion(String key, String value, String valueType) {
        this(key, value, valueType, ComparisonOperator.EQUALS);
    }

    /**
     * Creates a criterion object.
     *
     * @param key
     *         key of the payload to be compared
     * @param value
     *         value of the payload to be compared
     * @param valueType
     *         type of the payload to be compared
     * @param operator
     *         comparison operator
     */
    public Criterion(String key, String value, String valueType,
            ComparisonOperator operator) {
        this.key = key;
        this.value = value;
        this.valueType = valueType;
        this.operator = operator;
    }

    /**
     * Gets the key of the payload to be compared
     *
     * @return key of the payload to be compared
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key of the payload to be compared
     *
     * @param key
     *         key of the payload to be compared
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the value of the payload to be compared
     *
     * @return value of the payload to be compared
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the payload to be compared
     *
     * @param value
     *         value of the payload to be compared
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the type of the payload value to be compared
     *
     * @return type of the payload value to be compared
     */
    public String getValueType() {
        return valueType;
    }

    /**
     * Sets the type of the payload value to be compared
     *
     * @param valueType
     *         type of the payload value to be compared
     */
    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    /**
     * Gets the comparison operator.
     *
     * @return operator to be used when comparing payload value with criterion
     */
    public ComparisonOperator getOperator() {
        return operator;
    }

    /**
     * Sets the comparison operator.
     *
     * @param operator
     *         operator to be used when comparing payload value with criterion
     */
    public void setOperator(ComparisonOperator operator) {
        this.operator = operator;
    }

    /**
     * Gets the prefix of the payload string to be set as data type in the data
     * transfer object.
     *
     * @return start of the payload string, not containing the value of the
     * payload, e.g. {@code "v-item:string:key"}
     */
    public String getTypeNamePrefix() {
        return ITEM_PREFIX + ":" + valueType + ":" + key;
    }

    /**
     * Gets the payload string to be set as data type in the data transfer
     * object.
     *
     * @return payload string as transferred in data transfer object's data
     * type, e.g. {@code "v-item:string:key:value"}
     */
    public String getTypeName() {
        return getTypeNamePrefix() + ":" + value;
    }
}
