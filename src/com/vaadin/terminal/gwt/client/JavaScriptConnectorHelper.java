/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.user.client.Element;
import com.vaadin.terminal.gwt.client.communication.MethodInvocation;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent.StateChangeHandler;

public class JavaScriptConnectorHelper {

    public interface JavaScriptConnectorState {
        public Set<String> getCallbackNames();

        public Map<String, Set<String>> getRpcInterfaces();
    }

    private final ServerConnector connector;
    private final JavaScriptObject nativeState = JavaScriptObject
            .createObject();
    private final JavaScriptObject rpcMap = JavaScriptObject.createObject();

    private final Map<String, JavaScriptObject> rpcObjects = new HashMap<String, JavaScriptObject>();
    private final Map<String, Set<String>> rpcMethods = new HashMap<String, Set<String>>();

    private JavaScriptObject connectorWrapper;
    private int tag;

    private boolean inited = false;

    public JavaScriptConnectorHelper(ServerConnector connector) {
        this.connector = connector;

        // Wildcard rpc object
        rpcObjects.put("", JavaScriptObject.createObject());
    }

    public void init() {
        connector.addStateChangeHandler(new StateChangeHandler() {
            public void onStateChanged(StateChangeEvent stateChangeEvent) {
                JavaScriptObject wrapper = getConnectorWrapper();
                JavaScriptConnectorState state = getConnectorState();

                for (String callback : state.getCallbackNames()) {
                    ensureCallback(JavaScriptConnectorHelper.this, wrapper,
                            callback);
                }

                for (Entry<String, Set<String>> entry : state
                        .getRpcInterfaces().entrySet()) {
                    String rpcName = entry.getKey();
                    String jsName = getJsInterfaceName(rpcName);
                    if (!rpcObjects.containsKey(jsName)) {
                        Set<String> methods = entry.getValue();
                        rpcObjects.put(jsName,
                                createRpcObject(rpcName, methods));

                        // Init all methods for wildcard rpc
                        for (String method : methods) {
                            JavaScriptObject wildcardRpcObject = rpcObjects
                                    .get("");
                            Set<String> interfaces = rpcMethods.get(method);
                            if (interfaces == null) {
                                interfaces = new HashSet<String>();
                                rpcMethods.put(method, interfaces);
                                attachRpcMethod(wildcardRpcObject, null, method);
                            }
                            interfaces.add(rpcName);
                        }
                    }
                }

                // Init after setting up callbacks & rpc
                if (!inited) {
                    initJavaScript();
                    inited = true;
                }

                fireNativeStateChange(wrapper);
            }
        });
    }

    private static String getJsInterfaceName(String rpcName) {
        return rpcName.replace('$', '.');
    }

    protected JavaScriptObject createRpcObject(String iface, Set<String> methods) {
        JavaScriptObject object = JavaScriptObject.createObject();

        for (String method : methods) {
            attachRpcMethod(object, iface, method);
        }

        return object;
    }

    private boolean initJavaScript() {
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
            connectorWrapper = createConnectorWrapper(this, nativeState,
                    rpcMap, connector.getConnectorId(), rpcObjects);
        }

        return connectorWrapper;
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
            JavaScriptObject registeredRpc, String connectorId,
            Map<String, JavaScriptObject> rpcObjects)
    /*-{
        return {
            'getConnectorId': function() {
                return connectorId;
            },
            'getParentId': $entry(function(connectorId) {
                return h.@com.vaadin.terminal.gwt.client.JavaScriptConnectorHelper::getParentId(Ljava/lang/String;)(connectorId);
            }),
            'getState': function() {
                return nativeState;
            },
            'getRpcProxy': $entry(function(iface) {
                if (!iface) {
                    iface = '';
                }
                return rpcObjects.@java.util.Map::get(Ljava/lang/Object;)(iface);
            }),
            'getElement': $entry(function(connectorId) {
                return h.@com.vaadin.terminal.gwt.client.JavaScriptConnectorHelper::getWidgetElement(Ljava/lang/String;)(connectorId);
            }),
            'registerRpc': function(iface, rpcHandler) {
                //registerRpc(handler) -> registerRpc('', handler);
                if (!rpcHandler) {
                    rpcHandler = iface;
                    iface = '';
                }
                if (!registeredRpc[iface]) {
                    registeredRpc[iface] = [];
                }
                registeredRpc[iface].push(rpcHandler);
            },
        };
    }-*/;

    private native void attachRpcMethod(JavaScriptObject rpc, String iface,
            String method)
    /*-{
        var self = this;
        rpc[method] = $entry(function() {
            self.@com.vaadin.terminal.gwt.client.JavaScriptConnectorHelper::fireRpc(Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(iface, method, arguments);
        });
    }-*/;

    private String getParentId(String connectorId) {
        ServerConnector target = getConnector(connectorId);
        if (target == null) {
            return null;
        }
        ServerConnector parent = target.getParent();
        if (parent == null) {
            return null;
        } else {
            return parent.getConnectorId();
        }
    }

    private Element getWidgetElement(String connectorId) {
        ServerConnector target = getConnector(connectorId);
        if (target instanceof ComponentConnector) {
            return ((ComponentConnector) target).getWidget().getElement();
        } else {
            return null;
        }
    }

    private ServerConnector getConnector(String connectorId) {
        if (connectorId == null || connectorId.length() == 0) {
            return connector;
        }

        return ConnectorMap.get(connector.getConnection()).getConnector(
                connectorId);
    }

    private void fireRpc(String iface, String method,
            JsArray<JavaScriptObject> arguments) {
        if (iface == null) {
            iface = findWildcardInterface(method);
        }

        JSONArray argumentsArray = new JSONArray(arguments);
        Object[] parameters = new Object[arguments.length()];
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = argumentsArray.get(i);
        }
        connector.getConnection().addMethodInvocationToQueue(
                new MethodInvocation(connector.getConnectorId(), iface, method,
                        parameters), true);
    }

    private String findWildcardInterface(String method) {
        Set<String> interfaces = rpcMethods.get(method);
        if (interfaces.size() == 1) {
            return interfaces.iterator().next();
        } else {
            // TODO Resolve conflicts using argument count and types
            String interfaceList = "";
            for (String iface : interfaces) {
                if (interfaceList.length() != 0) {
                    interfaceList += ", ";
                }
                interfaceList += getJsInterfaceName(iface);
            }

            throw new IllegalStateException(
                    "Can not call method "
                            + method
                            + " for wildcard rpc proxy because the function is defined for multiple rpc interfaces: "
                            + interfaceList
                            + ". Retrieve a rpc proxy for a specific interface using getRpcProxy(interfaceName) to use the function.");
        }
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
        String iface = invocation.getInterfaceName();
        String method = invocation.getMethodName();
        if ("com.vaadin.ui.JavaScript$JavaScriptCallbackRpc".equals(iface)
                && "call".equals(method)) {
            String callbackName = parametersJson.get(0).isString()
                    .stringValue();
            JavaScriptObject arguments = parametersJson.get(1).isArray()
                    .getJavaScriptObject();
            invokeCallback(getConnectorWrapper(), callbackName, arguments);
        } else {
            JavaScriptObject arguments = parametersJson.getJavaScriptObject();
            invokeJsRpc(rpcMap, iface, method, arguments);
            // Also invoke wildcard interface
            invokeJsRpc(rpcMap, "", method, arguments);
        }
    }

    private static native void invokeCallback(JavaScriptObject connector,
            String name, JavaScriptObject arguments)
    /*-{
        connector[name].apply(connector, arguments);
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
