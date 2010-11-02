package com.vaadin.ui;

import com.vaadin.terminal.gwt.client.ui.VSplitPanelHorizontal;

@ClientWidget(VSplitPanelHorizontal.class)
public class HorizontalSplitPanel extends SplitPanel {
    public HorizontalSplitPanel() {
        super(ORIENTATION_HORIZONTAL);
    }
}
