/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.data.provider;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.server.SerializablePredicate;

public class AppendableFilteringDataProviderTest {
    private static final SerializablePredicate<String> CONTAINS_O = string -> string
            .contains("o");
    private static final SerializablePredicate<String> CONTAINS_T = string -> string
            .contains("t");

    private AppendableFilteringDataProvider<String, SerializablePredicate<String>> filtering = new AppendableFilteringDataProvider<>(
            DataProvider.create("one", "two", "three"));

    @Test
    public void basics() {

        SingleFilteringDataProviderTest.assertItems(filtering, "one", "two",
                "three");

        filtering.addFilter(CONTAINS_O);
        SingleFilteringDataProviderTest.assertItems(filtering, "one", "two");

        filtering.addFilter(CONTAINS_T);
        SingleFilteringDataProviderTest.assertItems(filtering, "two");

        filtering.setFilter(string -> string.contains("t"));
        SingleFilteringDataProviderTest.assertItems(filtering, "two", "three");

        filtering.clearFilters();
        SingleFilteringDataProviderTest.assertItems(filtering, "one", "two",
                "three");
    }

    @Test
    public void append() {
        AppendableFilterDataProvider<String, SerializablePredicate<String>> appended = filtering
                .withFilter(CONTAINS_T);
        SingleFilteringDataProviderTest.assertItems(appended, "two", "three");

        filtering.setFilter(CONTAINS_O);
        SingleFilteringDataProviderTest.assertItems(appended, "two");
    }

    @Test
    public void events() {
        AtomicInteger listenerCount = new AtomicInteger();

        filtering.addDataProviderListener(e -> listenerCount.incrementAndGet());

        filtering.setFilter(null);
        Assert.assertEquals(
                "Clearing from initial state should not fire events", 0,
                listenerCount.get());

        filtering.setFilter(CONTAINS_O);
        Assert.assertEquals("Setting a new filter should fire events", 1,
                listenerCount.get());

        filtering.setFilter(CONTAINS_O);
        Assert.assertEquals("Re-setting the same filter shouldn't fire events",
                1, listenerCount.get());

        filtering.setFilter(CONTAINS_T);
        Assert.assertEquals("Setting another filter should fire events", 2,
                listenerCount.get());

        filtering.addFilter(CONTAINS_T);
        Assert.assertEquals("Adding existing filter shouldn't fire events", 2,
                listenerCount.get());

        filtering.addFilter(item -> false);
        Assert.assertEquals("Adding new filter should fire events", 3,
                listenerCount.get());

        filtering.clearFilters();
        Assert.assertEquals("Clearing the filter should fire events", 4,
                listenerCount.get());
    }

}
