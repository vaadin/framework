/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.external.json.JSONArray;
import com.vaadin.external.json.JSONException;
import com.vaadin.terminal.AbstractExtension;
import com.vaadin.terminal.Page;
import com.vaadin.terminal.gwt.client.communication.ServerRpc;
import com.vaadin.terminal.gwt.client.extensions.javascriptmanager.ExecuteJavaScriptRpc;
import com.vaadin.terminal.gwt.client.extensions.javascriptmanager.JavaScriptManagerState;

/**
 * Provides access to JavaScript functionality in the web browser. To get an
 * instance of JavaScript, either use Page.getJavaScript() or
 * JavaScript.getCurrent() as a shorthand for getting the JavaScript object
 * corresponding to the current Page.
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 */
public class JavaScript extends AbstractExtension {
    private Map<String, JavaScriptCallback> callbacks = new HashMap<String, JavaScriptCallback>();

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
            public void call(String name, JSONArray arguments) {
                JavaScriptCallback callback = callbacks.get(name);
                // TODO handle situation if name is not registered
                try {
                    callback.call(arguments);
                } catch (JSONException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
    }

    @Override
    public JavaScriptManagerState getState() {
        return (JavaScriptManagerState) super.getState();
    }

    /**
     * Add a new function to the global JavaScript namespace (i.e. the window
     * object). The <code>call</code> method in the passed
     * {@link JavaScriptCallback} object will be invoked with the same
     * parameters whenever the JavaScript function is called in the browser.
     * 
     * A callback added with the name <code>"myCallback"</code> can thus be
     * invoked with the following JavaScript code:
     * <code>window.myCallback(argument1, argument2)</code>.
     * 
     * If the name parameter contains dots, simple objects are created on demand
     * to allow calling the function using the same name (e.g.
     * <code>window.myObject.myFunction</code>).
     * 
     * @param name
     *            the name that the callback function should get in the global
     *            JavaScript namespace.
     * @param callback
     *            the JavaScriptCallback that will be invoked if the JavaScript
     *            function is called.
     */
    public void addCallback(String name, JavaScriptCallback callback) {
        callbacks.put(name, callback);
        if (getState().getNames().add(name)) {
            requestRepaint();
        }
    }

    /**
     * Removes a JavaScripCallback from the browser's global JavaScript
     * namespace.
     * 
     * If the name contains dots and intermediate were created by
     * {@link #addCallback(String, JavaScriptCallback)}addCallback, these
     * objects will not be removed when the callback is removed.
     * 
     * @param name
     *            the name of the callback to remove
     */
    public void removeCallback(String name) {
        callbacks.remove(name);
        if (getState().getNames().remove(name)) {
            requestRepaint();
        }
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
    public void removeFromTarget() {
        throw new UnsupportedOperationException(
                "JavaScript is not designed to be removed.");
    }

}
