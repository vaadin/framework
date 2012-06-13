/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.extensions.javascriptmanager;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONArray;
import com.vaadin.terminal.gwt.client.communication.MethodInvocation;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.ui.AbstractConnector;
import com.vaadin.terminal.gwt.client.ui.Connect;
import com.vaadin.ui.JavascriptManager;

@Connect(JavascriptManager.class)
public class JavascriptManagerConnector extends AbstractConnector {
    private Set<String> currentNames = new HashSet<String>();

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        Set<String> newNames = getState().getNames();

        // Current names now only contains orphan callbacks
        currentNames.removeAll(newNames);

        for (String name : currentNames) {
            removeCallback(name);
        }

        currentNames = new HashSet<String>(newNames);
        for (String name : newNames) {
            addCallback(name);
        }
    }

    // TODO Ensure we don't overwrite anything (important) in $wnd
    private native void addCallback(String name)
    /*-{
        var m = this;
        $wnd[name] = $entry(function() {
            //Must make a copy because arguments is an array-like object (not instanceof Array), causing suboptimal JSON encoding
            var args = Array.prototype.slice.call(arguments, 0);
            m.@com.vaadin.terminal.gwt.client.extensions.javascriptmanager.JavascriptManagerConnector::sendRpc(Ljava/lang/String;Lcom/google/gwt/core/client/JsArray;)(name, args);
        });
    }-*/;

    // TODO only remove what we actually added
    private native void removeCallback(String name)
    /*-{
        delete $wnd[name];
    }-*/;

    public void sendRpc(String name, JsArray<JavaScriptObject> arguments) {
        Object[] parameters = new Object[] { name, new JSONArray(arguments) };

        /*
         * Must invoke manually as the RPC interface can't be used in GWT
         * because of the JSONArray parameter
         */
        getConnection()
                .addMethodInvocationToQueue(
                        new MethodInvocation(
                                getConnectorId(),
                                "com.vaadin.ui.JavascriptManager$JavascriptCallbackRpc",
                                "call", parameters), true);
    }

    @Override
    public JavascriptManagerState getState() {
        return (JavascriptManagerState) super.getState();
    }
}
