package com.vaadin.tests.server.container;

import java.util.Collection;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Container.Hierarchical;
import com.vaadin.data.Container.Sortable;

public class AbstractHierarchicalContainerTest extends AbstractContainerTest {

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
     * @param expectedSize
     *            Expected number of items in the container. Not related to
     *            hierarchy.
     * @param expectedTraversalSize
     *            Expected number of items found when traversing from the roots
     *            down to all available nodes.
     * @param expectedRootSize
     *            Expected number of root items
     */
    private void validateHierarchicalContainer(Hierarchical container,
            Object expectedFirstItemId, Object expectedLastItemId,
            Object itemIdInSet, Object itemIdNotInSet,
            boolean rootsHaveChildren, int expectedSize,
            int expectedTraversalSize, int expectedRootSize) {

        validateContainer(container, expectedFirstItemId, expectedLastItemId,
                itemIdInSet, itemIdNotInSet, expectedSize);

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

            // all roots have children allowed in this case
            assertTrue(container.areChildrenAllowed(rootId));

            // all roots have children in this case
            if (rootsHaveChildren) {
                Collection<?> children = container.getChildren(rootId);
                assertNotNull(rootId + " should have children", children);
                assertTrue(rootId + " should have children",
                        (children.size() > 0));

                // getParent
                for (Object childId : children) {
                    assertEquals(container.getParent(childId), rootId);
                }
            } else {

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

        assertEquals(expectedTraversalSize, countNodes(container));

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

        int packages = 21;
        int expectedSize = sampleData.length + packages;
        validateHierarchicalContainer(container, "com",
                "com.vaadin.util.SerializerHelper",
                "com.vaadin.terminal.ApplicationResource", "blah", true,
                expectedSize, expectedSize, 1);

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

        int packages = 21;
        int expectedSize = sampleData.length + packages;
        validateHierarchicalContainer(container, "com",
                "com.vaadin.util.SerializerHelper",
                "com.vaadin.terminal.ApplicationResource", "blah", true,
                expectedSize, expectedSize, 1);

        sortable.sort(new Object[] { REVERSE_FULLY_QUALIFIED_NAME },
                new boolean[] { true });

        validateHierarchicalContainer(container,
                "com.vaadin.terminal.gwt.server.ApplicationPortlet2",
                "com.vaadin.data.util.ObjectProperty",
                "com.vaadin.terminal.ApplicationResource", "blah", true,
                expectedSize, expectedSize, 1);

    }

    protected void testHierarchicalFiltering(Container.Hierarchical container) {
        Container.Filterable filterable = (Container.Filterable) container;

        initializeContainer(container);

        // Filter by "contains ab"
        filterable.addContainerFilter(FULLY_QUALIFIED_NAME, "ab", false, false);

        // 20 items should remain in the container but the root should be
        // filtered
        int expectedSize = 20;
        int expectedTraversalSize = 0;
        int expectedRoots = 0;

        validateHierarchicalContainer(container,
                "com.vaadin.data.BufferedValidatable",
                "com.vaadin.ui.TabSheet",
                "com.vaadin.terminal.gwt.client.Focusable", "blah", true,
                expectedSize, expectedTraversalSize, expectedRoots);

        // filter out every second item except hierarchy items
        filterable.removeAllContainerFilters();
        filterable.addContainerFilter(ID_NUMBER, "1", false, false);

        int packages = 21;
        int other = sampleData.length / 2;

        expectedSize = packages + other;
        expectedRoots = 1;
        expectedTraversalSize = expectedSize;

        validateHierarchicalContainer(container, "com", "com.vaadin.util",
                "com.vaadin.data.util.IndexedContainer", "blah", true,
                expectedSize, expectedTraversalSize, expectedRoots);

        // Additionally remove all without 'm' in the simple name. Hierarchy is
        // now one root only.
        filterable.addContainerFilter(SIMPLE_NAME, "m", false, false);

        expectedSize = 27;
        expectedRoots = 1;
        expectedTraversalSize = 1;

        validateHierarchicalContainer(container, "com",
                "com.vaadin.ui.UriFragmentUtility",
                "com.vaadin.terminal.gwt.client.ui.TreeImages", "blah", false,
                expectedSize, expectedTraversalSize, expectedRoots);

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

}
