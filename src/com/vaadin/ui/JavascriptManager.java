/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.external.json.JSONArray;
import com.vaadin.terminal.AbstractExtension;
import com.vaadin.terminal.gwt.client.communication.ServerRpc;
import com.vaadin.terminal.gwt.client.extensions.javascriptmanager.JavascriptManagerState;

public class JavascriptManager extends AbstractExtension {
    private Map<String, JavascriptCallback> callbacks = new HashMap<String, JavascriptCallback>();

    // Can not be defined in client package as this JSONArray is not available
    // in GWT
    public interface JavascriptCallbackRpc extends ServerRpc {
        public void call(String name, JSONArray arguments);
    }

    public JavascriptManager() {
        registerRpc(new JavascriptCallbackRpc() {
            public void call(String name, JSONArray arguments) {
                JavascriptCallback callback = callbacks.get(name);
                // TODO error handling
                callback.call(arguments);
            }
        });
    }

    @Override
    public JavascriptManagerState getState() {
        return (JavascriptManagerState) super.getState();
    }

    public void addCallback(String name, JavascriptCallback javascriptCallback) {
        callbacks.put(name, javascriptCallback);
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

}
