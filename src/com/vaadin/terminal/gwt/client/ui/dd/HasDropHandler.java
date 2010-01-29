package com.vaadin.terminal.gwt.client.ui.dd;

import com.vaadin.terminal.gwt.client.Paintable;

/**
 * Used to detect Widget from widget tree that has {@link #getDropHandler()}
 * 
 * Decide whether to get rid of this class. If so, {@link AbstractDropHandler} must
 * extend {@link Paintable}.
 * 
 */
public interface HasDropHandler {
    public DropHandler getDropHandler();
}
