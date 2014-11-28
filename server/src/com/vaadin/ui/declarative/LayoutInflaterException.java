package com.vaadin.ui.declarative;

@SuppressWarnings("serial")
public class LayoutInflaterException extends RuntimeException {

    public LayoutInflaterException(String message) {
        super(message);
    }

    public LayoutInflaterException(String message, Throwable e) {
        super(message, e);
    }

    public LayoutInflaterException(Throwable e) {
        super(e);
    }

}
