package com.vaadin.v7.tests.components.grid.basicfeatures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.elementsbase.ServerClass;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridDefaultTextRendererTest extends MultiBrowserTest {

    private GridElement grid;

    @ServerClass("com.vaadin.tests.widgetset.server.TestWidgetComponent")
    public static class MyGridElement extends GridElement {
    }

    @Before
    public void init() {
        setDebug(true);
        openTestURL();
        grid = $(MyGridElement.class).first();
        assertFalse("There was an unexpected notification during init",
                $(NotificationElement.class).exists());
    }

    @Test
    public void testNullIsRenderedAsEmptyStringByDefaultTextRenderer() {
        assertTrue("First cell should've been empty",
                grid.getCell(0, 0).getText().isEmpty());
    }

    @Test
    public void testStringIsRenderedAsStringByDefaultTextRenderer() {
        assertEquals("Second cell should've been populated ", "string",
                grid.getCell(1, 0).getText());
    }

    @Test
    public void testWarningShouldNotBeInDebugLog() {
        assertFalse("Warning visible with string content.", isElementPresent(
                By.xpath("//span[contains(.,'attached:#1')]")));
    }
}
