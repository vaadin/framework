package com.vaadin.tests.components.grid.basicfeatures.escalator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.components.grid.basicfeatures.EscalatorBasicClientFeaturesTest;

public class EscalatorBasicsTest extends EscalatorBasicClientFeaturesTest {

    @Before
    public void setUp() {
        setDebug(true);
        openTestURL();
    }

    @Test
    public void testDetachingAnEmptyEscalator() {
        selectMenuPath(GENERAL, DETACH_ESCALATOR);
        assertEscalatorIsRemovedCorrectly();
    }

    @Test
    public void testDetachingASemiPopulatedEscalator() throws IOException {
        selectMenuPath(COLUMNS_AND_ROWS, ADD_ONE_OF_EACH_ROW);
        selectMenuPath(COLUMNS_AND_ROWS, COLUMNS, ADD_ONE_COLUMN_TO_BEGINNING);
        selectMenuPath(GENERAL, DETACH_ESCALATOR);
        assertEscalatorIsRemovedCorrectly();
    }

    @Test
    public void testDetachingAPopulatedEscalator() {
        selectMenuPath(GENERAL, POPULATE_COLUMN_ROW);
        selectMenuPath(GENERAL, DETACH_ESCALATOR);
        assertEscalatorIsRemovedCorrectly();
    }

    @Test
    public void testDetachingAndReattachingAnEscalator() {
        selectMenuPath(GENERAL, POPULATE_COLUMN_ROW);

        scrollVerticallyTo(50);
        scrollHorizontallyTo(50);

        selectMenuPath(GENERAL, DETACH_ESCALATOR);
        selectMenuPath(GENERAL, ATTACH_ESCALATOR);

        assertEquals("Vertical scroll position", 50, getScrollTop());
        assertEquals("Horizontal scroll position", 50, getScrollLeft());

        assertEquals("First cell of first visible row", "Row 2: 0,2",
                getBodyCell(0, 0).getText());
    }

    private void assertEscalatorIsRemovedCorrectly() {
        assertFalse($(NotificationElement.class).exists());
        assertNull(getEscalator());
    }
}
