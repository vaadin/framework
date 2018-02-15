package com.vaadin.tests.extensions;

import com.vaadin.server.AbstractExtension;
import com.vaadin.server.EventTrigger;
import com.vaadin.shared.extension.PartInformationState;

public class EventTriggerExtension extends AbstractExtension {

    @Override
    protected PartInformationState getState() {
        return (PartInformationState) super.getState();
    }

    @Override
    protected PartInformationState getState(boolean markAsDirty) {
        return (PartInformationState) super.getState(markAsDirty);
    }

    public void extend(EventTrigger eventTrigger) {
        super.extend(eventTrigger.getConnector());
        getState().partInformation = eventTrigger.getPartInformation();
    }
}
