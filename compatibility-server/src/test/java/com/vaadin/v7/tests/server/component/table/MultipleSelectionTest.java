package com.vaadin.v7.tests.server.component.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Set;

import org.junit.Test;

import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Table;

public class MultipleSelectionTest {

    /**
     * Tests weather the multiple select mode is set when using Table.set
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testSetMultipleItems() {
        Table table = new Table("", createTestContainer());

        // Tests if multiple selection is set
        table.setMultiSelect(true);
        assertTrue(table.isMultiSelect());

        // Test multiselect by setting several items at once

        table.setValue(Arrays.asList("1", new String[] { "3" }));
        assertEquals(2, ((Set<String>) table.getValue()).size());
    }

    /**
     * Tests setting the multiselect mode of the Table. The multiselect mode
     * affects how mouse selection is made in the table by the user.
     */
    @Test
    public void testSetMultiSelectMode() {
        Table table = new Table("", createTestContainer());

        // Default multiselect mode should be MultiSelectMode.DEFAULT
        assertEquals(MultiSelectMode.DEFAULT, table.getMultiSelectMode());

        // Tests if multiselectmode is set
        table.setMultiSelectMode(MultiSelectMode.SIMPLE);
        assertEquals(MultiSelectMode.SIMPLE, table.getMultiSelectMode());
    }

    /**
     * Creates a testing container for the tests
     *
     * @return A new container with test items
     */
    private Container createTestContainer() {
        IndexedContainer container = new IndexedContainer(
                Arrays.asList("1", new String[] { "2", "3", "4" }));
        return container;
    }
}
