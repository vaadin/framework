package com.vaadin.client.ui.embeddedbrowser;

import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.AbstractEmbeddedState;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.embeddedbrowser.EmbeddedBrowserState;

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
                getResourceUrl(AbstractEmbeddedState.SOURCE_RESOURCE));
        getWidget().setName(getConnectorId());
    }

}
