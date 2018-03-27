package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridMultiSelectionScrollBarTest extends MultiBrowserTest {

    @Test
    public void testNoVisibleScrollBar() throws IOException {
        setDebug(true);
        openTestURL();

        assertTrue("Horizontal scrollbar should not be visible.",
                $(GridElement.class).first().getHorizontalScroller()
                        .getAttribute("style").toLowerCase()
                        .contains("display: none;"));

        // Just to make sure nothing odd happened.
        assertNoErrorNotifications();
    }

}
