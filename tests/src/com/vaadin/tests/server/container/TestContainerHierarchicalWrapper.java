package com.vaadin.tests.server.container;

import java.util.Collection;

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

    public void testRemoveSubtree() {
        testRemoveHierarchicalWrapperSubtree(new ContainerHierarchicalWrapper(
                new IndexedContainer()));
    }

    protected void testRemoveHierarchicalWrapperSubtree(
            ContainerHierarchicalWrapper container) {
        initializeContainer(container);

        // remove root item
        container.removeItemRecursively("org");

        int packages = 21 + 3 - 3;
        int expectedSize = sampleData.length + packages - 1;

        validateContainer(container, "com", "com.vaadin.util.SerializerHelper",
                "com.vaadin.terminal.ApplicationResource", "blah", expectedSize);

        // rootItemIds
        Collection<?> rootIds = container.rootItemIds();
        assertEquals(1, rootIds.size());
    }

}
