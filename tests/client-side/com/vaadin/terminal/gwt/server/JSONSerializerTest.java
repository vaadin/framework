package com.vaadin.terminal.gwt.server;

/*
 @VaadinApache2LicenseForJavaFiles@
 */
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.vaadin.external.json.JSONArray;
import com.vaadin.terminal.gwt.client.communication.JsonDecoder;
import com.vaadin.terminal.gwt.client.communication.JsonEncoder;
import com.vaadin.terminal.gwt.client.ui.splitpanel.AbstractSplitPanelState;

/**
 * Tests for {@link JsonCodec}, {@link JsonEncoder}, {@link JsonDecoder}
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
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
        s.setCaption("State 1");
        s.setDebugId("foo");
        s2.setCaption("State 2");
        s2.setDebugId("bar");
        stringToStateMap.put("string - state 1", s);
        stringToStateMap.put("String - state 2", s2);

        JSONArray encodedMap = JsonCodec.encode(stringToStateMap, null,
                mapType, null);

        ensureDecodedCorrectly(stringToStateMap, encodedMap, mapType);
    }

    public void testBeanToStringMapSerialization() throws Exception {
        Type mapType = getClass().getDeclaredField("stateToStringMap")
                .getGenericType();
        stateToStringMap = new HashMap<AbstractSplitPanelState, String>();
        AbstractSplitPanelState s = new AbstractSplitPanelState();
        AbstractSplitPanelState s2 = new AbstractSplitPanelState();
        s.setCaption("State 1");
        s2.setCaption("State 2");
        stateToStringMap.put(s, "string - state 1");
        stateToStringMap.put(s2, "String - state 2");

        JSONArray encodedMap = JsonCodec.encode(stateToStringMap, null,
                mapType, null);

        ensureDecodedCorrectly(stateToStringMap, encodedMap, mapType);
    }

    private void ensureDecodedCorrectly(Object original, JSONArray encoded,
            Type type) throws Exception {
        Object serverSideDecoded = JsonCodec.decodeInternalOrCustomType(type,
                encoded, null);
        assertTrue("Server decoded", equals(original, serverSideDecoded));

        // Object clientSideDecoded = JsonDecoder.decodeValue(
        // (com.google.gwt.json.client.JSONArray) JSONParser
        // .parseStrict(encoded.toString()), null, null, null);
        // assertTrue("Client decoded",
        // equals(original, clientSideDecoded));

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
        BeanInfo beanInfo = Introspector.getBeanInfo(o1.getClass());
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            String fieldName = JsonCodec.getTransportFieldName(pd);
            if (fieldName == null) {
                continue;
            }

            Object c1 = pd.getReadMethod().invoke(o1);
            Object c2 = pd.getReadMethod().invoke(o2);
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
