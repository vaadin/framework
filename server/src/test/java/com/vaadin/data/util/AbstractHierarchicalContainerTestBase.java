package com.vaadin.data.util;

import java.util.Collection;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Hierarchical;
import com.vaadin.data.Container.Sortable;
import com.vaadin.data.Item;

public abstract class AbstractHierarchicalContainerTestBase extends
        AbstractContainerTestBase {

    /**
     * @param container
     *            The container to validate
     * @param expectedFirstItemId
     *            Expected first item id
     * @param expectedLastItemId
     *            Expected last item id
     * @param itemIdInSet
     *            An item id that is in the container
     * @param itemIdNotInSet
     *            An item id that is not in the container
     * @param checkGetItemNull
     *            true if getItem() should return null for itemIdNotInSet, false
     *            to skip the check (container.containsId() is checked in any
     *            case)
     * @param expectedSize
     *            Expected number of items in the container. Not related to
     *            hierarchy.
     * @param expectedTraversalSize
     *            Expected number of items found when traversing from the roots
     *            down to all available nodes.
     * @param expectedRootSize
     *            Expected number of root items
     * @param rootsHaveChildren
     *            true if all roots have children, false otherwise (skips some
     *            asserts)
     */
    protected void validateHierarchicalContainer(Hierarchical container,
            Object expectedFirstItemId, Object expectedLastItemId,
            Object itemIdInSet, Object itemIdNotInSet,
            boolean checkGetItemNull, int expectedSize, int expectedRootSize,
            boolean rootsHaveChildren) {

        validateContainer(container, expectedFirstItemId, expectedLastItemId,
                itemIdInSet, itemIdNotInSet, checkGetItemNull, expectedSize);

        // rootItemIds
        Collection<?> rootIds = container.rootItemIds();
        assertEquals(expectedRootSize, rootIds.size());

        for (Object rootId : rootIds) {
            // All roots must be in container
            assertTrue(container.containsId(rootId));

            // All roots must have no parent
            assertNull(container.getParent(rootId));

            // all roots must be roots
            assertTrue(container.isRoot(rootId));

            if (rootsHaveChildren) {
                // all roots have children allowed in this case
                assertTrue(container.areChildrenAllowed(rootId));

                // all roots have children in this case
                Collection<?> children = container.getChildren(rootId);
                assertNotNull(rootId + " should have children", children);
                assertTrue(rootId + " should have children",
                        (children.size() > 0));
                // getParent
                for (Object childId : children) {
                    assertEquals(container.getParent(childId), rootId);
                }

            }
        }

        // isRoot should return false for unknown items
        assertFalse(container.isRoot(itemIdNotInSet));

        // hasChildren should return false for unknown items
        assertFalse(container.hasChildren(itemIdNotInSet));

        // areChildrenAllowed should return false for unknown items
        assertFalse(container.areChildrenAllowed(itemIdNotInSet));

        // removeItem of unknown items should return false
        assertFalse(container.removeItem(itemIdNotInSet));

        assertEquals(expectedSize, countNodes(container));

        validateHierarchy(container);
    }

    private int countNodes(Hierarchical container) {
        int totalNodes = 0;
        for (Object rootId : container.rootItemIds()) {
            totalNodes += countNodes(container, rootId);
        }

        return totalNodes;
    }

    private int countNodes(Hierarchical container, Object itemId) {
        int nodes = 1; // This
        Collection<?> children = container.getChildren(itemId);
        if (children != null) {
            for (Object id : children) {
                nodes += countNodes(container, id);
            }
        }

        return nodes;
    }

    private void validateHierarchy(Hierarchical container) {
        for (Object rootId : container.rootItemIds()) {
            validateHierarchy(container, rootId, null);
        }
    }

    private void validateHierarchy(Hierarchical container, Object itemId,
            Object parentId) {
        Collection<?> children = container.getChildren(itemId);

        // getParent
        assertEquals(container.getParent(itemId), parentId);

        if (!container.areChildrenAllowed(itemId)) {
            // If no children is allowed the item should have no children
            assertFalse(container.hasChildren(itemId));
            assertTrue(children == null || children.size() == 0);

            return;
        }
        if (children != null) {
            for (Object id : children) {
                validateHierarchy(container, id, itemId);
            }
        }
    }

    protected void testHierarchicalContainer(Container.Hierarchical container) {
        initializeContainer(container);

        int packages = 21 + 3;
        int expectedSize = sampleData.length + packages;
        validateHierarchicalContainer(container, "com",
                "org.vaadin.test.LastClass",
                "com.vaadin.server.ApplicationResource", "blah", true,
                expectedSize, 2, true);

    }

    protected void testHierarchicalSorting(Container.Hierarchical container) {
        Container.Sortable sortable = (Sortable) container;

        initializeContainer(container);

        // Must be able to sort based on PROP1 and PROP2 for this test
        assertTrue(sortable.getSortableContainerPropertyIds().contains(
                FULLY_QUALIFIED_NAME));
        assertTrue(sortable.getSortableContainerPropertyIds().contains(
                REVERSE_FULLY_QUALIFIED_NAME));

        sortable.sort(new Object[] { FULLY_QUALIFIED_NAME },
                new boolean[] { true });

        int packages = 21 + 3;
        int expectedSize = sampleData.length + packages;
        validateHierarchicalContainer(container, "com",
                "org.vaadin.test.LastClass",
                "com.vaadin.server.ApplicationResource", "blah", true,
                expectedSize, 2, true);

        sortable.sort(new Object[] { REVERSE_FULLY_QUALIFIED_NAME },
                new boolean[] { true });

        validateHierarchicalContainer(container,
                "com.vaadin.server.ApplicationPortlet2",
                "com.vaadin.data.util.ObjectProperty",
                "com.vaadin.server.ApplicationResource", "blah", true,
                expectedSize, 2, true);

    }

    protected void initializeContainer(Container.Hierarchical container) {
        container.removeAllItems();
        Object[] propertyIds = container.getContainerPropertyIds().toArray();
        for (Object propertyId : propertyIds) {
            container.removeContainerProperty(propertyId);
        }

        container.addContainerProperty(FULLY_QUALIFIED_NAME, String.class, "");
        container.addContainerProperty(SIMPLE_NAME, String.class, "");
        container.addContainerProperty(REVERSE_FULLY_QUALIFIED_NAME,
                String.class, null);
        container.addContainerProperty(ID_NUMBER, Integer.class, null);

        for (int i = 0; i < sampleData.length; i++) {
            String id = sampleData[i];

            // Add path as parent
            String paths[] = id.split("\\.");
            String path = paths[0];
            // Adds "com" and other items multiple times so should return null
            // for all but the first time
            if (container.addItem(path) != null) {
                assertTrue(container.setChildrenAllowed(path, false));
                Item item = container.getItem(path);
                item.getItemProperty(FULLY_QUALIFIED_NAME).setValue(path);
                item.getItemProperty(SIMPLE_NAME).setValue(getSimpleName(path));
                item.getItemProperty(REVERSE_FULLY_QUALIFIED_NAME).setValue(
                        reverse(path));
                item.getItemProperty(ID_NUMBER).setValue(1);
            }
            for (int j = 1; j < paths.length; j++) {
                String parent = path;
                path = path + "." + paths[j];

                // Adds "com" and other items multiple times so should return
                // null for all but the first time
                if (container.addItem(path) != null) {
                    assertTrue(container.setChildrenAllowed(path, false));

                    Item item = container.getItem(path);
                    item.getItemProperty(FULLY_QUALIFIED_NAME).setValue(path);
                    item.getItemProperty(SIMPLE_NAME).setValue(
                            getSimpleName(path));
                    item.getItemProperty(REVERSE_FULLY_QUALIFIED_NAME)
                            .setValue(reverse(path));
                    item.getItemProperty(ID_NUMBER).setValue(1);

                }
                assertTrue(container.setChildrenAllowed(parent, true));
                assertTrue(
                        "Failed to set " + parent + " as parent for " + path,
                        container.setParent(path, parent));
            }

            Item item = container.getItem(id);
            assertNotNull(item);
            String parent = id.substring(0, id.lastIndexOf('.'));
            assertTrue(container.setParent(id, parent));
            item.getItemProperty(FULLY_QUALIFIED_NAME).setValue(sampleData[i]);
            item.getItemProperty(SIMPLE_NAME).setValue(
                    getSimpleName(sampleData[i]));
            item.getItemProperty(REVERSE_FULLY_QUALIFIED_NAME).setValue(
                    reverse(sampleData[i]));
            item.getItemProperty(ID_NUMBER).setValue(i % 2);
        }
    }

    protected void testRemoveHierarchicalWrapperSubtree(
            Container.Hierarchical container) {
        initializeContainer(container);

        // remove root item
        removeItemRecursively(container, "org");

        int packages = 21 + 3 - 3;
        int expectedSize = sampleData.length + packages - 1;

        validateContainer(container, "com", "com.vaadin.util.SerializerHelper",
                "com.vaadin.server.ApplicationResource", "blah", true,
                expectedSize);

        // rootItemIds
        Collection<?> rootIds = container.rootItemIds();
        assertEquals(1, rootIds.size());
    }

    private void removeItemRecursively(Container.Hierarchical container,
            Object itemId) {
        if (container instanceof ContainerHierarchicalWrapper) {
            ((ContainerHierarchicalWrapper) container)
                    .removeItemRecursively("org");
        } else {
            HierarchicalContainer.removeItemRecursively(container, itemId);
        }
    }

}
