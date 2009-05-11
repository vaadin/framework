package com.vaadin.terminal.gwt.server;

import java.util.Map;

import com.vaadin.ui.Component;
import com.vaadin.ui.AbstractComponent.ComponentErrorEvent;

@SuppressWarnings("serial")
public class ChangeVariablesErrorEvent implements ComponentErrorEvent {

    private Throwable throwable;
    private Component component;

    private Map variableChanges;

    public ChangeVariablesErrorEvent(Component component, Throwable throwable,
            Map variableChanges) {
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

    public Map getVariableChanges() {
        return variableChanges;
    }

}