package com.vaadin.tests.components.tabsheet;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.themes.ValoTheme;

public class TabSheetInSplitPanel extends UI {

    @Override
    protected void init(VaadinRequest request) {
        VerticalSplitPanel verticalSplitter = new VerticalSplitPanel();
        setContent(verticalSplitter);
        verticalSplitter.setSizeFull();
        TabSheet t = new TabSheet();
        t.setHeight("100%");
        t.addTab(new Label("Hello in tab"), "Hello tab");
        t.setStyleName(ValoTheme.TABSHEET_FRAMED);
        verticalSplitter.addComponent(t);
        verticalSplitter.addComponent(new Label("Hello"));

    }

}
