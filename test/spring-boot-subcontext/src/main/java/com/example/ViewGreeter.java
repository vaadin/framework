package com.example;

import com.vaadin.spring.annotation.SpringComponent;

@SpringComponent
public class ViewGreeter {
    private int counter = 0;

    public String sayHello() {
        return "Hello number " + counter++
                + " from bean with same scope as view " + toString();
    }
}
