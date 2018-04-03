/**
 *
 */
package com.vaadin.tests.components.tabsheet;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;

public class TabSheetHotKeysWithModifiers extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TabSheet tabSheet = new TabSheet();
        tabSheet.setWidth("500px");
        tabSheet.setHeight("500px");
        tabSheet.addTab(new Label("Tab 1"), "Tab 1").setClosable(true);
        tabSheet.addTab(new Label("Tab 2"), "Tab 2").setClosable(true);
        tabSheet.addTab(new Label("Tab 3"), "Tab 3").setClosable(true);
        setContent(tabSheet);
    }

    @Override
    protected String getTestDescription() {
        return "Hot keys (left and right arrow keys and the delete key) should be ignored when they are pressed simultaneously with modifier keys";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12178;
    }

}
