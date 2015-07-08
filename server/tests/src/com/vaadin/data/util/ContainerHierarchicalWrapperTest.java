package com.vaadin.data.util;


public class ContainerHierarchicalWrapperTest extends
        AbstractHierarchicalContainerTestBase {

    public void testBasicOperations() {
        testBasicContainerOperations(new ContainerHierarchicalWrapper(
                new IndexedContainer()));
    }

    public void testHierarchicalContainer() {
        testHierarchicalContainer(new ContainerHierarchicalWrapper(
                new IndexedContainer()));
    }

    public void testRemoveSubtree() {
        testRemoveHierarchicalWrapperSubtree(new ContainerHierarchicalWrapper(
                new IndexedContainer()));
    }

}
