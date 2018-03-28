package com.vaadin.tests.components.grid;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that Grid gets correct height based on height mode, and resizes
 * properly with details row if height is undefined.
 *
 * @author Vaadin Ltd
 */
@TestCategory("grid")
public class GridManyColumnsTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        waitForElementPresent(By.className("v-grid"));
    }

    @Test
    public void testGridPerformance() throws InterruptedException {
        long renderingTime = testBench().totalTimeSpentRendering();
        long requestTime = testBench().totalTimeSpentServicingRequests();
        System.out.println("Grid with many columns spent " + renderingTime
                + "ms rendering and " + requestTime + "ms servicing requests ("
                + getDesiredCapabilities().getBrowserName() + ")");
    }

}
