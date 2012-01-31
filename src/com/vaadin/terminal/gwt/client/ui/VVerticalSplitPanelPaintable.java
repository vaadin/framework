package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;

public class VVerticalSplitPanelPaintable extends VAbstractSplitPanelPaintable {

    @Override
    protected VAbstractSplitPanel createWidget() {
        return GWT.create(VSplitPanelVertical.class);
    }

}
