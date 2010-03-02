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

    public void testHierarchicalFiltering() {
        testHierarchicalFiltering(new HierarchicalContainer());
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

    // public void testHierarchicalSortingAndFiltering() {
    // testHierarchicalSortingAndFiltering(new HierarchicalContainer());
    // }

}
