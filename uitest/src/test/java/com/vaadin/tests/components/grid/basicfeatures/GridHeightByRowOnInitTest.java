package com.vaadin.tests.components.grid.basicfeatures;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@SuppressWarnings("all")
@TestCategory("grid")
public class GridHeightByRowOnInitTest extends MultiBrowserTest {

    @Test
    public void gridHeightIsMoreThanACoupleOfRows() {
        openTestURL();
        int height = $(GridElement.class).first().getSize().getHeight();
        assertGreater("Grid should be much taller than 150px (was " + height
                + "px)", height, 150);
    }
}
