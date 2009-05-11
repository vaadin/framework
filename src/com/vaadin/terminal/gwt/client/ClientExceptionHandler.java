package com.vaadin.terminal.gwt.client;

public class ClientExceptionHandler {

    public static void displayError(Throwable e) {
        displayError(e.getMessage());
        e.printStackTrace();
    }

    public static void displayError(String msg) {

        Console console = ApplicationConnection.getConsole();

        if (console != null) {
            console.error(msg);
            // } else {
            // System.err.println(msg);
        }
    }

    public static void displayError(String msg, Throwable e) {
        displayError(msg);
        displayError(e);

    }

}
