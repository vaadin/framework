package com.vaadin.v7.data.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Container.Indexed.ItemAddEvent;
import com.vaadin.v7.data.Container.Indexed.ItemRemoveEvent;
import com.vaadin.v7.data.Container.ItemSetChangeListener;
import com.vaadin.v7.data.Container.ItemSetChangeNotifier;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.filter.SimpleStringFilter;

public class GeneratedPropertyContainerBasicTest
        extends AbstractInMemoryContainerTestBase {

    @Test
    public void testBasicOperations() {
        testBasicContainerOperations(createContainer());
    }

    private GeneratedPropertyContainer createContainer() {
        return new GeneratedPropertyContainer(new IndexedContainer());
    }

    @Test
    public void testFiltering() {
        testContainerFiltering(createContainer());
    }

    @Test
    public void testSorting() {
        testContainerSorting(createContainer());
    }

    @Test
    public void testSortingAndFiltering() {
        testContainerSortingAndFiltering(createContainer());
    }

    @Test
    public void testContainerOrdered() {
        testContainerOrdered(createContainer());
    }

    @Test
    public void testContainerIndexed() {
        testContainerIndexed(createContainer(), sampleData[2], 2, true,
                "newItemId", true);
    }

    @Test
    public void testItemSetChangeListeners() {
        GeneratedPropertyContainer container = createContainer();
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
        GeneratedPropertyContainer container = createContainer();
        ItemSetChangeCounter counter = new ItemSetChangeCounter();
        container.addListener(counter);

        // simply adding or removing container filters should cause events
        // (content changes)

        initializeContainer(container);
        counter.reset();
        SimpleStringFilter filter = new SimpleStringFilter(SIMPLE_NAME, "a",
                true, false);
        container.addContainerFilter(filter);
        counter.assertOnce();
        container.removeContainerFilter(filter);
        counter.assertOnce();
        container.addContainerFilter(
                new SimpleStringFilter(SIMPLE_NAME, "a", true, false));
        counter.assertOnce();
        container.removeAllContainerFilters();
        counter.assertOnce();
    }

    // TODO other tests should check positions after removing filter etc,
    // here concentrating on listeners
    @Test
    public void testItemSetChangeListenersFiltering() {
        Container.Indexed container = createContainer();
        ItemSetChangeCounter counter = new ItemSetChangeCounter();
        ((GeneratedPropertyContainer) container).addListener(counter);

        counter.reset();
        ((Container.Filterable) container)
                .addContainerFilter(new SimpleStringFilter(FULLY_QUALIFIED_NAME,
                        "Test", true, false));
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
        GeneratedPropertyContainer container = createContainer();
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
        GeneratedPropertyContainer container = createContainer();
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
        GeneratedPropertyContainer container = createContainer();
        ItemSetChangeListener addListener = createListenerMockFor(container);
        addListener.containerItemSetChange(EasyMock.isA(ItemAddEvent.class));
        EasyMock.replay(addListener);

        container.addItem();

        EasyMock.verify(addListener);
    }

    @Test
    public void testItemAddedEvent_AddedItem() {
        GeneratedPropertyContainer container = createContainer();
        ItemSetChangeListener addListener = createListenerMockFor(container);
        Capture<ItemAddEvent> capturedEvent = captureAddEvent(addListener);
        EasyMock.replay(addListener);

        Object itemId = container.addItem();

        assertEquals(itemId, capturedEvent.getValue().getFirstItemId());
    }

    @Test
    public void testItemAddedEvent_IndexOfAddedItem() {
        GeneratedPropertyContainer container = createContainer();
        ItemSetChangeListener addListener = createListenerMockFor(container);
        container.addItem();
        Capture<ItemAddEvent> capturedEvent = captureAddEvent(addListener);
        EasyMock.replay(addListener);

        Object itemId = container.addItemAt(1);

        assertEquals(1, capturedEvent.getValue().getFirstIndex());
    }

    @Test
    public void testItemRemovedEvent() {
        GeneratedPropertyContainer container = createContainer();
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
        GeneratedPropertyContainer container = createContainer();
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
        GeneratedPropertyContainer container = createContainer();
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
        GeneratedPropertyContainer container = createContainer();
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
            ItemSetChangeNotifier container) {
        ItemSetChangeListener listener = EasyMock
                .createNiceMock(ItemSetChangeListener.class);
        container.addItemSetChangeListener(listener);
        return listener;
    }

    // Ticket 8028
    @Test
    public void testGetItemIdsRangeIndexOutOfBounds() {
        GeneratedPropertyContainer ic = createContainer();
        try {
            ic.getItemIds(-1, 10);
            fail("Container returned items starting from index -1, something very wrong here!");
        } catch (IndexOutOfBoundsException e) {
            // This is expected...
        } catch (Exception e) {
            // Should not happen!
            fail("Container threw unspecified exception when fetching a range of items and the range started from -1");
        }

    }

    // Ticket 8028
    @Test
    public void testGetItemIdsRangeIndexOutOfBounds2() {
        GeneratedPropertyContainer ic = createContainer();
        ic.addItem(new Object());
        try {
            ic.getItemIds(2, 1);
            fail("Container returned items starting from index -1, something very wrong here!");
        } catch (IndexOutOfBoundsException e) {
            // This is expected...
        } catch (Exception e) {
            // Should not happen!
            fail("Container threw unspecified exception when fetching a out of bounds range of items");
        }

    }

    // Ticket 8028
    @Test
    public void testGetItemIdsRangeZeroRange() {
        GeneratedPropertyContainer ic = createContainer();
        ic.addItem(new Object());
        try {
            List<Object> itemIds = (List<Object>) ic.getItemIds(1, 0);

            assertTrue(
                    "Container returned actual values when asking for 0 items...",
                    itemIds.isEmpty());
        } catch (Exception e) {
            // Should not happen!
            fail("Container threw unspecified exception when fetching 0 items...");
        }

    }

    // Ticket 8028
    @Test
    public void testGetItemIdsRangeNegativeRange() {
        GeneratedPropertyContainer ic = createContainer();
        ic.addItem(new Object());
        try {
            List<Object> itemIds = (List<Object>) ic.getItemIds(1, -1);

            assertTrue(
                    "Container returned actual values when asking for -1 items...",
                    itemIds.isEmpty());
        } catch (IllegalArgumentException e) {
            // this is expected

        } catch (Exception e) {
            // Should not happen!
            fail("Container threw unspecified exception when fetching -1 items...");
        }

    }

    // Ticket 8028
    @Test
    public void testGetItemIdsRangeIndexOutOfBoundsDueToSizeChange() {
        GeneratedPropertyContainer ic = createContainer();
        ic.addItem(new Object());
        Assert.assertEquals(
                "Container returned too many items when the range was >> container size",
                1, ic.getItemIds(0, 10).size());
    }

    // Ticket 8028
    @Test
    public void testGetItemIdsRangeBaseCase() {
        GeneratedPropertyContainer ic = createContainer();
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

        try {
            List<Object> itemIds = (List<Object>) ic.getItemIds(1, 2);

            assertTrue(itemIds.contains(object2));
            assertTrue(itemIds.contains(object3));
            assertEquals(2, itemIds.size());

        } catch (Exception e) {
            // Should not happen!
            fail("Container threw  exception when fetching a range of items ");
        }
    }

    // test getting non-existing property (#10445)
    @Test
    public void testNonExistingProperty() {
        Container ic = createContainer();
        String object1 = new String("Obj1");
        ic.addItem(object1);
        assertNull(ic.getContainerProperty(object1, "xyz"));
    }

    // test getting null property id (#10445)
    @Test
    public void testNullPropertyId() {
        Container ic = createContainer();
        String object1 = new String("Obj1");
        ic.addItem(object1);
        assertNull(ic.getContainerProperty(object1, null));
    }

    @Override
    protected void initializeContainer(Container container) {
        if (container instanceof GeneratedPropertyContainer) {
            super.initializeContainer(((GeneratedPropertyContainer) container)
                    .getWrappedContainer());
        } else {
            super.initializeContainer(container);
        }
    }
}
