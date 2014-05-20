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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;

import com.vaadin.shared.JavaScriptConnectorState;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.JavaScript.JavaScriptCallbackRpc;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.util.ReflectTools;

/**
 * Internal helper class used to implement functionality common to
 * {@link AbstractJavaScriptComponent} and {@link AbstractJavaScriptExtension}.
 * Corresponding support in client-side code is in
 * {@link com.vaadin.client.JavaScriptConnectorHelper}.
 * <p>
 * You should most likely no use this class directly.
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class JavaScriptCallbackHelper implements Serializable {

    private static final Method CALL_METHOD = ReflectTools.findMethod(
            JavaScriptCallbackRpc.class, "call", String.class, JSONArray.class);
    private AbstractClientConnector connector;

    private Map<String, JavaScriptFunction> callbacks = new HashMap<String, JavaScriptFunction>();
    private JavaScriptCallbackRpc javascriptCallbackRpc;

    public JavaScriptCallbackHelper(AbstractClientConnector connector) {
        this.connector = connector;
    }

    public void registerCallback(String functionName,
            JavaScriptFunction javaScriptCallback) {
        callbacks.put(functionName, javaScriptCallback);
        JavaScriptConnectorState state = getConnectorState();
        state.getCallbackNames().add(functionName);
        ensureRpc();
    }

    private JavaScriptConnectorState getConnectorState() {
        JavaScriptConnectorState state = (JavaScriptConnectorState) connector
                .getState();
        return state;
    }

    private void ensureRpc() {
        if (javascriptCallbackRpc == null) {
            javascriptCallbackRpc = new JavaScriptCallbackRpc() {
                @Override
                public void call(String name, JSONArray arguments) {
                    JavaScriptFunction callback = callbacks.get(name);
                    try {
                        callback.call(arguments);
                    } catch (JSONException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            };
            connector.registerRpc(javascriptCallbackRpc);
        }
    }

    public void invokeCallback(String name, Object... arguments) {
        if (callbacks.containsKey(name)) {
            throw new IllegalStateException(
                    "Can't call callback "
                            + name
                            + " on the client because a callback with the same name is registered on the server.");
        }
        JSONArray args = new JSONArray(Arrays.asList(arguments));
        connector.addMethodInvocationToQueue(
                JavaScriptCallbackRpc.class.getName(), CALL_METHOD,
                new Object[] { name, args });
    }

    public void registerRpc(Class<?> rpcInterfaceType) {
        if (rpcInterfaceType == JavaScriptCallbackRpc.class) {
            // Ignore
            return;
        }
        Map<String, Set<String>> rpcInterfaces = getConnectorState()
                .getRpcInterfaces();
        String interfaceName = rpcInterfaceType.getName();
        if (!rpcInterfaces.containsKey(interfaceName)) {
            Set<String> methodNames = new HashSet<String>();

            for (Method method : rpcInterfaceType.getMethods()) {
                methodNames.add(method.getName());
            }

            rpcInterfaces.put(interfaceName, methodNames);
        }
    }

}
