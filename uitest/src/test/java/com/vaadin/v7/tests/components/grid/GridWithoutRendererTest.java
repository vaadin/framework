package com.vaadin.v7.tests.components.grid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;

@TestCategory("grid")
public class GridWithoutRendererTest extends SingleBrowserTest {

    @Test
    public void ensureNoError() {
        openTestURL();
        waitForElementPresent(By.className("v-grid"));
        List<WebElement> errorIndicator = findElements(
                By.className("v-errorindicator"));
        assertTrue("There should not be an error indicator",
                errorIndicator.isEmpty());

        // add an error to ensure that the check is correct
        $(ButtonElement.class).first().click();

        errorIndicator = findElements(By.className("v-errorindicator"));
        assertFalse("There should be an error indicator",
                errorIndicator.isEmpty());
    }

}
