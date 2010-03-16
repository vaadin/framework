package com.vaadin.tests.server.container;

import com.vaadin.data.util.HierarchicalContainer;

public class TestHierarchicalContainer extends
        AbstractHierarchicalContainerTest {

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
                "com.vaadin.terminal.gwt.client.Focusable", "blah",
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
                "blah", expectedSize, expectedRoots, true);

        // Additionally remove all without 'm' in the simple name.
        container.addContainerFilter(SIMPLE_NAME, "m", false, false);

        expectedSize = 7 + 18;
        expectedRoots = 1;

        validateHierarchicalContainer(
                container,
                "com",
                "com.vaadin.terminal.gwt.client.ui.VUriFragmentUtility",
                "com.vaadin.terminal.gwt.client.ui.layout.ChildComponentContainer",
                "blah", expectedSize, expectedRoots, true);

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
        // com.vaadin.terminal.gwt.server.ChangeVariablesErrorEvent
        // com.vaadin.terminal.Paintable
        // com.vaadin.terminal.Scrollable
        // com.vaadin.terminal.Sizeable
        // com.vaadin.terminal.VariableOwner
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
                expectedSize, expectedRoots, false);

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
                "com.vaadin.terminal.gwt.client.ui.VOptionGroup", "blah",
                expectedSize, expectedRoots, false);

        // Additionally remove all without 'P' in the simple name.
        container.addContainerFilter(SIMPLE_NAME, "P", false, false);

        expectedSize = 13;
        expectedRoots = expectedSize;

        validateHierarchicalContainer(container,
                "com.vaadin.terminal.gwt.client.Paintable",
                "com.vaadin.terminal.gwt.client.ui.VTabsheetPanel",
                "com.vaadin.terminal.gwt.client.ui.VPopupCalendar", "blah",
                expectedSize, expectedRoots, false);

    }
}
