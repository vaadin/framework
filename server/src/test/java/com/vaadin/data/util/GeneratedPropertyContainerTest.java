/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.data.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Container.PropertySetChangeEvent;
import com.vaadin.data.Container.PropertySetChangeListener;
import com.vaadin.data.Item;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.GeneratedPropertyContainer.GeneratedPropertyItem;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.UnsupportedFilterException;

public class GeneratedPropertyContainerTest {

    GeneratedPropertyContainer container;
    Indexed wrappedContainer;
    private static double MILES_CONVERSION = 0.6214d;

    private class GeneratedPropertyListener implements
            PropertySetChangeListener {

        private int callCount = 0;

        public int getCallCount() {
            return callCount;
        }

        @Override
        public void containerPropertySetChange(PropertySetChangeEvent event) {
            ++callCount;
            assertEquals(
                    "Container for event was not GeneratedPropertyContainer",
                    event.getContainer(), container);
        }
    }

    private class GeneratedItemSetListener implements ItemSetChangeListener {

        private int callCount = 0;

        public int getCallCount() {
            return callCount;
        }

        @Override
        public void containerItemSetChange(ItemSetChangeEvent event) {
            ++callCount;
            assertEquals(
                    "Container for event was not GeneratedPropertyContainer",
                    event.getContainer(), container);
        }
    }

    @Before
    public void setUp() {
        container = new GeneratedPropertyContainer(createContainer());
    }

    @Test
    public void testSimpleGeneratedProperty() {
        container.addGeneratedProperty("hello",
                new PropertyValueGenerator<String>() {

                    @Override
                    public String getValue(Item item, Object itemId,
                            Object propertyId) {
                        return "Hello World!";
                    }

                    @Override
                    public Class<String> getType() {
                        return String.class;
                    }
                });

        Object itemId = container.addItem();
        assertEquals("Expected value not in item.", container.getItem(itemId)
                .getItemProperty("hello").getValue(), "Hello World!");
    }

    @Test
    public void testSortableProperties() {
        container.addGeneratedProperty("baz",
                new PropertyValueGenerator<String>() {

                    @Override
                    public String getValue(Item item, Object itemId,
                            Object propertyId) {
                        return item.getItemProperty("foo").getValue() + " "
                                + item.getItemProperty("bar").getValue();
                    }

                    @Override
                    public Class<String> getType() {
                        return String.class;
                    }

                    @Override
                    public SortOrder[] getSortProperties(SortOrder order) {
                        SortOrder[] sortOrder = new SortOrder[1];
                        sortOrder[0] = new SortOrder("bar", order
                                .getDirection());
                        return sortOrder;
                    }
                });

        container.sort(new Object[] { "baz" }, new boolean[] { true });
        assertEquals("foo 0", container.getItem(container.getIdByIndex(0))
                .getItemProperty("baz").getValue());

        container.sort(new Object[] { "baz" }, new boolean[] { false });
        assertEquals("foo 10", container.getItem(container.getIdByIndex(0))
                .getItemProperty("baz").getValue());
    }

    @Test
    public void testOverrideSortableProperties() {

        assertTrue(container.getSortableContainerPropertyIds().contains("bar"));

        container.addGeneratedProperty("bar",
                new PropertyValueGenerator<String>() {

                    @Override
                    public String getValue(Item item, Object itemId,
                            Object propertyId) {
                        return item.getItemProperty("foo").getValue() + " "
                                + item.getItemProperty("bar").getValue();
                    }

                    @Override
                    public Class<String> getType() {
                        return String.class;
                    }
                });

        assertFalse(container.getSortableContainerPropertyIds().contains("bar"));
    }

    @Test
    public void testFilterByMiles() {
        container.addGeneratedProperty("miles",
                new PropertyValueGenerator<Double>() {

                    @Override
                    public Double getValue(Item item, Object itemId,
                            Object propertyId) {
                        return (Double) item.getItemProperty("km").getValue()
                                * MILES_CONVERSION;
                    }

                    @Override
                    public Class<Double> getType() {
                        return Double.class;
                    }

                    @Override
                    public Filter modifyFilter(Filter filter)
                            throws UnsupportedFilterException {
                        if (filter instanceof Compare.LessOrEqual) {
                            Double value = (Double) ((Compare.LessOrEqual) filter)
                                    .getValue();
                            value = value / MILES_CONVERSION;
                            return new Compare.LessOrEqual("km", value);
                        }
                        return super.modifyFilter(filter);
                    }
                });

        for (Object itemId : container.getItemIds()) {
            Item item = container.getItem(itemId);
            Double km = (Double) item.getItemProperty("km").getValue();
            Double miles = (Double) item.getItemProperty("miles").getValue();
            assertTrue(miles.equals(km * MILES_CONVERSION));
        }

        Filter filter = new Compare.LessOrEqual("miles", MILES_CONVERSION);
        container.addContainerFilter(filter);
        for (Object itemId : container.getItemIds()) {
            Item item = container.getItem(itemId);
            assertTrue("Item did not pass original filter.",
                    filter.passesFilter(itemId, item));
        }

        assertTrue(container.getContainerFilters().contains(filter));
        container.removeContainerFilter(filter);
        assertFalse(container.getContainerFilters().contains(filter));

        boolean allPass = true;
        for (Object itemId : container.getItemIds()) {
            Item item = container.getItem(itemId);
            if (!filter.passesFilter(itemId, item)) {
                allPass = false;
            }
        }

        if (allPass) {
            fail("Removing filter did not introduce any previous filtered items");
        }
    }

    @Test
    public void testPropertySetChangeNotifier() {
        GeneratedPropertyListener listener = new GeneratedPropertyListener();
        GeneratedPropertyListener removedListener = new GeneratedPropertyListener();
        container.addPropertySetChangeListener(listener);
        container.addPropertySetChangeListener(removedListener);

        container.addGeneratedProperty("foo",
                new PropertyValueGenerator<String>() {

                    @Override
                    public String getValue(Item item, Object itemId,
                            Object propertyId) {
                        return "";
                    }

                    @Override
                    public Class<String> getType() {
                        return String.class;
                    }
                });

        // Adding property to wrapped container should cause an event
        wrappedContainer.addContainerProperty("baz", String.class, "");
        container.removePropertySetChangeListener(removedListener);
        container.removeGeneratedProperty("foo");

        assertEquals("Listener was not called correctly.", 3,
                listener.getCallCount());
        assertEquals("Removed listener was not called correctly.", 2,
                removedListener.getCallCount());
    }

    @Test
    public void testItemSetChangeNotifier() {
        GeneratedItemSetListener listener = new GeneratedItemSetListener();
        container.addItemSetChangeListener(listener);

        container.sort(new Object[] { "foo" }, new boolean[] { true });
        container.sort(new Object[] { "foo" }, new boolean[] { false });

        assertEquals("Listener was not called correctly.", 2,
                listener.getCallCount());

    }

    @Test
    public void testRemoveProperty() {
        container.removeContainerProperty("foo");
        assertFalse("Container contained removed property", container
                .getContainerPropertyIds().contains("foo"));
        assertTrue("Wrapped container did not contain removed property",
                wrappedContainer.getContainerPropertyIds().contains("foo"));

        assertFalse(container.getItem(container.firstItemId())
                .getItemPropertyIds().contains("foo"));

        container.addContainerProperty("foo", null, null);
        assertTrue("Container did not contain returned property", container
                .getContainerPropertyIds().contains("foo"));
    }

    @Test
    public void testGetWrappedItem() {
        Object itemId = wrappedContainer.getItemIds().iterator().next();
        Item wrappedItem = wrappedContainer.getItem(itemId);
        GeneratedPropertyItem generatedPropertyItem = (GeneratedPropertyItem) container
                .getItem(itemId);
        assertEquals(wrappedItem, generatedPropertyItem.getWrappedItem());
    }

    private Indexed createContainer() {
        wrappedContainer = new IndexedContainer();
        wrappedContainer.addContainerProperty("foo", String.class, "foo");
        wrappedContainer.addContainerProperty("bar", Integer.class, 0);
        // km contains double values from 0.0 to 2.0
        wrappedContainer.addContainerProperty("km", Double.class, 0);

        for (int i = 0; i <= 10; ++i) {
            Object itemId = wrappedContainer.addItem();
            Item item = wrappedContainer.getItem(itemId);
            item.getItemProperty("foo").setValue("foo");
            item.getItemProperty("bar").setValue(i);
            item.getItemProperty("km").setValue(i / 5.0d);
        }

        return wrappedContainer;
    }

}
