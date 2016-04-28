package com.vaadin.tests.tooltip;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.MenuBar;

public class MenuBarTooltip extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        MenuBar menubar = new MenuBar();

        MenuBar.MenuItem menuitem = menubar.addItem("Menu item", null, null);
        menuitem.setDescription("Menu item description");

        MenuBar.MenuItem submenuitem1 = menuitem.addItem("Submenu item 1", null, null);
        submenuitem1.setDescription("Submenu item 1 description");

        MenuBar.MenuItem submenuitem2 = menuitem.addItem("Submenu item 2", null, null);
        submenuitem2.setDescription("Submenu item 2 description");

        addComponent(menubar);
    }

    @Override
    protected Integer getTicketNumber() {
        return 14854;
    }

    @Override
    protected String getTestDescription() {
        return "MenuItem tooltip should have a larger z-index than MenuBar/MenuItem.";
    }
}
