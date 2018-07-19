package com.vaadin.tests.components.tabsheet;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;

public class TabsheetCloseSelectedTabs extends AbstractTestUI {

    private TabSheet tabsheet = new TabSheet();

    @Override
    protected void setup(VaadinRequest request) {
        generateTabs();
        tabsheet.setSizeFull();
        addComponent(tabsheet);
        addButton("Select last tab", event -> {
            tabsheet.setSelectedTab(tabsheet.getComponentCount() - 1);
        });
        addButton("Remove all tabs", event -> {
            while (tabsheet.getComponentCount() > 0) {
                tabsheet.removeTab(tabsheet.getTab(0));
            }
        });

    }

    private void generateTabs() {
        tabsheet.removeAllComponents();
        for (int i = 0; i < 100; ++i) {
            tabsheet.addTab(new Panel(), "Tab" + i);
        }
    }

}
