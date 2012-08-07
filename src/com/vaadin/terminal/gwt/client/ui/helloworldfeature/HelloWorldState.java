/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.helloworldfeature;

import com.vaadin.terminal.gwt.client.communication.SharedState;

public class HelloWorldState extends SharedState {
    private String greeting = "Hello world";

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }
}
