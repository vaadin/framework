package com.vaadin.tests.widgetset.client.helloworldfeature;

import com.vaadin.shared.communication.SharedState;

public class HelloWorldState extends SharedState {
    private String greeting = "Hello world";

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }
}
