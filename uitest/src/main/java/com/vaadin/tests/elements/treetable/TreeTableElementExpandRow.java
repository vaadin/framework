package com.vaadin.tests.elements.treetable;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.v7.ui.TreeTable;

public class TreeTableElementExpandRow extends AbstractTestUI {

    TreeTable treeTable = new TreeTable();
    public static final String TEST_VALUE = "testValue";

    @Override
    protected void setup(VaadinRequest request) {
        treeTable.setWidth("200px");
        treeTable.addContainerProperty("Name", String.class, "");
        treeTable.addItem(new Object[] { "item1" }, "item1");
        treeTable.addItem(new Object[] { "item1_1" }, "item1_1");
        treeTable.addItem(new Object[] { "item1_2" }, "item1_2");
        treeTable.setParent("item1_1", "item1");
        treeTable.setParent("item1_2", "item1");
        treeTable.addItem(new Object[] { TEST_VALUE }, TEST_VALUE);
        addComponent(treeTable);

    }

    @Override
    protected String getTestDescription() {
        return "Test TreeTableRowElement toggleExpanded() method expands/collapses the row.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13773;
    }

}
