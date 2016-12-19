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

public class AppendableSearcherTest {
    private static final SerializablePredicate<String> CONTAINS_O = string -> string
            .contains("o");
    private static final SerializablePredicate<String> CONTAINS_T = string -> string
            .contains("t");

    private AppendableSearcher<String, SerializablePredicate<String>> searcher = new AppendableSearcher<>(
            DataProvider.create("one", "two", "three"));

    @Test
    public void basics() {

        SingleFilterSearcherTest.assertItems(searcher, "one", "two", "three");

        searcher.addFilter(CONTAINS_O);
        SingleFilterSearcherTest.assertItems(searcher, "one", "two");

        searcher.addFilter(CONTAINS_T);
        SingleFilterSearcherTest.assertItems(searcher, "two");

        searcher.searchBy(string -> string.contains("t"));
        SingleFilterSearcherTest.assertItems(searcher, "two", "three");

        searcher.clearFilters();
        SingleFilterSearcherTest.assertItems(searcher, "one", "two", "three");
    }

    @Test
    public void append() {
        AppendableFilterDataProvider<String, SerializablePredicate<String>> appended = searcher
                .withFilter(CONTAINS_T);
        SingleFilterSearcherTest.assertItems(appended, "two", "three");

        searcher.searchBy(CONTAINS_O);
        SingleFilterSearcherTest.assertItems(appended, "two");
    }

    @Test
    public void events() {
        AtomicInteger listenerCount = new AtomicInteger();

        searcher.addDataProviderListener(e -> listenerCount.incrementAndGet());

        searcher.searchBy(null);
        Assert.assertEquals(
                "Clearing from initial state should not fire events", 0,
                listenerCount.get());

        searcher.searchBy(CONTAINS_O);
        Assert.assertEquals("Setting a new filter should fire events", 1,
                listenerCount.get());

        searcher.searchBy(CONTAINS_O);
        Assert.assertEquals("Re-setting the same filter should fire events", 1,
                listenerCount.get());

        searcher.searchBy(CONTAINS_T);
        Assert.assertEquals("Setting another filter should fire events", 2,
                listenerCount.get());

        searcher.addFilter(CONTAINS_T);
        Assert.assertEquals("Adding existing filter shouldn't fire events", 2,
                listenerCount.get());

        searcher.addFilter(item -> false);
        Assert.assertEquals("Adding new filter should fire events", 3,
                listenerCount.get());

        searcher.clearFilters();
        Assert.assertEquals("Clearing the filter should fire events", 4,
                listenerCount.get());
    }

}
