package com.vaadin.tests.server.component.table;

import junit.framework.TestCase;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Table;

/**
 * Test case for testing the footer API
 * 
 */
public class FooterTest extends TestCase {

    /**
     * Tests if setting the footer visibility works properly
     */
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
