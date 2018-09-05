package com.vaadin.tests.components.grid.basicfeatures.client;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.tests.components.grid.basicfeatures.GridBasicClientFeaturesTest;

public class GridClientContextMenuEventTest
        extends GridBasicClientFeaturesTest {

    @Test
    public void testContextMenuEventIsHandledCorrectly() {
        setDebug(true);
        openTestURL();

        selectMenuPath("Component", "Internals", "Listeners",
                "Add context menu listener");

        openDebugLogTab();
        clearDebugMessages();

        new Actions(getDriver())
                .moveToElement(getGridElement().getCell(0, 0), 5, 5)
                .contextClick().perform();

        assertTrue("Debug log was not visible", isElementPresent(By.xpath(
                "//span[text() = 'Prevented opening a context menu in grid body']")));

        new Actions(getDriver())
                .moveToElement(getGridElement().getHeaderCell(0, 0), 5, 5)
                .contextClick().perform();

        assertTrue("Debug log was not visible", isElementPresent(By.xpath(
                "//span[text() = 'Prevented opening a context menu in grid header']")));

    }
}
