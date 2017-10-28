package com.vaadin.tests.components.treetable;

import com.vaadin.tests.components.TestBase;
import com.vaadin.v7.ui.TreeTable;

public class AddNodesOnExpand extends TestBase {
    private TreeTable treetable;

    @Override
    public void setup() {
        treetable = new TreeTable();
        treetable.setImmediate(true);
        treetable.setWidth("100%");
        treetable.setHeight(null);
        treetable.setPageLength(0);
        treetable.addContainerProperty("foo", String.class, "");
        treetable.addExpandListener(event -> {
            Object openedItemId = event.getItemId();
            if (!treetable.hasChildren(openedItemId)) {
                for (int j = 0; j < 3; j++) {
                    treetable.addItem(new String[] { "Subitem " + j },
                            openedItemId + "-" + j);
                    treetable.setParent(openedItemId + "-" + j, openedItemId);
                }
            }
        });
        treetable.addCollapseListener(event -> {
            /* Uncomment this to "fix" the TreeTable */
            // orgTree.refreshRowCache();
        });

        for (int i = 0; i < 3; i++) {
            treetable.addItem(new String[] { "Item " + i }, Integer.valueOf(i));
        }

        addComponent(treetable);
    }

    @Override
    protected String getDescription() {
        return "Expanding a node and then collapsing it should not cause scrollbars to appear";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(8041);
    }
}
