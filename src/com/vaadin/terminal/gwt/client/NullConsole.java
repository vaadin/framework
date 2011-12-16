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

    public void dirUIDL(ValueMap u, ApplicationConfiguration cnf) {
    }

    public void error(String msg) {
        GWT.log(msg);
    }

    public void log(String msg) {
        GWT.log(msg);
    }

    public void printObject(Object msg) {
        GWT.log(msg.toString());
    }

    public void printLayoutProblems(ValueMap meta,
            ApplicationConnection applicationConnection,
            Set<Paintable> zeroHeightComponents,
            Set<Paintable> zeroWidthComponents) {
    }

    public void log(Throwable e) {
        GWT.log(e.getMessage(), e);
    }

    public void error(Throwable e) {
        GWT.log(e.getMessage(), e);
    }

    public void setQuietMode(boolean quietDebugMode) {
    }

    public void init() {
    }

}
