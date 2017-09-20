package com.vaadin.tests.components.grid;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class DataCommunicatorInfiniteLoopTest extends SingleBrowserTest {

    @Test
    public void grid_does_not_get_stuck_in_infinite_loop() {
        openTestURL();
        waitUntil(driver -> driver.findElement(By.className("v-grid-body"))
                .findElements(By.className("v-grid-row")).size() == 1);
    }
}
