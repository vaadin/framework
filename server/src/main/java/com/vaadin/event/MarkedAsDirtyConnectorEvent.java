package com.vaadin.event;

import com.vaadin.server.ClientConnector;
import com.vaadin.ui.UI;

/**
 * Event which is fired for all registered MarkDirtyListeners when a
 * connector is marked as dirty.
 */
public class MarkedAsDirtyConnectorEvent extends ConnectorEvent {

    private final UI ui;

    public MarkedAsDirtyConnectorEvent(ClientConnector source, UI ui) {
        super(source);
        this.ui = ui;
    }

    /**
     * Get the UI for which the connector event was fired
     *
     * @return target ui for event
     */
    public UI getUi() {
        return ui;
    }
}