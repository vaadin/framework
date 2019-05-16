package com.vaadin.tests.components.grid;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridEventSentOnColumnVisibilityChangeTest extends SingleBrowserTest {

    @Test
    public void changeVisibilityAssertLog() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        getSidebarOpenButton(grid).click();
        // hide the first column
        getSidebarPopup().findElements(By.tagName("td")).get(0).click();

        sleep(200);
        Assert.assertEquals("There should have only log/event", 1,
                getLogs().size());
        Assert.assertTrue("Log content should match",
                "1. UserOriginated: true".equals(getLogRow(0)));
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
