package com.vaadin.tests.server.container;

import com.vaadin.data.util.IndexedContainer;

public class TestIndexedContainer extends AbstractInMemoryContainerTest {

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

    public void testContainerOrdered() {
        testContainerOrdered(new IndexedContainer());
    }

    public void testContainerIndexed() {
        testContainerIndexed(new IndexedContainer(), sampleData[2], 2, true,
                "newItemId", true);
    }

    public void testItemSetChangeListeners() {
        IndexedContainer container = new IndexedContainer();
        ItemSetChangeCounter counter = new ItemSetChangeCounter();
        container.addListener(counter);

        String id1 = "id1";
        String id2 = "id2";
        String id3 = "id3";

        initializeContainer(container);
        counter.reset();
        container.addItem();
        counter.assertOnce();
        container.addItem(id1);
        counter.assertOnce();

        initializeContainer(container);
        counter.reset();
        container.addItemAt(0);
        counter.assertOnce();
        container.addItemAt(0, id1);
        counter.assertOnce();
        container.addItemAt(0, id2);
        counter.assertOnce();
        container.addItemAt(container.size(), id3);
        counter.assertOnce();
        // no notification if already in container
        container.addItemAt(0, id1);
        counter.assertNone();

        initializeContainer(container);
        counter.reset();
        container.addItemAfter(null);
        counter.assertOnce();
        container.addItemAfter(null, id1);
        counter.assertOnce();
        container.addItemAfter(id1);
        counter.assertOnce();
        container.addItemAfter(id1, id2);
        counter.assertOnce();
        container.addItemAfter(container.firstItemId());
        counter.assertOnce();
        container.addItemAfter(container.lastItemId());
        counter.assertOnce();
        container.addItemAfter(container.lastItemId(), id3);
        counter.assertOnce();
        // no notification if already in container
        container.addItemAfter(0, id1);
        counter.assertNone();

        initializeContainer(container);
        counter.reset();
        container.removeItem(sampleData[0]);
        counter.assertOnce();

        initializeContainer(container);
        counter.reset();
        // no notification for removing a non-existing item
        container.removeItem(id1);
        counter.assertNone();

        initializeContainer(container);
        counter.reset();
        container.removeAllItems();
        counter.assertOnce();
        // TODO already empty, but causes notification anyway
        container.removeAllItems();
        counter.assertOptional();

    }

}
