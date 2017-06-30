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
package com.vaadin.shared.ui.dnd.criteria;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

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
     * Declares whether all or any of the given criteria should match when
     * compared against the payload.
     */
    public enum Match {
        /**
         * When compared to the payload, the drop will be accepted if any of the
         * criteria matches.
         */
        ANY,

        /**
         * When compared to the payload, the drop will be accepted only if all
         * of the given criteria matches.
         */
        ALL
    }

    private String key;
    private String value;
    private Payload.ValueType valueType;
    private ComparisonOperator operator;

    /**
     * Mandatory zero param constructor.
     */
    private Criterion() {

    }

    /**
     * Creates a criterion object with the default comparison operator {@link
     * ComparisonOperator#EQUALS}.
     *
     * @param key
     *         key of the payload to be compared
     * @param value
     *         value of the payload to be compared
     */
    public Criterion(String key, String value) {
        this(key, value, Payload.ValueType.STRING, ComparisonOperator.EQUALS);
    }

    /**
     * Creates a criterion object.
     *
     * @param key
     *         key of the payload to be compared
     * @param operator
     *         comparison operator
     * @param value
     *         value of the payload to be compared
     */
    public Criterion(String key, ComparisonOperator operator, int value) {
        this(key, String.valueOf(value), Payload.ValueType.INTEGER, operator);
    }

    /**
     * Creates a criterion object.
     *
     * @param key
     *         key of the payload to be compared
     * @param operator
     *         comparison operator
     * @param value
     *         value of the payload to be compared
     */
    public Criterion(String key, ComparisonOperator operator, double value) {
        this(key, String.valueOf(value), Payload.ValueType.DOUBLE, operator);
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
    private Criterion(String key, String value,
            Payload.ValueType valueType, ComparisonOperator operator) {
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
     * Gets the value of the payload to be compared
     *
     * @return value of the payload to be compared
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets the type of the payload value to be compared
     *
     * @return type of the payload value to be compared
     */
    public Payload.ValueType getValueType() {
        return valueType;
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
     * Compares this criterion's value to the given payload's value and returns
     * whether the result matches the criterion's operator.
     *
     * @param payloadCollection
     *         collection of payloads to compare the criterion against
     * @return {@code false} if there exists a payload in the collection with
     * the same key and value type and it doesn't match the criterion, {@code
     * true} otherwise
     */
    public boolean resolve(Collection<Payload> payloadCollection) {
        Optional<Payload> payload = payloadCollection.stream()
                .filter(p -> p.getKey().equals(key) && p.getValueType()
                        .equals(valueType)).findAny();

        return payload.map(this::compareCriterionValue).orElse(true);
    }

    private boolean compareCriterionValue(Payload payload) {
        int result;

        switch (valueType) {
        case STRING:
        default:
            result = value.compareTo(payload.getValue());
            break;
        case INTEGER:
            result = Integer.valueOf(value)
                    .compareTo(Integer.valueOf(payload.getValue()));
            break;
        case DOUBLE:
            result = Double.valueOf(value)
                    .compareTo(Double.valueOf(payload.getValue()));
            break;
        }

        switch (operator) {
        case SMALLER_THAN:
            return result < 0;
        case SMALLER_THAN_OR_EQUALS:
            return result <= 0;
        case EQUALS:
        default:
            return result == 0;
        case GREATER_THAN_OR_EQUALS:
            return result >= 0;
        case GREATER_THAN:
            return result > 0;
        case NOT_EQUALS:
            return result != 0;
        }
    }
}
