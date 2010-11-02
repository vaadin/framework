package com.vaadin.ui;

import com.vaadin.terminal.gwt.client.ui.VSplitPanelVertical;

@ClientWidget(VSplitPanelVertical.class)
public class VerticalSplitPanel extends SplitPanel {
    public VerticalSplitPanel() {
        super(ORIENTATION_VERTICAL);
    }
}
