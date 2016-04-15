package com.vaadin.tests.components.grid.basicfeatures;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@SuppressWarnings("all")
@TestCategory("grid")
public class GridClientHeightByRowOnInitTest extends MultiBrowserTest {
    @Test
    public void gridHeightIsMoreThanACoupleOfRows() {
        openTestURL();
        int height = findElement(By.className("v-grid")).getSize().getHeight();
        assertGreater("Grid should be much taller than 150px (was " + height
                + "px)", height, 150);
    }
}
