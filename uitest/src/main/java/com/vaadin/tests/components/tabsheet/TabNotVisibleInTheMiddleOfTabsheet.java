package com.vaadin.tests.components.tabsheet;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class TabNotVisibleInTheMiddleOfTabsheet extends AbstractTestUI {

    private TabSheet.Tab secondTab;

    @Override
    protected void setup(VaadinRequest request) {
        TabSheet tabSheet = new TabSheet();
        tabSheet.setWidth("600px");

        tabSheet.addTab(new Label("first visible tab"), "first visible tab");

        secondTab = tabSheet.addTab(new Label("second visible tab"),
                "second visible tab");

        for (int i = 3; i < 10; i++) {
            tabSheet.addTab(new Label("visible tab " + i), "visible tab " + i);
        }

        addComponent(new VerticalLayout(tabSheet, new Button(
                "Toggle second tab",
                event -> secondTab.setVisible(!secondTab.isVisible()))));
    }

    @Override
    protected Integer getTicketNumber() {
        return 10437;
    }

    @Override
    protected String getTestDescription() {
        return "First and third tab should have the usual gap "
                + "between them when second tab gets hidden.";
    }
}
