package com.vaadin.tests.components.tabsheet;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class EmptyTabSheetTest extends MultiBrowserTest {
    @Test
    public void emptyTabSheet() throws Exception {
        openTestURL();

        compareScreen("empty");
    }

    @Test
    public void emptyTabSheetValo() {
        openTestURL("theme=valo");

        WebElement deco = getDriver()
                .findElement(By.className("v-tabsheet-deco"));

        assertEquals("none", deco.getCssValue("display"));
    }

}
