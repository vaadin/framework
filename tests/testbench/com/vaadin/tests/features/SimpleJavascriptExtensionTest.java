/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.features;

import com.vaadin.annotations.LoadScripts;
import com.vaadin.terminal.AbstractJavascriptExtension;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.gwt.client.communication.ClientRpc;
import com.vaadin.terminal.gwt.client.communication.ServerRpc;
import com.vaadin.terminal.gwt.client.communication.SharedState;
import com.vaadin.tests.components.AbstractTestRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Root;

@LoadScripts({ "/statictestfiles/jsextension.js" })
public class SimpleJavascriptExtensionTest extends AbstractTestRoot {

    public static class SimpleJavascriptExtensionState extends SharedState {
        private String prefix;

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getPrefix() {
            return prefix;
        }
    }

    public static interface SimpleJavascriptExtensionClientRpc extends
            ClientRpc {
        public void greet(String message);
    }

    public static interface SimpleJavascriptExtensionServerRpc extends
            ServerRpc {
        public void greet(String message);
    }

    public static class SimpleJavascriptExtension extends
            AbstractJavascriptExtension {

        public SimpleJavascriptExtension() {
            registerRpc(new SimpleJavascriptExtensionServerRpc() {
                public void greet(String message) {
                    Root.getCurrentRoot().showNotification(
                            getState().getPrefix() + message);
                }
            });
        }

        @Override
        public SimpleJavascriptExtensionState getState() {
            return (SimpleJavascriptExtensionState) super.getState();
        }

        public void setPrefix(String prefix) {
            getState().setPrefix(prefix);
            requestRepaint();
        }

        public void greet(String message) {
            getRpcProxy(SimpleJavascriptExtensionClientRpc.class)
                    .greet(message);
        }
    }

    @Override
    protected void setup(WrappedRequest request) {
        final SimpleJavascriptExtension simpleJavascriptExtension = new SimpleJavascriptExtension();
        simpleJavascriptExtension.setPrefix("Prefix: ");
        addExtension(simpleJavascriptExtension);
        addComponent(new Button("Send greeting", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                simpleJavascriptExtension.greet("Greeted by button");
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
