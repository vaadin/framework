package com.vaadin.tests.server.container;

import java.util.Collection;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Container.Hierarchical;
import com.vaadin.data.Container.Sortable;

public class AbstractHierarchicalContainerTest extends AbstractContainerTest {
    private void validateHierarchicalContainer(Hierarchical container,
            Object expectedFirstItemId, Object expectedLastItemId,
            Object itemIdInSet, Object itemIdNotInSet, int expectedSize,
            int expectedRootSize) {

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
            Collection<?> children = container.getChildren(rootId);
            assertNotNull(rootId + " should have children", children);
            assertTrue(rootId + " should have children", (children.size() > 0));

            // getParent
            for (Object childId : children) {
                assertEquals(container.getParent(childId), rootId);
            }
        }

        // isRoot should return false for unknown items (#4215)
        assertFalse(container.isRoot(itemIdNotInSet));

        // hasChildren should return false for unknown items
        assertFalse(container.hasChildren(itemIdNotInSet));

        // areChildrenAllowed should return false for unknown items (#4216)
        assertFalse(container.areChildrenAllowed(itemIdNotInSet));

        // removeItem of unknown items should return false
        assertFalse(container.removeItem(itemIdNotInSet));

        assertEquals(countNodes(container), expectedSize);

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
        validateHierarchicalContainer(container, "com",
                "com.vaadin.util.SerializerHelper",
                "com.vaadin.terminal.ApplicationResource", "blah",
                sampleData.length + packages, 1);

    }

    protected void testHierarchicalSorting(Container.Hierarchical container) {
        Container.Sortable sortable = (Sortable) container;

        initializeContainer(container);

        // Must be able to sort based on PROP1 and PROP2 for this test
        assertTrue(sortable.getSortableContainerPropertyIds().contains(PROP1));
        assertTrue(sortable.getSortableContainerPropertyIds().contains(PROP2));

        sortable.sort(new Object[] { PROP1 }, new boolean[] { true });

        int packages = 21;
        validateHierarchicalContainer(container, "com",
                "com.vaadin.util.SerializerHelper",
                "com.vaadin.terminal.ApplicationResource", "blah",
                sampleData.length + packages, 1);

        sortable.sort(new Object[] { PROP2 }, new boolean[] { true });

        validateHierarchicalContainer(container,
                "com.vaadin.terminal.gwt.server.ApplicationPortlet2",
                "com.vaadin.data.util.ObjectProperty",
                "com.vaadin.terminal.ApplicationResource", "blah",
                sampleData.length + packages, 1);

    }

    protected void testHierarchicalFiltering(Container.Hierarchical container) {
        Container.Filterable filterable = (Container.Filterable) container;

        initializeContainer(container);

        // Filter by "contains ab"
        filterable.addContainerFilter(PROP1, "ab", false, false);

        validateHierarchicalContainer(container,
                "com.vaadin.data.BufferedValidatable",
                "com.vaadin.ui.TabSheet",
                "com.vaadin.terminal.gwt.client.Focusable",
                "com.vaadin.data.Buffered", 20, 0);

        // filter out every second item except hierarchy items
        filterable.removeAllContainerFilters();
        filterable.addContainerFilter(PROP3, "1", false, false);

        int packages = 21;
        int other = sampleData.length / 2;
        validateHierarchicalContainer(container, "com", "com.vaadin.util",
                "com.vaadin.data.util.IndexedContainer",
                "com.vaadin.data.util.ObjectProperty", packages + other, 0);

        // Additionally remove all without 'm'. Hierarchy is now one root only.
        filterable.addContainerFilter(PROP1, "m", false, false);

        validateHierarchicalContainer(container, "com.vaadin.data.Buffered",
                "com.vaadin.terminal.gwt.server.ComponentSizeValidator",
                "com.vaadin.data.util.IndexedContainer",
                "com.vaadin.terminal.gwt.client.ui.VUriFragmentUtility",
                packages + other, 0);
        //
        // int packages = 21;
        // validateHierarchicalContainer(container, "com",
        // "com.vaadin.util.SerializerHelper",
        // "com.vaadin.terminal.ApplicationResource", "blah",
        // sampleData.length + packages, 1);
        //
        // sortable.sort(new Object[] { PROP2 }, new boolean[] { true });
        //
        // validateHierarchicalContainer(container,
        // "com.vaadin.terminal.gwt.server.ApplicationPortlet2",
        // "com.vaadin.data.util.ObjectProperty",
        // "com.vaadin.terminal.ApplicationResource", "blah",
        // sampleData.length + packages, 1);

    }

    protected void initializeContainer(Container.Hierarchical container) {
        container.removeAllItems();
        Object[] propertyIds = container.getContainerPropertyIds().toArray();
        for (Object propertyId : propertyIds) {
            container.removeContainerProperty(propertyId);
        }

        container.addContainerProperty(PROP1, String.class, "");
        container.addContainerProperty(PROP2, String.class, null);
        container.addContainerProperty(PROP3, Integer.class, null);

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
                item.getItemProperty(PROP1).setValue(path);
                item.getItemProperty(PROP2).setValue(reverse(path));
                item.getItemProperty(PROP3).setValue(1);
            }
            for (int j = 1; j < paths.length; j++) {
                String parent = path;
                path = path + "." + paths[j];

                // Adds "com" and other items multiple times so should return
                // null for all but the first time
                if (container.addItem(path) != null) {
                    assertTrue(container.setChildrenAllowed(path, false));

                    Item item = container.getItem(path);
                    item.getItemProperty(PROP1).setValue(path);
                    item.getItemProperty(PROP2).setValue(reverse(path));
                    item.getItemProperty(PROP3).setValue(1);

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
            item.getItemProperty(PROP1).setValue(sampleData[i]);
            item.getItemProperty(PROP2).setValue(reverse(sampleData[i]));
            item.getItemProperty(PROP3).setValue(i % 2);
        }
    }

}
