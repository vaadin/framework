package com.vaadin.tests.server.container;

import com.vaadin.data.util.ContainerHierarchicalWrapper;
import com.vaadin.data.util.IndexedContainer;

public class TestContainerHierarchicalWrapper extends
        AbstractHierarchicalContainerTest {

    public void testBasicOperations() {
        testBasicContainerOperations(new ContainerHierarchicalWrapper(
                new IndexedContainer()));
    }

    public void testHierarchicalContainer() {
        testHierarchicalContainer(new ContainerHierarchicalWrapper(
                new IndexedContainer()));
    }

}
