package com.vaadin.tests.extensions;

import com.vaadin.server.AbstractExtension;
import com.vaadin.tests.widgetset.client.helloworldfeature.GreetAgainRpc;
import com.vaadin.tests.widgetset.client.helloworldfeature.HelloWorldRpc;
import com.vaadin.tests.widgetset.client.helloworldfeature.HelloWorldState;
import com.vaadin.ui.Notification;

public class HelloWorldExtension extends AbstractExtension {

    public HelloWorldExtension() {
        registerRpc(new HelloWorldRpc() {
            @Override
            public void onMessageSent(String message) {
                Notification.show(message);
            }
        });
    }

    @Override
    public HelloWorldState getState() {
        return (HelloWorldState) super.getState();
    }

    public void setGreeting(String greeting) {
        getState().setGreeting(greeting);
    }

    public String getGreeting() {
        return getState().getGreeting();
    }

    public void greetAgain() {
        getRpcProxy(GreetAgainRpc.class).greetAgain();
    }
}
