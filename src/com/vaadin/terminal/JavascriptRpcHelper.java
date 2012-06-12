/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.external.json.JSONArray;
import com.vaadin.tools.ReflectTools;
import com.vaadin.ui.JavascriptCallback;
import com.vaadin.ui.JavascriptManager.JavascriptCallbackRpc;

public class JavascriptRpcHelper {

    private static final Method CALL_METHOD = ReflectTools.findMethod(
            JavascriptCallbackRpc.class, "call", String.class, JSONArray.class);
    private AbstractClientConnector connector;

    private Map<String, JavascriptCallback> callbacks = new HashMap<String, JavascriptCallback>();
    private JavascriptCallbackRpc javascriptCallbackRpc;

    public JavascriptRpcHelper(AbstractClientConnector connector) {
        this.connector = connector;
    }

    public void registerRpc(JavascriptCallback javascriptCallback,
            String functionName) {
        callbacks.put(functionName, javascriptCallback);
        ensureRpc();
    }

    private void ensureRpc() {
        if (javascriptCallbackRpc == null) {
            javascriptCallbackRpc = new JavascriptCallbackRpc() {
                public void call(String name, JSONArray arguments) {
                    JavascriptCallback callback = callbacks.get(name);
                    callback.call(arguments);
                }
            };
            connector.registerRpc(javascriptCallbackRpc);
        }
    }

    public void callRpcFunction(String name, Object... arguments) {
        JSONArray args = new JSONArray(Arrays.asList(arguments));
        connector.addMethodInvocationToQueue(
                JavascriptCallbackRpc.class.getName(), CALL_METHOD,
                new Object[] { name, args });
    }

}
