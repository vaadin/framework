/**
 *
 */
package com.vaadin.tests.components.tabsheet;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

public class TabSheetWithTabIds extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TabSheet tabSheet = new TabSheet();

        final Tab tab1 = tabSheet.addTab(new Label("Label 1"), "Tab 1", null);

        final Tab tab2 = tabSheet.addTab(new Label("Label 2"), "Tab 2", null);

        final Tab tab3 = tabSheet.addTab(new Label("Label 3"), "Tab 3", null);

        addComponent(tabSheet);

        Button b = new Button("Set ids", event -> {
            tab1.setId("tab1");
            tab2.setId("tab2");
            tab3.setId("tab3");
        });
        addComponent(b);

        Button b2 = new Button("Clear ids", event -> {
            tab1.setId(null);
            tab2.setId(null);
            tab3.setId(null);
        });
        addComponent(b2);
    }

    @Override
    protected String getTestDescription() {
        return "Add support for setId to TabSheet.Tab";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12064;
    }

}
