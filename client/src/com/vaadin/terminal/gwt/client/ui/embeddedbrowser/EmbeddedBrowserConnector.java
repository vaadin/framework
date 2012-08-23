package com.vaadin.terminal.gwt.client.ui.embeddedbrowser;

import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.embeddedbrowser.EmbeddedBrowserState;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentConnector;

@Connect(com.vaadin.ui.EmbeddedBrowser.class)
public class EmbeddedBrowserConnector extends AbstractComponentConnector {

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public VEmbeddedBrowser getWidget() {
        return (VEmbeddedBrowser) super.getWidget();
    }

    @Override
    public EmbeddedBrowserState getState() {
        return (EmbeddedBrowserState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {

        super.onStateChanged(stateChangeEvent);

        getWidget().setAlternateText(getState().getAlternateText());
        getWidget().setSource(
                getState().getSource() != null ? getState().getSource()
                        .getURL() : null);
        getWidget().setName(getConnectorId());
    }

}
