package com.vaadin.tests.widgetset.client.helloworldfeature;

import java.util.logging.Logger;

import com.google.gwt.user.client.Window;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.Util;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.extensions.HelloWorldExtension;

@Connect(HelloWorldExtension.class)
public class HelloWorldExtensionConnector extends AbstractExtensionConnector {

    @Override
    public HelloWorldState getState() {
        return (HelloWorldState) super.getState();
    }

    @Override
    protected void init() {
        registerRpc(GreetAgainRpc.class, () -> greet());
    }

    @Override
    protected void extend(ServerConnector target) {
        greet();
    }

    private void greet() {
        String msg = getState().getGreeting() + " from "
                + Util.getConnectorString(this) + " attached to "
                + Util.getConnectorString(getParent());
        getLogger().info(msg);

        String response = Window.prompt(msg, "");
        getRpcProxy(HelloWorldRpc.class).onMessageSent(response);
    }

    private static Logger getLogger() {
        return Logger.getLogger(HelloWorldExtensionConnector.class.getName());
    }
}
