package com.vaadin.server;

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
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vaadin.server.JsonCodec.BeanProperty;
import com.vaadin.shared.communication.UidlValue;
import com.vaadin.shared.ui.splitpanel.AbstractSplitPanelState;

/**
 * Tests for {@link JsonCodec}
 * 
 * @author Vaadin Ltd
 * @since 7.0
 * 
 */
public class JSONSerializerTest extends TestCase {
    HashMap<String, AbstractSplitPanelState> stringToStateMap;
    HashMap<AbstractSplitPanelState, String> stateToStringMap;

    public void testStringToBeanMapSerialization() throws Exception {
        Type mapType = getClass().getDeclaredField("stringToStateMap")
                .getGenericType();
        stringToStateMap = new HashMap<String, AbstractSplitPanelState>();
        AbstractSplitPanelState s = new AbstractSplitPanelState();
        AbstractSplitPanelState s2 = new AbstractSplitPanelState();
        s.caption = "State 1";
        s.id = "foo";
        s2.caption = "State 2";
        s2.id = "bar";
        stringToStateMap.put("string - state 1", s);
        stringToStateMap.put("String - state 2", s2);

        Object encodedMap = JsonCodec.encode(stringToStateMap, null, mapType,
                null).getEncodedValue();

        ensureDecodedCorrectly(stringToStateMap, encodedMap, mapType);
    }

    public void testBeanToStringMapSerialization() throws Exception {
        Type mapType = getClass().getDeclaredField("stateToStringMap")
                .getGenericType();
        stateToStringMap = new HashMap<AbstractSplitPanelState, String>();
        AbstractSplitPanelState s = new AbstractSplitPanelState();
        AbstractSplitPanelState s2 = new AbstractSplitPanelState();
        s.caption = "State 1";
        s2.caption = "State 2";
        stateToStringMap.put(s, "string - state 1");
        stateToStringMap.put(s2, "String - state 2");

        Object encodedMap = JsonCodec.encode(stateToStringMap, null, mapType,
                null).getEncodedValue();

        ensureDecodedCorrectly(stateToStringMap, encodedMap, mapType);
    }

    public void testNullLegacyValue() throws JSONException {
        JSONArray inputArray = new JSONArray(
                Arrays.asList("n", JSONObject.NULL));
        UidlValue decodedObject = (UidlValue) JsonCodec.decodeInternalType(
                UidlValue.class, true, inputArray, null);
        assertNull(decodedObject.getValue());
    }

    public void testNullTypeOtherValue() {
        try {
            JSONArray inputArray = new JSONArray(Arrays.asList("n", "a"));
            UidlValue decodedObject = (UidlValue) JsonCodec.decodeInternalType(
                    UidlValue.class, true, inputArray, null);

            throw new AssertionFailedError("No JSONException thrown");
        } catch (JSONException e) {
            // Should throw exception
        }
    }

    private void ensureDecodedCorrectly(Object original, Object encoded,
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
