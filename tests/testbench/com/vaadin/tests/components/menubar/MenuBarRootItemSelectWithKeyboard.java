package com.vaadin.tests.components.menubar;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

public class MenuBarRootItemSelectWithKeyboard extends TestBase {

    @Override
    protected void setup() {
        Command c = new Command() {
            @Override
            public void menuSelected(MenuItem selectedItem) {
                getMainWindow().showNotification(selectedItem.getText());

            }
        };

        MenuBar root = new MenuBar();

        MenuItem submenu = root.addItem("Hello", null);
        submenu.addItem("World", c);

        root.addItem("World", c);
        addComponent(root);
    }

    @Override
    protected String getDescription() {
        return "When selecting an root menu item from the menubar with the keyboard (enter) the selection should be removed";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5180;
    }

}
