package com.vaadin.tests.components.treetable;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.ui.TreeTable;

@SuppressWarnings("deprecation")
public class TreeTableScrollOnExpand extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TreeTable t = new TreeTable();
        t.setSelectable(true);
        t.setImmediate(true);
        t.setSizeFull();
        t.addContainerProperty("Name", String.class, "null");
        for (int i = 1; i <= 100; i++) {
            String parentID = "Item " + i;
            t.addItem(new Object[] { parentID }, parentID);
            String childID = "Item " + (100 + i);
            t.addItem(new Object[] { childID }, childID);
            t.getContainerDataSource().setParent(childID, parentID);
        }
        addComponent(t);
    }

    @Override
    public Integer getTicketNumber() {
        return 18247;
    }

    @Override
    public String getTestDescription() {
        return "After selecting an item and scrolling it out of view, "
                + "TreeTable should not scroll to the "
                + "selected item when expanding an item.";
    }
}
