package com.vaadin.tests.components.menubar;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

/**
 *
 * @since 7.2
 * @author Vaadin Ltd
 */
public class SpaceMenuBarNavigation extends AbstractReindeerTestUI
        implements Command {

    @Override
    protected void setup(VaadinRequest request) {
        MenuBar menuBar = new MenuBar();
        menuBar.addStyleName("menu-bar");

        MenuItem item = menuBar.addItem("menu", null);

        item.addItem("subitem", this);

        addComponent(menuBar);
    }

    @Override
    protected String getTestDescription() {
        return "Space key code should trigger menu actions/submenu";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12525;
    }

    @Override
    public void menuSelected(MenuItem selectedItem) {
        Label label = new Label("action result");
        label.addStyleName("action-result");
        addComponent(label);
    }

}
