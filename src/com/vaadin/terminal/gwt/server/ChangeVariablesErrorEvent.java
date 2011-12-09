/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.server;

import java.util.Map;

import com.vaadin.ui.AbstractComponent.ComponentErrorEvent;
import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public class ChangeVariablesErrorEvent implements ComponentErrorEvent {

    private Throwable throwable;
    private Component component;

    private Map<String, Object> variableChanges;

    public ChangeVariablesErrorEvent(Component component, Throwable throwable,
            Map<String, Object> variableChanges) {
        this.component = component;
        this.throwable = throwable;
        this.variableChanges = variableChanges;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public Component getComponent() {
        return component;
    }

    public Map<String, Object> getVariableChanges() {
        return variableChanges;
    }

}