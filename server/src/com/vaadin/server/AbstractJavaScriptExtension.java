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

import com.vaadin.shared.JavaScriptExtensionState;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.ui.JavaScriptFunction;

/**
 * Base class for Extensions with all client-side logic implemented using
 * JavaScript.
 * <p>
 * When a new JavaScript extension is initialized in the browser, the framework
 * will look for a globally defined JavaScript function that will initialize the
 * extension. The name of the initialization function is formed by replacing .
 * with _ in the name of the server-side class. If no such function is defined,
 * each super class is used in turn until a match is found. The framework will
 * thus first attempt with <code>com_example_MyExtension</code> for the
 * server-side
 * <code>com.example.MyExtension extends AbstractJavaScriptExtension</code>
 * class. If MyExtension instead extends <code>com.example.SuperExtension</code>
 * , then <code>com_example_SuperExtension</code> will also be attempted if
 * <code>com_example_MyExtension</code> has not been defined.
 * <p>
 * 
 * The initialization function will be called with <code>this</code> pointing to
 * a connector wrapper object providing integration to Vaadin with the following
 * functions:
 * <ul>
 * <li><code>getConnectorId()</code> - returns a string with the id of the
 * connector.</li>
 * <li><code>getParentId([connectorId])</code> - returns a string with the id of
 * the connector's parent. If <code>connectorId</code> is provided, the id of
 * the parent of the corresponding connector with the passed id is returned
 * instead.</li>
 * <li><code>getElement([connectorId])</code> - returns the DOM Element that is
 * the root of a connector's widget. <code>null</code> is returned if the
 * connector can not be found or if the connector doesn't have a widget. If
 * <code>connectorId</code> is not provided, the connector id of the current
 * connector will be used.</li>
 * <li><code>getState()</code> - returns an object corresponding to the shared
 * state defined on the server. The scheme for conversion between Java and
 * JavaScript types is described bellow.</li>
 * <li><code>registerRpc([name, ] rpcObject)</code> - registers the
 * <code>rpcObject</code> as a RPC handler. <code>rpcObject</code> should be an
 * object with field containing functions for all eligible RPC functions. If
 * <code>name</code> is provided, the RPC handler will only used for RPC calls
 * for the RPC interface with the same fully qualified Java name. If no
 * <code>name</code> is provided, the RPC handler will be used for all incoming
 * RPC invocations where the RPC method name is defined as a function field in
 * the handler. The scheme for conversion between Java types in the RPC
 * interface definition and the JavaScript values passed as arguments to the
 * handler functions is described bellow.</li>
 * <li><code>getRpcProxy([name])</code> - returns an RPC proxy object. If
 * <code>name</code> is provided, the proxy object will contain functions for
 * all methods in the RPC interface with the same fully qualified name, provided
 * a RPC handler has been registered by the server-side code. If no
 * <code>name</code> is provided, the returned RPC proxy object will contain
 * functions for all methods in all RPC interfaces registered for the connector
 * on the server. If the same method name is present in multiple registered RPC
 * interfaces, the corresponding function in the RPC proxy object will throw an
 * exception when called. The scheme for conversion between Java types in the
 * RPC interface and the JavaScript values that should be passed to the
 * functions is described bellow.</li>
 * <li><code>translateVaadinUri(uri)</code> - Translates a Vaadin URI to a URL
 * that can be used in the browser. This is just way of accessing
 * {@link com.vaadin.client.ApplicationConnection#translateVaadinUri(String)}</li>
 * </ul>
 * The connector wrapper also supports these special functions:
 * <ul>
 * <li><code>onStateChange</code> - If the JavaScript code assigns a function to
 * the field, that function is called whenever the contents of the shared state
 * is changed.</li>
 * <li>Any field name corresponding to a call to
 * {@link #addFunction(String, JavaScriptFunction)} on the server will
 * automatically be present as a function that triggers the registered function
 * on the server.</li>
 * <li>Any field name referred to using {@link #callFunction(String, Object...)}
 * on the server will be called if a function has been assigned to the field.</li>
 * </ul>
 * <p>
 * 
 * Values in the Shared State and in RPC calls are converted between Java and
 * JavaScript using the following conventions:
 * <ul>
 * <li>Primitive Java numbers (byte, char, int, long, float, double) and their
 * boxed types (Byte, Character, Integer, Long, Float, Double) are represented
 * by JavaScript numbers.</li>
 * <li>The primitive Java boolean and the boxed Boolean are represented by
 * JavaScript booleans.</li>
 * <li>Java Strings are represented by JavaScript strings.</li>
 * <li>Java Dates are represented by JavaScript numbers containing the timestamp
 * </li>
 * <li>List, Set and all arrays in Java are represented by JavaScript arrays.</li>
 * <li>Map<String, ?> in Java is represented by JavaScript object with fields
 * corresponding to the map keys.</li>
 * <li>Any other Java Map is represented by a JavaScript array containing two
 * arrays, the first contains the keys and the second contains the values in the
 * same order.</li>
 * <li>A Java Bean is represented by a JavaScript object with fields
 * corresponding to the bean's properties.</li>
 * <li>A Java Connector is represented by a JavaScript string containing the
 * connector's id.</li>
 * <li>A pluggable serialization mechanism is provided for types not described
 * here. Please refer to the documentation for specific types for serialization
 * information.</li>
 * </ul>
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public abstract class AbstractJavaScriptExtension extends AbstractExtension {
    private JavaScriptCallbackHelper callbackHelper = new JavaScriptCallbackHelper(
            this);

    @Override
    protected <T extends ServerRpc> void registerRpc(T implementation,
            Class<T> rpcInterfaceType) {
        super.registerRpc(implementation, rpcInterfaceType);
        callbackHelper.registerRpc(rpcInterfaceType);
    }

    /**
     * Register a {@link JavaScriptFunction} that can be called from the
     * JavaScript using the provided name. A JavaScript function with the
     * provided name will be added to the connector wrapper object (initially
     * available as <code>this</code>). Calling that JavaScript function will
     * cause the call method in the registered {@link JavaScriptFunction} to be
     * invoked with the same arguments.
     * 
     * @param functionName
     *            the name that should be used for client-side callback
     * @param function
     *            the {@link JavaScriptFunction} object that will be invoked
     *            when the JavaScript function is called
     */
    protected void addFunction(String functionName, JavaScriptFunction function) {
        callbackHelper.registerCallback(functionName, function);
    }

    /**
     * Invoke a named function that the connector JavaScript has added to the
     * JavaScript connector wrapper object. The arguments should only contain
     * data types that can be represented in JavaScript including primitives,
     * their boxed types, arrays, String, List, Set, Map, Connector and
     * JavaBeans.
     * 
     * @param name
     *            the name of the function
     * @param arguments
     *            function arguments
     */
    protected void callFunction(String name, Object... arguments) {
        callbackHelper.invokeCallback(name, arguments);
    }

    @Override
    protected JavaScriptExtensionState getState() {
        return (JavaScriptExtensionState) super.getState();
    }
}
