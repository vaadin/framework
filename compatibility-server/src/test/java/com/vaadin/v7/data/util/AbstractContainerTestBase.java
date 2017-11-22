package com.vaadin.v7.data.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Container.Filterable;
import com.vaadin.v7.data.Container.ItemSetChangeEvent;
import com.vaadin.v7.data.Container.ItemSetChangeListener;
import com.vaadin.v7.data.Container.Ordered;
import com.vaadin.v7.data.Container.Sortable;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.filter.SimpleStringFilter;

public abstract class AbstractContainerTestBase {

    /**
     * Helper class for testing e.g. listeners expecting events to be fired.
     */
    protected abstract static class AbstractEventCounter {
        private int eventCount = 0;
        private int lastAssertedEventCount = 0;

        /**
         * Increment the event count. To be called by subclasses e.g. from a
         * listener method.
         */
        protected void increment() {
            ++eventCount;
        }

        /**
         * Check that no one event has occurred since the previous assert call.
         */
        public void assertNone() {
            assertEquals(lastAssertedEventCount, eventCount);
        }

        /**
         * Check that exactly one event has occurred since the previous assert
         * call.
         */
        public void assertOnce() {
            assertEquals(++lastAssertedEventCount, eventCount);
        }

        /**
         * Reset the counter and the expected count.
         */
        public void reset() {
            eventCount = 0;
            lastAssertedEventCount = 0;
        }
    }

    /**
     * Test class for counting item set change events and verifying they have
     * been received.
     */
    protected static class ItemSetChangeCounter extends AbstractEventCounter
            implements ItemSetChangeListener {

        @Override
        public void containerItemSetChange(ItemSetChangeEvent event) {
            increment();
        }

    }

    // #6043: for items that have been filtered out, Container interface does
    // not specify what to return from getItem() and getContainerProperty(), so
    // need checkGetItemNull parameter for the test to be usable for most
    // current containers
    protected void validateContainer(Container container,
            Object expectedFirstItemId, Object expectedLastItemId,
            Object itemIdInSet, Object itemIdNotInSet, boolean checkGetItemNull,
            int expectedSize) {
        Container.Indexed indexed = null;
        if (container instanceof Container.Indexed) {
            indexed = (Container.Indexed) container;
        }

        List<Object> itemIdList = new ArrayList<Object>(container.getItemIds());

        // size()
        assertEquals(expectedSize, container.size());
        assertEquals(expectedSize, itemIdList.size());

        // first item, last item
        Object first = itemIdList.get(0);
        Object last = itemIdList.get(itemIdList.size() - 1);

        assertEquals(expectedFirstItemId, first);
        assertEquals(expectedLastItemId, last);

        // containsId
        assertFalse(container.containsId(itemIdNotInSet));
        assertTrue(container.containsId(itemIdInSet));

        // getItem
        if (checkGetItemNull) {
            assertNull(container.getItem(itemIdNotInSet));
        }
        assertNotNull(container.getItem(itemIdInSet));

        // getContainerProperty
        for (Object propId : container.getContainerPropertyIds()) {
            if (checkGetItemNull) {
                assertNull(
                        container.getContainerProperty(itemIdNotInSet, propId));
            }
            assertNotNull(container.getContainerProperty(itemIdInSet, propId));
        }

        if (indexed != null) {
            // firstItemId
            assertEquals(first, indexed.firstItemId());

            // lastItemId
            assertEquals(last, indexed.lastItemId());

            // nextItemId
            assertEquals(itemIdList.get(1), indexed.nextItemId(first));

            // prevItemId
            assertEquals(itemIdList.get(itemIdList.size() - 2),
                    indexed.prevItemId(last));

            // isFirstId
            assertTrue(indexed.isFirstId(first));
            assertFalse(indexed.isFirstId(last));

            // isLastId
            assertTrue(indexed.isLastId(last));
            assertFalse(indexed.isLastId(first));

            // indexOfId
            assertEquals(0, indexed.indexOfId(first));
            assertEquals(expectedSize - 1, indexed.indexOfId(last));

            // getIdByIndex
            assertEquals(indexed.getIdByIndex(0), first);
            assertEquals(indexed.getIdByIndex(expectedSize - 1), last);

        }

        // getItemProperty
        assertNull(
                container.getItem(itemIdInSet).getItemProperty("notinset"));

    }

    protected static final Object FULLY_QUALIFIED_NAME = "fullyQualifiedName";
    protected static final Object SIMPLE_NAME = "simpleName";
    protected static final Object REVERSE_FULLY_QUALIFIED_NAME = "reverseFullyQualifiedName";
    protected static final Object ID_NUMBER = "idNumber";

    protected void testBasicContainerOperations(Container container) {
        initializeContainer(container);

        // Basic container
        validateContainer(container, sampleData[0],
                sampleData[sampleData.length - 1], sampleData[10], "abc", true,
                sampleData.length);

        validateRemovingItems(container);
        validateAddItem(container);
        if (container instanceof Container.Indexed) {
            validateAddItemAt((Container.Indexed) container);
        }
        if (container instanceof Container.Ordered) {
            validateAddItemAfter((Container.Ordered) container);
        }

    }

    protected void validateRemovingItems(Container container) {
        int sizeBeforeRemoving = container.size();

        List<Object> itemIdList = new ArrayList<Object>(container.getItemIds());
        // There should be at least four items in the list
        Object first = itemIdList.get(0);
        Object middle = itemIdList.get(2);
        Object last = itemIdList.get(itemIdList.size() - 1);

        container.removeItem(first);
        container.removeItem(middle); // Middle now that first has been removed
        container.removeItem(last);

        assertEquals(sizeBeforeRemoving - 3, container.size());

        container.removeAllItems();

        assertEquals(0, container.size());
    }

    protected void validateAddItem(Container container) {
        try {
            container.removeAllItems();

            Object id = container.addItem();
            assertTrue(container.containsId(id));
            assertNotNull(container.getItem(id));

            Item item = container.addItem("foo");
            assertNotNull(item);
            assertTrue(container.containsId("foo"));
            assertEquals(item, container.getItem("foo"));

            // Add again
            Item item2 = container.addItem("foo");
            assertNull(item2);

            // Null is not a valid itemId
            assertNull(container.addItem(null));
        } catch (UnsupportedOperationException e) {
            // Ignore contains which do not support addItem*
        }
    }

    protected void validateAddItemAt(Container.Indexed container) {
        try {
            container.removeAllItems();

            Object id = container.addItemAt(0);
            assertTrue(container.containsId(id));
            assertEquals(id, container.getIdByIndex(0));
            assertNotNull(container.getItem(id));

            Item item = container.addItemAt(0, "foo");
            assertNotNull(item);
            assertTrue(container.containsId("foo"));
            assertEquals(item, container.getItem("foo"));
            assertEquals("foo", container.getIdByIndex(0));

            Item itemAtEnd = container.addItemAt(2, "atend");
            assertNotNull(itemAtEnd);
            assertTrue(container.containsId("atend"));
            assertEquals(itemAtEnd, container.getItem("atend"));
            assertEquals("atend", container.getIdByIndex(2));

            // Add again
            Item item2 = container.addItemAt(0, "foo");
            assertNull(item2);
        } catch (UnsupportedOperationException e) {
            // Ignore contains which do not support addItem*
        }
    }

    protected void validateAddItemAfter(Container.Ordered container) {
        if (container instanceof AbstractBeanContainer) {
            // Doesn't work as bean container requires beans
            return;
        }

        try {
            container.removeAllItems();

            assertNotNull(container.addItem(0));

            Item item = container.addItemAfter(null, "foo");
            assertNotNull(item);
            assertTrue(container.containsId("foo"));
            assertEquals(item, container.getItem("foo"));
            assertEquals("foo",
                    container.getItemIds().iterator().next());

            Item itemAtEnd = container.addItemAfter(0, "atend");
            assertNotNull(itemAtEnd);
            assertTrue(container.containsId("atend"));
            assertEquals(itemAtEnd, container.getItem("atend"));
            Iterator<?> i = container.getItemIds().iterator();
            i.next();
            i.next();
            assertEquals("atend", i.next());

            // Add again
            assertNull(container.addItemAfter(null, "foo"));
            assertNull(container.addItemAfter("atend", "foo"));
            assertNull(container.addItemAfter("nonexistant", "123123"));
        } catch (UnsupportedOperationException e) {
            // Ignore contains which do not support addItem*
        }
    }

    protected void testContainerOrdered(Container.Ordered container) {
        // addItem with empty container
        Object id = container.addItem();
        assertOrderedContents(container, id);
        Item item = container.getItem(id);
        assertNotNull(item);

        // addItemAfter with empty container
        container.removeAllItems();
        assertOrderedContents(container);
        id = container.addItemAfter(null);
        assertOrderedContents(container, id);
        item = container.getItem(id);
        assertNotNull(item);

        // Add a new item before the first
        // addItemAfter
        Object newFirstId = container.addItemAfter(null);
        assertOrderedContents(container, newFirstId, id);

        // addItemAfter(Object)
        Object newSecondItemId = container.addItemAfter(newFirstId);
        // order is now: newFirstId, newSecondItemId, id
        assertOrderedContents(container, newFirstId, newSecondItemId, id);

        // addItemAfter(Object,Object)
        String fourthId = "id of the fourth item";
        Item fourth = container.addItemAfter(newFirstId, fourthId);
        // order is now: newFirstId, fourthId, newSecondItemId, id
        assertNotNull(fourth);
        assertEquals(fourth, container.getItem(fourthId));
        assertOrderedContents(container, newFirstId, fourthId, newSecondItemId,
                id);

        // addItemAfter(Object,Object)
        Object fifthId = new Object();
        Item fifth = container.addItemAfter(null, fifthId);
        // order is now: fifthId, newFirstId, fourthId, newSecondItemId, id
        assertNotNull(fifth);
        assertEquals(fifth, container.getItem(fifthId));
        assertOrderedContents(container, fifthId, newFirstId, fourthId,
                newSecondItemId, id);

        // addItemAfter(Object,Object)
        Object sixthId = new Object();
        Item sixth = container.addItemAfter(id, sixthId);
        // order is now: fifthId, newFirstId, fourthId, newSecondItemId, id,
        // sixthId
        assertNotNull(sixth);
        assertEquals(sixth, container.getItem(sixthId));
        assertOrderedContents(container, fifthId, newFirstId, fourthId,
                newSecondItemId, id, sixthId);

        // Test order after removing first item 'fifthId'
        container.removeItem(fifthId);
        // order is now: newFirstId, fourthId, newSecondItemId, id, sixthId
        assertOrderedContents(container, newFirstId, fourthId, newSecondItemId,
                id, sixthId);

        // Test order after removing last item 'sixthId'
        container.removeItem(sixthId);
        // order is now: newFirstId, fourthId, newSecondItemId, id
        assertOrderedContents(container, newFirstId, fourthId, newSecondItemId,
                id);

        // Test order after removing item from the middle 'fourthId'
        container.removeItem(fourthId);
        // order is now: newFirstId, newSecondItemId, id
        assertOrderedContents(container, newFirstId, newSecondItemId, id);

        // Delete remaining items
        container.removeItem(newFirstId);
        container.removeItem(newSecondItemId);
        container.removeItem(id);
        assertOrderedContents(container);

        Object finalItem = container.addItem();
        assertOrderedContents(container, finalItem);
    }

    private void assertOrderedContents(Ordered container, Object... ids) {
        assertEquals(ids.length, container.size());
        for (int i = 0; i < ids.length - 1; i++) {
            assertNotNull("The item id should not be null", ids[i]);
        }
        if (ids.length == 0) {
            assertNull("The first id is wrong", container.firstItemId());
            assertNull("The last id is wrong", container.lastItemId());
            return;
        }

        assertEquals("The first id is wrong", ids[0], container.firstItemId());
        assertEquals("The last id is wrong", ids[ids.length - 1],
                container.lastItemId());

        // isFirstId & isLastId
        assertTrue(container.isFirstId(container.firstItemId()));
        assertTrue(container.isLastId(container.lastItemId()));

        // nextId
        Object ref = container.firstItemId();
        for (int i = 1; i < ids.length; i++) {
            Object next = container.nextItemId(ref);
            assertEquals("The id after " + ref + " is wrong", ids[i], next);
            ref = next;
        }
        assertNull("The last id should not have a next id",
                container.nextItemId(ids[ids.length - 1]));
        assertNull(container.nextItemId("not-in-container"));

        // prevId
        ref = container.lastItemId();
        for (int i = ids.length - 2; i >= 0; i--) {
            Object prev = container.prevItemId(ref);
            assertEquals("The id before " + ref + " is wrong", ids[i], prev);
            ref = prev;
        }
        assertNull("The first id should not have a prev id",
                container.prevItemId(ids[0]));
        assertNull(container.prevItemId("not-in-container"));

    }

    protected void testContainerIndexed(Container.Indexed container,
            Object itemId, int itemPosition, boolean testAddEmptyItemAt,
            Object newItemId, boolean testAddItemAtWithId) {
        initializeContainer(container);

        // indexOfId
        assertEquals(itemPosition, container.indexOfId(itemId));

        // getIdByIndex
        assertEquals(itemId, container.getIdByIndex(itemPosition));

        // addItemAt
        if (testAddEmptyItemAt) {
            Object addedId = container.addItemAt(itemPosition);
            assertEquals(itemPosition, container.indexOfId(addedId));
            assertEquals(itemPosition + 1, container.indexOfId(itemId));
            assertEquals(addedId, container.getIdByIndex(itemPosition));
            assertEquals(itemId,
                    container.getIdByIndex(itemPosition + 1));

            Object newFirstId = container.addItemAt(0);
            assertEquals(0, container.indexOfId(newFirstId));
            assertEquals(itemPosition + 2, container.indexOfId(itemId));
            assertEquals(newFirstId, container.firstItemId());
            assertEquals(newFirstId, container.getIdByIndex(0));
            assertEquals(itemId,
                    container.getIdByIndex(itemPosition + 2));

            Object newLastId = container.addItemAt(container.size());
            assertEquals(container.size() - 1,
                    container.indexOfId(newLastId));
            assertEquals(itemPosition + 2, container.indexOfId(itemId));
            assertEquals(newLastId, container.lastItemId());
            assertEquals(newLastId,
                    container.getIdByIndex(container.size() - 1));
            assertEquals(itemId,
                    container.getIdByIndex(itemPosition + 2));

            assertTrue(container.removeItem(addedId));
            assertTrue(container.removeItem(newFirstId));
            assertTrue(container.removeItem(newLastId));

            assertFalse(
                    "Removing non-existing item should indicate failure",
                    container.removeItem(addedId));
        }

        // addItemAt
        if (testAddItemAtWithId) {
            container.addItemAt(itemPosition, newItemId);
            assertEquals(itemPosition, container.indexOfId(newItemId));
            assertEquals(itemPosition + 1, container.indexOfId(itemId));
            assertEquals(newItemId,
                    container.getIdByIndex(itemPosition));
            assertEquals(itemId,
                    container.getIdByIndex(itemPosition + 1));
            assertTrue(container.removeItem(newItemId));
            assertFalse(container.containsId(newItemId));

            container.addItemAt(0, newItemId);
            assertEquals(0, container.indexOfId(newItemId));
            assertEquals(itemPosition + 1, container.indexOfId(itemId));
            assertEquals(newItemId, container.firstItemId());
            assertEquals(newItemId, container.getIdByIndex(0));
            assertEquals(itemId,
                    container.getIdByIndex(itemPosition + 1));
            assertTrue(container.removeItem(newItemId));
            assertFalse(container.containsId(newItemId));

            container.addItemAt(container.size(), newItemId);
            assertEquals(container.size() - 1,
                    container.indexOfId(newItemId));
            assertEquals(itemPosition, container.indexOfId(itemId));
            assertEquals(newItemId, container.lastItemId());
            assertEquals(newItemId,
                    container.getIdByIndex(container.size() - 1));
            assertEquals(itemId, container.getIdByIndex(itemPosition));
            assertTrue(container.removeItem(newItemId));
            assertFalse(container.containsId(newItemId));
        }
    }

    protected void testContainerFiltering(Container.Filterable container) {
        initializeContainer(container);

        // Filter by "contains ab"
        SimpleStringFilter filter1 = new SimpleStringFilter(
                FULLY_QUALIFIED_NAME, "ab", false, false);
        container.addContainerFilter(filter1);

        assertTrue(container.getContainerFilters().size() == 1);
        assertEquals(filter1,
                container.getContainerFilters().iterator().next());

        validateContainer(container, "com.vaadin.data.BufferedValidatable",
                "com.vaadin.ui.TabSheet",
                "com.vaadin.terminal.gwt.client.Focusable",
                "com.vaadin.data.Buffered", isFilteredOutItemNull(), 20);

        // Filter by "contains da" (reversed as ad here)
        container.removeAllContainerFilters();

        assertTrue(container.getContainerFilters().isEmpty());

        SimpleStringFilter filter2 = new SimpleStringFilter(
                REVERSE_FULLY_QUALIFIED_NAME, "ad", false, false);
        container.addContainerFilter(filter2);

        assertTrue(container.getContainerFilters().size() == 1);
        assertEquals(filter2,
                container.getContainerFilters().iterator().next());

        validateContainer(container, "com.vaadin.data.Buffered",
                "com.vaadin.server.ComponentSizeValidator",
                "com.vaadin.data.util.IndexedContainer",
                "com.vaadin.terminal.gwt.client.ui.VUriFragmentUtility",
                isFilteredOutItemNull(), 37);
    }

    /**
     * Override in subclasses to return false if the container getItem() method
     * returns a non-null value for an item that has been filtered out.
     *
     * @return
     */
    protected boolean isFilteredOutItemNull() {
        return true;
    }

    protected void testContainerSortingAndFiltering(
            Container.Sortable sortable) {
        Filterable filterable = (Filterable) sortable;

        initializeContainer(sortable);

        // Filter by "contains ab"
        filterable.addContainerFilter(new SimpleStringFilter(
                FULLY_QUALIFIED_NAME, "ab", false, false));

        // Must be able to sort based on PROP1 for this test
        assertTrue(sortable.getSortableContainerPropertyIds()
                .contains(FULLY_QUALIFIED_NAME));

        sortable.sort(new Object[] { FULLY_QUALIFIED_NAME },
                new boolean[] { true });

        validateContainer(sortable, "com.vaadin.data.BufferedValidatable",
                "com.vaadin.ui.TableFieldFactory",
                "com.vaadin.ui.TableFieldFactory",
                "com.vaadin.data.util.BeanItem", isFilteredOutItemNull(), 20);
    }

    protected void testContainerSorting(Container.Filterable container) {
        Container.Sortable sortable = (Sortable) container;

        initializeContainer(container);

        // Must be able to sort based on PROP1 for this test
        assertTrue(sortable.getSortableContainerPropertyIds()
                .contains(FULLY_QUALIFIED_NAME));
        assertTrue(sortable.getSortableContainerPropertyIds()
                .contains(REVERSE_FULLY_QUALIFIED_NAME));

        sortable.sort(new Object[] { FULLY_QUALIFIED_NAME },
                new boolean[] { true });

        validateContainer(container, "com.vaadin.Application",
                "org.vaadin.test.LastClass",
                "com.vaadin.server.ApplicationResource", "blah", true,
                sampleData.length);

        sortable.sort(new Object[] { REVERSE_FULLY_QUALIFIED_NAME },
                new boolean[] { true });

        validateContainer(container, "com.vaadin.server.ApplicationPortlet2",
                "com.vaadin.data.util.ObjectProperty",
                "com.vaadin.ui.BaseFieldFactory", "blah", true,
                sampleData.length);

    }

    protected void initializeContainer(Container container) {
        assertTrue(container.removeAllItems());
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
            Item item = container.addItem(id);

            item.getItemProperty(FULLY_QUALIFIED_NAME).setValue(sampleData[i]);
            item.getItemProperty(SIMPLE_NAME)
                    .setValue(getSimpleName(sampleData[i]));
            item.getItemProperty(REVERSE_FULLY_QUALIFIED_NAME)
                    .setValue(reverse(sampleData[i]));
            item.getItemProperty(ID_NUMBER).setValue(i);
        }
    }

    protected static String getSimpleName(String name) {
        if (name.contains(".")) {
            return name.substring(name.lastIndexOf('.') + 1);
        } else {
            return name;
        }
    }

    protected static String reverse(String string) {
        return new StringBuilder(string).reverse().toString();
    }

    protected final String[] sampleData = {
            "com.vaadin.annotations.AutoGenerated", "com.vaadin.Application",
            "com.vaadin.data.Buffered", "com.vaadin.data.BufferedValidatable",
            "com.vaadin.data.Container", "com.vaadin.data.Item",
            "com.vaadin.data.Property", "com.vaadin.data.util.BeanItem",
            "com.vaadin.data.util.BeanItemContainer",
            "com.vaadin.data.util.ContainerHierarchicalWrapper",
            "com.vaadin.data.util.ContainerOrderedWrapper",
            "com.vaadin.data.util.DefaultItemSorter",
            "com.vaadin.data.util.FilesystemContainer",
            "com.vaadin.data.util.Filter",
            "com.vaadin.data.util.HierarchicalContainer",
            "com.vaadin.data.util.IndexedContainer",
            "com.vaadin.data.util.ItemSorter",
            "com.vaadin.data.util.MethodProperty",
            "com.vaadin.data.util.ObjectProperty",
            "com.vaadin.data.util.PropertyFormatter",
            "com.vaadin.data.util.PropertysetItem",
            "com.vaadin.data.util.QueryContainer",
            "com.vaadin.data.util.TextFileProperty",
            "com.vaadin.data.Validatable",
            "com.vaadin.data.validator.AbstractStringValidator",
            "com.vaadin.data.validator.AbstractValidator",
            "com.vaadin.data.validator.CompositeValidator",
            "com.vaadin.data.validator.DoubleValidator",
            "com.vaadin.data.validator.EmailValidator",
            "com.vaadin.data.validator.IntegerValidator",
            "com.vaadin.data.validator.NullValidator",
            "com.vaadin.data.validator.RegexpValidator",
            "com.vaadin.data.validator.StringLengthValidator",
            "com.vaadin.data.Validator", "com.vaadin.event.Action",
            "com.vaadin.event.ComponentEventListener",
            "com.vaadin.event.EventRouter", "com.vaadin.event.FieldEvents",
            "com.vaadin.event.ItemClickEvent", "com.vaadin.event.LayoutEvents",
            "com.vaadin.event.ListenerMethod",
            "com.vaadin.event.MethodEventSource",
            "com.vaadin.event.MouseEvents", "com.vaadin.event.ShortcutAction",
            "com.vaadin.launcher.DemoLauncher",
            "com.vaadin.launcher.DevelopmentServerLauncher",
            "com.vaadin.launcher.util.BrowserLauncher",
            "com.vaadin.service.ApplicationContext",
            "com.vaadin.service.FileTypeResolver",
            "com.vaadin.server.ApplicationResource",
            "com.vaadin.server.ClassResource",
            "com.vaadin.server.CompositeErrorMessage",
            "com.vaadin.server.DownloadStream",
            "com.vaadin.server.ErrorMessage",
            "com.vaadin.server.ExternalResource",
            "com.vaadin.server.FileResource",
            "com.vaadin.terminal.gwt.client.ApplicationConfiguration",
            "com.vaadin.terminal.gwt.client.ApplicationConnection",
            "com.vaadin.terminal.gwt.client.BrowserInfo",
            "com.vaadin.terminal.gwt.client.ClientExceptionHandler",
            "com.vaadin.terminal.gwt.client.ComponentDetail",
            "com.vaadin.terminal.gwt.client.ComponentDetailMap",
            "com.vaadin.terminal.gwt.client.ComponentLocator",
            "com.vaadin.terminal.gwt.client.Console",
            "com.vaadin.terminal.gwt.client.Container",
            "com.vaadin.terminal.gwt.client.ContainerResizedListener",
            "com.vaadin.terminal.gwt.client.CSSRule",
            "com.vaadin.terminal.gwt.client.DateTimeService",
            "com.vaadin.terminal.gwt.client.DefaultWidgetSet",
            "com.vaadin.terminal.gwt.client.Focusable",
            "com.vaadin.terminal.gwt.client.HistoryImplIEVaadin",
            "com.vaadin.terminal.gwt.client.LocaleNotLoadedException",
            "com.vaadin.terminal.gwt.client.LocaleService",
            "com.vaadin.terminal.gwt.client.MouseEventDetails",
            "com.vaadin.terminal.gwt.client.NullConsole",
            "com.vaadin.terminal.gwt.client.Paintable",
            "com.vaadin.terminal.gwt.client.RenderInformation",
            "com.vaadin.terminal.gwt.client.RenderSpace",
            "com.vaadin.terminal.gwt.client.StyleConstants",
            "com.vaadin.terminal.gwt.client.TooltipInfo",
            "com.vaadin.terminal.gwt.client.ui.Action",
            "com.vaadin.terminal.gwt.client.ui.ActionOwner",
            "com.vaadin.terminal.gwt.client.ui.AlignmentInfo",
            "com.vaadin.terminal.gwt.client.ui.CalendarEntry",
            "com.vaadin.terminal.gwt.client.ui.ClickEventHandler",
            "com.vaadin.terminal.gwt.client.ui.Field",
            "com.vaadin.terminal.gwt.client.ui.Icon",
            "com.vaadin.terminal.gwt.client.ui.layout.CellBasedLayout",
            "com.vaadin.terminal.gwt.client.ui.layout.ChildComponentContainer",
            "com.vaadin.terminal.gwt.client.ui.layout.Margins",
            "com.vaadin.terminal.gwt.client.ui.LayoutClickEventHandler",
            "com.vaadin.terminal.gwt.client.ui.MenuBar",
            "com.vaadin.terminal.gwt.client.ui.MenuItem",
            "com.vaadin.terminal.gwt.client.ui.richtextarea.VRichTextToolbar",
            "com.vaadin.terminal.gwt.client.ui.ShortcutActionHandler",
            "com.vaadin.terminal.gwt.client.ui.SubPartAware",
            "com.vaadin.terminal.gwt.client.ui.Table",
            "com.vaadin.terminal.gwt.client.ui.TreeAction",
            "com.vaadin.terminal.gwt.client.ui.TreeImages",
            "com.vaadin.terminal.gwt.client.ui.VAbsoluteLayout",
            "com.vaadin.terminal.gwt.client.ui.VAccordion",
            "com.vaadin.terminal.gwt.client.ui.VButton",
            "com.vaadin.terminal.gwt.client.ui.VCalendarPanel",
            "com.vaadin.terminal.gwt.client.ui.VCheckBox",
            "com.vaadin.terminal.gwt.client.ui.VContextMenu",
            "com.vaadin.terminal.gwt.client.ui.VCssLayout",
            "com.vaadin.terminal.gwt.client.ui.VCustomComponent",
            "com.vaadin.terminal.gwt.client.ui.VCustomLayout",
            "com.vaadin.terminal.gwt.client.ui.VDateField",
            "com.vaadin.terminal.gwt.client.ui.VDateFieldCalendar",
            "com.vaadin.terminal.gwt.client.ui.VEmbedded",
            "com.vaadin.terminal.gwt.client.ui.VFilterSelect",
            "com.vaadin.terminal.gwt.client.ui.VForm",
            "com.vaadin.terminal.gwt.client.ui.VFormLayout",
            "com.vaadin.terminal.gwt.client.ui.VGridLayout",
            "com.vaadin.terminal.gwt.client.ui.VHorizontalLayout",
            "com.vaadin.terminal.gwt.client.ui.VLabel",
            "com.vaadin.terminal.gwt.client.ui.VLink",
            "com.vaadin.terminal.gwt.client.ui.VListSelect",
            "com.vaadin.terminal.gwt.client.ui.VMarginInfo",
            "com.vaadin.terminal.gwt.client.ui.VMenuBar",
            "com.vaadin.terminal.gwt.client.ui.VNativeButton",
            "com.vaadin.terminal.gwt.client.ui.VNativeSelect",
            "com.vaadin.terminal.gwt.client.ui.VNotification",
            "com.vaadin.terminal.gwt.client.ui.VOptionGroup",
            "com.vaadin.terminal.gwt.client.ui.VOptionGroupBase",
            "com.vaadin.terminal.gwt.client.ui.VOrderedLayout",
            "com.vaadin.terminal.gwt.client.ui.VOverlay",
            "com.vaadin.terminal.gwt.client.ui.VPanel",
            "com.vaadin.terminal.gwt.client.ui.VPasswordField",
            "com.vaadin.terminal.gwt.client.ui.VPopupCalendar",
            "com.vaadin.terminal.gwt.client.ui.VPopupView",
            "com.vaadin.terminal.gwt.client.ui.VProgressIndicator",
            "com.vaadin.terminal.gwt.client.ui.VRichTextArea",
            "com.vaadin.terminal.gwt.client.ui.VScrollTable",
            "com.vaadin.terminal.gwt.client.ui.VSlider",
            "com.vaadin.terminal.gwt.client.ui.VSplitPanel",
            "com.vaadin.terminal.gwt.client.ui.VSplitPanelHorizontal",
            "com.vaadin.terminal.gwt.client.ui.VSplitPanelVertical",
            "com.vaadin.terminal.gwt.client.ui.VTablePaging",
            "com.vaadin.terminal.gwt.client.ui.VTabsheet",
            "com.vaadin.terminal.gwt.client.ui.VTabsheetBase",
            "com.vaadin.terminal.gwt.client.ui.VTabsheetPanel",
            "com.vaadin.terminal.gwt.client.ui.VTextArea",
            "com.vaadin.terminal.gwt.client.ui.VTextField",
            "com.vaadin.terminal.gwt.client.ui.VTextualDate",
            "com.vaadin.terminal.gwt.client.ui.VTime",
            "com.vaadin.terminal.gwt.client.ui.VTree",
            "com.vaadin.terminal.gwt.client.ui.VTwinColSelect",
            "com.vaadin.terminal.gwt.client.ui.VUnknownComponent",
            "com.vaadin.terminal.gwt.client.ui.VUpload",
            "com.vaadin.terminal.gwt.client.ui.VUriFragmentUtility",
            "com.vaadin.terminal.gwt.client.ui.VVerticalLayout",
            "com.vaadin.terminal.gwt.client.ui.VView",
            "com.vaadin.terminal.gwt.client.ui.VWindow",
            "com.vaadin.terminal.gwt.client.UIDL",
            "com.vaadin.terminal.gwt.client.Util",
            "com.vaadin.terminal.gwt.client.ValueMap",
            "com.vaadin.terminal.gwt.client.VCaption",
            "com.vaadin.terminal.gwt.client.VCaptionWrapper",
            "com.vaadin.terminal.gwt.client.VDebugConsole",
            "com.vaadin.terminal.gwt.client.VErrorMessage",
            "com.vaadin.terminal.gwt.client.VTooltip",
            "com.vaadin.terminal.gwt.client.VUIDLBrowser",
            "com.vaadin.terminal.gwt.client.WidgetMap",
            "com.vaadin.terminal.gwt.client.WidgetSet",
            "com.vaadin.server.AbstractApplicationPortlet",
            "com.vaadin.server.AbstractApplicationServlet",
            "com.vaadin.server.AbstractCommunicationManager",
            "com.vaadin.server.AbstractWebApplicationContext",
            "com.vaadin.server.ApplicationPortlet",
            "com.vaadin.server.ApplicationPortlet2",
            "com.vaadin.server.ApplicationRunnerServlet",
            "com.vaadin.server.ApplicationServlet",
            "com.vaadin.server.ChangeVariablesErrorEvent",
            "com.vaadin.server.CommunicationManager",
            "com.vaadin.server.ComponentSizeValidator",
            "com.vaadin.server.Constants",
            "com.vaadin.server.GAEApplicationServlet",
            "com.vaadin.server.HttpServletRequestListener",
            "com.vaadin.server.HttpUploadStream",
            "com.vaadin.server.JsonPaintTarget",
            "com.vaadin.server.PortletApplicationContext",
            "com.vaadin.server.PortletApplicationContext2",
            "com.vaadin.server.PortletCommunicationManager",
            "com.vaadin.server.PortletRequestListener",
            "com.vaadin.server.RestrictedRenderResponse",
            "com.vaadin.server.SessionExpiredException",
            "com.vaadin.server.SystemMessageException",
            "com.vaadin.server.WebApplicationContext",
            "com.vaadin.server.WebBrowser",
            "com.vaadin.server.widgetsetutils.ClassPathExplorer",
            "com.vaadin.server.widgetsetutils.WidgetMapGenerator",
            "com.vaadin.server.widgetsetutils.WidgetSetBuilder",
            "com.vaadin.server.KeyMapper", "com.vaadin.server.Paintable",
            "com.vaadin.server.PaintException", "com.vaadin.server.PaintTarget",
            "com.vaadin.server.ParameterHandler", "com.vaadin.server.Resource",
            "com.vaadin.server.Scrollable", "com.vaadin.server.Sizeable",
            "com.vaadin.server.StreamResource", "com.vaadin.server.SystemError",
            "com.vaadin.server.Terminal", "com.vaadin.server.ThemeResource",
            "com.vaadin.server.UploadStream", "com.vaadin.server.URIHandler",
            "com.vaadin.server.UserError", "com.vaadin.server.VariableOwner",
            "com.vaadin.tools.ReflectTools",
            "com.vaadin.tools.WidgetsetCompiler",
            "com.vaadin.ui.AbsoluteLayout", "com.vaadin.ui.AbstractComponent",
            "com.vaadin.ui.AbstractComponentContainer",
            "com.vaadin.ui.AbstractField", "com.vaadin.ui.AbstractLayout",
            "com.vaadin.ui.AbstractOrderedLayout",
            "com.vaadin.ui.AbstractSelect", "com.vaadin.ui.Accordion",
            "com.vaadin.ui.Alignment", "com.vaadin.ui.AlignmentUtils",
            "com.vaadin.ui.BaseFieldFactory", "com.vaadin.ui.Button",
            "com.vaadin.ui.CheckBox", "com.vaadin.ui.ClientWidget",
            "com.vaadin.ui.ComboBox", "com.vaadin.ui.Component",
            "com.vaadin.ui.ComponentContainer", "com.vaadin.ui.CssLayout",
            "com.vaadin.ui.CustomComponent", "com.vaadin.ui.CustomLayout",
            "com.vaadin.ui.DateField", "com.vaadin.ui.DefaultFieldFactory",
            "com.vaadin.ui.Embedded", "com.vaadin.ui.ExpandLayout",
            "com.vaadin.ui.Field", "com.vaadin.ui.FieldFactory",
            "com.vaadin.ui.Form", "com.vaadin.ui.FormFieldFactory",
            "com.vaadin.ui.FormLayout", "com.vaadin.ui.GridLayout",
            "com.vaadin.ui.HorizontalLayout", "com.vaadin.ui.InlineDateField",
            "com.vaadin.ui.Label", "com.vaadin.ui.Layout", "com.vaadin.ui.Link",
            "com.vaadin.ui.ListSelect", "com.vaadin.ui.LoginForm",
            "com.vaadin.ui.MenuBar", "com.vaadin.ui.NativeButton",
            "com.vaadin.ui.NativeSelect", "com.vaadin.ui.OptionGroup",
            "com.vaadin.ui.OrderedLayout", "com.vaadin.ui.Panel",
            "com.vaadin.ui.PopupDateField", "com.vaadin.ui.PopupView",
            "com.vaadin.ui.ProgressIndicator", "com.vaadin.ui.RichTextArea",
            "com.vaadin.ui.Select", "com.vaadin.ui.Slider",
            "com.vaadin.ui.SplitPanel", "com.vaadin.ui.Table",
            "com.vaadin.ui.TableFieldFactory", "com.vaadin.ui.TabSheet",
            "com.vaadin.ui.TextField", "com.vaadin.ui.Tree",
            "com.vaadin.ui.TwinColSelect", "com.vaadin.ui.Upload",
            "com.vaadin.ui.UriFragmentUtility", "com.vaadin.ui.VerticalLayout",
            "com.vaadin.ui.Window", "com.vaadin.util.SerializerHelper",
            "org.vaadin.test.LastClass" };
}
