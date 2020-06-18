package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridUnhideColumnsWithFrozenTest extends SingleBrowserTest {

    @Test
    public void visibleFrozenColumnCount() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        List<WebElement> frozen = grid.getHeader()
                .findElements(By.className("frozen"));
        assertEquals("Unexpected frozen column count before unhiding", 3,
                frozen.size());

        grid.findElement(By.className("v-grid-sidebar-button")).click();
        List<WebElement> hidden = findElement(
                By.className("v-grid-sidebar-content"))
                        .findElements(By.className("hidden"));
        assertEquals("Unexpected amount of hidden columns", 2, hidden.size());
        assertEquals("Unexpected hidden column", "4", hidden.get(1).getText());
        hidden.get(1).click();

        frozen = grid.getHeader().findElements(By.className("frozen"));
        assertEquals("Unexpected frozen column count after unhiding", 3,
                frozen.size());
    }
}
