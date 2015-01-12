package com.vaadin.tests.server.component.treetable;

import junit.framework.TestCase;

import com.vaadin.ui.TreeTable;

public class TreeTableSetContainerNullTest extends TestCase {

    public void testNullContainer() {
        TreeTable treeTable = new TreeTable();

        // should not cause an exception
        treeTable.setContainerDataSource(null);
    }
}
