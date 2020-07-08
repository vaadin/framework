package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridScrolledResizeTest extends MultiBrowserTest {

    @Test
    public void scrollUpdatedAfterResize() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        ButtonElement button = $(ButtonElement.class).first();
        WebElement scrollbar = grid
                .findElement(By.className("v-grid-scroller-horizontal"));
        TestBenchElement header = grid.getHeader();

        int initialHeaderRight = header.getLocation().getX()
                + header.getSize().getWidth();

        // resize to include scrollbar
        button.click();
        assertEquals("No visible scrollbar when one expected.", "block",
                scrollbar.getCssValue("display"));

        int scrolledHeaderRight = header.getLocation().getX()
                + header.getSize().getWidth();
        assertLessThan("Header should have moved with the resize.",
                scrolledHeaderRight, initialHeaderRight);

        // ensure the contents are scrolled
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollLeft = 1000", scrollbar);

        // resize to not include scrollbar
        button.click();
        waitUntilNot(ExpectedConditions.visibilityOf(scrollbar));

        // ensure the contents move with the resize
        int newHeaderRight = header.getLocation().getX()
                + header.getSize().getWidth();
        assertEquals("Header should have expanded back.", initialHeaderRight,
                newHeaderRight);
    }
}
