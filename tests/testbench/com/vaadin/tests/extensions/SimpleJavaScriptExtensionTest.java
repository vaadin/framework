/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.extensions;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.external.json.JSONArray;
import com.vaadin.external.json.JSONException;
import com.vaadin.terminal.AbstractJavaScriptExtension;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.gwt.client.JavaScriptExtensionState;
import com.vaadin.terminal.gwt.client.communication.ClientRpc;
import com.vaadin.terminal.gwt.client.communication.ServerRpc;
import com.vaadin.tests.components.AbstractTestRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.JavaScriptCallback;
import com.vaadin.ui.Notification;

public class SimpleJavaScriptExtensionTest extends AbstractTestRoot {

    public static class SimpleJavaScriptExtensionState extends
            JavaScriptExtensionState {
        private String prefix;

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getPrefix() {
            return prefix;
        }
    }

    public static interface SimpleJavaScriptExtensionClientRpc extends
            ClientRpc {
        public void greet(String message);
    }

    public static interface SimpleJavaScriptExtensionServerRpc extends
            ServerRpc {
        public void greet(String message);
    }

    @JavaScript("/statictestfiles/jsextension.js")
    @StyleSheet("/VAADIN/external1.css")
    public static class SimpleJavascriptExtension extends
            AbstractJavaScriptExtension {

        public SimpleJavascriptExtension() {
            registerRpc(new SimpleJavaScriptExtensionServerRpc() {
                public void greet(String message) {
                    Notification.show(getState().getPrefix() + message);
                }
            });
            registerCallback("greetToServer", new JavaScriptCallback() {
                public void call(JSONArray arguments) throws JSONException {
                    Notification.show(getState().getPrefix()
                            + arguments.getString(0));
                }
            });
        }

        @Override
        public SimpleJavaScriptExtensionState getState() {
            return (SimpleJavaScriptExtensionState) super.getState();
        }

        public void setPrefix(String prefix) {
            getState().setPrefix(prefix);
            requestRepaint();
        }

        public void greetRpc(String message) {
            getRpcProxy(SimpleJavaScriptExtensionClientRpc.class)
                    .greet(message);
        }

        public void greetCallback(String message) {
            invokeCallback("greetToClient", message);
        }
    }

    @Override
    protected void setup(WrappedRequest request) {
        final SimpleJavascriptExtension simpleJavascriptExtension = new SimpleJavascriptExtension();
        simpleJavascriptExtension.setPrefix("Prefix: ");
        addExtension(simpleJavascriptExtension);
        addComponent(new Button("Send rpc greeting",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        simpleJavascriptExtension.greetRpc("Rpc greeting");
                    }
                }));
        addComponent(new Button("Send callback greeting",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        simpleJavascriptExtension
                                .greetCallback("Callback greeting");
                    }
                }));
    }

    @Override
    protected String getTestDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
