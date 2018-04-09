package com.vaadin.tests.components.gridlayout;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridLayoutScrollPositionTest extends MultiBrowserTest {

    @Test
    public void testToggleChildComponents() throws Exception {

        final int SCROLLTOP = 100;

        openTestURL();

        WebDriver driver = getDriver();

        WebElement ui = driver.findElement(By.className("v-ui"));

        testBenchElement(ui).scroll(SCROLLTOP);

        driver.findElement(By.id("visibility-toggle"))
                .findElement(By.tagName("input")).click();

        assertEquals("UI scroll position", String.valueOf(SCROLLTOP),
                ui.getAttribute("scrollTop"));
    }
}
