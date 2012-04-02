/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.communication;

import java.io.Serializable;

import com.google.gwt.event.shared.EventHandler;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent.StateChangeHandler;

public class StateChangeEvent extends
        AbstractServerConnectorEvent<StateChangeHandler> {
    /**
     * Type of this event, used by the event bus.
     */
    public static final Type<StateChangeHandler> TYPE = new Type<StateChangeHandler>();

    @Override
    public Type<StateChangeHandler> getAssociatedType() {
        return TYPE;
    }

    public StateChangeEvent() {
    }

    @Override
    public void dispatch(StateChangeHandler listener) {
        listener.onStateChanged(this);
    }

    public interface StateChangeHandler extends Serializable, EventHandler {
        public void onStateChanged(StateChangeEvent stateChangeEvent);
    }
}
