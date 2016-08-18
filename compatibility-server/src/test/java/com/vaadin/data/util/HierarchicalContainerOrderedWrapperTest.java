package com.vaadin.data.util;

import org.junit.Test;

public class HierarchicalContainerOrderedWrapperTest
        extends AbstractHierarchicalContainerTestBase {

    private HierarchicalContainerOrderedWrapper createContainer() {
        return new HierarchicalContainerOrderedWrapper(
                new ContainerHierarchicalWrapper(new IndexedContainer()));
    }

    @Test
    public void testBasicOperations() {
        testBasicContainerOperations(createContainer());
    }

    @Test
    public void testHierarchicalContainer() {
        testHierarchicalContainer(createContainer());
    }

    @Test
    public void testContainerOrdered() {
        testContainerOrdered(createContainer());
    }

    @Test
    public void testRemoveSubtree() {
        testRemoveHierarchicalWrapperSubtree(createContainer());
    }

}
