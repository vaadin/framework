package com.vaadin.tests.server.component.treetable;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.vaadin.ui.TreeTable;

public class EmptyTreeTableTest {

    @Test
    public void testLastId() {
        TreeTable treeTable = new TreeTable();

        assertFalse(treeTable.isLastId(treeTable.getValue()));
    }
}
