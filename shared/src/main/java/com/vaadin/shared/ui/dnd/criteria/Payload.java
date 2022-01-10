/*
 * Copyright 2000-2022 Vaadin Ltd.
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
import java.util.Locale;

/**
 * Stores key/value pairs and the value type. Payload is set in
 * DragSourceExtension and is transferred during drag operation. It is used for
 * comparing values to acceptance criteria.
 *
 * @since 8.1
 */
public class Payload implements Serializable {

    /**
     * Type of the payload's value.
     */
    public enum ValueType {
        STRING, INTEGER, DOUBLE;
    }

    /**
     * Prefix of the payload data type.
     */
    public static final String ITEM_PREFIX = "v-item";

    private String key;
    private String value;
    private ValueType valueType;

    /**
     * Mandatory zero arg constructor.
     */
    private Payload() {

    }

    /**
     * Creates a payload object.
     *
     * @param key
     *            key of the payload
     * @param value
     *            value of the payload
     * @param valueType
     *            type of the payload value
     */
    public Payload(String key, String value, ValueType valueType) {
        this.key = key;
        this.value = value;
        this.valueType = valueType;
    }

    /**
     * Gets the key of this payload.
     *
     * @return key identifying this payload
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key of this payload.
     *
     * @param key
     *            key that identifies the payload
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the value of this payload.
     *
     * @return value of this payload
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of this payload.
     *
     * @param value
     *            value of the payload
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value type of this payload.
     *
     * @return the type of the value of this payload
     */
    public ValueType getValueType() {
        return valueType;
    }

    /**
     * Sets the value type of this payload.
     *
     * @param valueType
     *            type of the payload value
     */
    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    /**
     * Returns the string representation of this payload. It is used as the data
     * type in the {@code DataTransfer} object.
     *
     * @return the string representation of this payload
     */
    public String getPayloadString() {
        return ITEM_PREFIX + ":" + valueType.name().toLowerCase(Locale.ROOT)
                + ":" + key + ":" + value;
    }

    /**
     * Parses a payload string and returns a payload object represented by that
     * string.
     *
     * @param payloadString
     *            string that represents a payload object
     * @return a payload object represented by the given string
     */
    public static Payload parse(String payloadString) {
        String[] parts = payloadString.split(":");

        if (parts.length != 4 || !ITEM_PREFIX.equals(parts[0])) {
            throw new IllegalArgumentException(
                    "Data type does not have a valid payload format");
        }

        // Create payload object of the given parts. Value type is converted to
        // upper case to match the enum's case.
        return new Payload(parts[2], parts[3],
                ValueType.valueOf(parts[1].toUpperCase(Locale.ROOT)));
    }
}
