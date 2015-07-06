package com.vaadin.tests.themes.base;

import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.MenuBar;

public class DisabledMenuBarItem extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        MenuBar menubar = new MenuBar();

        MenuBar.MenuItem item = menubar.addItem("Item", null);
        item.setEnabled(false);
        item.setIcon(new ThemeResource("common/icons/error.png"));

        addComponent(menubar);
    }

    @Override
    protected String getTestDescription() {
        return "Image icon should be greyed out.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 15381;
    }
}
