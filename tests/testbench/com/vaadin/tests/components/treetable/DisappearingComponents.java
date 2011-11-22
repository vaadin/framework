package com.vaadin.tests.components.treetable;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.Link;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.Window;

public class DisappearingComponents extends AbstractTestCase {

    @Override
    public void init() {
        Window mainWindow = new Window("Application");
        final TreeTable tt = new TreeTable();
        tt.setSizeUndefined();
        tt.setWidth("100%");
        tt.setImmediate(true);
        tt.setPageLength(0);
        tt.addContainerProperty("i", Integer.class, null);
        tt.addContainerProperty("link", Link.class, null);
        Object[] items = new Object[3];
        for (int i = 0; i < items.length; i++) {
            items[i] = tt
                    .addItem(
                            new Object[] {
                                    i + 1,
                                    new Link(String.valueOf(i + 1),
                                            new ExternalResource(
                                                    "http://www.google.fi")) },
                            null);
        }
        tt.setChildrenAllowed(items[0], false);
        tt.setChildrenAllowed(items[2], false);
        tt.setParent(items[2], items[1]);

        mainWindow.addComponent(tt);

        setMainWindow(mainWindow);
    }

    @Override
    protected String getDescription() {
        return "TreeTable column component empty after expand+collapse when pageLength is set to zero";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7808;
    }

}
