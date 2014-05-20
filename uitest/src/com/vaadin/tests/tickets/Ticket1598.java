package com.vaadin.tests.tickets;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.LegacyApplication;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

public class Ticket1598 extends LegacyApplication {

    LegacyWindow main = new LegacyWindow("MenuBar test");

    final MenuBar menuBar = new MenuBar();

    @Override
    public void init() {
        setMainWindow(main);
        setTheme("runo");

        List<MenuItem> itemList = new ArrayList<MenuItem>();
        // Populate the menu bar
        for (int i = 0; i < 6; i++) {
            itemList.add(menuBar.addItem(new String("Menu " + i), null, null));
        }

        MenuItem first = itemList.get(0);

        for (int i = 0; i < 5; i++) {
            first.addItem(new String("Submenu item" + i), null, new Command() {

                @Override
                public void menuSelected(MenuItem selected) {
                    main.showNotification("Action " + selected.getText());
                }
            });
        }

        MenuItem firstSubItem1 = first.getChildren().get(1);

        for (int i = 0; i < 3; i++) {
            firstSubItem1.addItem(new String("Subsubmenu item" + i), null,
                    new Command() {

                        @Override
                        public void menuSelected(MenuItem selected) {
                            main.showNotification("Action "
                                    + selected.getText());
                        }
                    });
        }
        MenuItem firstSubItem2 = first.getChildren().get(3);

        for (int i = 0; i < 3; i++) {
            firstSubItem2.addItem(new String("Subsubmenu item" + i), null,
                    new Command() {

                        @Override
                        public void menuSelected(MenuItem selected) {
                            main.showNotification("Action "
                                    + selected.getText());
                        }
                    });
        }

        MenuItem second = menuBar.getItems().get(1);

        for (int i = 0; i < 5; i++) {
            second.addItem(new String("Second submenu item" + i), null,
                    new Command() {

                        @Override
                        public void menuSelected(MenuItem selected) {
                            main.showNotification("Action "
                                    + selected.getText());
                        }
                    });
        }

        MenuItem third = menuBar.getItems().get(2);
        third.setIcon(new ThemeResource("icons/16/document.png"));

        for (int i = 2; i <= 3; i++) {
            (menuBar.getItems().get(i)).setCommand(new Command() {

                @Override
                public void menuSelected(MenuItem selectedItem) {
                    main.showNotification("Action " + selectedItem.getText());
                }
            });
        }

        final MenuItem fourth = menuBar.getItems().get(3);
        fourth.setText("Add new item");

        fourth.setCommand(new Command() {
            @Override
            public void menuSelected(MenuItem selected) {
                menuBar.addItem("Newborn", null, null);
            }
        });

        final MenuItem fifth = menuBar.getItems().get(4);
        for (int i = 0; i < 5; i++) {
            fifth.addItem("Another subitem " + i, null);
        }

        final MenuItem last = menuBar.getItems().get(menuBar.getSize() - 1);
        last.setText("Remove me!");

        // A command for removing the selected menuitem
        Command removeCommand = new Command() {

            @Override
            public void menuSelected(MenuItem selected) {
                MenuItem parent = selected.getParent();
                if (parent != null) {
                    parent.removeChild(selected);
                } else {
                    menuBar.removeItem(selected);
                }
            }
        };

        last.setCommand(removeCommand);

        main.addComponent(menuBar);

    }
}
