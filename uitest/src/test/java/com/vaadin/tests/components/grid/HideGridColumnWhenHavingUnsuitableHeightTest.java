package com.vaadin.tests.components.grid;

import java.util.List;
import java.util.logging.Level;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class HideGridColumnWhenHavingUnsuitableHeightTest
        extends SingleBrowserTest {

    @Test
    public void hideAndScroll() {
        openTestURL("debug");
        GridElement grid = $(GridElement.class).first();

        getSidebarOpenButton(grid).click();
        // Hide first column
        getSidebarPopup().findElements(By.tagName("td")).get(0).click();

        grid.scrollToRow(25);
        assertNoDebugMessage(Level.SEVERE);
    }

    protected WebElement getSidebarOpenButton(GridElement grid) {
        List<WebElement> elements = grid
                .findElements(By.className("v-grid-sidebar-button"));
        return elements.isEmpty() ? null : elements.get(0);
    }

    protected WebElement getSidebarPopup() {
        List<WebElement> elements = findElements(
                By.className("v-grid-sidebar-popup"));
        return elements.isEmpty() ? null : elements.get(0);
    }
}
