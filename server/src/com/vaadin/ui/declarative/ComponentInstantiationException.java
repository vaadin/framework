package com.vaadin.ui.declarative;

@SuppressWarnings("serial")
public class ComponentInstantiationException extends RuntimeException {

    public ComponentInstantiationException() {
        super();
    }

    public ComponentInstantiationException(String message) {
        super(message);
    }

    public ComponentInstantiationException(String message, Throwable e) {
        super(message, e);
    }

}
