package com.vaadin.client.extensions;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.server.BrowserPopupOpener;
import com.vaadin.shared.ui.BrowserPopupExtensionState;
import com.vaadin.shared.ui.Connect;

@Connect(BrowserPopupOpener.class)
public class BrowserPopupOpenerConnector extends AbstractExtensionConnector
        implements ClickHandler {

    @Override
    protected void extend(ServerConnector target) {
        final Widget targetWidget = ((ComponentConnector) target).getWidget();

        targetWidget.addDomHandler(this, ClickEvent.getType());
    }

    @Override
    public BrowserPopupExtensionState getState() {
        return (BrowserPopupExtensionState) super.getState();
    }

    @Override
    public void onClick(ClickEvent event) {
        String url = getResourceUrl("popup");
        if (url != null) {
            Window.open(url, getState().target, getState().features);
        }
    }
}
