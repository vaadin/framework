/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client;

import com.google.gwt.core.client.GWT;

public class ClientExceptionHandler {

    public static void displayError(Throwable e) {
        displayError(e.getClass().getName() + ": " + e.getMessage());

        GWT.log(e.getMessage(), e);
    }

    public static void displayError(String msg) {

        Console console = ApplicationConnection.getConsole();

        if (console != null) {
            console.error(msg);
            GWT.log(msg);
        }
    }

    public static void displayError(String msg, Throwable e) {
        displayError(msg);
        displayError(e);

    }

}
