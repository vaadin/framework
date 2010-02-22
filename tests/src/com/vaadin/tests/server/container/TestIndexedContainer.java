package com.vaadin.tests.server.container;

import com.vaadin.data.util.IndexedContainer;

public class TestIndexedContainer extends AbstractContainerTest {

    public void testBasicOperations() {
        testBasicContainerOperations(new IndexedContainer());
    }

    public void testFiltering() {
        testContainerFiltering(new IndexedContainer());
    }

    public void testSorting() {
        testContainerSorting(new IndexedContainer());
    }

    public void testSortingAndFiltering() {
        testContainerSortingAndFiltering(new IndexedContainer());
    }

}
