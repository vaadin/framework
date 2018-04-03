package com.vaadin.event;

/**
 * An interface used for listening to marked as dirty events.
 */
@FunctionalInterface
public interface MarkedAsDirtyListener extends ConnectorEventListener {

    /**
     * Method called when a client connector has been marked as dirty.
     *
     * @param event
     *         marked as dirty connector event object
     */
    void connectorMarkedAsDirty(MarkedAsDirtyConnectorEvent event);
}