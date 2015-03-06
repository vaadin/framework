package com.vaadin.tests.components.table;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ReloadWidgetsTest extends MultiBrowserTest {

    private int rowHeight = -1;

    private WebElement wrapper;

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();

        TableElement table = $(TableElement.class).id("table");
        rowHeight = table.getCell(1, 0).getLocation().getY()
                - table.getCell(0, 0).getLocation().getY();

        wrapper = findElement(By.className("v-table-body-wrapper"));
    }

    @Test
    public void testScrollingThenUpdatingContents() throws Exception {
        // Scroll down to row 44 so that we get the cut-off point where the
        // problem becomes apparent
        testBenchElement(wrapper).scroll(44 * rowHeight);
        waitForScrollToFinish();

        // Assert that we have the button widget.
        Assert.assertTrue(
                "Button widget was not found after scrolling for the first time",
                !findElements(By.id("46")).isEmpty());

        // Now refresh the container contents
        WebElement refreshButton = findElement(By.id("refresh"));
        refreshButton.click();

        // Again scroll down to row 44 so we get the cut-off point visible
        testBenchElement(wrapper).scroll(44 * rowHeight);
        waitForScrollToFinish();

        // Assert that we still get the button
        Assert.assertTrue(
                "Button widget was not found after refreshing container items.",
                !findElements(By.id("46")).isEmpty());
    }

    /**
     * Waits until the scroll position indicator goes away, signifying that all
     * the required rows have been fetched.
     */
    private void waitForScrollToFinish() {
        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                List<WebElement> elements = findElements(By
                        .className("v-table-scrollposition"));
                return elements.isEmpty() || !elements.get(0).isDisplayed();
            }

            @Override
            public String toString() {
                // Timed out after 10 seconds waiting for ...
                return "scroll position indicator to vanish";
            }
        });
    }

}
