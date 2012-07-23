package com.vaadin.tests.components.menubar;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

public class MenuBarNavigation extends TestBase implements Command {

    private MenuItem edit;
    private MenuItem file;
    private Log log;
    private MenuItem export;

    @Override
    protected void setup() {
        MenuBar mb = new MenuBar();
        file = mb.addItem("File", null);
        file.addItem("Open", this);
        file.addItem("Save", this);
        file.addItem("Save As..", this);
        file.addSeparator();
        export = file.addItem("Export..", null);
        export.addItem("As PDF...", this);
        file.addSeparator();
        file.addItem("Exit", this);
        edit = mb.addItem("Edit", null);
        edit.addItem("Copy", this);
        edit.addItem("Cut", this);
        edit.addItem("Paste", this);
        mb.addItem("Help", this);

        addComponent(mb);

        log = new Log(5);
        addComponent(log);
    }

    @Override
    protected String getDescription() {
        return "Test case for mouse and keyboard navigation in MenuBar";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5174;
    }

    @Override
    public void menuSelected(MenuItem selectedItem) {
        log.log("MenuItem " + getName(selectedItem) + " selected");
    }

    private String getName(MenuItem selectedItem) {
        String name = "";
        if (selectedItem.getParent() != null) {
            name = getName(selectedItem.getParent()) + "/";
        }
        return name + selectedItem.getText();
    }
}
