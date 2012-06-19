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

public class JavaScript extends AbstractExtension {
    private Map<String, JavaScriptCallback> callbacks = new HashMap<String, JavaScriptCallback>();

    // Can not be defined in client package as this JSONArray is not available
    // in GWT
    public interface JavaScriptCallbackRpc extends ServerRpc {
        public void call(String name, JSONArray arguments);
    }

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

    public void addCallback(String name, JavaScriptCallback callback) {
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
        Page page = Page.getCurrent();
        if (page == null) {
            return null;
        }
        return page.getJavaScript();
    }

}
