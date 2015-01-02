package com.vaadin.data.util;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;

public class HierarchicalContainerTest extends
        AbstractHierarchicalContainerTestBase {

    public void testBasicOperations() {
        testBasicContainerOperations(new HierarchicalContainer());
    }

    public void testFiltering() {
        testContainerFiltering(new HierarchicalContainer());
    }

    public void testSorting() {
        testContainerSorting(new HierarchicalContainer());
    }

    public void testOrdered() {
        testContainerOrdered(new HierarchicalContainer());
    }

    public void testHierarchicalSorting() {
        testHierarchicalSorting(new HierarchicalContainer());
    }

    public void testSortingAndFiltering() {
        testContainerSortingAndFiltering(new HierarchicalContainer());
    }

    public void testRemovingItemsFromFilteredContainer() {
        HierarchicalContainer container = new HierarchicalContainer();
        initializeContainer(container);
        container.setIncludeParentsWhenFiltering(true);
        container.addContainerFilter(FULLY_QUALIFIED_NAME, "ab", false, false);
        Object p1 = container.getParent("com.vaadin.ui.TabSheet");
        assertEquals("com.vaadin.ui", p1);

        container.removeItem("com.vaadin.ui.TabSheet");
        // Parent for the removed item must be null because the item is no
        // longer in the container
        p1 = container.getParent("com.vaadin.ui.TabSheet");
        assertNull("Parent should be null, is " + p1, p1);

        container.removeAllItems();
        p1 = container.getParent("com.vaadin.terminal.gwt.client.Focusable");
        assertNull("Parent should be null, is " + p1, p1);

    }

    public void testParentWhenRemovingFilterFromContainer() {
        HierarchicalContainer container = new HierarchicalContainer();
        initializeContainer(container);
        container.setIncludeParentsWhenFiltering(true);
        container.addContainerFilter(FULLY_QUALIFIED_NAME, "ab", false, false);
        Object p1 = container.getParent("com.vaadin.ui.TabSheet");
        assertEquals("com.vaadin.ui", p1);
        p1 = container
                .getParent("com.vaadin.terminal.gwt.client.ui.VPopupCalendar");
        assertNull(p1);
        container.removeAllContainerFilters();
        p1 = container
                .getParent("com.vaadin.terminal.gwt.client.ui.VPopupCalendar");
        assertEquals("com.vaadin.terminal.gwt.client.ui", p1);

    }

    public void testChangeParentInFilteredContainer() {
        HierarchicalContainer container = new HierarchicalContainer();
        initializeContainer(container);
        container.setIncludeParentsWhenFiltering(true);
        container.addContainerFilter(FULLY_QUALIFIED_NAME, "Tab", false, false);

        // Change parent of filtered item
        Object p1 = container.getParent("com.vaadin.ui.TabSheet");
        assertEquals("com.vaadin.ui", p1);
        container.setParent("com.vaadin.ui.TabSheet", "com.vaadin");
        p1 = container.getParent("com.vaadin.ui.TabSheet");
        assertEquals("com.vaadin", p1);
        container.setParent("com.vaadin.ui.TabSheet", "com");
        p1 = container.getParent("com.vaadin.ui.TabSheet");
        assertEquals("com", p1);
        container.setParent("com.vaadin.ui.TabSheet", null);
        p1 = container.getParent("com.vaadin.ui.TabSheet");
        assertNull(p1);

        // root -> non-root
        container.setParent("com.vaadin.ui.TabSheet", "com");
        p1 = container.getParent("com.vaadin.ui.TabSheet");
        assertEquals("com", p1);

    }

    public void testHierarchicalFilteringWithParents() {
        HierarchicalContainer container = new HierarchicalContainer();
        initializeContainer(container);
        container.setIncludeParentsWhenFiltering(true);

        // Filter by "contains ab"
        container.addContainerFilter(FULLY_QUALIFIED_NAME, "ab", false, false);

        // 20 items match the filters and the have 8 parents that should also be
        // included
        // only one root "com" should exist
        // filtered
        int expectedSize = 29;
        int expectedRoots = 1;

        validateHierarchicalContainer(container, "com",
                "com.vaadin.ui.TabSheet",
                "com.vaadin.terminal.gwt.client.Focusable", "blah", true,
                expectedSize, expectedRoots, true);

        // only include .gwt.client classes
        container.removeAllContainerFilters();
        container.addContainerFilter(FULLY_QUALIFIED_NAME, ".gwt.client.",
                false, false);

        int packages = 6;
        int classes = 112;

        expectedSize = packages + classes;
        expectedRoots = 1;

        validateHierarchicalContainer(container, "com",
                "com.vaadin.terminal.gwt.client.WidgetSet",
                "com.vaadin.terminal.gwt.client.ui.VSplitPanelVertical",
                "blah", true, expectedSize, expectedRoots, true);

        // Additionally remove all without 'm' in the simple name.
        container.addContainerFilter(SIMPLE_NAME, "m", false, false);

        expectedSize = 7 + 18;
        expectedRoots = 1;

        validateHierarchicalContainer(
                container,
                "com",
                "com.vaadin.terminal.gwt.client.ui.VUriFragmentUtility",
                "com.vaadin.terminal.gwt.client.ui.layout.ChildComponentContainer",
                "blah", true, expectedSize, expectedRoots, true);

    }

    public void testRemoveLastChild() {
        HierarchicalContainer c = new HierarchicalContainer();

        c.addItem("root");
        assertEquals(false, c.hasChildren("root"));

        c.addItem("child");
        c.setParent("child", "root");
        assertEquals(true, c.hasChildren("root"));

        c.removeItem("child");
        assertFalse(c.containsId("child"));
        assertNull(c.getChildren("root"));
        assertNull(c.getChildren("child"));
        assertFalse(c.hasChildren("child"));
        assertFalse(c.hasChildren("root"));
    }

    public void testRemoveLastChildFromFiltered() {
        HierarchicalContainer c = new HierarchicalContainer();

        c.addItem("root");
        assertEquals(false, c.hasChildren("root"));

        c.addItem("child");
        c.setParent("child", "root");
        assertEquals(true, c.hasChildren("root"));

        // Dummy filter that does not remove any items
        c.addContainerFilter(new Filter() {

            @Override
            public boolean passesFilter(Object itemId, Item item)
                    throws UnsupportedOperationException {
                return true;
            }

            @Override
            public boolean appliesToProperty(Object propertyId) {
                return true;
            }
        });
        c.removeItem("child");

        assertFalse(c.containsId("child"));
        assertNull(c.getChildren("root"));
        assertNull(c.getChildren("child"));
        assertFalse(c.hasChildren("child"));
        assertFalse(c.hasChildren("root"));
    }

    public void testHierarchicalFilteringWithoutParents() {
        HierarchicalContainer container = new HierarchicalContainer();

        initializeContainer(container);
        container.setIncludeParentsWhenFiltering(false);

        // Filter by "contains ab"
        container.addContainerFilter(SIMPLE_NAME, "ab", false, false);

        // 20 items match the filter.
        // com.vaadin.data.BufferedValidatable
        // com.vaadin.data.Validatable
        // com.vaadin.terminal.gwt.client.Focusable
        // com.vaadin.terminal.gwt.client.Paintable
        // com.vaadin.terminal.gwt.client.ui.Table
        // com.vaadin.terminal.gwt.client.ui.VLabel
        // com.vaadin.terminal.gwt.client.ui.VScrollTable
        // com.vaadin.terminal.gwt.client.ui.VTablePaging
        // com.vaadin.terminal.gwt.client.ui.VTabsheet
        // com.vaadin.terminal.gwt.client.ui.VTabsheetBase
        // com.vaadin.terminal.gwt.client.ui.VTabsheetPanel
        // com.vaadin.server.ChangeVariablesErrorEvent
        // com.vaadin.server.Paintable
        // com.vaadin.server.Scrollable
        // com.vaadin.server.Sizeable
        // com.vaadin.server.VariableOwner
        // com.vaadin.ui.Label
        // com.vaadin.ui.Table
        // com.vaadin.ui.TableFieldFactory
        // com.vaadin.ui.TabSheet
        // all become roots.
        int expectedSize = 20;
        int expectedRoots = 20;

        validateHierarchicalContainer(container,
                "com.vaadin.data.BufferedValidatable",
                "com.vaadin.ui.TabSheet",
                "com.vaadin.terminal.gwt.client.ui.VTabsheetBase", "blah",
                true, expectedSize, expectedRoots, false);

        // only include .gwt.client classes
        container.removeAllContainerFilters();
        container.addContainerFilter(FULLY_QUALIFIED_NAME, ".gwt.client.",
                false, false);

        int packages = 3;
        int classes = 110;

        expectedSize = packages + classes;
        expectedRoots = 35 + 1; // com.vaadin.terminal.gwt.client.ui +
        // com.vaadin.terminal.gwt.client.*

        // Sorting is case insensitive
        validateHierarchicalContainer(container,
                "com.vaadin.terminal.gwt.client.ApplicationConfiguration",
                "com.vaadin.terminal.gwt.client.WidgetSet",
                "com.vaadin.terminal.gwt.client.ui.VOptionGroup", "blah", true,
                expectedSize, expectedRoots, false);

        // Additionally remove all without 'P' in the simple name.
        container.addContainerFilter(SIMPLE_NAME, "P", false, false);

        expectedSize = 13;
        expectedRoots = expectedSize;

        validateHierarchicalContainer(container,
                "com.vaadin.terminal.gwt.client.Paintable",
                "com.vaadin.terminal.gwt.client.ui.VTabsheetPanel",
                "com.vaadin.terminal.gwt.client.ui.VPopupCalendar", "blah",
                true, expectedSize, expectedRoots, false);

    }
}
