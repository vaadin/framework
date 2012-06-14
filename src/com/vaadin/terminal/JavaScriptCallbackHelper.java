/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.vaadin.external.json.JSONArray;
import com.vaadin.external.json.JSONException;
import com.vaadin.terminal.gwt.client.JavaScriptConnectorHelper.JavaScriptConnectorState;
import com.vaadin.tools.ReflectTools;
import com.vaadin.ui.JavaScript.JavaScriptCallbackRpc;
import com.vaadin.ui.JavaScriptCallback;

public class JavaScriptCallbackHelper {

    private static final Method CALL_METHOD = ReflectTools.findMethod(
            JavaScriptCallbackRpc.class, "call", String.class, JSONArray.class);
    private AbstractClientConnector connector;

    private Map<String, JavaScriptCallback> callbacks = new HashMap<String, JavaScriptCallback>();
    private JavaScriptCallbackRpc javascriptCallbackRpc;

    public JavaScriptCallbackHelper(AbstractClientConnector connector) {
        this.connector = connector;
    }

    public void registerCallback(String functionName,
            JavaScriptCallback javaScriptCallback) {
        callbacks.put(functionName, javaScriptCallback);
        JavaScriptConnectorState state = getConnectorState();
        if (state.getCallbackNames().add(functionName)) {
            connector.requestRepaint();
        }
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
                public void call(String name, JSONArray arguments) {
                    JavaScriptCallback callback = callbacks.get(name);
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
        connector.requestRepaint();
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
            connector.requestRepaint();
        }
    }

}
