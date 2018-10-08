package com.vaadin.tests.components.grid;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

/**
 * @author Vaadin Ltd
 */
public class RemoveGridWithComponentTest extends SingleBrowserTest {

    private GridElement grid;

    @Test
    public void RemoveGrid_CheckGridNotPresent() {
        openTestURL();

        grid = $(GridElement.class).id("grid-with-component");
        ButtonElement button = $(ButtonElement.class).id("remove-grid");
        button.click();
        assertElementNotPresent(By.id("grid-with-component"));
    }
}
