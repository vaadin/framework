package com.vaadin.tests.themes.valo;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class NonDraggableWindowTest extends MultiBrowserTest {

    @Test
    public void cursorIsDefault() {
        openTestURL();

        WebElement header = findElement(By.className("v-window-header"));

        assertThat(header.getCssValue("cursor"), is("default"));
    }
}