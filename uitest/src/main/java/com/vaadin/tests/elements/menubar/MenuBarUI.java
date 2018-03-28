package com.vaadin.tests.elements.menubar;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

/**
 *
 */
@SuppressWarnings("serial")
public class MenuBarUI extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(createDefaultMenuBar("", ""));
        addComponent(createDefaultMenuBar("2", ""));
        addComponent(createDefaultMenuBar("", "2"));
    }

    private MenuBar createDefaultMenuBar(String topLevelItemSuffix,
            String secondaryLevelItemSuffix) {
        MenuBar menuBar = new MenuBar();
        MenuItem file = menuBar.addItem("File" + topLevelItemSuffix, null);
        file.addItem("Open" + secondaryLevelItemSuffix, new MenuBarCommand())
                .setDescription("<b>Preformatted</b>\ndescription");
        file.addItem("Save" + secondaryLevelItemSuffix, new MenuBarCommand())
                .setDescription("plain description,\n <b>HTML</b> is visible",
                        ContentMode.TEXT);
        file.addItem("Save As.." + secondaryLevelItemSuffix,
                new MenuBarCommand());
        file.addSeparator();

        MenuItem export = file.addItem("Export.." + secondaryLevelItemSuffix,
                null);
        export.addItem("As PDF..." + secondaryLevelItemSuffix,
                new MenuBarCommand());
        export.addItem("As Doc..." + secondaryLevelItemSuffix,
                new MenuBarCommand());

        file.addSeparator();
        file.addItem("Exit" + secondaryLevelItemSuffix, new MenuBarCommand())
                .setDescription("<b>HTML</b><br/>description",
                        ContentMode.HTML);

        MenuItem edit = menuBar.addItem("Edit" + topLevelItemSuffix, null);
        edit.addItem("Copy" + secondaryLevelItemSuffix, new MenuBarCommand());
        edit.addItem("Cut" + secondaryLevelItemSuffix, new MenuBarCommand());
        edit.addItem("Paste" + secondaryLevelItemSuffix, new MenuBarCommand());

        menuBar.addItem("Help" + topLevelItemSuffix, new MenuBarCommand());
        return menuBar;
    }

    @Override
    protected String getTestDescription() {
        return "UI used to validate MenuBarElement API";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13364;
    }

    private class MenuBarCommand implements Command {

        @Override
        public void menuSelected(MenuItem selectedItem) {
        }

    }
}
