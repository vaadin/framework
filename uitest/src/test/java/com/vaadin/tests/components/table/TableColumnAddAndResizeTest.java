package com.vaadin.tests.components.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableColumnAddAndResizeTest extends MultiBrowserTest {

    @Test
    public void testAddAndResizeColumn() {
        setDebug(true);
        openTestURL();

        $(ButtonElement.class).caption("Add and Resize").first().click();
        assertFalse("Error notification present.",
                $(NotificationElement.class).exists());
        assertEquals("Unexpected column width. ", 200, $(TableElement.class)
                .first().getCell(0, 1).getSize().getWidth());
    }
}
