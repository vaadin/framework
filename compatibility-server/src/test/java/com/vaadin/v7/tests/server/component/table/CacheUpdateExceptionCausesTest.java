package com.vaadin.v7.tests.server.component.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.CacheUpdateException;

public class CacheUpdateExceptionCausesTest {
    @Test
    public void testSingleCauseException() {
        Table table = new Table();
        Throwable[] causes = { new RuntimeException("Broken in one way.") };

        CacheUpdateException exception = new CacheUpdateException(table,
                "Error during Table cache update.", causes);

        assertSame(causes[0], exception.getCause());
        assertEquals("Error during Table cache update.",
                exception.getMessage());
    }

    @Test
    public void testMultipleCauseException() {
        Table table = new Table();
        Throwable[] causes = { new RuntimeException("Broken in the first way."),
                new RuntimeException("Broken in the second way.") };

        CacheUpdateException exception = new CacheUpdateException(table,
                "Error during Table cache update.", causes);

        assertSame(causes[0], exception.getCause());
        assertEquals(
                "Error during Table cache update. Additional causes not shown.",
                exception.getMessage());
    }
}
