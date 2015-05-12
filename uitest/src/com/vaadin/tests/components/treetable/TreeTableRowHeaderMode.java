package com.vaadin.tests.components.treetable;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Table.RowHeaderMode;
import com.vaadin.ui.TreeTable;

/**
 * Test UI for RowHeaderMode.ICON_ONLY in TreeTable.
 * 
 * @author Vaadin Ltd
 */
public class TreeTableRowHeaderMode extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Resource icon = new ThemeResource("../runo/icons/16/ok.png");

        TreeTable tree = new TreeTable();
        tree.addContainerProperty("Name", String.class, "");
        tree.setRowHeaderMode(RowHeaderMode.ICON_ONLY);

        Object item = tree.addItem(new Object[] { "name" }, null);
        tree.setItemIcon(item, icon);

        tree.setCollapsed(item, false);

        addComponent(tree);
    }

    @Override
    protected String getTestDescription() {
        return "RowHeaderMode.ICON_ONLY shouldn't create an empty column in TreeTable";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14799;
    }

}