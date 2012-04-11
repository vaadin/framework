package com.vaadin.terminal.gwt.client.ui.absolutelayout;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.terminal.gwt.client.Connector;
import com.vaadin.terminal.gwt.client.ui.AbstractLayoutState;

public class AbsoluteLayoutState extends AbstractLayoutState {
    // Maps each component to a position
    private Map<String, String> connectorToCssPosition = new HashMap<String, String>();

    public String getConnectorPosition(Connector connector) {
        return connectorToCssPosition.get(connector.getConnectorId());
    }

    public Map<String, String> getConnectorToCssPosition() {
        return connectorToCssPosition;
    }

    public void setConnectorToCssPosition(
            Map<String, String> componentToCssPosition) {
        connectorToCssPosition = componentToCssPosition;
    }

}