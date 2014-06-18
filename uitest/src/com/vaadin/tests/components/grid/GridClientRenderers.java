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
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.NativeButtonElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.elements.ServerClass;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.widgetset.client.grid.GridClientColumnRendererConnector.Renderers;
import com.vaadin.tests.widgetset.server.grid.GridClientColumnRenderers;

/**
 * Tests Grid client side renderers
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class GridClientRenderers extends MultiBrowserTest {

    private int latency = 0;

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        List<DesiredCapabilities> browsers = super.getBrowsersToTest();
        // FIXME: PhantomJS can't handle alerts. Fix test to use something else.
        browsers.remove(Browser.PHANTOMJS.getDesiredCapabilities());
        return browsers;
    }

    @Override
    protected Class<?> getUIClass() {
        return GridClientColumnRenderers.class;
    }

    @Override
    protected String getDeploymentPath() {
        if (latency > 0) {
            return super.getDeploymentPath() + "?latency=" + latency;
        }
        return super.getDeploymentPath();
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
        TestBenchElement cell = getGrid().getCell(1, 1);
        WebElement gwtButton = cell.findElement(By.tagName("button"));
        gwtButton.click();

        // Should be an alert visible
        Alert alert = driver.switchTo().alert();
        assertEquals("Alert window did not contain text \"Click\"",
                alert.getText(), "Click");
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
        TestBenchElement cell = getGrid().getCell(1, 1);
        WebElement gwtButton = cell.findElement(By.tagName("button"));
        gwtButton.click();

        // Should be an alert visible
        Alert alert = driver.switchTo().alert();
        assertEquals("Alert window did not contain text \"Click\"",
                alert.getText(), "Click");
    }

    @Test
    public void rowsWithDataHasStyleName() throws Exception {

        // Simulate network latency with 1500ms unless it's IE. IE takes longer
        // time to get started.
        latency = (BrowserUtil.isIE(getDesiredCapabilities()) ? 4000 : 1500);

        openTestURL();

        TestBenchElement row = getGrid().getRow(1);
        String className = row.getAttribute("class");
        assertFalse(
                "Row should not yet contain style name v-grid-row-has-data",
                className.contains("v-grid-row-has-data"));

        // Wait for data to arrive
        sleep(latency + 1000);

        row = getGrid().getRow(1);
        className = row.getAttribute("class");
        assertTrue("Row should now contain style name v-grid-row-has-data",
                className.contains("v-grid-row-has-data"));
    }

    @Test
    public void complexRendererSetVisibleContent() throws Exception {

        // Simulate network latency with 1500ms
        latency = 1500;

        // Chrome uses RGB instead of RGBA
        String colorRed = "rgba(255, 0, 0, 1)";
        String colorWhite = "rgba(255, 255, 255, 1)";
        if (BrowserUtil.isChrome(getDesiredCapabilities())) {
            colorRed = "rgb(255, 0, 0)";
            colorWhite = "rgb(255, 255, 255)";
        }

        openTestURL();

        addColumn(Renderers.CPLX_RENDERER);

        // Cell should be red (setContentVisible set cell red)
        String backgroundColor = getGrid().getCell(51, 1).getCssValue(
                "backgroundColor");
        assertEquals("Background color was not red.", colorRed, backgroundColor);

        // Wait for data to arrive
        sleep(latency + 1000);

        // Cell should no longer be red
        backgroundColor = getGrid().getCell(51, 1).getCssValue(
                "backgroundColor");
        assertEquals("Background color was not white", colorWhite,
                backgroundColor);
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
