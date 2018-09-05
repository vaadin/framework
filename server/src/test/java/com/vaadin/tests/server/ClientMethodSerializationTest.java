package com.vaadin.tests.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;

import org.junit.Test;

import com.vaadin.server.ClientConnector;
import com.vaadin.server.ClientMethodInvocation;
import com.vaadin.server.JavaScriptCallbackHelper;
import com.vaadin.server.JsonCodec;
import com.vaadin.ui.JavaScript.JavaScriptCallbackRpc;
import com.vaadin.util.ReflectTools;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonValue;
import elemental.json.impl.JsonUtil;

public class ClientMethodSerializationTest {

    private static final Method JAVASCRIPT_CALLBACK_METHOD = ReflectTools
            .findMethod(JavaScriptCallbackRpc.class, "call", String.class,
                    JsonArray.class);

    private static final Method BASIC_PARAMS_CALL_METHOD = ReflectTools
            .findMethod(ClientMethodSerializationTest.class,
                    "basicParamsMethodForTesting", String.class, Integer.class);

    private static final Method NO_PARAMS_CALL_METHOD = ReflectTools.findMethod(
            ClientMethodSerializationTest.class, "noParamsMethodForTesting");

    public void basicParamsMethodForTesting(String stringParam,
            Integer integerParam) {
    }

    public void noParamsMethodForTesting() {
    }

    /**
     * Tests the {@link ClientMethodInvocation} serialization when using
     * {@link JavaScriptCallbackHelper#invokeCallback(String, Object...)}.
     * #12532
     */
    @Test
    public void testClientMethodSerialization_WithJSONArray_ContentStaysSame()
            throws Exception {
        JsonArray originalArray = Json.createArray();
        originalArray.set(0, "callbackParameter1");
        originalArray.set(1, "callBackParameter2");
        originalArray.set(2, "12345");
        ClientMethodInvocation original = new ClientMethodInvocation(null,
                "interfaceName", JAVASCRIPT_CALLBACK_METHOD,
                new Object[] { "callBackMethodName", originalArray });

        ClientMethodInvocation copy = (ClientMethodInvocation) serializeAndDeserialize(
                original);
        JsonArray copyArray = (JsonArray) copy.getParameters()[1];
        assertEquals(JsonUtil.stringify(originalArray),
                JsonUtil.stringify(copyArray));
    }

    @Test
    public void testClientMethodSerialization_WithBasicParams_NoChanges()
            throws Exception {
        String stringParam = "a string 123";
        Integer integerParam = 1234567890;
        ClientMethodInvocation original = new ClientMethodInvocation(null,
                "interfaceName", BASIC_PARAMS_CALL_METHOD,
                new Serializable[] { stringParam, integerParam });
        ClientMethodInvocation copy = (ClientMethodInvocation) serializeAndDeserialize(
                original);
        String copyString = (String) copy.getParameters()[0];
        Integer copyInteger = (Integer) copy.getParameters()[1];
        assertEquals(copyString, stringParam);
        assertEquals(copyInteger, integerParam);
    }

    @Test
    public void testClientMethodSerialization_NoParams_NoExceptions() {
        ClientMethodInvocation original = new ClientMethodInvocation(null,
                "interfaceName", NO_PARAMS_CALL_METHOD, null);
        ClientMethodInvocation copy = (ClientMethodInvocation) serializeAndDeserialize(
                original);
    }

    private static Serializable serializeAndDeserialize(Serializable input) {
        Serializable output = null;
        try {
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bs);
            out.writeObject(input);
            byte[] data = bs.toByteArray();
            ObjectInputStream in = new ObjectInputStream(
                    new ByteArrayInputStream(data));
            output = (Serializable) in.readObject();
        } catch (Exception e) {
            fail("Exception during serialization/deserialization: "
                    + e.getMessage());
        }
        return output;
    }

    @Test
    public void testSerializeTwice() {
        String name = "javascriptFunctionName";
        String[] arguments = { "1", "2", "3" };
        JsonArray args = (JsonArray) JsonCodec
                .encode(arguments, null, Object[].class, null)
                .getEncodedValue();
        ClientConnector connector = null;

        ClientMethodInvocation original = new ClientMethodInvocation(connector,
                "interfaceName", JAVASCRIPT_CALLBACK_METHOD,
                new Object[] { name, args });

        ClientMethodInvocation copy = (ClientMethodInvocation) serializeAndDeserialize(
                original);
        assertEquals(copy.getMethodName(), original.getMethodName());
        assertEquals(copy.getParameters().length,
                original.getParameters().length);
        for (int i = 0; i < copy.getParameters().length; i++) {
            Object originalParameter = original.getParameters()[i];
            Object copyParameter = copy.getParameters()[i];
            if (originalParameter instanceof JsonValue) {
                assertEquals(((JsonValue) originalParameter).toJson(),
                        ((JsonValue) copyParameter).toJson());
            } else {
                assertEquals(originalParameter, copyParameter);
            }
        }
    }

}
