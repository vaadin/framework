package com.vaadin.tests.server.component.table;

import junit.framework.TestCase;

import com.vaadin.ui.Table;
import com.vaadin.ui.Table.HeaderClickEvent;
import com.vaadin.ui.Table.HeaderClickHandler;

/**
 * Tests the header click handler
 */
public class TestHeaderClickHandlers extends TestCase {

    /**
     * Tests setting the click handler
     */
    public void testAddingClickHandler() {
        final Table table = new Table();

        // Create click handler
        HeaderClickHandler handler = new HeaderClickHandler() {
            public void handleHeaderClick(HeaderClickEvent event) {
            }
        };

        // No predefined header click listeners should be present
        assertNull(table.getHeaderClickHandler());

        // Set the click handler
        table.setHeaderClickHandler(handler);
        assertEquals(handler, table.getHeaderClickHandler());
    }

    /**
     * Tests changing the click handler to another one
     */
    public void testChangingClickHandler() {
        final Table table = new Table();

        // Create 2 click handlers
        HeaderClickHandler handler1 = new HeaderClickHandler() {
            public void handleHeaderClick(HeaderClickEvent event) {
            }
        };

        HeaderClickHandler handler2 = new HeaderClickHandler() {
            public void handleHeaderClick(HeaderClickEvent event) {
            }
        };

        // Set the click handler
        table.setHeaderClickHandler(handler1);
        assertEquals(handler1, table.getHeaderClickHandler());

        // Change the click handler to another one
        table.setHeaderClickHandler(handler2);
        assertEquals(handler2, table.getHeaderClickHandler());
    }

    /**
     * Tests if click handler is removed
     */
    public void testRemovingClickHandler() {
        final Table table = new Table();

        // Create click handler
        HeaderClickHandler handler = new HeaderClickHandler() {
            public void handleHeaderClick(HeaderClickEvent event) {
            }
        };

        // No predefined header click listeners should be present
        assertNull(table.getHeaderClickHandler());

        // Set the click handler
        table.setHeaderClickHandler(handler);
        assertEquals(handler, table.getHeaderClickHandler());

        // Remove the click handler
        table.setHeaderClickHandler(null);
        assertNull(table.getHeaderClickHandler());
    }
}
