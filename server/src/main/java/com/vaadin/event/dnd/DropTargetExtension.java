package com.vaadin.event.dnd;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.AbstractExtension;
import com.vaadin.shared.ui.dnd.DropTargetState;

public class DropTargetExtension extends AbstractExtension {

    @Override
    public void extend(AbstractClientConnector target) {
        super.extend(target);
    }

    public void setDropEffect(DropEffect dropEffect) {
        getState().dropEffect = dropEffect.name();
    }

    public enum DropEffect {
        /**
         * A copy of the source item is made at the new location.
         */
        COPY,

        /**
         * An item is moved to a new location.
         */
        MOVE,

        /**
         * A link is established to the source at the new location.
         */
        LINK,

        /**
         * The item may not be dropped.
         */
        NONE
    }


    @Override
    protected DropTargetState getState() {
        return (DropTargetState) super.getState();
    }

    @Override
    protected DropTargetState getState(boolean markAsDirty) {
        return (DropTargetState) super.getState(markAsDirty);
    }
}
