package com.vaadin.tests.components.menubar;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

/**
 * Test UI for top click on expanded top level menu and sub-menus.
 *
 * @author Vaadin Ltd
 */
public class MenuBarClickOpenedMenu extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        MenuBar menuBar = new MenuBar();
        menuBar.addStyleName("top-level");
        MenuItem file = menuBar.addItem("File", null);
        file.setStyleName("first-level");
        MenuItem open = file.addItem("Open", null);
        open.setStyleName("second-level");
        MenuItem as = open.addItem("as", null);
        as.setStyleName("third-level");
        MenuItem leaf = as.addItem("Text", new MenuBarCommand());
        leaf.setStyleName("leaf");
        addComponent(menuBar);
    }

    @Override
    protected String getTestDescription() {
        return "Top level menu item should always close menu on click. "
                + "Submenu should not close if it's already opened";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14568;
    }

    private class MenuBarCommand implements Command {
        @Override
        public void menuSelected(MenuItem selectedItem) {
        }
    }

}
