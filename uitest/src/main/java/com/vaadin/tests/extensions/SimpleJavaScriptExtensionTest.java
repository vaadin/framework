package com.vaadin.tests.extensions;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.JavaScriptExtensionState;
import com.vaadin.shared.communication.ClientRpc;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.Notification;

import elemental.json.JsonArray;

public class SimpleJavaScriptExtensionTest extends AbstractReindeerTestUI {

    public static class SimpleJavaScriptExtensionState
            extends JavaScriptExtensionState {
        private String prefix;

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getPrefix() {
            return prefix;
        }
    }

    public static interface SimpleJavaScriptExtensionClientRpc
            extends ClientRpc {
        public void greet(String message);
    }

    public static interface SimpleJavaScriptExtensionServerRpc
            extends ServerRpc {
        public void greet(String message);
    }

    @JavaScript("/statictestfiles/jsextension.js")
    @StyleSheet("/VAADIN/external1.css")
    public static class SimpleJavascriptExtension
            extends AbstractJavaScriptExtension {

        public SimpleJavascriptExtension() {
            registerRpc(new SimpleJavaScriptExtensionServerRpc() {
                @Override
                public void greet(String message) {
                    Notification.show(getState().getPrefix() + message);
                }
            });
            addFunction("greetToServer", new JavaScriptFunction() {
                @Override
                public void call(JsonArray arguments) {
                    Notification.show(
                            getState().getPrefix() + arguments.getString(0));
                }
            });
        }

        @Override
        public SimpleJavaScriptExtensionState getState() {
            return (SimpleJavaScriptExtensionState) super.getState();
        }

        public void setPrefix(String prefix) {
            getState().setPrefix(prefix);
        }

        public void greetRpc(String message) {
            getRpcProxy(SimpleJavaScriptExtensionClientRpc.class)
                    .greet(message);
        }

        public void greetCallback(String message) {
            callFunction("greetToClient", message);
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        final SimpleJavascriptExtension simpleJavascriptExtension = new SimpleJavascriptExtension();
        simpleJavascriptExtension.setPrefix("Prefix: ");
        addExtension(simpleJavascriptExtension);
        addComponent(new Button("Send rpc greeting",
                event -> simpleJavascriptExtension.greetRpc("Rpc greeting")));
        addComponent(new Button("Send callback greeting",
                event -> simpleJavascriptExtension
                        .greetCallback("Callback greeting")));
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
