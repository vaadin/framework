package com.vaadin.tests.components.treetable;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Link;
import com.vaadin.ui.TreeTable;

public class DisappearingComponents extends AbstractTestUI {

    @Override
    public void setup(VaadinRequest request) {
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

        addComponent(tt);
    }

    @Override
    protected String getTestDescription() {
        return "TreeTable column component empty after expand+collapse when pageLength is set to zero";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7808;
    }

}
