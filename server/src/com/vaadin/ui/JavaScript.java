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

package com.vaadin.ui;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import com.vaadin.server.AbstractExtension;
import com.vaadin.server.Page;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.extension.javascriptmanager.ExecuteJavaScriptRpc;
import com.vaadin.shared.extension.javascriptmanager.JavaScriptManagerState;

/**
 * Provides access to JavaScript functionality in the web browser. To get an
 * instance of JavaScript, either use Page.getJavaScript() or
 * JavaScript.getCurrent() as a shorthand for getting the JavaScript object
 * corresponding to the current Page.
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class JavaScript extends AbstractExtension {
    private Map<String, JavaScriptFunction> functions = new HashMap<String, JavaScriptFunction>();

    // Can not be defined in client package as this JSONArray is not available
    // in GWT
    public interface JavaScriptCallbackRpc extends ServerRpc {
        public void call(String name, JSONArray arguments);
    }

    /**
     * Creates a new JavaScript object. You should typically not this, but
     * instead use the JavaScript object already associated with your Page
     * object.
     */
    public JavaScript() {
        registerRpc(new JavaScriptCallbackRpc() {
            @Override
            public void call(String name, JSONArray arguments) {
                JavaScriptFunction function = functions.get(name);
                // TODO handle situation if name is not registered
                try {
                    function.call(arguments);
                } catch (JSONException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
    }

    @Override
    protected JavaScriptManagerState getState() {
        return (JavaScriptManagerState) super.getState();
    }

    /**
     * Add a new function to the global JavaScript namespace (i.e. the window
     * object). The <code>call</code> method in the passed
     * {@link JavaScriptFunction} object will be invoked with the same
     * parameters whenever the JavaScript function is called in the browser.
     * 
     * A function added with the name <code>"myFunction"</code> can thus be
     * invoked with the following JavaScript code:
     * <code>window.myFunction(argument1, argument2)</code>.
     * 
     * If the name parameter contains dots, simple objects are created on demand
     * to allow calling the function using the same name (e.g.
     * <code>window.myObject.myFunction</code>).
     * 
     * @param name
     *            the name that the function should get in the global JavaScript
     *            namespace.
     * @param function
     *            the JavaScriptFunction that will be invoked if the JavaScript
     *            function is called.
     */
    public void addFunction(String name, JavaScriptFunction function) {
        functions.put(name, function);
        getState().names.add(name);
    }

    /**
     * Removes a JavaScripFunction from the browser's global JavaScript
     * namespace.
     * 
     * If the name contains dots and intermediate objects were created by
     * {@link #addFunction(String, JavaScriptFunction)}, these objects will not
     * be removed by this method.
     * 
     * @param name
     *            the name of the callback to remove
     */
    public void removeFunction(String name) {
        functions.remove(name);
        getState().names.remove(name);
    }

    /**
     * Executes the given JavaScript code in the browser.
     * 
     * @param script
     *            The JavaScript code to run.
     */
    public void execute(String script) {
        getRpcProxy(ExecuteJavaScriptRpc.class).executeJavaScript(script);
    }

    /**
     * Executes the given JavaScript code in the browser.
     * 
     * @param script
     *            The JavaScript code to run.
     */
    public static void eval(String script) {
        getCurrent().execute(script);
    }

    /**
     * Get the JavaScript object for the current Page, or null if there is no
     * current page.
     * 
     * @see Page#getCurrent()
     * 
     * @return the JavaScript object corresponding to the current Page, or
     *         <code>null</code> if there is no current page.
     */
    public static JavaScript getCurrent() {
        Page page = Page.getCurrent();
        if (page == null) {
            return null;
        }
        return page.getJavaScript();
    }

    /**
     * JavaScript is not designed to be removed.
     * 
     * @throws UnsupportedOperationException
     *             when invoked
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException(
                "JavaScript is not designed to be removed.");
    }

}
