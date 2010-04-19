package com.vaadin.tests.server.component.table;

import junit.framework.TestCase;

import com.vaadin.ui.Table;
import com.vaadin.ui.Table.FooterClickEvent;
import com.vaadin.ui.Table.FooterClickHandler;

/**
 * Tests the footer click handler
 */
public class TestFooterClickHandlers extends TestCase {

    /**
     * Tests setting the click handler
     */
    public void testAddingClickHandler() {
        final Table table = new Table();

        // Create click handler
        FooterClickHandler handler = new FooterClickHandler() {
            public void handleFooterClick(FooterClickEvent event) {
            }
        };

        // No predefined footer click listeners should be present
        assertNull(table.getFooterClickHandler());

        // Set the click handler
        table.setFooterClickHandler(handler);
        assertEquals(handler, table.getFooterClickHandler());
    }

    /**
     * Tests changing the click handler to another one
     */
    public void testChangingClickHandler() {
        final Table table = new Table();

        // Create 2 click handlers
        FooterClickHandler handler1 = new FooterClickHandler() {
            public void handleFooterClick(FooterClickEvent event) {
            }
        };

        FooterClickHandler handler2 = new FooterClickHandler() {
            public void handleFooterClick(FooterClickEvent event) {
            }
        };

        // Set the click handler
        table.setFooterClickHandler(handler1);
        assertEquals(handler1, table.getFooterClickHandler());

        // Change the click handler to another one
        table.setFooterClickHandler(handler2);
        assertEquals(handler2, table.getFooterClickHandler());
    }

    /**
     * Tests if click handler is removed
     */
    public void testRemovingClickHandler() {
        final Table table = new Table();

        // Create click handler
        FooterClickHandler handler = new FooterClickHandler() {
            public void handleFooterClick(FooterClickEvent event) {
            }
        };

        // No predefined footer click listeners should be present
        assertNull(table.getFooterClickHandler());

        // Set the click handler
        table.setFooterClickHandler(handler);
        assertEquals(handler, table.getFooterClickHandler());

        // Remove the click handler
        table.setFooterClickHandler(null);
        assertNull(table.getFooterClickHandler());
    }
}
