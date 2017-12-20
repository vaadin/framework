package com.vaadin.tests.themes.valo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class NonDraggableWindowTest extends MultiBrowserTest {

    @Test
    public void cursorIsDefault() {
        openTestURL();

        WebElement header = findElement(By.className("v-window-header"));

        assertEquals("default", header.getCssValue("cursor"));
    }
}
