package com.vaadin.client.connectors.grid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.SimplePanel;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.renderers.ClickableRenderer;
import com.vaadin.client.widget.grid.RendererCellReference;

public abstract class ComponentRenderer extends ClickableRenderer<String, SimplePanel> {

    @Override
    public SimplePanel createWidget() {
        SimplePanel panel = GWT.create(SimplePanel.class);
        panel.setStyleName("component-wrap");
        addClickHandler(panel);
        return panel;
    }

    @Override
    public void render(RendererCellReference cell, String connectorId,
                       SimplePanel widget) {
        if (connectorId != null) {
            ComponentConnector connector = (ComponentConnector) ConnectorMap
                    .get(this.getConnectorConnection()).getConnector(connectorId);
            widget.setWidget(connector.getWidget());
        } else if (widget.getWidget() != null) {
            widget.remove(widget.getWidget());
        }
        addClickHandler(widget);
    }

    public abstract ApplicationConnection getConnectorConnection();

    private void addClickHandler(SimplePanel panel) {
        panel.iterator().forEachRemaining(widget -> widget.addDomHandler(this, ClickEvent.getType()));
    }

}
