/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.external.json.JSONArray;
import com.vaadin.external.json.JSONException;
import com.vaadin.tools.ReflectTools;
import com.vaadin.ui.JavaScript.JavascriptCallbackRpc;
import com.vaadin.ui.JavascriptCallback;

public class JavascriptRpcHelper {

    private static final Method CALL_METHOD = ReflectTools.findMethod(
            JavascriptCallbackRpc.class, "call", String.class, JSONArray.class);
    private AbstractClientConnector connector;

    private Map<String, JavascriptCallback> callbacks = new HashMap<String, JavascriptCallback>();
    private JavascriptCallbackRpc javascriptCallbackRpc;

    public JavascriptRpcHelper(AbstractClientConnector connector) {
        this.connector = connector;
    }

    public void registerCallback(String functionName,
            JavascriptCallback javascriptCallback) {
        callbacks.put(functionName, javascriptCallback);
        ensureRpc();
    }

    private void ensureRpc() {
        if (javascriptCallbackRpc == null) {
            javascriptCallbackRpc = new JavascriptCallbackRpc() {
                public void call(String name, JSONArray arguments) {
                    JavascriptCallback callback = callbacks.get(name);
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
        JSONArray args = new JSONArray(Arrays.asList(arguments));
        connector.addMethodInvocationToQueue(
                JavascriptCallbackRpc.class.getName(), CALL_METHOD,
                new Object[] { name, args });
        connector.requestRepaint();
    }

}
