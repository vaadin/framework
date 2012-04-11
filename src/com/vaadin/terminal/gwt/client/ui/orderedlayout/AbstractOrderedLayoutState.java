package com.vaadin.terminal.gwt.client.ui.orderedlayout;

import com.vaadin.terminal.gwt.client.ui.AbstractLayoutState;

public class AbstractOrderedLayoutState extends AbstractLayoutState {
    private boolean spacing = false;

    public boolean isSpacing() {
        return spacing;
    }

    public void setSpacing(boolean spacing) {
        this.spacing = spacing;
    }

}