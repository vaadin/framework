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

package com.vaadin.tests.extensions;

import org.json.JSONArray;
import org.json.JSONException;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.JavaScriptExtensionState;
import com.vaadin.shared.communication.ClientRpc;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.Notification;

public class SimpleJavaScriptExtensionTest extends AbstractTestUI {

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
                @Override
                public void greet(String message) {
                    Notification.show(getState().getPrefix() + message);
                }
            });
            addFunction("greetToServer", new JavaScriptFunction() {
                @Override
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
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        simpleJavascriptExtension.greetRpc("Rpc greeting");
                    }
                }));
        addComponent(new Button("Send callback greeting",
                new Button.ClickListener() {
                    @Override
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
