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

package com.vaadin.data.util;

import java.util.AbstractList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonNumber;
import elemental.json.JsonObject;
import elemental.json.JsonType;
import elemental.json.JsonValue;

/**
 * Helpers for using <code>elemental.json</code>.
 *
 * @author Vaadin Ltd
 */
public class JsonUtil {

    /**
     * Collects a stream of JSON values to a JSON array.
     */
    private static final class JsonArrayCollector
            implements Collector<JsonValue, JsonArray, JsonArray> {
        @Override
        public Supplier<JsonArray> supplier() {
            return Json::createArray;
        }

        @Override
        public BiConsumer<JsonArray, JsonValue> accumulator() {
            return (array, value) -> array.set(array.length(), value);
        }

        @Override
        public BinaryOperator<JsonArray> combiner() {
            return (left, right) -> {
                for (int i = 0; i < right.length(); i++) {
                    left.set(left.length(), right.<JsonValue> get(i));
                }
                return left;
            };
        }

        @Override
        public Function<JsonArray, JsonArray> finisher() {
            return Function.identity();
        }

        @Override
        public Set<Collector.Characteristics> characteristics() {
            return ARRAY_COLLECTOR_CHARACTERISTICS;
        }
    }

    private static final Set<Collector.Characteristics> ARRAY_COLLECTOR_CHARACTERISTICS = Collections
            .unmodifiableSet(
                    EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));

    private JsonUtil() {
        // Static-only class
    }

    /**
     * Compares two JSON values for deep equality.
     * <p>
     * This is a helper for overcoming the fact that {@link JsonValue} doesn't
     * override {@link Object#equals(Object)} and
     * {@link JsonValue#jsEquals(JsonValue)} is defined to use JavaScript
     * semantics where arrays and objects are equals only based on identity.
     *
     * @param a
     *            the first JSON value to check, may not be null
     * @param b
     *            the second JSON value to check, may not be null
     * @return <code>true</code> if both JSON values are the same;
     *         <code>false</code> otherwise
     */
    public static boolean jsonEquals(JsonValue a, JsonValue b) {
        assert a != null;
        assert b != null;

        if (a == b) {
            return true;
        }

        JsonType type = a.getType();
        if (type != b.getType()) {
            return false;
        }

        switch (type) {
        case NULL:
            return true;
        case BOOLEAN:
            return a.asBoolean() == b.asBoolean();
        case NUMBER:
            return Double.doubleToRawLongBits(a.asNumber()) == Double
                    .doubleToRawLongBits(b.asNumber());
        case STRING:
            return a.asString().equals(b.asString());
        case OBJECT:
            return jsonObjectEquals((JsonObject) a, (JsonObject) b);
        case ARRAY:
            return jsonArrayEquals((JsonArray) a, (JsonArray) b);
        default:
            throw new IllegalArgumentException("Unsupported JsonType: " + type);
        }
    }

    private static boolean jsonObjectEquals(JsonObject a, JsonObject b) {
        assert a != null;
        assert b != null;

        if (a == b) {
            return true;
        }

        String[] keys = a.keys();

        if (keys.length != b.keys().length) {
            return false;
        }

        for (String key : keys) {
            JsonValue value = b.get(key);
            if (value == null || !jsonEquals(a.get(key), value)) {
                return false;
            }
        }

        return true;
    }

    private static boolean jsonArrayEquals(JsonArray a, JsonArray b) {
        assert a != null;
        assert b != null;

        if (a == b) {
            return true;
        }

        if (a.length() != b.length()) {
            return false;
        }
        for (int i = 0; i < a.length(); i++) {
            if (!jsonEquals(a.get(i), b.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a stream from a JSON array.
     *
     * @param array
     *            the JSON array to create a stream from
     * @return a stream of JSON values
     */
    public static <T extends JsonValue> Stream<T> stream(JsonArray array) {
        assert array != null;
        return new AbstractList<T>() {
            @Override
            public T get(int index) {
                return array.get(index);
            }

            @Override
            public int size() {
                return array.length();
            }
        }.stream();
    }

    /**
     * Creates a stream from a JSON array of objects. This method does not
     * verify that all items in the array are actually JSON objects instead of
     * some other JSON type.
     *
     * @param array
     *            the JSON array to create a stream from
     * @return a stream of JSON objects
     */
    public static Stream<JsonObject> objectStream(JsonArray array) {
        return stream(array);
    }

    /**
     * Creates a double stream from a JSON array of numbers. This method does
     * not verify that all items in the array are actually JSON numbers instead
     * of some other JSON type.
     *
     * @param array
     *            the JSON array to create a stream from
     * @return a double stream of the values in the array
     */
    public static DoubleStream numberStream(JsonArray array) {
        return JsonUtil.<JsonNumber> stream(array)
                .mapToDouble(JsonNumber::getNumber);
    }

    /**
     * Creates a collector that collects values into a JSON array.
     *
     * @return the collector
     */
    public static Collector<JsonValue, ?, JsonArray> asArray() {
        return new JsonArrayCollector();
    }

    /**
     * Creates a new JSON array with the given values.
     *
     * @param values
     *            the values that should be in the created array
     * @return the created array
     */
    public static JsonArray createArray(JsonValue... values) {
        return Stream.of(values).collect(asArray());
    }
}
