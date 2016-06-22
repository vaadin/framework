/*
 * Copyright 2000-2014 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.NativeButtonElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.elementsbase.ServerClass;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.widgetset.client.grid.GridClientColumnRendererConnector.Renderers;
import com.vaadin.tests.widgetset.server.grid.GridClientColumnRenderers;

/**
 * Tests Grid client side renderers
 * 
 * @author Vaadin Ltd
 */
@TestCategory("grid")
public class GridClientRenderers extends MultiBrowserTest {

    private static final double SLEEP_MULTIPLIER = 1.2;
    private int latency = 0;

    @Override
    protected Class<?> getUIClass() {
        return GridClientColumnRenderers.class;
    }

    @Override
    protected String getDeploymentPath(Class<?> uiClass) {
        String path = super.getDeploymentPath(uiClass);
        if (latency > 0) {
            path += (path.contains("?") ? "&" : "?") + "latency=" + latency;
        }
        return path;
    }

    @ServerClass("com.vaadin.tests.widgetset.server.grid.GridClientColumnRenderers.GridController")
    public static class MyClientGridElement extends GridElement {
    }

    @Override
    public void setup() throws Exception {
        latency = 0; // reset
        super.setup();
    }

    @Test
    public void addWidgetRenderer() throws Exception {
        openTestURL();

        // Add widget renderer column
        $(NativeSelectElement.class).first().selectByText(
                Renderers.WIDGET_RENDERER.toString());
        $(NativeButtonElement.class).caption("Add").first().click();

        // Click the button in cell 1,1
        TestBenchElement cell = getGrid().getCell(1, 2);
        WebElement gwtButton = cell.findElement(By.tagName("button"));
        gwtButton.click();

        // Should be an alert visible
        assertEquals("Button did not contain text \"Clicked\"", "Clicked",
                gwtButton.getText());
    }

    @Test
    public void detachAndAttachGrid() {
        openTestURL();

        // Add widget renderer column
        $(NativeSelectElement.class).first().selectByText(
                Renderers.WIDGET_RENDERER.toString());
        $(NativeButtonElement.class).caption("Add").first().click();

        // Detach and re-attach the Grid
        $(NativeButtonElement.class).caption("DetachAttach").first().click();

        // Click the button in cell 1,1
        TestBenchElement cell = getGrid().getCell(1, 2);
        WebElement gwtButton = cell.findElement(By.tagName("button"));
        gwtButton.click();

        // Should be an alert visible
        assertEquals("Button did not contain text \"Clicked\"",
                gwtButton.getText(), "Clicked");
    }

    @Test
    public void rowsWithDataHasStyleName() throws Exception {

        testBench().disableWaitForVaadin();

        // Simulate network latency with 2000ms
        latency = 2000;

        openTestURL();

        sleep((int) (latency * SLEEP_MULTIPLIER));

        TestBenchElement row = getGrid().getRow(51);
        String className = row.getAttribute("class");
        assertFalse(
                "Row should not yet contain style name v-grid-row-has-data",
                className.contains("v-grid-row-has-data"));

        // Wait for data to arrive
        sleep((int) (latency * SLEEP_MULTIPLIER));

        row = getGrid().getRow(51);
        className = row.getAttribute("class");
        assertTrue("Row should now contain style name v-grid-row-has-data",
                className.contains("v-grid-row-has-data"));
    }

    @Test
    public void complexRendererSetVisibleContent() throws Exception {

        DesiredCapabilities desiredCapabilities = getDesiredCapabilities();

        // Simulate network latency with 2000ms
        latency = 2000;
        if (BrowserUtil.isIE8(desiredCapabilities)) {
            // IE8 is slower than other browsers. Bigger latency is needed for
            // stability in this test.
            latency = 3000;
        }

        // Chrome uses RGB instead of RGBA
        String colorRed = "rgba(255, 0, 0, 1)";
        String colorWhite = "rgba(255, 255, 255, 1)";
        String colorDark = "rgba(239, 240, 241, 1)";

        openTestURL();

        getGrid();

        testBench().disableWaitForVaadin();

        // Test initial renderering with contentVisible = False
        TestBenchElement cell = getGrid().getCell(51, 1);
        String backgroundColor = cell.getCssValue("backgroundColor");
        assertEquals("Background color was not red.", colorRed, backgroundColor);

        // data arrives...
        sleep((int) (latency * SLEEP_MULTIPLIER));

        // Content becomes visible
        cell = getGrid().getCell(51, 1);
        backgroundColor = cell.getCssValue("backgroundColor");
        assertNotEquals("Background color was red.", colorRed, backgroundColor);

        // scroll down, new cells becomes contentVisible = False
        getGrid().scrollToRow(60);

        // Cell should be red (setContentVisible set cell red)
        cell = getGrid().getCell(55, 1);
        backgroundColor = cell.getCssValue("backgroundColor");
        assertEquals("Background color was not red.", colorRed, backgroundColor);

        // data arrives...
        sleep((int) (latency * SLEEP_MULTIPLIER));

        // Cell should no longer be red
        backgroundColor = cell.getCssValue("backgroundColor");
        assertTrue(
                "Background color was not reset",
                backgroundColor.equals(colorWhite)
                        || backgroundColor.equals(colorDark));
    }

    @Test
    public void testSortingEvent() throws Exception {
        openTestURL();

        $(NativeButtonElement.class).caption("Trigger sorting event").first()
                .click();

        String consoleText = $(LabelElement.class).id("testDebugConsole")
                .getText();

        assertTrue("Console text as expected",
                consoleText.contains("Columns: 1, order: Column 1: ASCENDING"));

    }

    @Test
    public void testListSorter() throws Exception {
        openTestURL();

        $(NativeButtonElement.class).caption("Shuffle").first().click();

        GridElement gridElem = $(MyClientGridElement.class).first();

        // XXX: DANGER! We'll need to know how many rows the Grid has!
        // XXX: Currently, this is impossible; hence the hardcoded value of 70.

        boolean shuffled = false;
        for (int i = 1, l = 70; i < l; ++i) {

            String str_a = gridElem.getCell(i - 1, 0).getAttribute("innerHTML");
            String str_b = gridElem.getCell(i, 0).getAttribute("innerHTML");

            int value_a = Integer.parseInt(str_a);
            int value_b = Integer.parseInt(str_b);

            if (value_a > value_b) {
                shuffled = true;
                break;
            }
        }
        assertTrue("Grid shuffled", shuffled);

        $(NativeButtonElement.class).caption("Test sorting").first().click();

        for (int i = 1, l = 70; i < l; ++i) {

            String str_a = gridElem.getCell(i - 1, 0).getAttribute("innerHTML");
            String str_b = gridElem.getCell(i, 0).getAttribute("innerHTML");

            int value_a = Integer.parseInt(str_a);
            int value_b = Integer.parseInt(str_b);

            if (value_a > value_b) {
                assertTrue("Grid sorted", false);
            }
        }
    }

    @Test
    public void testComplexRendererOnActivate() {
        openTestURL();

        GridCellElement cell = getGrid().getCell(3, 1);
        cell.click();
        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();

        assertEquals("onActivate was not called on KeyDown Enter.",
                "Activated!", cell.getText());

        cell = getGrid().getCell(4, 1);
        cell.click();
        new Actions(getDriver()).moveToElement(cell).doubleClick().perform();
        assertEquals("onActivate was not called on double click.",
                "Activated!", cell.getText());
    }

    private GridElement getGrid() {
        return $(MyClientGridElement.class).first();
    }

    private void addColumn(Renderers renderer) {
        // Add widget renderer column
        $(NativeSelectElement.class).first().selectByText(renderer.toString());
        $(NativeButtonElement.class).caption("Add").first().click();
    }
}
