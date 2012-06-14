/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.external.json.JSONArray;
import com.vaadin.external.json.JSONException;
import com.vaadin.terminal.AbstractExtension;
import com.vaadin.terminal.Extension;
import com.vaadin.terminal.gwt.client.communication.ServerRpc;
import com.vaadin.terminal.gwt.client.extensions.javascriptmanager.ExecuteJavaScriptRpc;
import com.vaadin.terminal.gwt.client.extensions.javascriptmanager.JavascriptManagerState;

public class JavaScript extends AbstractExtension {
    private Map<String, JavascriptCallback> callbacks = new HashMap<String, JavascriptCallback>();

    // Can not be defined in client package as this JSONArray is not available
    // in GWT
    public interface JavascriptCallbackRpc extends ServerRpc {
        public void call(String name, JSONArray arguments);
    }

    public JavaScript() {
        registerRpc(new JavascriptCallbackRpc() {
            public void call(String name, JSONArray arguments) {
                JavascriptCallback callback = callbacks.get(name);
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
    public JavascriptManagerState getState() {
        return (JavascriptManagerState) super.getState();
    }

    public void addCallback(String name, JavascriptCallback callback) {
        callbacks.put(name, callback);
        if (getState().getNames().add(name)) {
            requestRepaint();
        }
    }

    public void removeCallback(String name) {
        callbacks.remove(name);
        if (getState().getNames().remove(name)) {
            requestRepaint();
        }
    }

    public void execute(String script) {
        getRpcProxy(ExecuteJavaScriptRpc.class).executeJavaScript(script);
    }

    public static JavaScript getCurrent() {
        return Root.getCurrentRoot().getJavaScript();
    }

    private static JavaScript getJavascript(Root root) {
        // TODO Add caching to avoid iterating collection every time
        // Caching should use weak references to avoid memory leaks -> cache
        // should be transient to avoid serialization problems
        for (Extension extension : root.getExtensions()) {
            if (extension instanceof JavaScript) {
                return (JavaScript) extension;
            }
        }

        // Extend root if it isn't yet done
        JavaScript javascript = new JavaScript();
        javascript.extend(root);
        return javascript;
    }

}
