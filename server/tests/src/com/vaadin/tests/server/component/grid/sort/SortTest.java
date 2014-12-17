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
package com.vaadin.tests.server.component.grid.sort;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.sort.Sort;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.SortEvent;
import com.vaadin.event.SortEvent.SortListener;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Grid;

public class SortTest {

    class DummySortingIndexedContainer extends IndexedContainer {

        private Object[] expectedProperties;
        private boolean[] expectedAscending;
        private boolean sorted = true;

        @Override
        public void sort(Object[] propertyId, boolean[] ascending) {
            Assert.assertEquals(
                    "Different amount of expected and actual properties,",
                    expectedProperties.length, propertyId.length);
            Assert.assertEquals(
                    "Different amount of expected and actual directions",
                    expectedAscending.length, ascending.length);
            for (int i = 0; i < propertyId.length; ++i) {
                Assert.assertEquals("Sorting properties differ",
                        expectedProperties[i], propertyId[i]);
                Assert.assertEquals("Sorting directions differ",
                        expectedAscending[i], ascending[i]);
            }
            sorted = true;
        }

        public void expectedSort(Object[] properties, SortDirection[] directions) {
            assert directions.length == properties.length : "Array dimensions differ";
            expectedProperties = properties;
            expectedAscending = new boolean[directions.length];
            for (int i = 0; i < directions.length; ++i) {
                expectedAscending[i] = (directions[i] == SortDirection.ASCENDING);
            }
            sorted = false;
        }

        public boolean isSorted() {
            return sorted;
        }
    }

    class RegisteringSortChangeListener implements SortListener {
        private List<SortOrder> order;

        @Override
        public void sort(SortEvent event) {
            assert order == null : "The same listener was notified multipe times without checking";

            order = event.getSortOrder();
        }

        public void assertEventFired(SortOrder... expectedOrder) {
            Assert.assertEquals(Arrays.asList(expectedOrder), order);

            // Reset for nest test
            order = null;
        }

    }

    private DummySortingIndexedContainer container;
    private RegisteringSortChangeListener listener;
    private Grid grid;

    @Before
    public void setUp() {
        container = createContainer();
        container.expectedSort(new Object[] {}, new SortDirection[] {});

        listener = new RegisteringSortChangeListener();

        grid = new Grid(container);
        grid.addSortListener(listener);
    }

    @After
    public void tearDown() {
        Assert.assertTrue("Container was not sorted after the test.",
                container.isSorted());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidSortDirection() {
        Sort.by("foo", null);
    }

    @Test(expected = IllegalStateException.class)
    public void testSortOneColumnMultipleTimes() {
        Sort.by("foo").then("bar").then("foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSortingByUnexistingProperty() {
        grid.sort("foobar");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSortingByUnsortableProperty() {
        container.addContainerProperty("foobar", Object.class, null);
        grid.sort("foobar");
    }

    @Test
    public void testGridDirectSortAscending() {
        container.expectedSort(new Object[] { "foo" },
                new SortDirection[] { SortDirection.ASCENDING });
        grid.sort("foo");

        listener.assertEventFired(new SortOrder("foo", SortDirection.ASCENDING));
    }

    @Test
    public void testGridDirectSortDescending() {
        container.expectedSort(new Object[] { "foo" },
                new SortDirection[] { SortDirection.DESCENDING });
        grid.sort("foo", SortDirection.DESCENDING);

        listener.assertEventFired(new SortOrder("foo", SortDirection.DESCENDING));
    }

    @Test
    public void testGridSortBy() {
        container.expectedSort(new Object[] { "foo", "bar", "baz" },
                new SortDirection[] { SortDirection.ASCENDING,
                        SortDirection.ASCENDING, SortDirection.DESCENDING });
        grid.sort(Sort.by("foo").then("bar")
                .then("baz", SortDirection.DESCENDING));

        listener.assertEventFired(
                new SortOrder("foo", SortDirection.ASCENDING), new SortOrder(
                        "bar", SortDirection.ASCENDING), new SortOrder("baz",
                        SortDirection.DESCENDING));

    }

    @Test
    public void testChangeContainerAfterSorting() {
        class Person {
        }

        container.expectedSort(new Object[] { "foo", "bar", "baz" },
                new SortDirection[] { SortDirection.ASCENDING,
                        SortDirection.ASCENDING, SortDirection.DESCENDING });
        grid.sort(Sort.by("foo").then("bar")
                .then("baz", SortDirection.DESCENDING));

        listener.assertEventFired(
                new SortOrder("foo", SortDirection.ASCENDING), new SortOrder(
                        "bar", SortDirection.ASCENDING), new SortOrder("baz",
                        SortDirection.DESCENDING));

        container = new DummySortingIndexedContainer();
        container.addContainerProperty("foo", Person.class, null);
        container.addContainerProperty("baz", String.class, "");
        container.addContainerProperty("bar", Person.class, null);
        container.expectedSort(new Object[] { "baz" },
                new SortDirection[] { SortDirection.DESCENDING });
        grid.setContainerDataSource(container);

        listener.assertEventFired(new SortOrder("baz", SortDirection.DESCENDING));

    }

    private DummySortingIndexedContainer createContainer() {
        DummySortingIndexedContainer container = new DummySortingIndexedContainer();
        container.addContainerProperty("foo", Integer.class, 0);
        container.addContainerProperty("bar", Integer.class, 0);
        container.addContainerProperty("baz", Integer.class, 0);
        return container;
    }
}
