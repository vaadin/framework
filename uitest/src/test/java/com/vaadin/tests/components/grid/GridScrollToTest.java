package com.vaadin.tests.components.grid;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;
import com.vaadin.ui.Button;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@TestCategory("grid")
public class GridScrollToTest extends SingleBrowserTest {

    @Test
    public void testScrollToEnd() {
        openTestURL();
        String finalCellText = "199";

        assertEquals("Found final element even though should be at top of list",
                0, cellsContaining(finalCellText));

        $(ButtonElement.class).id("end").click();

        assertEquals("Could not find final element", 1,
                cellsContaining(finalCellText));
    }

    @Test
    public void testScrollToRow() {
        openTestURL();
        String row = "50";

        assertEquals("Found row element even though should be at top of list",
                0, cellsContaining(row));

        $(TextFieldElement.class).id("row-field").setValue(row);

        $(ButtonElement.class).id("row").click();

        assertEquals("Could not find row element", 1, cellsContaining(row));
    }

    @Test
    public void testScrollTop() {
        openTestURL();
        String row = "Name 0";

        assertEquals("Couldn't find first element", 1, cellsContaining(row));

        getGrid().getVerticalScroller().scroll(800);

        assertEquals(
                "Found first element even though we should have scrolled down",
                0, cellsContaining(row));

        $(ButtonElement.class).id("top").click();

        assertEquals("Couldn't find first element", 1, cellsContaining(row));
    }

    @Test
    public void scrollToLastWithDetailsShowDetails() {
        openTestURL();

        // Scroll to end
        $(ButtonElement.class).id("end").click();

        // Open details
        clickCellContaining("199");

        waitForElementPresent(By.className("v-grid-spacer"));
        waitForElementPresent(By.className("v-label"));
        assertTrue("Details not visible", getGrid().findElements(By.className("v-label")).stream()
                .filter(element -> element.getText().contains("Name 199 details")).findFirst().get().isDisplayed());
        // scroll away
        $(ButtonElement.class).id("top").click();

        assertEquals("Found final element even though should be at top of list",
                0, cellsContaining("199"));

        assertFalse("Found final element  details even though should be at top of list", getGrid().findElements(By.className("v-label")).stream()
                .filter(element -> element.getText().contains("Name 199 details")).findFirst().isPresent());

        // Scroll to end
        $(ButtonElement.class).id("end").click();

        assertTrue("Details not visible", getGrid().findElements(By.className("v-label")).stream()
                .filter(element -> element.getText().contains("Name 199 details")).findFirst().get().isDisplayed());
    }

    @Test
    public void scrollToRowShowsDetails() {
        openTestURL();

        // Scroll to 50
        $(TextFieldElement.class).id("row-field").setValue("50");

        $(ButtonElement.class).id("row").click();

        clickCellContaining("50");

        waitForElementPresent(By.className("v-grid-spacer"));
        waitForElementPresent(By.className("v-label"));
        assertTrue("Details not visible", getGrid().findElements(By.className("v-label")).stream()
                .filter(element -> element.getText().contains("Name 50 details")).findFirst().get().isDisplayed());
        // scroll away
        $(ButtonElement.class).id("top").click();

        assertEquals("Found final element even though should be at top of list",
                0, cellsContaining("50"));

        assertFalse("Found final element  details even though should be at top of list", getGrid().findElements(By.className("v-label")).stream()
                .filter(element -> element.getText().contains("Name 50 details")).findFirst().isPresent());

        // Scroll to end
        $(ButtonElement.class).id("row").click();

        assertTrue("Details not visible", getGrid().findElements(By.className("v-label")).stream()
                .filter(element -> element.getText().contains("Name 50 details")).findFirst().get().isDisplayed());
    }

    private GridElement getGrid() {
        return $(GridElement.class).first();
    }

    private long cellsContaining(String text) {
        return getGrid().findElements(By.className("v-grid-cell")).stream()
                .filter(element -> element.getText().contains(text)).count();
    }

    private void clickCellContaining(String text) {
        Optional<WebElement> first = getGrid()
                .findElements(By.className("v-grid-cell")).stream()
                .filter(element -> element.getText().contains(text))
                .findFirst();
        if (first.isPresent())
            first.get().click();
        else
            Assert.fail("Cell not present");
    }
}
