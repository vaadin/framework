package com.vaadin.tests.components.menubar;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;

public class MenuBarToolTips extends TestBase {

    @Override
    protected void setup() {
        MenuBar bar = new MenuBar();
        bar.setDescription("Root Menu");

        // File
        final MenuItem file = bar.addItem("File", null);
        file.setDescription("File menu");

        MenuItem foo = file.addItem("Foo", null);
        foo.setDescription("File - Foo menu");

        MenuItem foobar = foo.addItem("Foobar", null);
        foobar.setDescription("File - Foo menu - Foobar menu");

        MenuItem bar2 = file.addItem("Bar", null);
        bar2.setDescription("File - Bar menu");

        // Edit
        MenuItem edit = bar.addItem("Edit", null);
        edit.setDescription("Edit menu");

        MenuItem foo2 = edit.addItem("Foo", null);
        foo2.setDescription("Edit - Foo menu");

        MenuItem bar3 = edit.addItem("Bar", null);
        bar3.setDescription("Edit - Bar menu");

        addComponent(bar);
    }

    @Override
    protected String getDescription() {
        return "There should be tooltips on the menubar and its items";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5919;
    }

}
