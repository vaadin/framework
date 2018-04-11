package com.vaadin.server;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.vaadin.server.JsonCodec.BeanProperty;
import com.vaadin.shared.communication.UidlValue;
import com.vaadin.shared.ui.splitpanel.AbstractSplitPanelState;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonException;
import elemental.json.JsonValue;

/**
 * Tests for {@link JsonCodec}
 *
 * @author Vaadin Ltd
 * @since 7.0
 *
 */
public class JSONSerializerTest {
    Map<String, AbstractSplitPanelState> stringToStateMap;
    Map<AbstractSplitPanelState, String> stateToStringMap;

    @Test
    public void testStringToBeanMapSerialization() throws Exception {
        Type mapType = getClass().getDeclaredField("stringToStateMap")
                .getGenericType();
        stringToStateMap = new HashMap<>();
        AbstractSplitPanelState s = new AbstractSplitPanelState();
        AbstractSplitPanelState s2 = new AbstractSplitPanelState();
        s.caption = "State 1";
        s.id = "foo";
        s2.caption = "State 2";
        s2.id = "bar";
        stringToStateMap.put("string - state 1", s);
        stringToStateMap.put("String - state 2", s2);

        JsonValue encodedMap = JsonCodec
                .encode(stringToStateMap, null, mapType, null)
                .getEncodedValue();

        ensureDecodedCorrectly(stringToStateMap, encodedMap, mapType);
    }

    @Test
    public void testBeanToStringMapSerialization() throws Exception {
        Type mapType = getClass().getDeclaredField("stateToStringMap")
                .getGenericType();
        stateToStringMap = new HashMap<>();
        AbstractSplitPanelState s = new AbstractSplitPanelState();
        AbstractSplitPanelState s2 = new AbstractSplitPanelState();
        s.caption = "State 1";
        s2.caption = "State 2";
        stateToStringMap.put(s, "string - state 1");
        stateToStringMap.put(s2, "String - state 2");

        JsonValue encodedMap = JsonCodec
                .encode(stateToStringMap, null, mapType, null)
                .getEncodedValue();

        ensureDecodedCorrectly(stateToStringMap, encodedMap, mapType);
    }

    @Test
    public void testNullLegacyValue() throws JsonException {
        JsonArray inputArray = Json.createArray();
        inputArray.set(0, "n");
        inputArray.set(1, Json.createNull());
        UidlValue decodedObject = (UidlValue) JsonCodec
                .decodeInternalType(UidlValue.class, true, inputArray, null);
        assertNull(decodedObject.getValue());
    }

    @Test(expected = JsonException.class)
    public void testNullTypeOtherValue() {
        JsonArray inputArray = Json.createArray();
        inputArray.set(0, "n");
        inputArray.set(1, "a");
        UidlValue decodedObject = (UidlValue) JsonCodec
                .decodeInternalType(UidlValue.class, true, inputArray, null);
    }

    private void ensureDecodedCorrectly(Object original, JsonValue encoded,
            Type type) throws Exception {
        Object serverSideDecoded = JsonCodec.decodeInternalOrCustomType(type,
                encoded, null);
        assertTrue("Server decoded", equals(original, serverSideDecoded));

    }

    private boolean equals(Object o1, Object o2) throws Exception {
        if (o1 == null) {
            return (o2 == null);
        }
        if (o2 == null) {
            return false;
        }

        if (o1 instanceof Map) {
            if (!(o2 instanceof Map)) {
                return false;
            }
            return equalsMap((Map) o1, (Map) o2);
        }

        if (o1.getClass() != o2.getClass()) {
            return false;
        }

        if (o1 instanceof Collection || o1 instanceof Number
                || o1 instanceof String) {
            return o1.equals(o2);
        }

        return equalsBean(o1, o2);
    }

    private boolean equalsBean(Object o1, Object o2) throws Exception {
        for (BeanProperty property : JsonCodec.getProperties(o1.getClass())) {
            Object c1 = property.getValue(o1);
            Object c2 = property.getValue(o2);
            if (!equals(c1, c2)) {
                return false;
            }
        }
        return true;
    }

    private boolean equalsMap(Map o1, Map o2) throws Exception {
        for (Object key1 : o1.keySet()) {
            Object key2 = key1;
            if (!(o2.containsKey(key2))) {
                // Try to fins a key that is equal
                for (Object k2 : o2.keySet()) {
                    if (equals(key1, k2)) {
                        key2 = k2;
                        break;
                    }
                }
            }
            if (!equals(o1.get(key1), o2.get(key2))) {
                return false;
            }

        }
        return true;
    }
}
