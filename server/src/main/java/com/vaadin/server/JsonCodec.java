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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.vaadin.server.communication.DateSerializer;
import com.vaadin.server.communication.JSONSerializer;
import com.vaadin.shared.Connector;
import com.vaadin.shared.JsonConstants;
import com.vaadin.shared.communication.UidlValue;
import com.vaadin.ui.Component;
import com.vaadin.ui.ConnectorTracker;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonException;
import elemental.json.JsonNull;
import elemental.json.JsonObject;
import elemental.json.JsonString;
import elemental.json.JsonType;
import elemental.json.JsonValue;
import elemental.json.impl.JreJsonArray;

/**
 * Decoder for converting RPC parameters and other values from JSON in transfer
 * between the client and the server and vice versa.
 *
 * @since 7.0
 */
public class JsonCodec implements Serializable {

    /* Immutable Encode Result representing null */
    private static final EncodeResult ENCODE_RESULT_NULL = new EncodeResult(
            Json.createNull());

    /* Immutable empty JSONArray */
    private static final JsonArray EMPTY_JSON_ARRAY = new JreJsonArray(
            Json.instance()) {
        @Override
        public void set(int index, JsonValue value) {
            throw new UnsupportedOperationException(
                    "Immutable empty JsonArray.");
        }

        @Override
        public void set(int index, String string) {
            throw new UnsupportedOperationException(
                    "Immutable empty JsonArray.");
        }

        @Override
        public void set(int index, double number) {
            throw new UnsupportedOperationException(
                    "Immutable empty JsonArray.");
        }

        @Override
        public void set(int index, boolean bool) {
            throw new UnsupportedOperationException(
                    "Immutable empty JsonArray.");
        }
    };

    public static interface BeanProperty extends Serializable {
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
            Field[] fields = type.getFields();
            Collection<FieldProperty> properties = new ArrayList<FieldProperty>(
                    fields.length);
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

    /**
     * Cache the collection of bean properties for a given type to avoid doing a
     * quite expensive lookup multiple times. Will be used from any thread that
     * happens to process Vaadin requests, so it must be protected from
     * corruption caused by concurrent access.
     */
    private static ConcurrentMap<Class<?>, Collection<BeanProperty>> typePropertyCache = new ConcurrentHashMap<Class<?>, Collection<BeanProperty>>();

    private static Map<Class<?>, String> typeToTransportType = new HashMap<Class<?>, String>();

    /**
     * Note! This does not contain primitives.
     * <p>
     */
    private static Map<String, Class<?>> transportTypeToType = new HashMap<String, Class<?>>();

    private static Map<Class<?>, JSONSerializer<?>> customSerializers = new HashMap<Class<?>, JSONSerializer<?>>();
    static {
        customSerializers.put(Date.class, new DateSerializer());
    }

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
            JsonValue value, ConnectorTracker connectorTracker) {
        if (isInternalType(targetType)) {
            return decodeInternalType(targetType, false, value,
                    connectorTracker);
        } else {
            return decodeCustomType(targetType, value, connectorTracker);
        }
    }

    public static Object decodeCustomType(Type targetType, JsonValue value,
            ConnectorTracker connectorTracker) {
        if (isInternalType(targetType)) {
            throw new JsonException("decodeCustomType cannot be used for "
                    + targetType + ", which is an internal type");
        }

        // Try to decode object using fields
        if (isJsonType(targetType)) {
            return value;
        } else if (value.getType() == JsonType.NULL) {
            return null;
        } else if (targetType == byte.class || targetType == Byte.class) {
            return Byte.valueOf((byte) value.asNumber());
        } else if (targetType == char.class || targetType == Character.class) {
            return Character.valueOf(value.asString().charAt(0));
        } else if (targetType instanceof Class<?>
                && ((Class<?>) targetType).isArray()) {
            // Legacy Object[] and String[] handled elsewhere, this takes care
            // of generic arrays
            Class<?> componentType = ((Class<?>) targetType).getComponentType();
            return decodeArray(componentType, (JsonArray) value,
                    connectorTracker);
        } else if (targetType instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) targetType)
                    .getGenericComponentType();
            return decodeArray(componentType, (JsonArray) value,
                    connectorTracker);
        } else if (JsonValue.class
                .isAssignableFrom(getClassForType(targetType))) {
            return value;
        } else if (Enum.class.isAssignableFrom(getClassForType(targetType))) {
            Class<?> classForType = getClassForType(targetType);
            return decodeEnum(classForType.asSubclass(Enum.class),
                    (JsonString) value);
        } else if (customSerializers.containsKey(getClassForType(targetType))) {
            return customSerializers.get(getClassForType(targetType))
                    .deserialize(targetType, value, connectorTracker);
        } else {
            return decodeObject(targetType, (JsonObject) value,
                    connectorTracker);
        }
    }

    private static boolean isJsonType(Type type) {
        return type instanceof Class<?>
                && JsonValue.class.isAssignableFrom((Class<?>) type);
    }

    private static Object decodeArray(Type componentType, JsonArray value,
            ConnectorTracker connectorTracker) {
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
     */
    public static Object decodeInternalType(Type targetType,
            boolean restrictToInternalTypes, JsonValue encodedJsonValue,
            ConnectorTracker connectorTracker) {
        if (!isInternalType(targetType)) {
            throw new JsonException("Type " + targetType
                    + " is not a supported internal type.");
        }
        String transportType = getInternalTransportType(targetType);

        if (encodedJsonValue.getType() == JsonType.NULL) {
            return null;
        } else if (targetType == Void.class) {
            throw new JsonException(
                    "Something other than null was encoded for a null type");
        }

        // UidlValue
        if (targetType == UidlValue.class) {
            return decodeUidlValue((JsonArray) encodedJsonValue,
                    connectorTracker);
        }

        // Collections
        if (JsonConstants.VTYPE_LIST.equals(transportType)) {
            return decodeList(targetType, restrictToInternalTypes,
                    (JsonArray) encodedJsonValue, connectorTracker);
        } else if (JsonConstants.VTYPE_SET.equals(transportType)) {
            return decodeSet(targetType, restrictToInternalTypes,
                    (JsonArray) encodedJsonValue, connectorTracker);
        } else if (JsonConstants.VTYPE_MAP.equals(transportType)) {
            return decodeMap(targetType, restrictToInternalTypes,
                    encodedJsonValue, connectorTracker);
        }

        // Arrays
        if (JsonConstants.VTYPE_ARRAY.equals(transportType)) {

            return decodeObjectArray(targetType, (JsonArray) encodedJsonValue,
                    connectorTracker);

        } else if (JsonConstants.VTYPE_STRINGARRAY.equals(transportType)) {
            return decodeArray(String.class, (JsonArray) encodedJsonValue, null);
        }

        // Special Vaadin types

        if (JsonConstants.VTYPE_CONNECTOR.equals(transportType)) {
            return connectorTracker.getConnector(encodedJsonValue.asString());
        }

        // Legacy types

        if (JsonConstants.VTYPE_STRING.equals(transportType)) {
            return encodedJsonValue.asString();
        } else if (JsonConstants.VTYPE_INTEGER.equals(transportType)) {
            return (int) encodedJsonValue.asNumber();
        } else if (JsonConstants.VTYPE_LONG.equals(transportType)) {
            return (long) encodedJsonValue.asNumber();
        } else if (JsonConstants.VTYPE_FLOAT.equals(transportType)) {
            return (float) encodedJsonValue.asNumber();
        } else if (JsonConstants.VTYPE_DOUBLE.equals(transportType)) {
            return encodedJsonValue.asNumber();
        } else if (JsonConstants.VTYPE_BOOLEAN.equals(transportType)) {
            return encodedJsonValue.asBoolean();
        }

        throw new JsonException("Unknown type " + transportType);
    }

    private static UidlValue decodeUidlValue(JsonArray encodedJsonValue,
            ConnectorTracker connectorTracker) {
        String type = encodedJsonValue.getString(0);

        Object decodedValue = decodeInternalType(getType(type), true,
                encodedJsonValue.get(1), connectorTracker);
        return new UidlValue(decodedValue);
    }

    private static Map<Object, Object> decodeMap(Type targetType,
            boolean restrictToInternalTypes, JsonValue jsonMap,
            ConnectorTracker connectorTracker) {
        if (jsonMap.getType() == JsonType.ARRAY) {
            // Client-side has no declared type information to determine
            // encoding method for empty maps, so these are handled separately.
            // See #8906.
            JsonArray jsonArray = (JsonArray) jsonMap;
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
                return decodeStringMap(valueType, (JsonObject) jsonMap,
                        connectorTracker);
            } else if (keyType == Connector.class) {
                return decodeConnectorMap(valueType, (JsonObject) jsonMap,
                        connectorTracker);
            } else {
                return decodeObjectMap(keyType, valueType, (JsonArray) jsonMap,
                        connectorTracker);
            }
        } else {
            return decodeStringMap(UidlValue.class, (JsonObject) jsonMap,
                    connectorTracker);
        }
    }

    private static Map<Object, Object> decodeObjectMap(Type keyType,
            Type valueType, JsonArray jsonMap, ConnectorTracker connectorTracker) {

        JsonArray keys = jsonMap.getArray(0);
        JsonArray values = jsonMap.getArray(1);

        assert (keys.length() == values.length());

        Map<Object, Object> map = new HashMap<Object, Object>(keys.length() * 2);
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
            JsonObject jsonMap, ConnectorTracker connectorTracker) {
        Map<Object, Object> map = new HashMap<Object, Object>();

        for (String key : jsonMap.keys()) {
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
            JsonObject jsonMap, ConnectorTracker connectorTracker) {
        Map<Object, Object> map = new HashMap<Object, Object>();

        for (String key : jsonMap.keys()) {
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
     */
    private static Object decodeParametrizedType(Type targetType,
            boolean restrictToInternalTypes, int typeIndex, JsonValue value,
            ConnectorTracker connectorTracker) {
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

    private static Object decodeEnum(Class<? extends Enum> cls, JsonString value) {
        return Enum.valueOf(cls, value.getString());
    }

    private static Object[] decodeObjectArray(Type targetType,
            JsonArray jsonArray, ConnectorTracker connectorTracker) {
        List<Object> list = decodeList(List.class, true, jsonArray,
                connectorTracker);
        return list.toArray(new Object[list.size()]);
    }

    private static List<Object> decodeList(Type targetType,
            boolean restrictToInternalTypes, JsonArray jsonArray,
            ConnectorTracker connectorTracker) {
        int arrayLength = jsonArray.length();
        List<Object> list = new ArrayList<Object>(arrayLength);
        for (int i = 0; i < arrayLength; ++i) {
            // each entry always has two elements: type and value
            JsonValue encodedValue = jsonArray.get(i);
            Object decodedChild = decodeParametrizedType(targetType,
                    restrictToInternalTypes, 0, encodedValue, connectorTracker);
            list.add(decodedChild);
        }
        return list;
    }

    private static Set<Object> decodeSet(Type targetType,
            boolean restrictToInternalTypes, JsonArray jsonArray,
            ConnectorTracker connectorTracker) {
        HashSet<Object> set = new HashSet<Object>();
        set.addAll(decodeList(targetType, restrictToInternalTypes, jsonArray,
                connectorTracker));
        return set;
    }

    private static Object decodeObject(Type targetType,
            JsonObject serializedObject, ConnectorTracker connectorTracker) {

        Class<?> targetClass = getClassForType(targetType);

        try {
            Object decodedObject = targetClass.newInstance();
            for (BeanProperty property : getProperties(targetClass)) {

                String fieldName = property.getName();
                JsonValue encodedFieldValue = serializedObject.get(fieldName);
                Type fieldType = property.getType();
                Object decodedFieldValue = decodeInternalOrCustomType(
                        fieldType, encodedFieldValue, connectorTracker);

                property.setValue(decodedObject, decodedFieldValue);
            }

            return decodedObject;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static EncodeResult encode(Object value, JsonValue diffState,
            Type valueType, ConnectorTracker connectorTracker) {

        if (null == value) {
            return ENCODE_RESULT_NULL;
        }

        // Storing a single reference and only returning the EncodeResult at the
        // end the method is much shorter in bytecode which allows inlining
        JsonValue toReturn;

        if (value instanceof JsonValue) {
            // all JSON compatible types are returned as is.
            toReturn = (JsonValue) value;
        } else if (value instanceof String) {
            toReturn = Json.create((String) value);
        } else if (value instanceof Boolean) {
            toReturn = Json.create((Boolean) value);
        } else if (value instanceof Number) {
            toReturn = Json.create(((Number) value).doubleValue());
        } else if (value instanceof Character) {
            toReturn = Json.create(Character.toString((Character) value));
        } else if (value instanceof Collection) {
            toReturn = encodeCollection(valueType, (Collection<?>) value,
                    connectorTracker);
        } else if (value instanceof Map) {
            toReturn = encodeMap(valueType, (Map<?, ?>) value, connectorTracker);
        } else if (value instanceof Connector) {
            if (value instanceof Component
                    && !(LegacyCommunicationManager
                            .isComponentVisibleToClient((Component) value))) {
                // an encoded null is cached, return it directly.
                return ENCODE_RESULT_NULL;
            }
            // Connectors are simply serialized as ID.
            toReturn = Json.create(((Connector) value).getConnectorId());
        } else if (value instanceof Enum) {
            toReturn = Json.create(((Enum<?>) value).name());
        } else if (customSerializers.containsKey(value.getClass())) {
            toReturn = serializeJson(value, connectorTracker);
        } else if (valueType instanceof GenericArrayType) {
            toReturn = encodeArrayContents(
                    ((GenericArrayType) valueType).getGenericComponentType(),
                    value, connectorTracker);
        } else if (valueType instanceof Class<?>) {
            if (((Class<?>) valueType).isArray()) {
                toReturn = encodeArrayContents(
                        ((Class<?>) valueType).getComponentType(), value,
                        connectorTracker);
            } else {
                // encodeObject returns an EncodeResult with a diff, thus it
                // needs to return it directly rather than assigning it to
                // toReturn.
                return encodeObject(value, (Class<?>) valueType,
                        (JsonObject) diffState, connectorTracker);
            }
        } else {
            throw new JsonException("Can not encode type " + valueType);
        }
        return new EncodeResult(toReturn);
    }

    public static Collection<BeanProperty> getProperties(Class<?> type)
            throws IntrospectionException {
        Collection<BeanProperty> cachedProperties = typePropertyCache.get(type);
        if (cachedProperties != null) {
            return cachedProperties;
        }
        Collection<BeanProperty> properties = new ArrayList<BeanProperty>();

        properties.addAll(MethodProperty.find(type));
        properties.addAll(FieldProperty.find(type));

        // Doesn't matter if the same calculation is done multiple times from
        // different threads, so there's no need to do e.g. putIfAbsent
        typePropertyCache.put(type, properties);
        return properties;
    }

    /*
     * Loops through the fields of value and encodes them.
     */
    private static EncodeResult encodeObject(Object value, Class<?> valueType,
            JsonObject referenceValue, ConnectorTracker connectorTracker) {
        JsonObject encoded = Json.createObject();
        JsonObject diff = Json.createObject();

        try {
            for (BeanProperty property : getProperties(valueType)) {
                String fieldName = property.getName();
                // We can't use PropertyDescriptor.getPropertyType() as it does
                // not support generics
                Type fieldType = property.getType();
                Object fieldValue = property.getValue(value);

                if (encoded.hasKey(fieldName)) {
                    throw new RuntimeException(
                            "Can't encode "
                                    + valueType.getName()
                                    + " as it has multiple properties with the name "
                                    + fieldName.toLowerCase()
                                    + ". This can happen if there are getters and setters for a public field (the framework can't know which to ignore) or if there are properties with only casing distinguishing between the names (e.g. getFoo() and getFOO())");
                }

                JsonValue fieldReference;
                if (referenceValue != null) {
                    fieldReference = referenceValue.get(fieldName);
                    if (fieldReference instanceof JsonNull) {
                        fieldReference = null;
                    }
                } else {
                    fieldReference = null;
                }

                EncodeResult encodeResult = encode(fieldValue, fieldReference,
                        fieldType, connectorTracker);
                encoded.put(fieldName, encodeResult.getEncodedValue());

                if (valueChanged(encodeResult.getEncodedValue(), fieldReference)) {
                    diff.put(fieldName, encodeResult.getDiffOrValue());
                }
            }
        } catch (Exception e) {
            // TODO: Should exceptions be handled in a different way?
            throw new RuntimeException(e);
        }
        return new EncodeResult(encoded, diff);
    }

    /**
     * Compares the value with the reference. If they match, returns false.
     *
     * @param fieldValue
     * @param referenceValue
     * @return
     */
    private static boolean valueChanged(JsonValue fieldValue,
            JsonValue referenceValue) {
        if (fieldValue instanceof JsonNull) {
            fieldValue = null;
        }

        if (fieldValue == referenceValue) {
            return false;
        } else if (fieldValue == null || referenceValue == null) {
            return true;
        } else {
            return !jsonEquals(fieldValue, referenceValue);
        }
    }

    /**
     * Compares two json values for deep equality.
     * 
     * This is a helper for overcoming the fact that
     * {@link JsonValue#equals(Object)} only does an identity check and
     * {@link JsonValue#jsEquals(JsonValue)} is defined to use JavaScript
     * semantics where arrays and objects are equals only based on identity.
     * 
     * @since 7.4
     * @param a
     *            the first json value to check, may not be null
     * @param b
     *            the second json value to check, may not be null
     * @return <code>true</code> if both json values are the same;
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
            return a.asNumber() == b.asNumber();
        case STRING:
            return a.asString().equals(b.asString());
        case OBJECT:
            return jsonObjectEquals((JsonObject) a, (JsonObject) b);
        case ARRAY:
            return jsonArrayEquals((JsonArray) a, (JsonArray) b);
        default:
            throw new RuntimeException("Unsupported JsonType: " + type);
        }
    }

    private static boolean jsonObjectEquals(JsonObject a, JsonObject b) {
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

    private static JsonArray encodeArrayContents(Type componentType,
            Object array, ConnectorTracker connectorTracker) {
        JsonArray jsonArray = Json.createArray();
        for (int i = 0; i < Array.getLength(array); i++) {
            EncodeResult encodeResult = encode(Array.get(array, i), null,
                    componentType, connectorTracker);
            jsonArray.set(i, encodeResult.getEncodedValue());
        }
        return jsonArray;
    }

    private static JsonArray encodeCollection(Type targetType,
            Collection<?> collection, ConnectorTracker connectorTracker) {
        JsonArray jsonArray = Json.createArray();
        for (Object o : collection) {
            jsonArray.set(jsonArray.length(),
                    encodeChild(targetType, 0, o, connectorTracker));
        }
        return jsonArray;
    }

    private static JsonValue encodeChild(Type targetType, int typeIndex,
            Object o, ConnectorTracker connectorTracker) {
        if (targetType instanceof ParameterizedType) {
            Type childType = ((ParameterizedType) targetType)
                    .getActualTypeArguments()[typeIndex];
            // Encode using the given type
            EncodeResult encodeResult = encode(o, null, childType,
                    connectorTracker);
            return encodeResult.getEncodedValue();
        } else {
            throw new JsonException("Collection is missing generics");
        }
    }

    private static JsonValue encodeMap(Type mapType, Map<?, ?> map,
            ConnectorTracker connectorTracker) {
        Type keyType, valueType;

        if (mapType instanceof ParameterizedType) {
            keyType = ((ParameterizedType) mapType).getActualTypeArguments()[0];
            valueType = ((ParameterizedType) mapType).getActualTypeArguments()[1];
        } else {
            throw new JsonException("Map is missing generics");
        }

        if (map.isEmpty()) {
            // Client -> server encodes empty map as an empty array because of
            // #8906. Do the same for server -> client to maintain symmetry.
            return EMPTY_JSON_ARRAY;
        }

        if (keyType == String.class) {
            return encodeStringMap(valueType, map, connectorTracker);
        } else if (keyType == Connector.class) {
            return encodeConnectorMap(valueType, map, connectorTracker);
        } else {
            return encodeObjectMap(keyType, valueType, map, connectorTracker);
        }
    }

    private static JsonArray encodeObjectMap(Type keyType, Type valueType,
            Map<?, ?> map, ConnectorTracker connectorTracker) {
        JsonArray keys = Json.createArray();
        JsonArray values = Json.createArray();

        for (Entry<?, ?> entry : map.entrySet()) {
            EncodeResult encodedKey = encode(entry.getKey(), null, keyType,
                    connectorTracker);
            EncodeResult encodedValue = encode(entry.getValue(), null,
                    valueType, connectorTracker);

            keys.set(keys.length(), encodedKey.getEncodedValue());
            values.set(values.length(), encodedValue.getEncodedValue());
        }

        JsonArray jsonMap = Json.createArray();
        jsonMap.set(0, keys);
        jsonMap.set(1, values);

        return jsonMap;
    }

    /*
     * Encodes a connector map. Invisible connectors are skipped.
     */
    private static JsonObject encodeConnectorMap(Type valueType, Map<?, ?> map,
            ConnectorTracker connectorTracker) {
        JsonObject jsonMap = Json.createObject();

        for (Entry<?, ?> entry : map.entrySet()) {
            ClientConnector key = (ClientConnector) entry.getKey();
            if (LegacyCommunicationManager.isConnectorVisibleToClient(key)) {
                EncodeResult encodedValue = encode(entry.getValue(), null,
                        valueType, connectorTracker);
                jsonMap.put(key.getConnectorId(),
                        encodedValue.getEncodedValue());
            }
        }

        return jsonMap;
    }

    private static JsonObject encodeStringMap(Type valueType, Map<?, ?> map,
            ConnectorTracker connectorTracker) {
        JsonObject jsonMap = Json.createObject();

        for (Entry<?, ?> entry : map.entrySet()) {
            String key = (String) entry.getKey();
            EncodeResult encodedValue = encode(entry.getValue(), null,
                    valueType, connectorTracker);
            jsonMap.put(key, encodedValue.getEncodedValue());
        }

        return jsonMap;
    }

    /*
     * These methods looks good to inline, but are on a cold path of the
     * otherwise hot encode method, which needed to be shorted to allow inlining
     * of the hot part.
     */
    private static String getInternalTransportType(Type valueType) {
        return typeToTransportType.get(getClassForType(valueType));
    }

    private static JsonValue serializeJson(Object value,
            ConnectorTracker connectorTracker) {
        JSONSerializer serializer = customSerializers.get(value.getClass());
        return serializer.serialize(value, connectorTracker);
    }
}
