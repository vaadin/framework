/*
 * Copyright 2000-2014 Vaadin Ltd.
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

package com.vaadin.client.communication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.FastStringSet;
import com.vaadin.client.JsArrayObject;
import com.vaadin.client.Profiler;
import com.vaadin.client.metadata.NoDataException;
import com.vaadin.client.metadata.Property;
import com.vaadin.client.metadata.Type;
import com.vaadin.shared.Connector;

import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonType;
import elemental.json.JsonValue;

/**
 * Client side decoder for decodeing shared state and other values from JSON
 * received from the server.
 *
 * Currently, basic data types as well as Map, String[] and Object[] are
 * supported, where maps and Object[] can contain other supported data types.
 *
 * TODO extensible type support
 *
 * @since 7.0
 */
public class JsonDecoder {

    private static final FastStringSet decodedWithoutReference = FastStringSet
            .create();
    static {
        decodedWithoutReference.add(String.class.getName());
        decodedWithoutReference.add(Boolean.class.getName());
        decodedWithoutReference.add(Byte.class.getName());
        decodedWithoutReference.add(Character.class.getName());
        decodedWithoutReference.add(Short.class.getName());
        decodedWithoutReference.add(Integer.class.getName());
        decodedWithoutReference.add(Long.class.getName());
        decodedWithoutReference.add(Float.class.getName());
        decodedWithoutReference.add(Double.class.getName());
        decodedWithoutReference.add(Connector.class.getName());
        decodedWithoutReference.add(Map.class.getName());
        decodedWithoutReference.add(List.class.getName());
        decodedWithoutReference.add(Set.class.getName());
    }

    /**
     * Decode a JSON array with two elements (type and value) into a client-side
     * type, recursively if necessary.
     *
     * @param jsonValue
     *            JSON value with encoded data
     * @param connection
     *            reference to the current ApplicationConnection
     * @return decoded value (does not contain JSON types)
     */
    public static Object decodeValue(Type type, JsonValue jsonValue,
            Object target, ApplicationConnection connection) {
        String baseTypeName = type.getBaseTypeName();
        if (baseTypeName.startsWith("elemental.json.Json")) {
            return jsonValue;
        }

        // Null is null, regardless of type (except JSON)
        if (jsonValue.getType() == JsonType.NULL) {
            return null;
        }

        if (Map.class.getName().equals(baseTypeName)
                || HashMap.class.getName().equals(baseTypeName)) {
            return decodeMap(type, jsonValue, connection);
        } else if (List.class.getName().equals(baseTypeName)
                || ArrayList.class.getName().equals(baseTypeName)) {
            assert jsonValue.getType() == JsonType.ARRAY;
            return decodeList(type, (JsonArray) jsonValue, connection);
        } else if (Set.class.getName().equals(baseTypeName)) {
            assert jsonValue.getType() == JsonType.ARRAY;
            return decodeSet(type, (JsonArray) jsonValue, connection);
        } else if (String.class.getName().equals(baseTypeName)) {
            return jsonValue.asString();
        } else if (Integer.class.getName().equals(baseTypeName)) {
            return Integer.valueOf((int) jsonValue.asNumber());
        } else if (Long.class.getName().equals(baseTypeName)) {
            return Long.valueOf((long) jsonValue.asNumber());
        } else if (Float.class.getName().equals(baseTypeName)) {
            return Float.valueOf((float) jsonValue.asNumber());
        } else if (Double.class.getName().equals(baseTypeName)) {
            return Double.valueOf(jsonValue.asNumber());
        } else if (Boolean.class.getName().equals(baseTypeName)) {
            return Boolean.valueOf(jsonValue.asString());
        } else if (Byte.class.getName().equals(baseTypeName)) {
            return Byte.valueOf((byte) jsonValue.asNumber());
        } else if (Character.class.getName().equals(baseTypeName)) {
            return Character.valueOf(jsonValue.asString().charAt(0));
        } else if (Connector.class.getName().equals(baseTypeName)) {
            return ConnectorMap.get(connection).getConnector(
                    jsonValue.asString());
        } else {
            return decodeObject(type, jsonValue, target, connection);
        }
    }

    private static Object decodeObject(Type type, JsonValue jsonValue,
            Object target, ApplicationConnection connection) {
        Profiler.enter("JsonDecoder.decodeObject");
        JSONSerializer<Object> serializer = (JSONSerializer<Object>) type
                .findSerializer();
        if (serializer != null) {
            if (target != null && serializer instanceof DiffJSONSerializer<?>) {
                DiffJSONSerializer<Object> diffSerializer = (DiffJSONSerializer<Object>) serializer;
                diffSerializer.update(target, type, jsonValue, connection);
                Profiler.leave("JsonDecoder.decodeObject");
                return target;
            } else {
                Object object = serializer.deserialize(type, jsonValue,
                        connection);
                Profiler.leave("JsonDecoder.decodeObject");
                return object;
            }
        } else {
            try {
                Profiler.enter("JsonDecoder.decodeObject meta data processing");
                JsArrayObject<Property> properties = type
                        .getPropertiesAsArray();
                if (target == null) {
                    target = type.createInstance();
                }
                JsonObject jsonObject = (JsonObject) jsonValue;

                int size = properties.size();
                for (int i = 0; i < size; i++) {
                    Property property = properties.get(i);
                    if (!jsonObject.hasKey(property.getName())) {
                        continue;
                    }

                    Type propertyType = property.getType();

                    Object propertyReference;
                    if (needsReferenceValue(propertyType)) {
                        propertyReference = property.getValue(target);
                    } else {
                        propertyReference = null;
                    }

                    Profiler.leave("JsonDecoder.decodeObject meta data processing");
                    JsonValue encodedPropertyValue = jsonObject.get(property
                            .getName());
                    Object decodedValue = decodeValue(propertyType,
                            encodedPropertyValue, propertyReference, connection);
                    Profiler.enter("JsonDecoder.decodeObject meta data processing");
                    property.setValue(target, decodedValue);
                }
                Profiler.leave("JsonDecoder.decodeObject meta data processing");
                Profiler.leave("JsonDecoder.decodeObject");
                return target;
            } catch (NoDataException e) {
                Profiler.leave("JsonDecoder.decodeObject meta data processing");
                Profiler.leave("JsonDecoder.decodeObject");
                throw new RuntimeException("Can not deserialize "
                        + type.getSignature(), e);
            }
        }
    }

    private static boolean needsReferenceValue(Type type) {
        return !decodedWithoutReference.contains(type.getBaseTypeName());
    }

    private static Map<Object, Object> decodeMap(Type type, JsonValue jsonMap,
            ApplicationConnection connection) {
        // Client -> server encodes empty map as an empty array because of
        // #8906. Do the same for server -> client to maintain symmetry.
        if (jsonMap.getType() == JsonType.ARRAY) {
            JsonArray array = (JsonArray) jsonMap;
            if (array.length() == 0) {
                return new HashMap<Object, Object>();
            }
        }

        Type keyType = type.getParameterTypes()[0];
        Type valueType = type.getParameterTypes()[1];

        if (keyType.getBaseTypeName().equals(String.class.getName())) {
            assert jsonMap.getType() == JsonType.OBJECT;
            return decodeStringMap(valueType, (JsonObject) jsonMap, connection);
        } else if (keyType.getBaseTypeName().equals(Connector.class.getName())) {
            assert jsonMap.getType() == JsonType.OBJECT;
            return decodeConnectorMap(valueType, (JsonObject) jsonMap,
                    connection);
        } else {
            assert jsonMap.getType() == JsonType.ARRAY;
            return decodeObjectMap(keyType, valueType, (JsonArray) jsonMap,
                    connection);
        }
    }

    private static Map<Object, Object> decodeObjectMap(Type keyType,
            Type valueType, JsonArray jsonValue,
            ApplicationConnection connection) {
        Map<Object, Object> map = new HashMap<Object, Object>();

        JsonArray keys = jsonValue.get(0);
        JsonArray values = jsonValue.get(1);

        assert (keys.length() == values.length());

        for (int i = 0; i < keys.length(); i++) {
            Object decodedKey = decodeValue(keyType, keys.get(i), null,
                    connection);
            Object decodedValue = decodeValue(valueType, values.get(i), null,
                    connection);

            map.put(decodedKey, decodedValue);
        }

        return map;
    }

    private static Map<Object, Object> decodeConnectorMap(Type valueType,
            JsonObject jsonMap, ApplicationConnection connection) {
        Map<Object, Object> map = new HashMap<Object, Object>();

        ConnectorMap connectorMap = ConnectorMap.get(connection);

        for (String connectorId : jsonMap.keys()) {
            Object value = decodeValue(valueType, jsonMap.get(connectorId),
                    null, connection);
            map.put(connectorMap.getConnector(connectorId), value);
        }

        return map;
    }

    private static Map<Object, Object> decodeStringMap(Type valueType,
            JsonObject jsonMap, ApplicationConnection connection) {
        Map<Object, Object> map = new HashMap<Object, Object>();

        for (String key : jsonMap.keys()) {
            Object value = decodeValue(valueType, jsonMap.get(key), null,
                    connection);
            map.put(key, value);
        }

        return map;
    }

    private static List<Object> decodeList(Type type, JsonArray jsonArray,
            ApplicationConnection connection) {
        List<Object> tokens = new ArrayList<Object>();
        decodeIntoCollection(type.getParameterTypes()[0], jsonArray,
                connection, tokens);
        return tokens;
    }

    private static Set<Object> decodeSet(Type type, JsonArray jsonArray,
            ApplicationConnection connection) {
        Set<Object> tokens = new HashSet<Object>();
        decodeIntoCollection(type.getParameterTypes()[0], jsonArray,
                connection, tokens);
        return tokens;
    }

    private static void decodeIntoCollection(Type childType,
            JsonArray jsonArray, ApplicationConnection connection,
            Collection<Object> tokens) {
        for (int i = 0; i < jsonArray.length(); ++i) {
            // each entry always has two elements: type and value
            JsonValue entryValue = jsonArray.get(i);
            tokens.add(decodeValue(childType, entryValue, null, connection));
        }
    }

    /**
     * Called by generated deserialization code to treat a generic object as a
     * JsonValue. This is needed because GWT refuses to directly cast String
     * typed as Object into a JSO.
     */
    public static native <T extends JsonValue> T obj2jso(Object object)
    /*-{
        return object;
    }-*/;
}
