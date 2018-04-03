package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridColumnAutoExpandTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // The functionality doesn't work for PHJS_1. And the test fails. It
        // works for PHJS_2.
        return getBrowsersExcludingPhantomJS();
    }

    @Test
    public void testSecondColumnHasExpanded() {
        openTestURL();

        GridCellElement headerCell = $(GridElement.class).first()
                .getHeaderCell(0, 1);

        assertTrue("Column did not expand as expected",
                headerCell.getSize().getWidth() > 400);
    }

}
