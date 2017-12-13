package com.vaadin.test.cdi.views;

import javax.enterprise.context.Dependent;

@Dependent
public class GreetingService {

    private int count;

    public String getGreeting(String name) {
        ++count;
        if (name.isEmpty()) {
            return "Hello!";
        }
        return "Hello, " + name + "!";
    }

    public int getCallCount() {
        return count;
    }
}
