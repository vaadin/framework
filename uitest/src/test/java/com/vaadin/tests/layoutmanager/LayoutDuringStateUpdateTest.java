package com.vaadin.tests.layoutmanager;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class LayoutDuringStateUpdateTest extends SingleBrowserTest {

    @Test
    public void layoutDuringStateUpdate() {
        openTestURL();

        WebElement label = findElement(By.className("gwt-Label"));

        assertEquals("Layout phase count: 1", label.getText());
    }

}
