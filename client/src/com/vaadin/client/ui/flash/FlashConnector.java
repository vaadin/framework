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
        getWidget().setArchive(getState().archive);
        getWidget().setClassId(getState().classId);
        getWidget().setCodebase(getState().codebase);
        getWidget().setCodetype(getState().codetype);
        getWidget().setStandby(getState().standby);
        getWidget().setAlternateText(getState().alternateText);
        getWidget().setEmbedParams(getState().embedParams);

        getWidget().rebuildIfNeeded();
    }
}
