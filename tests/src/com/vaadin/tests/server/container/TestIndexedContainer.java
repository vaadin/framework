package com.vaadin.tests.server.container;

import com.vaadin.data.Item;
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
        // already empty
        container.removeAllItems();
        counter.assertNone();

    }

    public void testAddRemoveContainerFilter() {
        IndexedContainer container = new IndexedContainer();
        ItemSetChangeCounter counter = new ItemSetChangeCounter();
        container.addListener(counter);

        // simply adding or removing container filters should cause events
        // (content changes)

        initializeContainer(container);
        counter.reset();
        container.addContainerFilter(SIMPLE_NAME, "a", true, false);
        counter.assertOnce();
        container.removeContainerFilters(SIMPLE_NAME);
        counter.assertOnce();
        container.addContainerFilter(SIMPLE_NAME, "a", true, false);
        counter.assertOnce();
        container.removeAllContainerFilters();
        counter.assertOnce();
    }

    // TODO other tests should check positions after removing filter etc,
    // here concentrating on listeners
    public void testItemSetChangeListenersFiltering() {
        IndexedContainer container = new IndexedContainer();
        ItemSetChangeCounter counter = new ItemSetChangeCounter();
        container.addListener(counter);

        counter.reset();
        container.addContainerFilter(FULLY_QUALIFIED_NAME, "Test", true, false);
        // no real change, so no notification required
        counter.assertNone();

        String id1 = "com.example.Test1";
        String id2 = "com.example.Test2";
        String id3 = "com.example.Other";

        // perform operations while filtering container

        Item item;

        initializeContainer(container);
        counter.reset();
        // passes filter
        item = container.addItem(id1);
        // no event if filtered out
        counter.assertNone();
        item.getItemProperty(FULLY_QUALIFIED_NAME).setValue(id1);
        counter.assertOnce();
        // passes filter but already in the container
        item = container.addItem(id1);
        counter.assertNone();

        initializeContainer(container);
        counter.reset();
        // passes filter after change
        item = container.addItemAt(0, id1);
        counter.assertNone();
        item.getItemProperty(FULLY_QUALIFIED_NAME).setValue(id1);
        counter.assertOnce();
        item = container.addItemAt(container.size(), id2);
        counter.assertNone();
        item.getItemProperty(FULLY_QUALIFIED_NAME).setValue(id2);
        counter.assertOnce();
        // passes filter but already in the container
        item = container.addItemAt(0, id1);
        counter.assertNone();
        item = container.addItemAt(container.size(), id2);
        counter.assertNone();

        initializeContainer(container);
        counter.reset();
        // passes filter
        item = container.addItemAfter(null, id1);
        counter.assertNone();
        item.getItemProperty(FULLY_QUALIFIED_NAME).setValue(id1);
        counter.assertOnce();
        item = container.addItemAfter(container.lastItemId(), id2);
        counter.assertNone();
        item.getItemProperty(FULLY_QUALIFIED_NAME).setValue(id2);
        counter.assertOnce();
        // passes filter but already in the container
        item = container.addItemAfter(null, id1);
        counter.assertNone();
        item = container.addItemAfter(container.lastItemId(), id2);
        counter.assertNone();

        // does not pass filter

        // TODO implement rest

        initializeContainer(container);
        counter.reset();
        item = container.addItemAfter(null, id3);
        counter.assertNone();
        item.getItemProperty(FULLY_QUALIFIED_NAME).setValue(id3);
        counter.assertNone();

        initializeContainer(container);
        counter.reset();
        item = container.addItemAfter(container.firstItemId(), id3);
        counter.assertNone();
        item.getItemProperty(FULLY_QUALIFIED_NAME).setValue(id3);
        counter.assertNone();

        initializeContainer(container);
        counter.reset();
        item = container.addItemAfter(container.lastItemId(), id3);
        counter.assertNone();
        item.getItemProperty(FULLY_QUALIFIED_NAME).setValue(id3);
        counter.assertNone();

        initializeContainer(container);
        counter.reset();
        item = container.addItemAt(0, id3);
        counter.assertNone();
        item.getItemProperty(FULLY_QUALIFIED_NAME).setValue(id3);
        counter.assertNone();

        initializeContainer(container);
        counter.reset();
        item = container.addItemAt(1, id3);
        counter.assertNone();
        item.getItemProperty(FULLY_QUALIFIED_NAME).setValue(id3);
        counter.assertNone();

        initializeContainer(container);
        counter.reset();
        item = container.addItemAt(container.size(), id3);
        counter.assertNone();
        item.getItemProperty(FULLY_QUALIFIED_NAME).setValue(id3);
        counter.assertNone();

        // passes filter

        initializeContainer(container);
        counter.reset();
        item = container.addItem(id1);
        counter.assertNone();
        item.getItemProperty(FULLY_QUALIFIED_NAME).setValue(id1);
        counter.assertOnce();
        container.removeItem(id1);
        counter.assertOnce();
        // already removed
        container.removeItem(id1);
        counter.assertNone();

        item = container.addItem(id3);
        counter.assertNone();
        item.getItemProperty(FULLY_QUALIFIED_NAME).setValue(id3);
        counter.assertNone();
        // not visible
        container.removeItem(id3);
        counter.assertNone();

        // remove all

        initializeContainer(container);
        item = container.addItem(id1);
        item.getItemProperty(FULLY_QUALIFIED_NAME).setValue(id1);
        counter.reset();
        container.removeAllItems();
        counter.assertOnce();
        // no visible items
        container.removeAllItems();
        counter.assertNone();
    }

}
