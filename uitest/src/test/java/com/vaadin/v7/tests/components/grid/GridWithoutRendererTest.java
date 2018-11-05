package com.vaadin.v7.tests.components.grid;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;

@TestCategory("grid")
public class GridWithoutRendererTest extends SingleBrowserTest {

    @Test
    public void ensureNoError() {
        openTestURL();
        // WebElement errorIndicator = findElement(By
        // .cssSelector("v-error-indicator"));
        // System.out.println(errorIndicator);
        List<WebElement> errorIndicator = findElements(
                By.xpath("//span[@class='v-errorindicator']"));
        assertTrue("There should not be an error indicator",
                errorIndicator.isEmpty());
    }

}
