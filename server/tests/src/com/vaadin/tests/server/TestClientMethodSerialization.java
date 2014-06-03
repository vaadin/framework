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
package com.vaadin.tests.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;

import junit.framework.TestCase;

import org.json.JSONArray;

import com.vaadin.server.ClientMethodInvocation;
import com.vaadin.server.JavaScriptCallbackHelper;
import com.vaadin.ui.JavaScript.JavaScriptCallbackRpc;
import com.vaadin.util.ReflectTools;

public class TestClientMethodSerialization extends TestCase {

    private static final Method JAVASCRIPT_CALLBACK_METHOD = ReflectTools
            .findMethod(JavaScriptCallbackRpc.class, "call", String.class,
                    JSONArray.class);

    private static final Method BASIC_PARAMS_CALL_METHOD = ReflectTools
            .findMethod(TestClientMethodSerialization.class,
                    "basicParamsMethodForTesting", String.class, Integer.class);

    private static final Method NO_PARAMS_CALL_METHOD = ReflectTools
            .findMethod(TestClientMethodSerialization.class,
                    "noParamsMethodForTesting");

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
    public void testClientMethodSerialization_WithJSONArray_ContentStaysSame()
            throws Exception {
        JSONArray originalArray = new JSONArray(Arrays.asList(
                "callbackParameter1", "callBackParameter2", "12345"));
        ClientMethodInvocation original = new ClientMethodInvocation(null,
                "interfaceName", JAVASCRIPT_CALLBACK_METHOD, new Object[] {
                        "callBackMethodName", originalArray });

        ClientMethodInvocation copy = (ClientMethodInvocation) serializeAndDeserialize(original);
        JSONArray copyArray = (JSONArray) copy.getParameters()[1];
        assertEquals(originalArray.toString(), copyArray.toString());
    }

    public void testClientMethodSerialization_WithBasicParams_NoChanges()
            throws Exception {
        String stringParam = "a string 123";
        Integer integerParam = 1234567890;
        ClientMethodInvocation original = new ClientMethodInvocation(null,
                "interfaceName", BASIC_PARAMS_CALL_METHOD, new Serializable[] {
                        stringParam, integerParam });
        ClientMethodInvocation copy = (ClientMethodInvocation) serializeAndDeserialize(original);
        String copyString = (String) copy.getParameters()[0];
        Integer copyInteger = (Integer) copy.getParameters()[1];
        assertEquals(copyString, stringParam);
        assertEquals(copyInteger, integerParam);
    }

    public void testClientMethodSerialization_NoParams_NoExceptions() {
        ClientMethodInvocation original = new ClientMethodInvocation(null,
                "interfaceName", NO_PARAMS_CALL_METHOD, null);
        ClientMethodInvocation copy = (ClientMethodInvocation) serializeAndDeserialize(original);
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

}
