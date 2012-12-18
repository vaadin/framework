/*
 * Copyright 2011 Vaadin Ltd.
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

package com.vaadin.server;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vaadin.shared.Connector;
import com.vaadin.shared.JsonConstants;
import com.vaadin.shared.communication.UidlValue;
import com.vaadin.ui.Component;
import com.vaadin.ui.ConnectorTracker;

/**
 * Decoder for converting RPC parameters and other values from JSON in transfer
 * between the client and the server and vice versa.
 * 
 * @since 7.0
 */
public class JsonCodec implements Serializable {

    public static interface BeanProperty {
        public Object getValue(Object bean) throws Exception;

        public void setValue(Object bean, Object value) throws Exception;

        public String getName();

        public Type getType();
    }

    private static class FieldProperty implements BeanProperty {
        private final Field field;

        public FieldProperty(Field field) {
            this.field = field;
        }

        @Override
        public Object getValue(Object bean) throws Exception {
            return field.get(bean);
        }

        @Override
        public void setValue(Object bean, Object value) throws Exception {
            field.set(bean, value);
        }

        @Override
        public String getName() {
            return field.getName();
        }

        @Override
        public Type getType() {
            return field.getGenericType();
        }

        public static Collection<FieldProperty> find(Class<?> type)
                throws IntrospectionException {
            Collection<FieldProperty> properties = new ArrayList<FieldProperty>();

            Field[] fields = type.getFields();
            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    properties.add(new FieldProperty(field));
                }
            }

            return properties;
        }

    }

    private static class MethodProperty implements BeanProperty {
        private final PropertyDescriptor pd;

        public MethodProperty(PropertyDescriptor pd) {
            this.pd = pd;
        }

        @Override
        public Object getValue(Object bean) throws Exception {
            Method readMethod = pd.getReadMethod();
            return readMethod.invoke(bean);
        }

        @Override
        public void setValue(Object bean, Object value) throws Exception {
            pd.getWriteMethod().invoke(bean, value);
        }

        @Override
        public String getName() {
            String fieldName = pd.getWriteMethod().getName().substring(3);
            fieldName = Character.toLowerCase(fieldName.charAt(0))
                    + fieldName.substring(1);
            return fieldName;
        }

        public static Collection<MethodProperty> find(Class<?> type)
                throws IntrospectionException {
            Collection<MethodProperty> properties = new ArrayList<MethodProperty>();

            for (PropertyDescriptor pd : Introspector.getBeanInfo(type)
                    .getPropertyDescriptors()) {
                if (pd.getReadMethod() == null || pd.getWriteMethod() == null) {
                    continue;
                }

                properties.add(new MethodProperty(pd));
            }
            return properties;
        }

        @Override
        public Type getType() {
            return pd.getReadMethod().getGenericReturnType();
        }

    }

    private static Map<Class<?>, String> typeToTransportType = new HashMap<Class<?>, String>();

    /**
     * Note! This does not contain primitives.
     * <p>
     */
    private static Map<String, Class<?>> transportTypeToType = new HashMap<String, Class<?>>();

    static {
        registerType(String.class, JsonConstants.VTYPE_STRING);
        registerType(Connector.class, JsonConstants.VTYPE_CONNECTOR);
        registerType(Boolean.class, JsonConstants.VTYPE_BOOLEAN);
        registerType(boolean.class, JsonConstants.VTYPE_BOOLEAN);
        registerType(Integer.class, JsonConstants.VTYPE_INTEGER);
        registerType(int.class, JsonConstants.VTYPE_INTEGER);
        registerType(Float.class, JsonConstants.VTYPE_FLOAT);
        registerType(float.class, JsonConstants.VTYPE_FLOAT);
        registerType(Double.class, JsonConstants.VTYPE_DOUBLE);
        registerType(double.class, JsonConstants.VTYPE_DOUBLE);
        registerType(Long.class, JsonConstants.VTYPE_LONG);
        registerType(long.class, JsonConstants.VTYPE_LONG);
        registerType(String[].class, JsonConstants.VTYPE_STRINGARRAY);
        registerType(Object[].class, JsonConstants.VTYPE_ARRAY);
        registerType(Map.class, JsonConstants.VTYPE_MAP);
        registerType(HashMap.class, JsonConstants.VTYPE_MAP);
        registerType(List.class, JsonConstants.VTYPE_LIST);
        registerType(Set.class, JsonConstants.VTYPE_SET);
        registerType(Void.class, JsonConstants.VTYPE_NULL);
    }

    private static void registerType(Class<?> type, String transportType) {
        typeToTransportType.put(type, transportType);
        if (!type.isPrimitive()) {
            transportTypeToType.put(transportType, type);
        }
    }

    public static boolean isInternalTransportType(String transportType) {
        return transportTypeToType.containsKey(transportType);
    }

    public static boolean isInternalType(Type type) {
        if (type instanceof Class && ((Class<?>) type).isPrimitive()) {
            if (type == byte.class || type == char.class) {
                // Almost all primitive types are handled internally
                return false;
            }
            // All primitive types are handled internally
            return true;
        } else if (type == UidlValue.class) {
            // UidlValue is a special internal type wrapping type info and a
            // value
            return true;
        }
        return typeToTransportType.containsKey(getClassForType(type));
    }

    private static Class<?> getClassForType(Type type) {
        if (type instanceof ParameterizedType) {
            return (Class<?>) (((ParameterizedType) type).getRawType());
        } else if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else {
            return null;
        }
    }

    private static Class<?> getType(String transportType) {
        return transportTypeToType.get(transportType);
    }

    public static Object decodeInternalOrCustomType(Type targetType,
            Object value, ConnectorTracker connectorTracker)
            throws JSONException {
        if (isInternalType(targetType)) {
            return decodeInternalType(targetType, false, value,
                    connectorTracker);
        } else {
            return decodeCustomType(targetType, value, connectorTracker);
        }
    }

    public static Object decodeCustomType(Type targetType, Object value,
            ConnectorTracker connectorTracker) throws JSONException {
        if (isInternalType(targetType)) {
            throw new JSONException("decodeCustomType cannot be used for "
                    + targetType + ", which is an internal type");
        }

        // Try to decode object using fields
        if (value == JSONObject.NULL) {
            return null;
        } else if (targetType == byte.class || targetType == Byte.class) {
            return Byte.valueOf(String.valueOf(value));
        } else if (targetType == char.class || targetType == Character.class) {
            return Character.valueOf(String.valueOf(value).charAt(0));
        } else if (targetType instanceof Class<?>
                && ((Class<?>) targetType).isArray()) {
            // Legacy Object[] and String[] handled elsewhere, this takes care
            // of generic arrays
            Class<?> componentType = ((Class<?>) targetType).getComponentType();
            return decodeArray(componentType, (JSONArray) value,
                    connectorTracker);
        } else if (targetType instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) targetType)
                    .getGenericComponentType();
            return decodeArray(componentType, (JSONArray) value,
                    connectorTracker);
        } else if (targetType == JSONObject.class
                || targetType == JSONArray.class) {
            return value;
        } else if (Enum.class.isAssignableFrom(getClassForType(targetType))) {
            Class<?> classForType = getClassForType(targetType);
            return decodeEnum(classForType.asSubclass(Enum.class),
                    (String) value);
        } else {
            return decodeObject(targetType, (JSONObject) value,
                    connectorTracker);
        }
    }

    private static Object decodeArray(Type componentType, JSONArray value,
            ConnectorTracker connectorTracker) throws JSONException {
        Class<?> componentClass = getClassForType(componentType);
        Object array = Array.newInstance(componentClass, value.length());
        for (int i = 0; i < value.length(); i++) {
            Object decodedValue = decodeInternalOrCustomType(componentType,
                    value.get(i), connectorTracker);
            Array.set(array, i, decodedValue);
        }
        return array;
    }

    /**
     * Decodes a value that is of an internal type.
     * <p>
     * Ensures the encoded value is of the same type as target type.
     * </p>
     * <p>
     * Allows restricting collections so that they must be declared using
     * generics. If this is used then all objects in the collection are encoded
     * using the declared type. Otherwise only internal types are allowed in
     * collections.
     * </p>
     * 
     * @param targetType
     *            The type that should be returned by this method
     * @param valueAndType
     *            The encoded value and type array
     * @param application
     *            A reference to the application
     * @param enforceGenericsInCollections
     *            true if generics should be enforce, false to only allow
     *            internal types in collections
     * @return
     * @throws JSONException
     */
    public static Object decodeInternalType(Type targetType,
            boolean restrictToInternalTypes, Object encodedJsonValue,
            ConnectorTracker connectorTracker) throws JSONException {
        if (!isInternalType(targetType)) {
            throw new JSONException("Type " + targetType
                    + " is not a supported internal type.");
        }
        String transportType = getInternalTransportType(targetType);

        if (encodedJsonValue == JSONObject.NULL) {
            return null;
        } else if (targetType == Void.class) {
            throw new JSONException(
                    "Something other than null was encoded for a null type");
        }

        // UidlValue
        if (targetType == UidlValue.class) {
            return decodeUidlValue((JSONArray) encodedJsonValue,
                    connectorTracker);
        }

        // Collections
        if (JsonConstants.VTYPE_LIST.equals(transportType)) {
            return decodeList(targetType, restrictToInternalTypes,
                    (JSONArray) encodedJsonValue, connectorTracker);
        } else if (JsonConstants.VTYPE_SET.equals(transportType)) {
            return decodeSet(targetType, restrictToInternalTypes,
                    (JSONArray) encodedJsonValue, connectorTracker);
        } else if (JsonConstants.VTYPE_MAP.equals(transportType)) {
            return decodeMap(targetType, restrictToInternalTypes,
                    encodedJsonValue, connectorTracker);
        }

        // Arrays
        if (JsonConstants.VTYPE_ARRAY.equals(transportType)) {

            return decodeObjectArray(targetType, (JSONArray) encodedJsonValue,
                    connectorTracker);

        } else if (JsonConstants.VTYPE_STRINGARRAY.equals(transportType)) {
            return decodeStringArray((JSONArray) encodedJsonValue);
        }

        // Special Vaadin types

        String stringValue = String.valueOf(encodedJsonValue);

        if (JsonConstants.VTYPE_CONNECTOR.equals(transportType)) {
            return connectorTracker.getConnector(stringValue);
        }

        // Legacy types

        if (JsonConstants.VTYPE_STRING.equals(transportType)) {
            return stringValue;
        } else if (JsonConstants.VTYPE_INTEGER.equals(transportType)) {
            return Integer.valueOf(stringValue);
        } else if (JsonConstants.VTYPE_LONG.equals(transportType)) {
            return Long.valueOf(stringValue);
        } else if (JsonConstants.VTYPE_FLOAT.equals(transportType)) {
            return Float.valueOf(stringValue);
        } else if (JsonConstants.VTYPE_DOUBLE.equals(transportType)) {
            return Double.valueOf(stringValue);
        } else if (JsonConstants.VTYPE_BOOLEAN.equals(transportType)) {
            return Boolean.valueOf(stringValue);
        }

        throw new JSONException("Unknown type " + transportType);
    }

    private static UidlValue decodeUidlValue(JSONArray encodedJsonValue,
            ConnectorTracker connectorTracker) throws JSONException {
        String type = encodedJsonValue.getString(0);

        Object decodedValue = decodeInternalType(getType(type), true,
                encodedJsonValue.get(1), connectorTracker);
        return new UidlValue(decodedValue);
    }

    private static boolean transportTypesCompatible(
            String encodedTransportType, String transportType) {
        if (encodedTransportType == null) {
            return false;
        }
        if (encodedTransportType.equals(transportType)) {
            return true;
        }
        if (encodedTransportType.equals(JsonConstants.VTYPE_NULL)) {
            return true;
        }

        return false;
    }

    private static Map<Object, Object> decodeMap(Type targetType,
            boolean restrictToInternalTypes, Object jsonMap,
            ConnectorTracker connectorTracker) throws JSONException {
        if (jsonMap instanceof JSONArray) {
            // Client-side has no declared type information to determine
            // encoding method for empty maps, so these are handled separately.
            // See #8906.
            JSONArray jsonArray = (JSONArray) jsonMap;
            if (jsonArray.length() == 0) {
                return new HashMap<Object, Object>();
            }
        }

        if (!restrictToInternalTypes && targetType instanceof ParameterizedType) {
            Type keyType = ((ParameterizedType) targetType)
                    .getActualTypeArguments()[0];
            Type valueType = ((ParameterizedType) targetType)
                    .getActualTypeArguments()[1];
            if (keyType == String.class) {
                return decodeStringMap(valueType, (JSONObject) jsonMap,
                        connectorTracker);
            } else if (keyType == Connector.class) {
                return decodeConnectorMap(valueType, (JSONObject) jsonMap,
                        connectorTracker);
            } else {
                return decodeObjectMap(keyType, valueType, (JSONArray) jsonMap,
                        connectorTracker);
            }
        } else {
            return decodeStringMap(UidlValue.class, (JSONObject) jsonMap,
                    connectorTracker);
        }
    }

    private static Map<Object, Object> decodeObjectMap(Type keyType,
            Type valueType, JSONArray jsonMap, ConnectorTracker connectorTracker)
            throws JSONException {
        Map<Object, Object> map = new HashMap<Object, Object>();

        JSONArray keys = jsonMap.getJSONArray(0);
        JSONArray values = jsonMap.getJSONArray(1);

        assert (keys.length() == values.length());

        for (int i = 0; i < keys.length(); i++) {
            Object key = decodeInternalOrCustomType(keyType, keys.get(i),
                    connectorTracker);
            Object value = decodeInternalOrCustomType(valueType, values.get(i),
                    connectorTracker);

            map.put(key, value);
        }

        return map;
    }

    private static Map<Object, Object> decodeConnectorMap(Type valueType,
            JSONObject jsonMap, ConnectorTracker connectorTracker)
            throws JSONException {
        Map<Object, Object> map = new HashMap<Object, Object>();

        for (Iterator<?> iter = jsonMap.keys(); iter.hasNext();) {
            String key = (String) iter.next();
            Object value = decodeInternalOrCustomType(valueType,
                    jsonMap.get(key), connectorTracker);
            if (valueType == UidlValue.class) {
                value = ((UidlValue) value).getValue();
            }
            map.put(connectorTracker.getConnector(key), value);
        }

        return map;
    }

    private static Map<Object, Object> decodeStringMap(Type valueType,
            JSONObject jsonMap, ConnectorTracker connectorTracker)
            throws JSONException {
        Map<Object, Object> map = new HashMap<Object, Object>();

        for (Iterator<?> iter = jsonMap.keys(); iter.hasNext();) {
            String key = (String) iter.next();
            Object value = decodeInternalOrCustomType(valueType,
                    jsonMap.get(key), connectorTracker);
            if (valueType == UidlValue.class) {
                value = ((UidlValue) value).getValue();
            }
            map.put(key, value);
        }

        return map;
    }

    /**
     * @param targetType
     * @param restrictToInternalTypes
     * @param typeIndex
     *            The index of a generic type to use to define the child type
     *            that should be decoded
     * @param encodedValueAndType
     * @param application
     * @return
     * @throws JSONException
     */
    private static Object decodeParametrizedType(Type targetType,
            boolean restrictToInternalTypes, int typeIndex, Object value,
            ConnectorTracker connectorTracker) throws JSONException {
        if (!restrictToInternalTypes && targetType instanceof ParameterizedType) {
            Type childType = ((ParameterizedType) targetType)
                    .getActualTypeArguments()[typeIndex];
            // Only decode the given type
            return decodeInternalOrCustomType(childType, value,
                    connectorTracker);
        } else {
            // Only UidlValue when not enforcing a given type to avoid security
            // issues
            UidlValue decodeInternalType = (UidlValue) decodeInternalType(
                    UidlValue.class, true, value, connectorTracker);
            return decodeInternalType.getValue();
        }
    }

    private static Object decodeEnum(Class<? extends Enum> cls, String value) {
        return Enum.valueOf(cls, value);
    }

    private static String[] decodeStringArray(JSONArray jsonArray)
            throws JSONException {
        int length = jsonArray.length();
        List<String> tokens = new ArrayList<String>(length);
        for (int i = 0; i < length; ++i) {
            tokens.add(jsonArray.getString(i));
        }
        return tokens.toArray(new String[tokens.size()]);
    }

    private static Object[] decodeObjectArray(Type targetType,
            JSONArray jsonArray, ConnectorTracker connectorTracker)
            throws JSONException {
        List list = decodeList(List.class, true, jsonArray, connectorTracker);
        return list.toArray(new Object[list.size()]);
    }

    private static List<Object> decodeList(Type targetType,
            boolean restrictToInternalTypes, JSONArray jsonArray,
            ConnectorTracker connectorTracker) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            // each entry always has two elements: type and value
            Object encodedValue = jsonArray.get(i);
            Object decodedChild = decodeParametrizedType(targetType,
                    restrictToInternalTypes, 0, encodedValue, connectorTracker);
            list.add(decodedChild);
        }
        return list;
    }

    private static Set<Object> decodeSet(Type targetType,
            boolean restrictToInternalTypes, JSONArray jsonArray,
            ConnectorTracker connectorTracker) throws JSONException {
        HashSet<Object> set = new HashSet<Object>();
        set.addAll(decodeList(targetType, restrictToInternalTypes, jsonArray,
                connectorTracker));
        return set;
    }

    private static Object decodeObject(Type targetType,
            JSONObject serializedObject, ConnectorTracker connectorTracker)
            throws JSONException {

        Class<?> targetClass = getClassForType(targetType);

        try {
            Object decodedObject = targetClass.newInstance();
            for (BeanProperty property : getProperties(targetClass)) {

                String fieldName = property.getName();
                Object encodedFieldValue = serializedObject.get(fieldName);
                Type fieldType = property.getType();
                Object decodedFieldValue = decodeInternalOrCustomType(
                        fieldType, encodedFieldValue, connectorTracker);

                property.setValue(decodedObject, decodedFieldValue);
            }

            return decodedObject;
        } catch (Exception e) {
            throw new JSONException(e);
        }
    }

    public static EncodeResult encode(Object value, Object diffState,
            Type valueType, ConnectorTracker connectorTracker)
            throws JSONException {

        if (valueType == null) {
            throw new IllegalArgumentException("type must be defined");
        }

        if (valueType instanceof WildcardType) {
            throw new IllegalStateException(
                    "Can not serialize type with wildcard: " + valueType);
        }

        if (null == value) {
            return encodeNull();
        }

        if (value instanceof String[]) {
            String[] array = (String[]) value;
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < array.length; ++i) {
                jsonArray.put(array[i]);
            }
            return new EncodeResult(jsonArray);
        } else if (value instanceof String) {
            return new EncodeResult(value);
        } else if (value instanceof Boolean) {
            return new EncodeResult(value);
        } else if (value instanceof Number) {
            return new EncodeResult(value);
        } else if (value instanceof Character) {
            // Character is not a Number
            return new EncodeResult(value);
        } else if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            JSONArray jsonArray = encodeCollection(valueType, collection,
                    connectorTracker);
            return new EncodeResult(jsonArray);
        } else if (valueType instanceof Class<?>
                && ((Class<?>) valueType).isArray()) {
            JSONArray jsonArray = encodeArrayContents(
                    ((Class<?>) valueType).getComponentType(), value,
                    connectorTracker);
            return new EncodeResult(jsonArray);
        } else if (valueType instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) valueType)
                    .getGenericComponentType();
            JSONArray jsonArray = encodeArrayContents(componentType, value,
                    connectorTracker);
            return new EncodeResult(jsonArray);
        } else if (value instanceof Map) {
            Object jsonMap = encodeMap(valueType, (Map<?, ?>) value,
                    connectorTracker);
            return new EncodeResult(jsonMap);
        } else if (value instanceof Connector) {
            Connector connector = (Connector) value;
            if (value instanceof Component
                    && !(AbstractCommunicationManager
                            .isComponentVisibleToClient((Component) value))) {
                return encodeNull();
            }
            return new EncodeResult(connector.getConnectorId());
        } else if (value instanceof Enum) {
            return encodeEnum((Enum<?>) value, connectorTracker);
        } else if (value instanceof JSONArray || value instanceof JSONObject) {
            return new EncodeResult(value);
        } else if (valueType instanceof Class<?>) {
            // Any object that we do not know how to encode we encode by looping
            // through fields
            return encodeObject(value, (Class<?>) valueType,
                    (JSONObject) diffState, connectorTracker);
        } else {
            throw new JSONException("Can not encode " + valueType);
        }
    }

    private static EncodeResult encodeNull() {
        return new EncodeResult(JSONObject.NULL);
    }

    public static Collection<BeanProperty> getProperties(Class<?> type)
            throws IntrospectionException {
        Collection<BeanProperty> properties = new ArrayList<BeanProperty>();

        properties.addAll(MethodProperty.find(type));
        properties.addAll(FieldProperty.find(type));

        return properties;
    }

    private static EncodeResult encodeObject(Object value, Class<?> valueType,
            JSONObject referenceValue, ConnectorTracker connectorTracker)
            throws JSONException {
        JSONObject encoded = new JSONObject();
        JSONObject diff = new JSONObject();

        try {
            for (BeanProperty property : getProperties(valueType)) {
                String fieldName = property.getName();
                // We can't use PropertyDescriptor.getPropertyType() as it does
                // not support generics
                Type fieldType = property.getType();
                Object fieldValue = property.getValue(value);

                if (encoded.has(fieldName)) {
                    throw new RuntimeException(
                            "Can't encode "
                                    + valueType.getName()
                                    + " as it has multiple properties with the name "
                                    + fieldName.toLowerCase()
                                    + ". This can happen if there are getters and setters for a public field (the framework can't know which to ignore) or if there are properties with only casing distinguishing between the names (e.g. getFoo() and getFOO())");
                }

                Object fieldReference;
                if (referenceValue != null) {
                    fieldReference = referenceValue.get(fieldName);
                    if (JSONObject.NULL.equals(fieldReference)) {
                        fieldReference = null;
                    }
                } else {
                    fieldReference = null;
                }

                EncodeResult encodeResult = encode(fieldValue, fieldReference,
                        fieldType, connectorTracker);
                encoded.put(fieldName, encodeResult.getEncodedValue());

                if (!jsonEquals(encodeResult.getEncodedValue(), fieldReference)) {
                    diff.put(fieldName, encodeResult.getDiffOrValue());
                }
            }
        } catch (Exception e) {
            // TODO: Should exceptions be handled in a different way?
            throw new JSONException(e);
        }
        return new EncodeResult(encoded, diff);
    }

    /**
     * Compares the value with the reference. If they match, returns true.
     * 
     * @param fieldValue
     * @param referenceValue
     * @return
     */
    private static boolean jsonEquals(Object fieldValue, Object referenceValue) {
        if (fieldValue == JSONObject.NULL) {
            fieldValue = null;
        }
        if (referenceValue == JSONObject.NULL) {
            referenceValue = null;
        }

        if (fieldValue == referenceValue) {
            return true;
        } else if (fieldValue == null || referenceValue == null) {
            return false;
        } else {
            return fieldValue.toString().equals(referenceValue.toString());
        }
    }

    private static EncodeResult encodeEnum(Enum<?> e,
            ConnectorTracker connectorTracker) throws JSONException {
        return new EncodeResult(e.name());
    }

    private static JSONArray encodeArrayContents(Type componentType,
            Object array, ConnectorTracker connectorTracker)
            throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < Array.getLength(array); i++) {
            EncodeResult encodeResult = encode(Array.get(array, i), null,
                    componentType, connectorTracker);
            jsonArray.put(encodeResult.getEncodedValue());
        }
        return jsonArray;
    }

    private static JSONArray encodeCollection(Type targetType,
            Collection collection, ConnectorTracker connectorTracker)
            throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (Object o : collection) {
            jsonArray.put(encodeChild(targetType, 0, o, connectorTracker));
        }
        return jsonArray;
    }

    private static Object encodeChild(Type targetType, int typeIndex, Object o,
            ConnectorTracker connectorTracker) throws JSONException {
        if (targetType instanceof ParameterizedType) {
            Type childType = ((ParameterizedType) targetType)
                    .getActualTypeArguments()[typeIndex];
            // Encode using the given type
            EncodeResult encodeResult = encode(o, null, childType,
                    connectorTracker);
            return encodeResult.getEncodedValue();
        } else {
            throw new JSONException("Collection is missing generics");
        }
    }

    private static Object encodeMap(Type mapType, Map<?, ?> map,
            ConnectorTracker connectorTracker) throws JSONException {
        Type keyType, valueType;

        if (mapType instanceof ParameterizedType) {
            keyType = ((ParameterizedType) mapType).getActualTypeArguments()[0];
            valueType = ((ParameterizedType) mapType).getActualTypeArguments()[1];
        } else {
            throw new JSONException("Map is missing generics");
        }

        if (map.isEmpty()) {
            // Client -> server encodes empty map as an empty array because of
            // #8906. Do the same for server -> client to maintain symmetry.
            return new JSONArray();
        }

        if (keyType == String.class) {
            return encodeStringMap(valueType, map, connectorTracker);
        } else if (keyType == Connector.class) {
            return encodeConnectorMap(valueType, map, connectorTracker);
        } else {
            return encodeObjectMap(keyType, valueType, map, connectorTracker);
        }
    }

    private static JSONArray encodeObjectMap(Type keyType, Type valueType,
            Map<?, ?> map, ConnectorTracker connectorTracker)
            throws JSONException {
        JSONArray keys = new JSONArray();
        JSONArray values = new JSONArray();

        for (Entry<?, ?> entry : map.entrySet()) {
            EncodeResult encodedKey = encode(entry.getKey(), null, keyType,
                    connectorTracker);
            EncodeResult encodedValue = encode(entry.getValue(), null,
                    valueType, connectorTracker);

            keys.put(encodedKey.getEncodedValue());
            values.put(encodedValue.getEncodedValue());
        }

        return new JSONArray(Arrays.asList(keys, values));
    }

    private static JSONObject encodeConnectorMap(Type valueType, Map<?, ?> map,
            ConnectorTracker connectorTracker) throws JSONException {
        JSONObject jsonMap = new JSONObject();

        for (Entry<?, ?> entry : map.entrySet()) {
            ClientConnector key = (ClientConnector) entry.getKey();
            if (AbstractCommunicationManager.isConnectorVisibleToClient(key)) {
                EncodeResult encodedValue = encode(entry.getValue(), null,
                        valueType, connectorTracker);
                jsonMap.put(key.getConnectorId(),
                        encodedValue.getEncodedValue());
            }
        }

        return jsonMap;
    }

    private static JSONObject encodeStringMap(Type valueType, Map<?, ?> map,
            ConnectorTracker connectorTracker) throws JSONException {
        JSONObject jsonMap = new JSONObject();

        for (Entry<?, ?> entry : map.entrySet()) {
            String key = (String) entry.getKey();
            EncodeResult encodedValue = encode(entry.getValue(), null,
                    valueType, connectorTracker);
            jsonMap.put(key, encodedValue.getEncodedValue());
        }

        return jsonMap;
    }

    /**
     * Gets the transport type for the given class. Returns null if no transport
     * type can be found.
     * 
     * @param valueType
     *            The type that should be transported
     * @return
     * @throws JSONException
     */
    private static String getInternalTransportType(Type valueType) {
        return typeToTransportType.get(getClassForType(valueType));
    }

    private static String getCustomTransportType(Class<?> targetType) {
        return targetType.getName();
    }

}
