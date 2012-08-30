package com.vaadin.client.ui.flash;

import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.AbstractEmbeddedState;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.flash.FlashState;

@Connect(com.vaadin.ui.Flash.class)
public class FlashConnector extends AbstractComponentConnector {

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public VFlash getWidget() {
        return (VFlash) super.getWidget();
    }

    @Override
    public FlashState getState() {
        return (FlashState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {

        super.onStateChanged(stateChangeEvent);

        getWidget().setSource(
                getResourceUrl(AbstractEmbeddedState.SOURCE_RESOURCE));
        getWidget().setArchive(getState().getArchive());
        getWidget().setClassId(getState().getClassId());
        getWidget().setCodebase(getState().getCodebase());
        getWidget().setCodetype(getState().getCodetype());
        getWidget().setStandby(getState().getStandby());
        getWidget().setAlternateText(getState().getAlternateText());
        getWidget().setEmbedParams(getState().getEmbedParams());

        getWidget().rebuildIfNeeded();
    }
}
