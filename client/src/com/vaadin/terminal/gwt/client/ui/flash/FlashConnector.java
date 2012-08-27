package com.vaadin.terminal.gwt.client.ui.flash;

import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.flash.FlashState;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentConnector;

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
                getState().getSource() != null ? getState().getSource()
                        .getURL() : null);
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
