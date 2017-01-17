package com.vaadin.event.dnd;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractExtension;
import com.vaadin.shared.ui.dnd.DragSourceState;

public class DragSourceExtension extends AbstractExtension {

    @Override
    public void extend(AbstractClientConnector target) {
        super.extend(target);
    }

    @Override
    protected DragSourceState getState() {
        return (DragSourceState) super.getState();
    }

    @Override
    protected DragSourceState getState(boolean markAsDirty) {
        return (DragSourceState) super.getState(markAsDirty);
    }
}
