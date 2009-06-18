package com.vaadin.tests.components.tabsheet;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;

public class TabSheetWithoutTabCaption extends TestBase {

    @Override
    protected String getDescription() {
        return "There should be a tabsheet with one tab visible. The tab has no caption and contains a label saying 'Tab contents'.";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void setup() {
        final TabSheet moduleArea = new TabSheet();
        Label label = new Label("Tab contents");
        moduleArea.addTab(label);
        addComponent(moduleArea);
    }

}
