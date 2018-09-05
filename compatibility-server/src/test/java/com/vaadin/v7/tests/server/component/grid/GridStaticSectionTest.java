package com.vaadin.v7.tests.server.component.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.v7.data.Container.Indexed;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Grid;

public class GridStaticSectionTest extends Grid {

    private Indexed dataSource = new IndexedContainer();

    @Before
    public void setUp() {
        dataSource.addContainerProperty("firstName", String.class, "");
        dataSource.addContainerProperty("lastName", String.class, "");
        dataSource.addContainerProperty("streetAddress", String.class, "");
        dataSource.addContainerProperty("zipCode", Integer.class, null);
        setContainerDataSource(dataSource);
    }

    @Test
    public void testAddAndRemoveHeaders() {
        assertEquals(1, getHeaderRowCount());
        prependHeaderRow();
        assertEquals(2, getHeaderRowCount());
        removeHeaderRow(0);
        assertEquals(1, getHeaderRowCount());
        removeHeaderRow(0);
        assertEquals(0, getHeaderRowCount());
        assertEquals(null, getDefaultHeaderRow());
        HeaderRow row = appendHeaderRow();
        assertEquals(1, getHeaderRowCount());
        assertEquals(null, getDefaultHeaderRow());
        setDefaultHeaderRow(row);
        assertEquals(row, getDefaultHeaderRow());
    }

    @Test
    public void testAddAndRemoveFooters() {
        // By default there are no footer rows
        assertEquals(0, getFooterRowCount());
        FooterRow row = appendFooterRow();

        assertEquals(1, getFooterRowCount());
        prependFooterRow();
        assertEquals(2, getFooterRowCount());
        assertEquals(row, getFooterRow(1));
        removeFooterRow(0);
        assertEquals(1, getFooterRowCount());
        removeFooterRow(0);
        assertEquals(0, getFooterRowCount());
    }

    @Test
    public void testUnusedPropertyNotInCells() {
        removeColumn("firstName");
        assertNull("firstName cell was not removed from existing row",
                getDefaultHeaderRow().getCell("firstName"));
        HeaderRow newRow = appendHeaderRow();
        assertNull("firstName cell was created when it should not.",
                newRow.getCell("firstName"));
        addColumn("firstName");
        assertNotNull(
                "firstName cell was not created for default row when added again",
                getDefaultHeaderRow().getCell("firstName"));
        assertNotNull(
                "firstName cell was not created for new row when added again",
                newRow.getCell("firstName"));

    }

    @Test
    public void testJoinHeaderCells() {
        HeaderRow mergeRow = prependHeaderRow();
        mergeRow.join("firstName", "lastName").setText("Name");
        mergeRow.join(mergeRow.getCell("streetAddress"),
                mergeRow.getCell("zipCode"));
    }

    @Test(expected = IllegalStateException.class)
    public void testJoinHeaderCellsIncorrectly() throws Throwable {
        HeaderRow mergeRow = prependHeaderRow();
        mergeRow.join("firstName", "zipCode").setText("Name");
        sanityCheck();
    }

    @Test
    public void testJoinAllFooterCells() {
        FooterRow mergeRow = prependFooterRow();
        mergeRow.join(dataSource.getContainerPropertyIds().toArray())
                .setText("All the stuff.");
    }

    private void sanityCheck() throws Throwable {
        Method sanityCheckHeader;
        try {
            sanityCheckHeader = Grid.Header.class
                    .getDeclaredMethod("sanityCheck");
            sanityCheckHeader.setAccessible(true);
            Method sanityCheckFooter = Grid.Footer.class
                    .getDeclaredMethod("sanityCheck");
            sanityCheckFooter.setAccessible(true);
            sanityCheckHeader.invoke(getHeader());
            sanityCheckFooter.invoke(getFooter());
        } catch (Exception e) {
            throw e.getCause();
        }
    }
}
