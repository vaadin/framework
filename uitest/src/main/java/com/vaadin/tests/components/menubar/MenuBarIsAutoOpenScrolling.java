package com.vaadin.tests.components.menubar;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;

public class MenuBarIsAutoOpenScrolling extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        MenuBar menuBar = new MenuBar();
        menuBar.setAutoOpen(true);
        menuBar.addStyleName("menu-bar");

        MenuItem item = menuBar.addItem("Item", null);

        for (int i = 1; i < 100; i++) {
            item.addItem("SubItem" + i, null);
        }

        addComponent(menuBar);
    }

    @Override
    protected String getTestDescription() {
        return "SubMenu (when MenuBar.isAutoOpen()) should not dissapear after a couple of seconds when the mouse pointer is over the scrollbar.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10456;
    }

}
