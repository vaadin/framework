/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import java.util.Set;

import com.google.gwt.core.client.GWT;

/**
 * Client side console implementation for non-debug mode that discards all
 * messages.
 * 
 */
public class NullConsole implements Console {

    @Override
    public void dirUIDL(ValueMap u, ApplicationConfiguration cnf) {
    }

    @Override
    public void error(String msg) {
        GWT.log(msg);
    }

    @Override
    public void log(String msg) {
        GWT.log(msg);
    }

    @Override
    public void printObject(Object msg) {
        GWT.log(msg.toString());
    }

    @Override
    public void printLayoutProblems(ValueMap meta,
            ApplicationConnection applicationConnection,
            Set<ComponentConnector> zeroHeightComponents,
            Set<ComponentConnector> zeroWidthComponents) {
    }

    @Override
    public void log(Throwable e) {
        GWT.log(e.getMessage(), e);
    }

    @Override
    public void error(Throwable e) {
        // Borrow exception handling from VDebugConsole
        VDebugConsole.handleError(e, this);
    }

    @Override
    public void setQuietMode(boolean quietDebugMode) {
    }

    @Override
    public void init() {
    }

}
