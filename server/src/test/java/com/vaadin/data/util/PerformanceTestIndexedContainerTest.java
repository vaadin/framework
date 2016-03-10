package com.vaadin.data.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.junit.Assert;

public class PerformanceTestIndexedContainerTest extends TestCase {

    private static final int REPEATS = 10;
    private final static int ITEMS = 50000;
    private static final long ADD_ITEM_FAIL_THRESHOLD = 200;
    // TODO should improve performance of these methods
    private static final long ADD_ITEM_AT_FAIL_THRESHOLD = 5000;
    private static final long ADD_ITEM_AFTER_FAIL_THRESHOLD = 5000;
    private static final long ADD_ITEM_AFTER_LAST_FAIL_THRESHOLD = 5000;
    private static final long ADD_ITEMS_CONSTRUCTOR_FAIL_THRESHOLD = 200;

    public void testAddItemPerformance() {
        Collection<Long> times = new ArrayList<Long>();
        for (int j = 0; j < REPEATS; ++j) {
            IndexedContainer c = new IndexedContainer();
            long start = System.currentTimeMillis();
            for (int i = 0; i < ITEMS; i++) {
                c.addItem();
            }
            times.add(System.currentTimeMillis() - start);
        }
        checkMedian(ITEMS, times, "IndexedContainer.addItem()",
                ADD_ITEM_FAIL_THRESHOLD);
    }

    public void testAddItemAtPerformance() {
        Collection<Long> times = new ArrayList<Long>();
        for (int j = 0; j < REPEATS; ++j) {
            IndexedContainer c = new IndexedContainer();
            long start = System.currentTimeMillis();
            for (int i = 0; i < ITEMS; i++) {
                c.addItemAt(0);
            }
            times.add(System.currentTimeMillis() - start);
        }
        checkMedian(ITEMS, times, "IndexedContainer.addItemAt()",
                ADD_ITEM_AT_FAIL_THRESHOLD);
    }

    public void testAddItemAfterPerformance() {
        Object initialId = "Item0";
        Collection<Long> times = new ArrayList<Long>();
        for (int j = 0; j < REPEATS; ++j) {
            IndexedContainer c = new IndexedContainer();
            c.addItem(initialId);
            long start = System.currentTimeMillis();
            for (int i = 0; i < ITEMS; i++) {
                c.addItemAfter(initialId);
            }
            times.add(System.currentTimeMillis() - start);
        }
        checkMedian(ITEMS, times, "IndexedContainer.addItemAfter()",
                ADD_ITEM_AFTER_FAIL_THRESHOLD);
    }

    public void testAddItemAfterLastPerformance() {
        // TODO running with less items because slow otherwise
        Collection<Long> times = new ArrayList<Long>();
        for (int j = 0; j < REPEATS; ++j) {
            IndexedContainer c = new IndexedContainer();
            c.addItem();
            long start = System.currentTimeMillis();
            for (int i = 0; i < ITEMS / 3; i++) {
                c.addItemAfter(c.lastItemId());
            }
            times.add(System.currentTimeMillis() - start);
        }
        checkMedian(ITEMS / 3, times, "IndexedContainer.addItemAfter(lastId)",
                ADD_ITEM_AFTER_LAST_FAIL_THRESHOLD);
    }

    public void testAddItemsConstructorPerformance() {
        Collection<Object> items = new ArrayList<Object>(50000);
        for (int i = 0; i < ITEMS; ++i) {
            items.add(new Object());
        }

        SortedSet<Long> times = new TreeSet<Long>();
        for (int j = 0; j < REPEATS; ++j) {
            long start = System.currentTimeMillis();
            new IndexedContainer(items);
            times.add(System.currentTimeMillis() - start);
        }
        checkMedian(ITEMS, times, "IndexedContainer(Collection)",
                ADD_ITEMS_CONSTRUCTOR_FAIL_THRESHOLD);
    }

    private void checkMedian(int items, Collection<Long> times,
            String methodName, long threshold) {
        long median = median(times);
        System.out.println(methodName + " timings (ms) for " + items
                + " items: " + times);
        Assert.assertTrue(methodName + " too slow, median time " + median
                + "ms for " + items + " items", median <= threshold);
    }

    private Long median(Collection<Long> times) {
        ArrayList<Long> list = new ArrayList<Long>(times);
        Collections.sort(list);
        // not exact median in some cases, but good enough
        return list.get(list.size() / 2);
    }

}
