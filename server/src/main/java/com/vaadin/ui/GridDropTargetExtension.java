package com.vaadin.ui;

import com.vaadin.event.dnd.DropTargetExtension;
import com.vaadin.shared.ui.grid.GridDropTargetExtensionState;

public class GridDropTargetExtension extends DropTargetExtension<Grid> {
    public GridDropTargetExtension(Grid target) {
        super(target);
    }

    @Override
    protected GridDropTargetExtensionState getState() {
        return (GridDropTargetExtensionState) super.getState();
    }

    @Override
    protected GridDropTargetExtensionState getState(boolean markAsDirty) {
        return (GridDropTargetExtensionState) super.getState(markAsDirty);
    }
}
