package com.vaadin.data.util;

import org.junit.Test;

public class ContainerHierarchicalWrapperTest
        extends AbstractHierarchicalContainerTestBase {

    @Test
    public void testBasicOperations() {
        testBasicContainerOperations(
                new ContainerHierarchicalWrapper(new IndexedContainer()));
    }

    @Test
    public void testHierarchicalContainer() {
        testHierarchicalContainer(
                new ContainerHierarchicalWrapper(new IndexedContainer()));
    }

    @Test
    public void testRemoveSubtree() {
        testRemoveHierarchicalWrapperSubtree(
                new ContainerHierarchicalWrapper(new IndexedContainer()));
    }

}
