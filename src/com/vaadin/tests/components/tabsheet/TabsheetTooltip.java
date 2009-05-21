package com.vaadin.tests.components.tabsheet;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

public class TabsheetTooltip extends TestBase {

    @Override
    protected String getDescription() {
        return "The label inside the tabsheet should show a tooltip 'This is a label' and the tab should show a different tooltip 'This is a tab'";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2995;
    }

    @Override
    protected void setup() {
        TabSheet tabSheet = new TabSheet();
        Label l = new Label("Label");
        l.setDescription("This is a label");

        Tab tab = tabSheet.addTab(l, "Tab", null);
        tab.setDescription("This is a tab");

        addComponent(tabSheet);
    }
}
