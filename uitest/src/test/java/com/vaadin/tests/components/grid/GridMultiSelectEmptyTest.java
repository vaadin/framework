package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridMultiSelectEmptyTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // On PhantomJS the result is more correct before recalculation.
        return getBrowsersExcludingPhantomJS();
    }

    @Test
    public void testCheckBoxColumnCorrectSize() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        int startingWidth = grid.getHeaderCell(0, 0).getSize().getWidth();
        $(ButtonElement.class).caption("Add Row").first().click();
        int currentWidth = grid.getHeaderCell(0, 0).getSize().getWidth();

        assertEquals(
                "Checkbox column size should not change when data is added",
                startingWidth, currentWidth);

        $(ButtonElement.class).caption("Recalculate").first().click();
        currentWidth = grid.getHeaderCell(0, 0).getSize().getWidth();
        assertEquals(
                "Checkbox column size should not change when columns are recalculated",
                startingWidth, currentWidth);
    }

}
