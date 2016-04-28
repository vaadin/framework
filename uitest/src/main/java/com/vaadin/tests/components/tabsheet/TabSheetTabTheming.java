package com.vaadin.tests.components.tabsheet;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;

public class TabSheetTabTheming extends TestBase {

    @Override
    public void setup() {
        TabSheet tabsheet = new TabSheet();
        tabsheet.setStyleName("pg");
        tabsheet.addTab(new Label(), "Brown fox and the fence", null);
        tabsheet.addTab(new Label(), "Something about using all the keys", null);
        addComponent(tabsheet);
        setTheme("tests-tickets");
    }

    @Override
    protected String getDescription() {
        return "Changing tabs should not cause flickering, cut text or text that moves back and forth.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6781;
    }
}
