/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import java.util.ArrayList;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONArray;
import com.vaadin.terminal.gwt.client.communication.MethodInvocation;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent.StateChangeHandler;

public class JavaScriptConnectorHelper {

    public interface JavaScriptConnectorState {
        public Set<String> getCallbackNames();
    }

    private final ServerConnector connector;
    private final JavaScriptObject nativeState = JavaScriptObject
            .createObject();
    private final JavaScriptObject rpcMap = JavaScriptObject.createObject();

    private JavaScriptObject connectorWrapper;
    private int tag;

    public JavaScriptConnectorHelper(ServerConnector connector) {
        this.connector = connector;
        connector.addStateChangeHandler(new StateChangeHandler() {
            public void onStateChanged(StateChangeEvent stateChangeEvent) {
                JavaScriptObject wrapper = getConnectorWrapper();

                for (String callback : getConnectorState().getCallbackNames()) {
                    ensureCallback(JavaScriptConnectorHelper.this, wrapper,
                            callback);
                }

                fireNativeStateChange(wrapper);
            }
        });
    }

    public boolean init() {
        ApplicationConfiguration conf = connector.getConnection()
                .getConfiguration();
        ArrayList<String> attemptedNames = new ArrayList<String>();
        Integer tag = Integer.valueOf(this.tag);
        while (tag != null) {
            String serverSideClassName = conf.getServerSideClassNameForTag(tag);
            String initFunctionName = serverSideClassName
                    .replaceAll("\\.", "_");
            if (tryInitJs(initFunctionName, getConnectorWrapper())) {
                VConsole.log("JavaScript connector initialized using "
                        + initFunctionName);
                return true;
            } else {
                VConsole.log("No JavaScript function " + initFunctionName
                        + " found");
                attemptedNames.add(initFunctionName);
                tag = conf.getParentTag(tag.intValue());
            }
        }
        VConsole.log("No JavaScript init for connector not found");
        showInitProblem(attemptedNames);
        return false;
    }

    protected void showInitProblem(ArrayList<String> attemptedNames) {
        // Default does nothing
    }

    private static native boolean tryInitJs(String initFunctionName,
            JavaScriptObject connectorWrapper)
    /*-{
        if (typeof $wnd[initFunctionName] == 'function') {
            $wnd[initFunctionName].apply(connectorWrapper);
            return true;
        } else {
            return false;
        }
    }-*/;

    private JavaScriptObject getConnectorWrapper() {
        if (connectorWrapper == null) {
            connectorWrapper = createConnectorWrapper();
        }

        return connectorWrapper;
    }

    protected JavaScriptObject createConnectorWrapper() {
        return createConnectorWrapper(this, nativeState, rpcMap,
                connector.getConnectorId());
    }

    private static native void fireNativeStateChange(
            JavaScriptObject connectorWrapper)
    /*-{
        if (typeof connectorWrapper.onStateChange == 'function') {
            connectorWrapper.onStateChange();
        }
    }-*/;

    private static native JavaScriptObject createConnectorWrapper(
            JavaScriptConnectorHelper h, JavaScriptObject nativeState,
            JavaScriptObject registeredRpc, String connectorId)
    /*-{
        return {
            'getConnectorId': function() {
                return connectorId;
            },
            'getState': function() {
                return nativeState;
            },
            'getRpcProxyFunction': function(iface, method) {
                    return $entry(function() {
                        h.@com.vaadin.terminal.gwt.client.JavaScriptConnectorHelper::fireRpc(Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(iface, method, arguments);
                    });
            },
            'registerRpc': function(iface, rpcHandler) {
                if (!registeredRpc[iface]) {
                    registeredRpc[iface] = [];
                }
                registeredRpc[iface].push(rpcHandler);
            },
        };
    }-*/;

    private void fireRpc(String iface, String method,
            JsArray<JavaScriptObject> arguments) {
        JSONArray argumentsArray = new JSONArray(arguments);
        Object[] parameters = new Object[arguments.length()];
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = argumentsArray.get(i);
        }
        connector.getConnection().addMethodInvocationToQueue(
                new MethodInvocation(connector.getConnectorId(), iface, method,
                        parameters), true);
    }

    private void fireCallback(String name, JsArray<JavaScriptObject> arguments) {
        MethodInvocation invocation = new MethodInvocation(
                connector.getConnectorId(),
                "com.vaadin.ui.JavaScript$JavaScriptCallbackRpc", "call",
                new Object[] { name, new JSONArray(arguments) });
        connector.getConnection().addMethodInvocationToQueue(invocation, true);
    }

    public void setNativeState(JavaScriptObject state) {
        updateNativeState(nativeState, state);
    }

    private static native void updateNativeState(JavaScriptObject state,
            JavaScriptObject input)
    /*-{
        // Copy all fields to existing state object 
        for(var key in state) {
            if (state.hasOwnProperty(key)) {
                delete state[key];
            }
        }
        
        for(var key in input) {
            if (input.hasOwnProperty(key)) {
                state[key] = input[key];
            }
        }
    }-*/;

    public Object[] decodeRpcParameters(JSONArray parametersJson) {
        return new Object[] { parametersJson.getJavaScriptObject() };
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public void invokeJsRpc(MethodInvocation invocation,
            JSONArray parametersJson) {
        if ("com.vaadin.ui.JavaScript$JavaScriptCallbackRpc".equals(invocation
                .getInterfaceName())
                && "call".equals(invocation.getMethodName())) {
            String callbackName = parametersJson.get(0).isString()
                    .stringValue();
            JavaScriptObject arguments = parametersJson.get(1).isArray()
                    .getJavaScriptObject();
            invokeCallback(getConnectorWrapper(), callbackName, arguments);
        } else {
            invokeJsRpc(rpcMap, invocation.getInterfaceName(),
                    invocation.getMethodName(),
                    parametersJson.getJavaScriptObject());
        }
    }

    private static native void invokeCallback(JavaScriptObject connector,
            String name, JavaScriptObject arguments)
    /*-{
        connector[name](arguments);
    }-*/;

    private static native void invokeJsRpc(JavaScriptObject rpcMap,
            String interfaceName, String methodName, JavaScriptObject parameters)
    /*-{
        var targets = rpcMap[interfaceName];
        if (!targets) {
            return;
        }
        for(var i = 0; i < targets.length; i++) {
            var target = targets[i];
            target[methodName].apply(target, parameters);
        }
    }-*/;

    private static native void ensureCallback(JavaScriptConnectorHelper h,
            JavaScriptObject connector, String name)
    /*-{
        connector[name] = $entry(function() {
            var args = Array.prototype.slice.call(arguments, 0);
            h.@com.vaadin.terminal.gwt.client.JavaScriptConnectorHelper::fireCallback(Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(name, args);
        });
    }-*/;

    private JavaScriptConnectorState getConnectorState() {
        return (JavaScriptConnectorState) connector.getState();
    }

}
