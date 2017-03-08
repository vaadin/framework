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
package com.vaadin.v7.tests.server.component.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Table;

/**
 * Test case for testing the footer API
 *
 */
public class FooterTest {

    /**
     * Tests if setting the footer visibility works properly
     */
    @Test
    public void testFooterVisibility() {
        Table table = new Table("Test table", createContainer());

        // The footer should by default be hidden
        assertFalse(table.isFooterVisible());

        // Set footer visibility to tru should be reflected in the
        // isFooterVisible() method
        table.setFooterVisible(true);
        assertTrue(table.isFooterVisible());
    }

    /**
     * Tests adding footers to the columns
     */
    @Test
    public void testAddingFooters() {
        Table table = new Table("Test table", createContainer());

        // Table should not contain any footers at initialization
        assertNull(table.getColumnFooter("col1"));
        assertNull(table.getColumnFooter("col2"));
        assertNull(table.getColumnFooter("col3"));

        // Adding column footer
        table.setColumnFooter("col1", "Footer1");
        assertEquals("Footer1", table.getColumnFooter("col1"));

        // Add another footer
        table.setColumnFooter("col2", "Footer2");
        assertEquals("Footer2", table.getColumnFooter("col2"));

        // Add footer for a non-existing column
        table.setColumnFooter("fail", "FooterFail");
    }

    /**
     * Test removing footers
     */
    @Test
    public void testRemovingFooters() {
        Table table = new Table("Test table", createContainer());
        table.setColumnFooter("col1", "Footer1");
        table.setColumnFooter("col2", "Footer2");

        // Test removing footer
        assertNotNull(table.getColumnFooter("col1"));
        table.setColumnFooter("col1", null);
        assertNull(table.getColumnFooter("col1"));

        // The other footer should still be there
        assertNotNull(table.getColumnFooter("col2"));

        // Remove non-existing footer
        table.setColumnFooter("fail", null);
    }

    /**
     * Creates a container with three properties "col1,col2,col3" with 100 items
     *
     * @return Returns the created table
     */
    private static Container createContainer() {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("col1", String.class, "");
        container.addContainerProperty("col2", String.class, "");
        container.addContainerProperty("col3", String.class, "");

        for (int i = 0; i < 100; i++) {
            Item item = container.addItem("item " + i);
            item.getItemProperty("col1").setValue("first" + i);
            item.getItemProperty("col2").setValue("middle" + i);
            item.getItemProperty("col3").setValue("last" + i);
        }

        return container;
    }
}
