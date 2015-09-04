package com.vaadin.data.util;

public class HierarchicalContainerOrderedWrapperTest extends
        AbstractHierarchicalContainerTestBase {

    private HierarchicalContainerOrderedWrapper createContainer() {
        return new HierarchicalContainerOrderedWrapper(
                new ContainerHierarchicalWrapper(new IndexedContainer()));
    }

    public void testBasicOperations() {
        testBasicContainerOperations(createContainer());
    }

    public void testHierarchicalContainer() {
        testHierarchicalContainer(createContainer());
    }

    public void testContainerOrdered() {
        testContainerOrdered(createContainer());
    }

    public void testRemoveSubtree() {
        testRemoveHierarchicalWrapperSubtree(createContainer());
    }

}
