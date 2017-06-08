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

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.server.SerializablePredicate;

public class SingleFilteringDataProviderTest {
    private SingleFilteringDataProvider<String, SerializablePredicate<String>> filtering = new SingleFilteringDataProvider<>(
            DataProvider.create("one", "two", "three"));

    @Test
    public void basics() {

        assertItems(filtering, "one", "two", "three");

        filtering.setFilter("two"::equals);
        assertItems(filtering, "two");

        filtering.setFilter(null);
        assertItems(filtering, "one", "two", "three");
    }

    public static <T> void assertItems(DataProvider<String, T> dataProvider,
            String... items) {
        Query<String, T> emptyQuery = new Query<>();

        Assert.assertEquals(Arrays.asList(items),
                dataProvider.fetch(emptyQuery).collect(Collectors.toList()));

        Assert.assertEquals(items.length, dataProvider.size(emptyQuery));
    }

    @Test
    public void events() {
        SerializablePredicate<String> filter = item -> false;
        AtomicInteger listenerCount = new AtomicInteger();

        filtering.addDataProviderListener(e -> listenerCount.incrementAndGet());

        filtering.setFilter(null);
        Assert.assertEquals(
                "Clearing from initial state should not fire events", 0,
                listenerCount.get());

        filtering.setFilter(filter);
        Assert.assertEquals("Setting a new filter should fire events", 1,
                listenerCount.get());

        filtering.setFilter(filter);
        Assert.assertEquals("Re-setting the same filter shouldn't fire events",
                1, listenerCount.get());

        filtering.setFilter("foo"::equals);
        Assert.assertEquals("Setting another filter should fire events", 2,
                listenerCount.get());

        filtering.setFilter(null);
        Assert.assertEquals("Clearing the filter should fire events", 3,
                listenerCount.get());
    }
}
