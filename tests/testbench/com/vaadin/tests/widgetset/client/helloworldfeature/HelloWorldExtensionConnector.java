/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.tests.widgetset.client.helloworldfeature;

import com.google.gwt.user.client.Window;
import com.vaadin.shared.ui.Connect;
import com.vaadin.terminal.gwt.client.ServerConnector;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VConsole;
import com.vaadin.terminal.gwt.client.communication.RpcProxy;
import com.vaadin.terminal.gwt.client.extensions.AbstractExtensionConnector;
import com.vaadin.tests.extensions.HelloWorldExtension;

@Connect(HelloWorldExtension.class)
public class HelloWorldExtensionConnector extends AbstractExtensionConnector {
    HelloWorldRpc rpc = RpcProxy.create(HelloWorldRpc.class, this);

    @Override
    public HelloWorldState getState() {
        return (HelloWorldState) super.getState();
    }

    @Override
    protected void init() {
        registerRpc(GreetAgainRpc.class, new GreetAgainRpc() {
            @Override
            public void greetAgain() {
                greet();
            }
        });
    }

    @Override
    public void setParent(ServerConnector parent) {
        super.setParent(parent);
        greet();
    }

    private void greet() {
        String msg = getState().getGreeting() + " from "
                + Util.getConnectorString(this) + " attached to "
                + Util.getConnectorString(getParent());
        VConsole.log(msg);

        String response = Window.prompt(msg, "");
        rpc.onMessageSent(response);
    }
}
