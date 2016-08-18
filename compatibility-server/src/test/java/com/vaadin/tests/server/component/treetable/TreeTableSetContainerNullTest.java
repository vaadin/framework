package com.vaadin.tests.server.component.treetable;

import org.junit.Test;

import com.vaadin.ui.TreeTable;

public class TreeTableSetContainerNullTest {

    @Test
    public void testNullContainer() {
        TreeTable treeTable = new TreeTable();

        // should not cause an exception
        treeTable.setContainerDataSource(null);
    }
}
