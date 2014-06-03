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

import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebElement;

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

    @Override
    protected Class<?> getUIClass() {
        return GridClientColumnRenderers.class;
    }

    @ServerClass("com.vaadin.tests.widgetset.server.grid.GridClientColumnRenderers.GridController")
    public static class MyClientGridElement extends GridElement {
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
        assertEquals(alert.getText(), "Click");
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
        assertEquals(alert.getText(), "Click");
    }

    private GridElement getGrid() {
        return $(MyClientGridElement.class).first();
    }
}
