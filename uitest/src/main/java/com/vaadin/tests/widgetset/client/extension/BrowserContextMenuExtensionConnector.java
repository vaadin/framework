package com.vaadin.tests.widgetset.client.extension;

import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.contextclick.BrowserContextMenuInSubComponent.BrowserContextMenuExtension;

/**
 * Client-side connector of the {@link BrowserContextMenuExtension}.
 */
@Connect(BrowserContextMenuExtension.class)
public class BrowserContextMenuExtensionConnector
        extends AbstractExtensionConnector {

    @Override
    protected void extend(ServerConnector target) {
        getParent().getWidget().addDomHandler(event -> {
            // Stop context click events from propagating.
            event.stopPropagation();
        }, ContextMenuEvent.getType());
    }

    @Override
    public AbstractComponentConnector getParent() {
        return (AbstractComponentConnector) super.getParent();
    }
}
