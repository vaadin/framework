package com.vaadin.tests.components.tabsheet;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class FirstTabNotVisibleInTabsheet extends AbstractReindeerTestUI {

    private TabSheet.Tab firstTab;

    @Override
    protected void setup(VaadinRequest request) {
        TabSheet tabSheet = new TabSheet();
        tabSheet.setWidth("600px");

        firstTab = tabSheet.addTab(new Label("first visible tab"),
                "first visible tab");

        for (int i = 2; i < 10; i++) {
            tabSheet.addTab(new Label("visible tab " + i), "visible tab " + i);
        }

        addComponent(new VerticalLayout(tabSheet, new Button("Toggle first tab",
                event -> firstTab.setVisible(!firstTab.isVisible()))));
    }

    @Override
    protected Integer getTicketNumber() {
        return 14644;
    }

    @Override
    protected String getTestDescription() {
        return "First tabsheet tab is not set visible back once it gets invisible";
    }
}
