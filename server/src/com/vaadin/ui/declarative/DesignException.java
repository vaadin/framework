package com.vaadin.ui.declarative;

@SuppressWarnings("serial")
public class DesignException extends RuntimeException {

    public DesignException() {
        super();
    }

    public DesignException(String message) {
        super(message);
    }

    public DesignException(String message, Throwable e) {
        super(message, e);
    }

}
