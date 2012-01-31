package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;

public class VHorizontalSplitPanelPaintable extends
        VAbstractSplitPanelPaintable {

    @Override
    protected VAbstractSplitPanel createWidget() {
        return GWT.create(VSplitPanelHorizontal.class);
    }

}
