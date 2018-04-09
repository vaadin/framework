package com.vaadin.v7.ui;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.v7.data.util.BeanItemContainerGenerator;

public class TableTest {

    Table table;

    @Before
    public void init() {
        table = new Table();
    }

    @Test
    public void initiallyEmpty() {
        assertTrue(table.isEmpty());
    }

    @Test
    public void emptyAfterClearSingleSelect() {
        table.setContainerDataSource(
                BeanItemContainerGenerator.createContainer(100));
        assertTrue(table.isEmpty());
        Object first = table.getContainerDataSource().getItemIds().iterator()
                .next();
        table.setValue(first);
        assertEquals(first, table.getValue());
        assertFalse(table.isEmpty());
        table.clear();
        assertEquals(null, table.getValue());
        assertTrue(table.isEmpty());
    }

    @Test
    public void emptyAfterClearMultiSelect() {
        table.setMultiSelect(true);
        table.setContainerDataSource(
                BeanItemContainerGenerator.createContainer(100));

        assertTrue(table.isEmpty());
        assertArrayEquals(new Object[] {},
                ((Collection) table.getValue()).toArray());

        Object first = table.getContainerDataSource().getItemIds().iterator()
                .next();
        table.select(first);
        assertArrayEquals(new Object[] { first },
                ((Collection) table.getValue()).toArray());
        assertFalse(table.isEmpty());

        table.clear();
        assertArrayEquals(new Object[] {},
                ((Collection) table.getValue()).toArray());
        assertTrue(table.isEmpty());
    }

}
