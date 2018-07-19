package com.vaadin.tests.components.tabsheet;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that tabsheet's scroll button are rendered correctly in Chameleon
 * theme.
 *
 * Ticket #12154
 *
 * @author Vaadin Ltd
 */
public class TabsheetNotEnoughHorizontalSpaceTest extends MultiBrowserTest {

    @Test
    public void testThatTabScrollButtonsAreRenderedCorrectly()
            throws IOException {
        openTestURL();

        driver.findElement(By.className("v-tabsheet-scrollerPrev-disabled"));
        driver.findElement(By.className("v-tabsheet-scrollerNext"));

        compareScreen("init");
    }

}
