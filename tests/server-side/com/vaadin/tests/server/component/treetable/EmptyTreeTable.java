package com.vaadin.tests.server.component.treetable;

import junit.framework.TestCase;

import com.vaadin.ui.TreeTable;

public class EmptyTreeTable extends TestCase {
    public void testLastId() {
        TreeTable treeTable = new TreeTable();

        assertFalse(treeTable.isLastId(treeTable.getValue()));
    }
}
