package com.vaadin.v7.ui;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

import com.vaadin.v7.data.util.ObjectProperty;

public class AbsSelectTest {

    @Test
    public void addItemsStrings() {
        NativeSelect ns = new NativeSelect();
        ns.addItems("Foo", "bar", "baz");
        assertEquals(3, ns.size());
        assertArrayEquals(new Object[] { "Foo", "bar", "baz" },
                ns.getItemIds().toArray());
    }

    @Test
    public void addItemsObjects() {
        Object o1 = new Object();
        Object o2 = new Object();
        Object o3 = new Object();

        NativeSelect ns = new NativeSelect();
        ns.addItems(o1, o2, o3);
        assertEquals(3, ns.size());
        assertArrayEquals(new Object[] { o1, o2, o3 },
                ns.getItemIds().toArray());
    }

    @Test
    public void addItemsStringList() {
        List<String> itemIds = new ArrayList<String>();
        itemIds.add("foo");
        itemIds.add("bar");
        itemIds.add("baz");
        NativeSelect ns = new NativeSelect();
        ns.addItems(itemIds);
        assertEquals(3, ns.size());
        assertArrayEquals(new Object[] { "foo", "bar", "baz" },
                ns.getItemIds().toArray());
    }

    @Test
    public void addItemsObjectList() {
        Object o1 = new Object();
        Object o2 = new Object();
        Object o3 = new Object();
        List<Object> itemIds = new ArrayList<Object>();
        itemIds.add(o1);
        itemIds.add(o2);
        itemIds.add(o3);
        NativeSelect ns = new NativeSelect();
        ns.addItems(itemIds);
        assertEquals(3, ns.size());
        assertArrayEquals(new Object[] { o1, o2, o3 },
                ns.getItemIds().toArray());

    }

    @Test
    public void singleSelectInitiallyEmpty() {
        AbstractSelect s = new ListSelect();
        assertTrue(s.isEmpty());
    }

    @Test
    public void singleSelectEmptyAfterClearUsingPDS() {
        AbstractSelect s = new ListSelect();
        s.addItem("foo");
        s.addItem("bar");
        s.setPropertyDataSource(new ObjectProperty<String>("foo"));

        assertFalse(s.isEmpty());
        s.clear();
        assertTrue(s.isEmpty());
    }

    @Test
    public void singleSelectEmptyAfterClear() {
        AbstractSelect s = new ListSelect();
        s.addItem("foo");
        s.addItem("bar");
        s.setValue("bar");

        assertFalse(s.isEmpty());
        s.clear();
        assertTrue(s.isEmpty());
    }

    @Test
    public void multiSelectInitiallyEmpty() {
        AbstractSelect s = new ListSelect();
        s.setMultiSelect(true);
        assertTrue(s.isEmpty());
    }

    @Test
    public void multiSelectEmptyAfterClearUsingPDS() {
        AbstractSelect s = new ListSelect();
        s.setMultiSelect(true);
        s.addItem("foo");
        s.addItem("bar");
        HashSet<String> sel = new HashSet<String>();
        sel.add("foo");
        sel.add("bar");
        s.setPropertyDataSource(new ObjectProperty<HashSet>(sel));

        assertFalse(s.isEmpty());
        s.clear();
        assertTrue(s.isEmpty());
    }

    @Test
    public void multiSelectEmptyAfterClear() {
        AbstractSelect s = new ListSelect();
        s.setMultiSelect(true);
        s.addItem("foo");
        s.addItem("bar");
        s.select("foo");
        s.select("bar");

        assertFalse(s.isEmpty());
        s.clear();
        assertTrue(s.isEmpty());
    }

}
