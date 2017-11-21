package com.vaadin.v7.data.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;

import com.vaadin.v7.data.Container.Indexed.ItemAddEvent;
import com.vaadin.v7.data.Container.Indexed.ItemRemoveEvent;
import com.vaadin.v7.data.Container.ItemSetChangeListener;
import com.vaadin.v7.data.Item;

public class IndexedContainerTest extends AbstractInMemoryContainerTestBase {

    @Test
    public void testBasicOperations() {
        testBasicContainerOperations(new IndexedContainer());
    }

    @Test
    public void testFiltering() {
        testContainerFiltering(new IndexedContainer());
    }

    @Test
    public void testSorting() {
        testContainerSorting(new IndexedContainer());
    }

    @Test
    public void testSortingAndFiltering() {
        testContainerSortingAndFiltering(new IndexedContainer());
    }

    @Test
    public void testContainerOrdered() {
        testContainerOrdered(new IndexedContainer());
    }

    @Test
    public void testContainerIndexed() {
        testContainerIndexed(new IndexedContainer(), sampleData[2], 2, true,
                "newItemId", true);
    }

    @Test
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

    @Test
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
    @Test
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

    @Test
    public void testItemAdd_idSequence() {
        IndexedContainer container = new IndexedContainer();
        Object itemId;

        itemId = container.addItem();
        assertEquals(Integer.valueOf(1), itemId);

        itemId = container.addItem();
        assertEquals(Integer.valueOf(2), itemId);

        itemId = container.addItemAfter(null);
        assertEquals(Integer.valueOf(3), itemId);

        itemId = container.addItemAt(2);
        assertEquals(Integer.valueOf(4), itemId);
    }

    @Test
    public void testItemAddRemove_idSequence() {
        IndexedContainer container = new IndexedContainer();
        Object itemId;

        itemId = container.addItem();
        assertEquals(Integer.valueOf(1), itemId);

        container.removeItem(itemId);

        itemId = container.addItem();
        assertEquals(
                "Id sequence should continue from the previous value even if an item is removed",
                Integer.valueOf(2), itemId);
    }

    @Test
    public void testItemAddedEvent() {
        IndexedContainer container = new IndexedContainer();
        ItemSetChangeListener addListener = createListenerMockFor(container);
        addListener.containerItemSetChange(EasyMock.isA(ItemAddEvent.class));
        EasyMock.replay(addListener);

        container.addItem();

        EasyMock.verify(addListener);
    }

    @Test
    public void testItemAddedEvent_AddedItem() {
        IndexedContainer container = new IndexedContainer();
        ItemSetChangeListener addListener = createListenerMockFor(container);
        Capture<ItemAddEvent> capturedEvent = captureAddEvent(addListener);
        EasyMock.replay(addListener);

        Object itemId = container.addItem();

        assertEquals(itemId, capturedEvent.getValue().getFirstItemId());
    }

    @Test
    public void testItemAddedEvent_IndexOfAddedItem() {
        IndexedContainer container = new IndexedContainer();
        ItemSetChangeListener addListener = createListenerMockFor(container);
        container.addItem();
        Capture<ItemAddEvent> capturedEvent = captureAddEvent(addListener);
        EasyMock.replay(addListener);

        Object itemId = container.addItemAt(1);

        assertEquals(1, capturedEvent.getValue().getFirstIndex());
    }

    @Test
    public void testItemRemovedEvent() {
        IndexedContainer container = new IndexedContainer();
        Object itemId = container.addItem();
        ItemSetChangeListener removeListener = createListenerMockFor(container);
        removeListener
                .containerItemSetChange(EasyMock.isA(ItemRemoveEvent.class));
        EasyMock.replay(removeListener);

        container.removeItem(itemId);

        EasyMock.verify(removeListener);
    }

    @Test
    public void testItemRemovedEvent_RemovedItem() {
        IndexedContainer container = new IndexedContainer();
        Object itemId = container.addItem();
        ItemSetChangeListener removeListener = createListenerMockFor(container);
        Capture<ItemRemoveEvent> capturedEvent = captureRemoveEvent(
                removeListener);
        EasyMock.replay(removeListener);

        container.removeItem(itemId);

        assertEquals(itemId, capturedEvent.getValue().getFirstItemId());
    }

    @Test
    public void testItemRemovedEvent_indexOfRemovedItem() {
        IndexedContainer container = new IndexedContainer();
        container.addItem();
        Object secondItemId = container.addItem();
        ItemSetChangeListener removeListener = createListenerMockFor(container);
        Capture<ItemRemoveEvent> capturedEvent = captureRemoveEvent(
                removeListener);
        EasyMock.replay(removeListener);

        container.removeItem(secondItemId);

        assertEquals(1, capturedEvent.getValue().getFirstIndex());
    }

    @Test
    public void testItemRemovedEvent_amountOfRemovedItems() {
        IndexedContainer container = new IndexedContainer();
        container.addItem();
        container.addItem();
        ItemSetChangeListener removeListener = createListenerMockFor(container);
        Capture<ItemRemoveEvent> capturedEvent = captureRemoveEvent(
                removeListener);
        EasyMock.replay(removeListener);

        container.removeAllItems();

        assertEquals(2, capturedEvent.getValue().getRemovedItemsCount());
    }

    private Capture<ItemAddEvent> captureAddEvent(
            ItemSetChangeListener addListener) {
        Capture<ItemAddEvent> capturedEvent = new Capture<ItemAddEvent>();
        addListener.containerItemSetChange(EasyMock.capture(capturedEvent));
        return capturedEvent;
    }

    private Capture<ItemRemoveEvent> captureRemoveEvent(
            ItemSetChangeListener removeListener) {
        Capture<ItemRemoveEvent> capturedEvent = new Capture<ItemRemoveEvent>();
        removeListener.containerItemSetChange(EasyMock.capture(capturedEvent));
        return capturedEvent;
    }

    private ItemSetChangeListener createListenerMockFor(
            IndexedContainer container) {
        ItemSetChangeListener listener = EasyMock
                .createNiceMock(ItemSetChangeListener.class);
        container.addItemSetChangeListener(listener);
        return listener;
    }

    // Ticket 8028
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetItemIdsRangeIndexOutOfBounds() {
        IndexedContainer ic = new IndexedContainer();
        ic.getItemIds(-1, 10);
    }

    // Ticket 8028
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetItemIdsRangeIndexOutOfBounds2() {
        IndexedContainer ic = new IndexedContainer();
        ic.addItem(new Object());
        ic.getItemIds(2, 1);
    }

    // Ticket 8028
    @Test
    public void testGetItemIdsRangeZeroRange() {
        IndexedContainer ic = new IndexedContainer();
        ic.addItem(new Object());
        List<Object> itemIds = ic.getItemIds(1, 0);

        assertTrue(
                "Container returned actual values when asking for 0 items...",
                itemIds.isEmpty());
    }

    // Ticket 8028
    @Test(expected = IllegalArgumentException.class)
    public void testGetItemIdsRangeNegativeRange() {
        IndexedContainer ic = new IndexedContainer();
        ic.addItem(new Object());
        List<Object> itemIds = ic.getItemIds(1, -1);

        assertTrue(
                "Container returned actual values when asking for -1 items...",
                itemIds.isEmpty());
    }

    // Ticket 8028
    @Test
    public void testGetItemIdsRangeIndexOutOfBoundsDueToSizeChange() {
        IndexedContainer ic = new IndexedContainer();
        ic.addItem(new Object());
        assertEquals(
                "Container returned too many items when the range was >> container size",
                1, ic.getItemIds(0, 10).size());
    }

    // Ticket 8028
    @Test
    public void testGetItemIdsRangeBaseCase() {
        IndexedContainer ic = new IndexedContainer();
        String object1 = new String("Obj1");
        String object2 = new String("Obj2");
        String object3 = new String("Obj3");
        String object4 = new String("Obj4");
        String object5 = new String("Obj5");

        ic.addItem(object1);
        ic.addItem(object2);
        ic.addItem(object3);
        ic.addItem(object4);
        ic.addItem(object5);

        List<Object> itemIds = ic.getItemIds(1, 2);

        assertTrue(itemIds.contains(object2));
        assertTrue(itemIds.contains(object3));
        assertEquals(2, itemIds.size());
    }

    // test getting non-existing property (#10445)
    @Test
    public void testNonExistingProperty() {
        IndexedContainer ic = new IndexedContainer();
        String object1 = new String("Obj1");
        ic.addItem(object1);
        assertNull(ic.getContainerProperty(object1, "xyz"));
    }

    // test getting null property id (#10445)
    @Test
    public void testNullPropertyId() {
        IndexedContainer ic = new IndexedContainer();
        String object1 = new String("Obj1");
        ic.addItem(object1);
        assertNull(ic.getContainerProperty(object1, null));
    }
}
