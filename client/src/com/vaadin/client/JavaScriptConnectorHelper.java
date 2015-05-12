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

package com.vaadin.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.vaadin.client.communication.JavaScriptMethodInvocation;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.communication.StateChangeEvent.StateChangeHandler;
import com.vaadin.client.ui.layout.ElementResizeEvent;
import com.vaadin.client.ui.layout.ElementResizeListener;
import com.vaadin.shared.JavaScriptConnectorState;
import com.vaadin.shared.communication.MethodInvocation;

import elemental.json.JsonArray;

public class JavaScriptConnectorHelper {

    private final ServerConnector connector;
    private final JavaScriptObject nativeState = JavaScriptObject
            .createObject();
    private final JavaScriptObject rpcMap = JavaScriptObject.createObject();

    private final Map<String, JavaScriptObject> rpcObjects = new HashMap<String, JavaScriptObject>();
    private final Map<String, Set<String>> rpcMethods = new HashMap<String, Set<String>>();
    private final Map<Element, Map<JavaScriptObject, ElementResizeListener>> resizeListeners = new HashMap<Element, Map<JavaScriptObject, ElementResizeListener>>();

    private JavaScriptObject connectorWrapper;
    private int tag;

    private String initFunctionName;

    public JavaScriptConnectorHelper(ServerConnector connector) {
        this.connector = connector;

        // Wildcard rpc object
        rpcObjects.put("", JavaScriptObject.createObject());
    }

    /**
     * The id of the previous response for which state changes have been
     * processed. If this is the same as the
     * {@link ApplicationConnection#getLastResponseId()}, it means that the
     * state change has already been handled and should not be done again.
     */
    private int processedResponseId = -1;

    public void init() {
        connector.addStateChangeHandler(new StateChangeHandler() {
            @Override
            public void onStateChanged(StateChangeEvent stateChangeEvent) {
                processStateChanges();
            }
        });
    }

    /**
     * Makes sure the javascript part of the connector has been initialized. The
     * javascript is usually initalized the first time a state change event is
     * received, but it might in some cases be necessary to make this happen
     * earlier.
     * 
     * @since 7.4.0
     */
    public void ensureJavascriptInited() {
        if (initFunctionName == null) {
            processStateChanges();
        }
    }

    private void processStateChanges() {
        int lastResponseId = connector.getConnection().getLastResponseId();
        if (processedResponseId == lastResponseId) {
            return;
        }
        processedResponseId = lastResponseId;

        JavaScriptObject wrapper = getConnectorWrapper();
        JavaScriptConnectorState state = getConnectorState();

        for (String callback : state.getCallbackNames()) {
            ensureCallback(JavaScriptConnectorHelper.this, wrapper, callback);
        }

        for (Entry<String, Set<String>> entry : state.getRpcInterfaces()
                .entrySet()) {
            String rpcName = entry.getKey();
            String jsName = getJsInterfaceName(rpcName);
            if (!rpcObjects.containsKey(jsName)) {
                Set<String> methods = entry.getValue();
                rpcObjects.put(jsName, createRpcObject(rpcName, methods));

                // Init all methods for wildcard rpc
                for (String method : methods) {
                    JavaScriptObject wildcardRpcObject = rpcObjects.get("");
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
        if (initFunctionName == null) {
            initJavaScript();
        }

        invokeIfPresent(wrapper, "onStateChange");
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

    protected boolean initJavaScript() {
        ApplicationConfiguration conf = connector.getConnection()
                .getConfiguration();
        ArrayList<String> attemptedNames = new ArrayList<String>();
        Integer tag = Integer.valueOf(this.tag);
        while (tag != null) {
            String serverSideClassName = conf.getServerSideClassNameForTag(tag);
            String initFunctionName = serverSideClassName
                    .replaceAll("\\.", "_");
            if (tryInitJs(initFunctionName, getConnectorWrapper())) {
                getLogger().info(
                        "JavaScript connector initialized using "
                                + initFunctionName);
                this.initFunctionName = initFunctionName;
                return true;
            } else {
                getLogger()
                        .warning(
                                "No JavaScript function " + initFunctionName
                                        + " found");
                attemptedNames.add(initFunctionName);
                tag = conf.getParentTag(tag.intValue());
            }
        }
        getLogger().info("No JavaScript init for connector found");
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

    public JavaScriptObject getConnectorWrapper() {
        if (connectorWrapper == null) {
            connectorWrapper = createConnectorWrapper(this,
                    connector.getConnection(), nativeState, rpcMap,
                    connector.getConnectorId(), rpcObjects);
        }

        return connectorWrapper;
    }

    private static native JavaScriptObject createConnectorWrapper(
            JavaScriptConnectorHelper h, ApplicationConnection c,
            JavaScriptObject nativeState, JavaScriptObject registeredRpc,
            String connectorId, Map<String, JavaScriptObject> rpcObjects)
    /*-{
        return {
            'getConnectorId': function() {
                return connectorId;
            },
            'getParentId': $entry(function(connectorId) {
                return h.@com.vaadin.client.JavaScriptConnectorHelper::getParentId(Ljava/lang/String;)(connectorId);
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
                return h.@com.vaadin.client.JavaScriptConnectorHelper::getWidgetElement(Ljava/lang/String;)(connectorId);
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
            'translateVaadinUri': $entry(function(uri) {
                return c.@com.vaadin.client.ApplicationConnection::translateVaadinUri(Ljava/lang/String;)(uri);
            }),
            'addResizeListener': function(element, resizeListener) {
                if (!element || element.nodeType != 1) throw "element must be defined";
                if (typeof resizeListener != "function") throw "resizeListener must be defined";
                $entry(h.@com.vaadin.client.JavaScriptConnectorHelper::addResizeListener(*)).call(h, element, resizeListener);
            },
            'removeResizeListener': function(element, resizeListener) {
                if (!element || element.nodeType != 1) throw "element must be defined";
                if (typeof resizeListener != "function") throw "resizeListener must be defined";
                $entry(h.@com.vaadin.client.JavaScriptConnectorHelper::removeResizeListener(*)).call(h, element, resizeListener);
            }
        };
    }-*/;

    // Called from JSNI to add a listener
    private void addResizeListener(Element element,
            final JavaScriptObject callbackFunction) {
        Map<JavaScriptObject, ElementResizeListener> elementListeners = resizeListeners
                .get(element);
        if (elementListeners == null) {
            elementListeners = new HashMap<JavaScriptObject, ElementResizeListener>();
            resizeListeners.put(element, elementListeners);
        }

        ElementResizeListener listener = elementListeners.get(callbackFunction);
        if (listener == null) {
            LayoutManager layoutManager = LayoutManager.get(connector
                    .getConnection());
            listener = new ElementResizeListener() {
                @Override
                public void onElementResize(ElementResizeEvent e) {
                    invokeElementResizeCallback(e.getElement(),
                            callbackFunction);
                }
            };
            layoutManager.addElementResizeListener(element, listener);
            elementListeners.put(callbackFunction, listener);
        }
    }

    private static native void invokeElementResizeCallback(Element element,
            JavaScriptObject callbackFunction)
    /*-{
        // Call with a simple event object and 'this' pointing to the global scope
        callbackFunction.call($wnd, {'element': element});
    }-*/;

    // Called from JSNI to remove a listener
    private void removeResizeListener(Element element,
            JavaScriptObject callbackFunction) {
        Map<JavaScriptObject, ElementResizeListener> listenerMap = resizeListeners
                .get(element);
        if (listenerMap == null) {
            return;
        }

        ElementResizeListener listener = listenerMap.remove(callbackFunction);
        if (listener != null) {
            LayoutManager.get(connector.getConnection())
                    .removeElementResizeListener(element, listener);
            if (listenerMap.isEmpty()) {
                resizeListeners.remove(element);
            }
        }
    }

    private native void attachRpcMethod(JavaScriptObject rpc, String iface,
            String method)
    /*-{
        var self = this;
        rpc[method] = $entry(function() {
            self.@com.vaadin.client.JavaScriptConnectorHelper::fireRpc(Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(iface, method, arguments);
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

        JsonArray argumentsArray = Util.jso2json(arguments);
        Object[] parameters = new Object[arguments.length()];
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = argumentsArray.get(i);
        }
        connector.getConnection().addMethodInvocationToQueue(
                new JavaScriptMethodInvocation(connector.getConnectorId(),
                        iface, method, parameters), false, false);
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
        MethodInvocation invocation = new JavaScriptMethodInvocation(
                connector.getConnectorId(),
                "com.vaadin.ui.JavaScript$JavaScriptCallbackRpc", "call",
                new Object[] { name, arguments });
        connector.getConnection().addMethodInvocationToQueue(invocation, false,
                false);
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

    public Object[] decodeRpcParameters(JsonArray parametersJson) {
        return new Object[] { Util.json2jso(parametersJson) };
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public void invokeJsRpc(MethodInvocation invocation,
            JsonArray parametersJson) {
        String iface = invocation.getInterfaceName();
        String method = invocation.getMethodName();
        if ("com.vaadin.ui.JavaScript$JavaScriptCallbackRpc".equals(iface)
                && "call".equals(method)) {
            String callbackName = parametersJson.getString(0);
            JavaScriptObject arguments = Util.json2jso(parametersJson.get(1));
            invokeCallback(getConnectorWrapper(), callbackName, arguments);
        } else {
            JavaScriptObject arguments = Util.json2jso(parametersJson);
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
            h.@com.vaadin.client.JavaScriptConnectorHelper::fireCallback(Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(name, args);
        });
    }-*/;

    private JavaScriptConnectorState getConnectorState() {
        return (JavaScriptConnectorState) connector.getState();
    }

    public void onUnregister() {
        invokeIfPresent(connectorWrapper, "onUnregister");

        if (!resizeListeners.isEmpty()) {
            LayoutManager layoutManager = LayoutManager.get(connector
                    .getConnection());
            for (Entry<Element, Map<JavaScriptObject, ElementResizeListener>> entry : resizeListeners
                    .entrySet()) {
                Element element = entry.getKey();
                for (ElementResizeListener listener : entry.getValue().values()) {
                    layoutManager
                            .removeElementResizeListener(element, listener);
                }
            }
            resizeListeners.clear();
        }
    }

    private static native void invokeIfPresent(
            JavaScriptObject connectorWrapper, String functionName)
    /*-{
        if (typeof connectorWrapper[functionName] == 'function') {
            connectorWrapper[functionName].apply(connectorWrapper, arguments);
        }
    }-*/;

    public String getInitFunctionName() {
        return initFunctionName;
    }

    private static Logger getLogger() {
        return Logger.getLogger(JavaScriptConnectorHelper.class.getName());
    }
}
